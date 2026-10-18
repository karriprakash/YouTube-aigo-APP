package com.example.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserVideoMetadata
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.RadiantCoral
import com.example.ui.theme.SunsetAmber

@Composable
fun OfflineVideosSection(
    userVideos: List<UserVideoMetadata>,
    onDeleteVideo: (UserVideoMetadata) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var searchQuery by remember { mutableStateOf("") }
    var videoToDelete by remember { mutableStateOf<UserVideoMetadata?>(null) }

    val filteredVideos = remember(userVideos, searchQuery) {
        if (searchQuery.isBlank()) {
            userVideos
        } else {
            userVideos.filter {
                it.projectTitle.contains(searchQuery, ignoreCase = true) ||
                    it.localFilePath.contains(searchQuery, ignoreCase = true) ||
                    (it.tags?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    val totalBytes = remember(userVideos) {
        userVideos.sumOf { it.fileSizeBytes }
    }
    val totalStorageFormatted = remember(totalBytes) {
        val mb = totalBytes / (1024.0 * 1024.0)
        String.format(java.util.Locale.getDefault(), "%.1f MB", mb)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("offline_videos_section")
    ) {
        // Storage & Metadata Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("offline_storage_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131927)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222F49))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Offline Video Vault", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text("Stored in local Room Database", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x3310B981)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("100% OFFLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SAVED VIDEOS", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("${userVideos.size} files", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("LOCAL DISK USED", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text(totalStorageFormatted, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SunsetAmber)
                    }
                    Column {
                        Text("DATABASE ENTITY", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("Room SQLite", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonIndigo)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_offline_videos_input"),
            placeholder = { Text("Search by title, path, or tag...", fontSize = 13.sp, color = Color(0xFF64748B)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E2433),
                unfocusedContainerColor = Color(0xFF141926),
                focusedBorderColor = NeonIndigo,
                unfocusedBorderColor = Color(0xFF263048)
            )
        )

        Spacer(Modifier.height(12.dp))

        if (filteredVideos.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131926))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matching videos found" else "No Offline Videos Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "Try another search term" else "Export or finalize a video project in the Studio to store it in this offline Room database vault.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredVideos.forEach { video ->
                    UserVideoMetadataCard(
                        video = video,
                        onCopyPath = {
                            clipboardManager.setText(AnnotatedString(video.localFilePath))
                            Toast.makeText(context, "Path copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_SUBJECT, video.projectTitle)
                                putExtra(Intent.EXTRA_TEXT, "Video: ${video.projectTitle}\nPath: ${video.localFilePath}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Video Info"))
                        },
                        onDeleteClick = {
                            videoToDelete = video
                        }
                    )
                }
            }
        }
    }

    if (videoToDelete != null) {
        val target = videoToDelete!!
        AlertDialog(
            onDismissRequest = { videoToDelete = null },
            title = { Text("Delete Video Metadata?") },
            text = {
                Text(
                    "Are you sure you want to remove '${target.projectTitle}' from offline storage?\n\nLocal path:\n${target.localFilePath}",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteVideo(target)
                        videoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantCoral)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { videoToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserVideoMetadataCard(
    video: UserVideoMetadata,
    onCopyPath: () -> Unit,
    onShare: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_video_card_${video.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2C)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25304B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Title, and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonIndigo, Color(0xFFA855F7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = video.projectTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("offline_video_title_${video.id}")
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(11.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = video.formattedDate,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("share_video_btn_${video.id}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_video_btn_${video.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RadiantCoral.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Specs badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E263B)
                ) {
                    Text(
                        text = "⏱ ${video.formattedDuration}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SunsetAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E263B)
                ) {
                    Text(
                        text = "📐 ${video.aspectRatio} (${video.resolution})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E263B)
                ) {
                    Text(
                        text = "💾 ${video.formattedFileSize}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Local File Path Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F1420),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E263B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = video.localFilePath,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = onCopyPath,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("copy_path_btn_${video.id}")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Path", tint = NeonIndigo, modifier = Modifier.size(12.dp))
                    }
                }
            }

            if (!video.notes.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = video.notes,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
