package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiVideoAiService
import com.example.ai.GeneratedSocialMediaKit
import com.example.data.local.AppDatabase
import com.example.data.model.AudioSourcePreset
import com.example.data.model.MediaAsset
import com.example.data.model.MediaSourceItem
import com.example.data.model.TranscriptSegment
import com.example.data.model.TranscriptionParser
import com.example.data.model.UserVideoMetadata
import com.example.data.model.VideoProject
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

enum class EditorTab(val title: String) {
    TRIM("Trim"),
    TEXT("Text Overlay"),
    MEMES("Memes"),
    MEDIA("Media Sources"),
    AUDIO("Audio Levels"),
    FILTERS("Filters"),
    FORMAT("Format"),
    AI_SUITE("AI Suite"),
    SHARE("Share & Export")
}

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Success(val kit: GeneratedSocialMediaKit) : AiUiState
    data class Error(val message: String) : AiUiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    private val aiService = GeminiVideoAiService()

    val projects: StateFlow<List<VideoProject>>
    val assets: StateFlow<List<MediaAsset>>
    val userVideos: StateFlow<List<UserVideoMetadata>>

    private val _activeProject = MutableStateFlow<VideoProject?>(null)
    val activeProject: StateFlow<VideoProject?> = _activeProject.asStateFlow()

    private val _currentTab = MutableStateFlow(EditorTab.TRIM)
    val currentTab: StateFlow<EditorTab> = _currentTab.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayheadMs = MutableStateFlow(0L)
    val currentPlayheadMs: StateFlow<Long> = _currentPlayheadMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _aiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val aiState: StateFlow<AiUiState> = _aiState.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private var playbackJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProjectRepository(db.projectDao(), db.assetDao(), db.userVideoDao())

        projects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        assets = repository.allAssets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        userVideos = repository.allUserVideos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectProject(project: VideoProject) {
        _activeProject.value = project
        _currentPlayheadMs.value = project.trimStartMs
        _isPlaying.value = false
        playbackJob?.cancel()
        _aiState.value = AiUiState.Idle
    }

    fun setTab(tab: EditorTab) {
        _currentTab.value = tab
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        val project = _activeProject.value ?: return
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 50L
            while (isActive && _isPlaying.value) {
                delay(stepMs)
                val current = _currentPlayheadMs.value
                val next = current + (stepMs * _playbackSpeed.value).toLong()
                if (next > project.trimEndMs) {
                    _currentPlayheadMs.value = project.trimStartMs // Loop within trim boundary
                } else {
                    _currentPlayheadMs.value = next
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(ms: Long) {
        val project = _activeProject.value ?: return
        val clamped = ms.coerceIn(project.trimStartMs, project.trimEndMs)
        _currentPlayheadMs.value = clamped
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    // --- Advanced Editing Tools ---

    fun updateTrim(startMs: Long, endMs: Long) {
        val current = _activeProject.value ?: return
        val validStart = startMs.coerceIn(0L, current.originalDurationMs - 1000L)
        val validEnd = endMs.coerceIn(validStart + 1000L, current.originalDurationMs)

        val updated = current.copy(
            trimStartMs = validStart,
            trimEndMs = validEnd,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        _currentPlayheadMs.value = _currentPlayheadMs.value.coerceIn(validStart, validEnd)
        persistProject(updated)
    }

    fun setTrimStartToPlayhead() {
        val current = _activeProject.value ?: return
        val playhead = _currentPlayheadMs.value
        if (playhead < current.trimEndMs - 500L) {
            updateTrim(playhead, current.trimEndMs)
        }
    }

    fun setTrimEndToPlayhead() {
        val current = _activeProject.value ?: return
        val playhead = _currentPlayheadMs.value
        if (playhead > current.trimStartMs + 500L) {
            updateTrim(current.trimStartMs, playhead)
        }
    }

    fun updateColorFilter(filter: String, intensity: Float) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            colorFilter = filter,
            filterIntensity = intensity.coerceIn(0.0f, 1.0f),
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    fun updateAudioLevels(
        originalVolume: Float,
        isMuted: Boolean,
        voiceoverVolume: Float,
        voiceClarity: Boolean
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            originalAudioVolume = originalVolume.coerceIn(0.0f, 2.0f),
            isOriginalAudioMuted = isMuted,
            voiceoverVolume = voiceoverVolume.coerceIn(0.0f, 2.0f),
            voiceClarityEnhanced = voiceClarity,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    fun updateTextOverlay(
        text: String,
        colorHex: String,
        bgStyle: String,
        position: String,
        size: Int
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            overlayText = text,
            overlayTextColorHex = colorHex,
            overlayTextBgStyle = bgStyle,
            overlayTextPosition = position,
            overlayTextSize = size,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    fun updateAspectRatio(ratio: String) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            aspectRatio = ratio,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    fun updateProjectTitle(title: String) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            title = title,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    fun updateProjectNotes(notes: String) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            rawTopicNotes = notes,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    fun updateLanguage(lang: String) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            language = lang,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    // --- Google Gemini Media AI Execution ---

    fun generateAiSocialKit(customNotes: String? = null) {
        val project = _activeProject.value ?: return
        val notesToUse = customNotes?.ifBlank { null } ?: project.rawTopicNotes.ifBlank { project.title }
        val durationSec = (project.trimmedDurationMs / 1000L).toInt()

        viewModelScope.launch {
            _aiState.value = AiUiState.Loading
            try {
                val result = aiService.generateMultiFormatContent(
                    projectTitle = project.title,
                    rawNotesOrTranscript = notesToUse,
                    languagePreference = project.language,
                    aspectRatio = project.aspectRatio,
                    durationSeconds = durationSec
                )

                result.fold(
                    onSuccess = { kit ->
                        _aiState.value = AiUiState.Success(kit)
                        // Automatically update project with generated content and save to Room for offline access
                        val updated = project.copy(
                            aiSummaryEn = kit.summaryEn,
                            aiSummaryTe = kit.summaryTe,
                            youtubeTitle = kit.youtubeTitle,
                            youtubeDescription = kit.youtubeDescription,
                            youtubeChapters = kit.youtubeChapters,
                            instagramCaption = kit.instagramCaption,
                            instagramHashtags = kit.instagramHashtags,
                            tiktokHook = kit.tiktokHook,
                            tiktokCaptions = kit.tiktokCaptions,
                            brollSuggestions = kit.brollSuggestions,
                            overlayText = if (project.overlayText.isBlank()) kit.overlayTextSuggestion else project.overlayText,
                            updatedAt = System.currentTimeMillis()
                        )
                        _activeProject.value = updated
                        persistProject(updated)
                        _userMessage.value = "AI Generation complete! Saved to offline storage."
                    },
                    onFailure = { error ->
                        Log.e(TAG, "AI Generation failed", error)
                        _aiState.value = AiUiState.Error(error.localizedMessage ?: "Failed to generate AI content.")
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during AI generation", e)
                _aiState.value = AiUiState.Error(e.localizedMessage ?: "An error occurred.")
            }
        }
    }

    fun markProjectExported() {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            isExported = true,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)

        // Store video metadata in Room database for offline management
        viewModelScope.launch {
            val app = getApplication<Application>()
            val sanitized = current.title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(24)
            val localPath = "${app.filesDir.absolutePath}/videos/${sanitized}_${System.currentTimeMillis()}.mp4"
            val res = when (current.aspectRatio) {
                "16:9" -> "1920x1080"
                "1:1" -> "1080x1080"
                "4:5" -> "1080x1350"
                else -> "1080x1920"
            }
            val simulatedBytes = (current.trimmedDurationMs * 1_200_000L / 1000L).coerceAtLeast(3_000_000L)

            val videoMeta = UserVideoMetadata(
                projectTitle = current.title,
                localFilePath = localPath,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                durationMs = current.trimmedDurationMs,
                fileSizeBytes = simulatedBytes,
                resolution = res,
                aspectRatio = current.aspectRatio,
                mimeType = "video/mp4",
                isSynced = false,
                tags = current.language,
                notes = current.overlayText.ifBlank { current.rawTopicNotes.take(60) }
            )
            repository.insertUserVideo(videoMeta)
        }
        _userMessage.value = "Video package saved to offline storage!"
    }

    fun createProjectFromAsset(asset: MediaAsset, customTitle: String? = null) {
        viewModelScope.launch {
            val title = customTitle?.ifBlank { null } ?: asset.name.substringBeforeLast(".")
            val newProject = VideoProject(
                title = title,
                videoUri = asset.uri,
                audioUri = if (asset.type == "VOICEOVER" || asset.type == "AUDIO") asset.uri else null,
                originalDurationMs = asset.durationMs,
                trimStartMs = 0L,
                trimEndMs = asset.durationMs,
                aspectRatio = if (asset.type == "VIDEO") "9:16" else "16:9",
                language = asset.language,
                rawTopicNotes = asset.description.ifBlank { asset.sampleTopic }
            )
            val newId = repository.insertProject(newProject)
            val inserted = newProject.copy(id = newId)
            selectProject(inserted)
            _userMessage.value = "Created project '$title'"
        }
    }

    fun createBlankProject(title: String, durationSec: Int, language: String, notes: String) {
        viewModelScope.launch {
            val totalMs = (durationSec * 1000L).coerceIn(5_000L, 120_000L)
            val newProject = VideoProject(
                title = title.ifBlank { "Untitled Reel" },
                videoUri = "asset://custom_timeline_video.mp4",
                originalDurationMs = totalMs,
                trimStartMs = 0L,
                trimEndMs = totalMs,
                aspectRatio = "9:16",
                language = language,
                rawTopicNotes = notes
            )
            val newId = repository.insertProject(newProject)
            val inserted = newProject.copy(id = newId)
            selectProject(inserted)
            _userMessage.value = "New video project created!"
        }
    }

    fun addCustomMediaAsset(name: String, type: String, uri: String, durationMs: Long, language: String, desc: String) {
        viewModelScope.launch {
            val asset = MediaAsset(
                name = name,
                type = type,
                uri = uri,
                durationMs = durationMs,
                language = language,
                description = desc,
                sampleTopic = desc
            )
            repository.insertAsset(asset)
            _userMessage.value = "Imported $name to Media Assets"
        }
    }

    fun deleteProject(project: VideoProject) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_activeProject.value?.id == project.id) {
                _activeProject.value = null
                pausePlayback()
            }
            _userMessage.value = "Deleted '${project.title}'"
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun deleteUserVideo(video: UserVideoMetadata) {
        viewModelScope.launch {
            repository.deleteUserVideo(video)
            _userMessage.value = "Removed '${video.projectTitle}' from offline storage"
        }
    }

    fun recordCreatedVideo(
        title: String,
        localFilePath: String,
        durationMs: Long,
        fileSizeBytes: Long = 0L,
        aspectRatio: String = "9:16"
    ) {
        viewModelScope.launch {
            val metadata = UserVideoMetadata(
                projectTitle = title,
                localFilePath = localFilePath,
                durationMs = durationMs,
                fileSizeBytes = fileSizeBytes,
                aspectRatio = aspectRatio,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.insertUserVideo(metadata)
            _userMessage.value = "Saved '$title' to offline video vault"
        }
    }

    // --- Audio-to-Video & Transcription Suite ---

    fun createProjectFromAudioSource(
        title: String,
        durationMs: Long,
        language: String,
        audioUri: String,
        transcript: String,
        segments: List<TranscriptSegment>,
        aspectRatio: String = "9:16",
        backgroundUri: String? = null,
        backgroundType: String = "GRADIENT",
        backgroundSource: String = "LOCAL"
    ) {
        viewModelScope.launch {
            val validDuration = durationMs.coerceIn(5_000L, 120_000L)
            val segmentsJson = TranscriptionParser.toJson(segments)
            val newProject = VideoProject(
                title = title.ifBlank { "Audio Story Reel" },
                videoUri = "asset://audio_visualizer_base.mp4",
                audioUri = audioUri,
                originalDurationMs = validDuration,
                trimStartMs = 0L,
                trimEndMs = validDuration,
                aspectRatio = aspectRatio,
                language = language,
                rawTopicNotes = transcript,
                audioSourceUri = audioUri,
                audioTranscript = transcript,
                transcriptSegmentsJson = segmentsJson,
                captionStyle = "POP_IN",
                backgroundMediaUri = backgroundUri,
                backgroundMediaType = backgroundType,
                backgroundMediaSource = backgroundSource
            )
            val newId = repository.insertProject(newProject)
            val inserted = newProject.copy(id = newId)
            selectProject(inserted)
            _userMessage.value = "Audio-to-Video project created with live transcription!"
        }
    }

    fun updateTranscription(
        transcript: String,
        segmentsJson: String,
        captionStyle: String
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            audioTranscript = transcript,
            transcriptSegmentsJson = segmentsJson,
            captionStyle = captionStyle,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
        _userMessage.value = "Updated video transcription captions"
    }

    fun updateCaptionStyle(style: String) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            captionStyle = style,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
    }

    // --- Background Media Sourcing (Google Photos / Google Search / Social Media) ---

    fun updateBackgroundMedia(
        uri: String,
        type: String, // "PHOTO", "VIDEO_CLIP", "GRADIENT"
        source: String // "GOOGLE_PHOTOS", "GOOGLE_SEARCH", "SOCIAL_MEDIA", "LOCAL"
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            backgroundMediaUri = uri,
            backgroundMediaType = type,
            backgroundMediaSource = source,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
        _userMessage.value = "Updated background from $source"
    }

    fun importMediaSourceAsAsset(item: MediaSourceItem) {
        viewModelScope.launch {
            val asset = MediaAsset(
                name = item.title,
                type = if (item.mediaType == "PHOTO") "IMAGE" else "VIDEO",
                uri = item.sampleUri,
                durationMs = if (item.mediaType == "PHOTO") 10_000L else 18_000L,
                language = "te",
                description = item.description,
                sampleTopic = item.tags.joinToString(", ")
            )
            repository.insertAsset(asset)
            _userMessage.value = "Imported '${item.title}' to Asset Library"
        }
    }

    // --- Meme Integration & Overlays ---

    fun updateProjectMeme(
        memeId: String,
        title: String,
        topText: String,
        bottomText: String,
        imageUri: String? = null,
        position: String = "BOTTOM_RIGHT",
        startMs: Long = 0L,
        endMs: Long = 30_000L
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            activeMemeId = memeId,
            memeTitle = title,
            memeTopText = topText,
            memeBottomText = bottomText,
            memeImageUri = imageUri,
            memePosition = position,
            memeStartMs = startMs,
            memeEndMs = endMs.coerceAtMost(current.originalDurationMs),
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
        _userMessage.value = "Applied '$title' meme to video timeline!"
    }

    fun removeProjectMeme() {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            activeMemeId = null,
            memeTitle = null,
            memeTopText = null,
            memeBottomText = null,
            memeImageUri = null,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        persistProject(updated)
        _userMessage.value = "Removed meme overlay from project"
    }

    private fun persistProject(project: VideoProject) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }
}
