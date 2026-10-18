package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.components.AiSuiteSection
import com.example.ui.components.AudioLevelsToolSection
import com.example.ui.components.ExportShareSection
import com.example.ui.components.FilterToolSection
import com.example.ui.components.FormatToolSection
import com.example.ui.components.MediaSourcingSection
import com.example.ui.components.MemeStudioSection
import com.example.ui.components.TextOverlayToolSection
import com.example.ui.components.TrimToolSection
import com.example.ui.components.VideoPreviewCanvas
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber
import com.example.viewmodel.EditorTab
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    viewModel: MainViewModel,
    project: VideoProject,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val currentTab by viewModel.currentTab.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPlayheadMs by viewModel.currentPlayheadMs.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val aiState by viewModel.aiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = project.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${project.aspectRatio} • ${project.formattedDuration} • ${if (project.language == "te") "Telugu" else "English"}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("editor_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "OFFLINE DRAFT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Live Video Canvas & Realtime Renderer
            VideoPreviewCanvas(
                project = project,
                currentPlayheadMs = currentPlayheadMs,
                isPlaying = isPlaying,
                playbackSpeed = playbackSpeed,
                onTogglePlayback = { viewModel.togglePlayback() },
                onSeek = { viewModel.seekTo(it) },
                onSpeedChange = { viewModel.setPlaybackSpeed(it) }
            )

            Spacer(Modifier.height(14.dp))

            // Tool Tabs Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditorTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val tabIcon = when (tab) {
                        EditorTab.TRIM -> Icons.Default.ContentCut
                        EditorTab.TEXT -> Icons.Default.Title
                        EditorTab.MEMES -> Icons.Default.EmojiEmotions
                        EditorTab.MEDIA -> Icons.Default.PhotoLibrary
                        EditorTab.AUDIO -> Icons.Default.VolumeUp
                        EditorTab.FILTERS -> Icons.Default.ColorLens
                        EditorTab.FORMAT -> Icons.Default.AspectRatio
                        EditorTab.AI_SUITE -> Icons.Default.AutoAwesome
                        EditorTab.SHARE -> Icons.Default.Share
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        label = { Text(tab.title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = {
                            Icon(
                                imageVector = tabIcon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (tab == EditorTab.AI_SUITE) Color(0xFF4F46E5) else NeonIndigo,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Active Tool Section
            when (currentTab) {
                EditorTab.TRIM -> {
                    TrimToolSection(
                        project = project,
                        currentPlayheadMs = currentPlayheadMs,
                        onUpdateTrim = { start, end -> viewModel.updateTrim(start, end) },
                        onSetStartToPlayhead = { viewModel.setTrimStartToPlayhead() },
                        onSetEndToPlayhead = { viewModel.setTrimEndToPlayhead() }
                    )
                }
                EditorTab.TEXT -> {
                    TextOverlayToolSection(
                        project = project,
                        onUpdateOverlay = { text, color, bg, pos, size ->
                            viewModel.updateTextOverlay(text, color, bg, pos, size)
                        }
                    )
                }
                EditorTab.MEMES -> {
                    MemeStudioSection(
                        project = project,
                        currentPlayheadMs = currentPlayheadMs,
                        onApplyMeme = { memeId, title, top, bottom, img, pos, start, end ->
                            viewModel.updateProjectMeme(memeId, title, top, bottom, img, pos, start, end)
                        },
                        onRemoveMeme = { viewModel.removeProjectMeme() }
                    )
                }
                EditorTab.MEDIA -> {
                    MediaSourcingSection(
                        project = project,
                        onSetBackgroundMedia = { uri, type, source ->
                            viewModel.updateBackgroundMedia(uri, type, source)
                        },
                        onImportAsset = { item ->
                            viewModel.importMediaSourceAsAsset(item)
                        }
                    )
                }
                EditorTab.AUDIO -> {
                    AudioLevelsToolSection(
                        project = project,
                        onUpdateAudio = { origVol, isMuted, voiceVol, clarity ->
                            viewModel.updateAudioLevels(origVol, isMuted, voiceVol, clarity)
                        }
                    )
                }
                EditorTab.FILTERS -> {
                    FilterToolSection(
                        project = project,
                        onUpdateFilter = { filter, intensity ->
                            viewModel.updateColorFilter(filter, intensity)
                        }
                    )
                }
                EditorTab.FORMAT -> {
                    FormatToolSection(
                        project = project,
                        onUpdateAspectRatio = { viewModel.updateAspectRatio(it) }
                    )
                }
                EditorTab.AI_SUITE -> {
                    AiSuiteSection(
                        project = project,
                        aiState = aiState,
                        onGenerate = { notes -> viewModel.generateAiSocialKit(notes) },
                        onUpdateNotes = { viewModel.updateProjectNotes(it) },
                        onUpdateLanguage = { viewModel.updateLanguage(it) }
                    )
                }
                EditorTab.SHARE -> {
                    ExportShareSection(
                        project = project,
                        onMarkExported = { viewModel.markProjectExported() }
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}
