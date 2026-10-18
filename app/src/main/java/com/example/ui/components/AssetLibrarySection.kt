package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.MediaAsset
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber

@Composable
fun AssetLibrarySection(
    assets: List<MediaAsset>,
    onCreateProjectFromAsset: (MediaAsset) -> Unit,
    onAddCustomAsset: (String, String, String, Long, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showRecordAudioDialog by remember { mutableStateOf(false) }

    // Modern Photo Picker for Videos (zero-permission compliant)
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "Imported_Raw_Video.mp4"
            onAddCustomAsset(
                fileName,
                "VIDEO",
                uri.toString(),
                30_000L,
                "te",
                "Imported raw video footage"
            )
        }
    }

    // Modern GetContent for Audio files
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "Imported_Audio_Track.mp3"
            onAddCustomAsset(
                fileName,
                "AUDIO",
                uri.toString(),
                45_000L,
                "te",
                "Imported background audio track"
            )
        }
    }

    val filteredAssets = when (selectedFilter) {
        "VIDEO" -> assets.filter { it.type == "VIDEO" }
        "AUDIO" -> assets.filter { it.type == "AUDIO" }
        "VOICEOVER" -> assets.filter { it.type == "VOICEOVER" }
        else -> assets
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("asset_library_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = SunsetAmber,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Creative Asset Library",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "${assets.size} ASSETS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Action Buttons to Import or Record
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("import_video_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp), tint = SunsetAmber)
                    Spacer(Modifier.width(6.dp))
                    Text("Add Video", fontSize = 12.sp, color = Color.White)
                }

                Button(
                    onClick = { audioPickerLauncher.launch("audio/*") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("import_audio_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Icon(Icons.Default.Audiotrack, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeonCyan)
                    Spacer(Modifier.width(6.dp))
                    Text("Add Audio", fontSize = 12.sp, color = Color.White)
                }

                Button(
                    onClick = { showRecordAudioDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("record_audio_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                    Spacer(Modifier.width(6.dp))
                    Text("Voiceover", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All",
                    "VIDEO" to "Videos",
                    "AUDIO" to "Music/BGM",
                    "VOICEOVER" to "Voiceovers"
                ).forEach { (fKey, fLabel) ->
                    FilterChip(
                        selected = selectedFilter == fKey,
                        onClick = { selectedFilter = fKey },
                        label = { Text(fLabel, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Asset Cards List
            filteredAssets.forEach { asset ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (asset.type) {
                                            "VIDEO" -> Color(0xFF312E81)
                                            "VOICEOVER" -> Color(0xFF831843)
                                            else -> Color(0xFF065F46)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (asset.type) {
                                        "VIDEO" -> Icons.Default.VideoFile
                                        "VOICEOVER" -> Icons.Default.Mic
                                        else -> Icons.Default.MusicNote
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = asset.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = asset.formattedDuration,
                                        fontSize = 11.sp,
                                        color = SunsetAmber
                                    )
                                    Text("•", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = if (asset.language == "te") "Telugu (తెలుగు)" else "English",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { onCreateProjectFromAsset(asset) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp), tint = NeonIndigo)
                            Spacer(Modifier.width(4.dp))
                            Text("Craft Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Voiceover Narration Quick Recorder Dialog
    if (showRecordAudioDialog) {
        var recordingTitle by remember { mutableStateOf("Telugu_Narration_${System.currentTimeMillis() % 1000}") }
        var selectedLang by remember { mutableStateOf("te") }
        var topicDescription by remember { mutableStateOf("Spoken travel commentary in Telugu") }

        AlertDialog(
            onDismissRequest = { showRecordAudioDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.padding(end = 8.dp))
                    Text("Add Spoken Voiceover Audio")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Record or register raw voice narration in Telugu or English for multi-format video generation.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = recordingTitle,
                        onValueChange = { recordingTitle = it },
                        label = { Text("Audio Track Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = topicDescription,
                        onValueChange = { topicDescription = it },
                        label = { Text("Spoken Topic / Dialog Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedLang == "te",
                            onClick = { selectedLang = "te" },
                            label = { Text("Telugu (తెలుగు)") }
                        )
                        FilterChip(
                            selected = selectedLang == "en",
                            onClick = { selectedLang = "en" },
                            label = { Text("English") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddCustomAsset(
                            "$recordingTitle.m4a",
                            "VOICEOVER",
                            "asset://$recordingTitle.m4a",
                            25_000L,
                            selectedLang,
                            topicDescription
                        )
                        showRecordAudioDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo)
                ) {
                    Text("Save to Assets")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecordAudioDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
