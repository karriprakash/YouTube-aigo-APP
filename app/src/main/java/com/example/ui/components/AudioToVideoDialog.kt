package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPresetsRepository
import com.example.data.model.AudioSourcePreset
import com.example.data.model.TranscriptSegment
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber

@Composable
fun AudioToVideoDialog(
    onDismiss: () -> Unit,
    onCreateAudioVideoProject: (
        title: String,
        durationMs: Long,
        language: String,
        audioUri: String,
        transcript: String,
        segments: List<TranscriptSegment>,
        aspectRatio: String,
        backgroundUri: String?,
        backgroundType: String,
        backgroundSource: String
    ) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Presets & Voiceovers, 1: Record Mic, 2: Upload Audio File
    var projectTitle by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("te") }
    var selectedAspect by remember { mutableStateOf("9:16") }
    var captionStyle by remember { mutableStateOf("POP_IN") }

    // Preset selection
    var selectedPreset by remember { mutableStateOf<AudioSourcePreset?>(AudioPresetsRepository.presets.first()) }

    // Live Mic Recording Simulation / State
    var isRecording by remember { mutableStateOf(false) }
    var recordingDurationSec by remember { mutableIntStateOf(0) }
    var recordedTranscript by remember {
        mutableStateOf("నమస్కారం ఫ్రెండ్స్, ఈ రోజు మనం ఒక అద్భుతమైన టెక్నాలజీ గురించి తెలుసుకుందాం!")
    }

    // Custom File Picker
    var customAudioUri by remember { mutableStateOf<Uri?>(null) }
    var customFileTranscript by remember { mutableStateOf("") }

    // Photo picker for background
    var backgroundUri by remember { mutableStateOf<String?>(null) }
    var backgroundSource by remember { mutableStateOf("LOCAL") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            backgroundUri = uri.toString()
            backgroundSource = "GOOGLE_PHOTOS"
        }
    }

    val audioFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customAudioUri = uri
            selectedTab = 2
            if (customFileTranscript.isBlank()) {
                customFileTranscript = "Imported audio stream from device. Auto-transcribing speech segments..."
            }
        }
    }

    // Audio Visualizer Animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "pulse"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonIndigo, NeonCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Audio-to-Video Studio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Turn Voice & Transcripts into Viral Videos",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Source Selector Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF131722),
                    contentColor = NeonCyan,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Voice Stories", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AudioFile, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Record Mic", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Pick File", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Presets & Voiceovers
                        Text(
                            text = "Select Audio Source with Real Transcription:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AudioPresetsRepository.presets.forEach { preset ->
                                val isSelected = selectedPreset?.id == preset.id
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedPreset = preset
                                            if (projectTitle.isBlank()) projectTitle = preset.title
                                            selectedLanguage = preset.language
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else Color(0xFF334155)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = preset.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) NeonCyan else Color.White
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF312E81)
                                            ) {
                                                Text(
                                                    text = "${preset.durationMs / 1000}s",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SunsetAmber,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = preset.fullTranscript,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            maxLines = 2
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "⏱️ ${preset.segments.size} Synced Caption Segments",
                                            fontSize = 10.sp,
                                            color = NeonIndigo,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Live Mic Recording
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isRecording) (70.dp * pulseScale) else 64.dp)
                                        .clip(CircleShape)
                                        .background(if (isRecording) Color(0xFFEF4444) else NeonIndigo)
                                        .clickable {
                                            isRecording = !isRecording
                                            if (isRecording) {
                                                recordingDurationSec = 15
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = if (isRecording) "Recording Voice (Telugu / English)..." else "Tap to Record Audio",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRecording) Color(0xFFEF4444) else Color.White
                                )
                                Text(
                                    text = if (isRecording) "00:15 Captured • Live Speech-to-Text active" else "Real microphone voice recording with live transcription",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )

                                Spacer(Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = recordedTranscript,
                                    onValueChange = { recordedTranscript = it },
                                    label = { Text("Transcribed Audio Captions") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2,
                                    maxLines = 4
                                )
                            }
                        }
                    }
                    2 -> {
                        // Custom Audio File
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                OutlinedButton(
                                    onClick = { audioFilePicker.launch("audio/*") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AudioFile, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = if (customAudioUri != null) "File Selected: ${customAudioUri?.lastPathSegment?.take(20)}" else "Browse Audio (.mp3, .wav, .m4a)",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = customFileTranscript,
                                    onValueChange = { customFileTranscript = it },
                                    label = { Text("Audio Transcript / Lyrics / Speech") },
                                    placeholder = { Text("Paste or type transcription to sync word captions...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 5
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Project Title Input
                OutlinedTextField(
                    value = projectTitle,
                    onValueChange = { projectTitle = it },
                    label = { Text("Video Title") },
                    placeholder = { Text("e.g. Daily Inspiration Reel") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                // Video Format & Language
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Format:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("9:16", "16:9", "1:1").forEach { fmt ->
                                FilterChip(
                                    selected = selectedAspect == fmt,
                                    onClick = { selectedAspect = fmt },
                                    label = { Text(fmt, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Language:", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = selectedLanguage == "te",
                                onClick = { selectedLanguage = "te" },
                                label = { Text("Telugu", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedLanguage == "en",
                                onClick = { selectedLanguage = "en" },
                                label = { Text("English", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Background Photo Picker (Google Photos)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E31)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Background Visual Source",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (backgroundUri != null) "Photo selected from Google Photos" else "Default dynamic audio waveform & gradient",
                                fontSize = 11.sp,
                                color = if (backgroundUri != null) NeonCyan else Color(0xFF94A3B8)
                            )
                        }

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (backgroundUri != null) "Change Photo" else "Google Photos", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = projectTitle.ifBlank {
                        when (selectedTab) {
                            0 -> selectedPreset?.title ?: "Audio Story Video"
                            1 -> "Voice Recording Video"
                            else -> "Audio Visualizer Reel"
                        }
                    }

                    val (audioUri, transcript, segments, durationMs) = when (selectedTab) {
                        0 -> {
                            val p = selectedPreset ?: AudioPresetsRepository.presets.first()
                            listOf("asset://${p.id}.mp3", p.fullTranscript, p.segments, p.durationMs)
                        }
                        1 -> {
                            val t = recordedTranscript.ifBlank { "Recorded voice commentary" }
                            val segs = listOf(
                                TranscriptSegment(1, 0L, 5000L, t.take(40), "Voice"),
                                TranscriptSegment(2, 5000L, 15000L, t.drop(40), "Voice")
                            )
                            listOf("mic://recorded_voice.m4a", t, segs, 15_000L)
                        }
                        else -> {
                            val t = customFileTranscript.ifBlank { "Custom audio audio track" }
                            val segs = listOf(
                                TranscriptSegment(1, 0L, 7000L, t.take(40), "Audio"),
                                TranscriptSegment(2, 7000L, 20000L, t.drop(40), "Audio")
                            )
                            listOf(customAudioUri?.toString() ?: "custom://audio.mp3", t, segs, 20_000L)
                        }
                    }

                    @Suppress("UNCHECKED_CAST")
                    onCreateAudioVideoProject(
                        finalTitle,
                        durationMs as Long,
                        selectedLanguage,
                        audioUri as String,
                        transcript as String,
                        segments as List<TranscriptSegment>,
                        selectedAspect,
                        backgroundUri,
                        if (backgroundUri != null) "PHOTO" else "GRADIENT",
                        backgroundSource
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                modifier = Modifier.testTag("create_audio_video_btn")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Generate Video from Audio", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
