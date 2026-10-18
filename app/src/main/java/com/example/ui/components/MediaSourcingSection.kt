package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaPlatformSource
import com.example.data.model.MediaSourceItem
import com.example.data.model.MediaSourcingRepository
import com.example.data.model.VideoProject
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber

@Composable
fun MediaSourcingSection(
    project: VideoProject,
    onSetBackgroundMedia: (uri: String, type: String, source: String) -> Unit,
    onImportAsset: (MediaSourceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Google Photos, 1: Google Search, 2: Social Media
    var searchQuery by remember { mutableStateOf("") }
    var selectedSocialPlatform by remember { mutableStateOf<MediaPlatformSource?>(null) }
    var socialUrlInput by remember { mutableStateOf("") }
    var userPickedMediaUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            userPickedMediaUri = uri
            onSetBackgroundMedia(uri.toString(), "PHOTO", "GOOGLE_PHOTOS")
        }
    }

    val searchResults = remember(searchQuery, selectedSocialPlatform) {
        MediaSourcingRepository.searchClips(searchQuery, selectedSocialPlatform)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("media_sourcing_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131722)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF23293D))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Media Sourcing Center",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Google Photos, Google Search & Social Media Apps",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                if (project.backgroundMediaUri != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "Active: ${project.backgroundMediaSource}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Channel Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F121C),
                contentColor = NeonCyan,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Google Photos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedSocialPlatform = MediaPlatformSource.GOOGLE_SEARCH
                    },
                    text = { Text("Google Search", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        selectedSocialPlatform = MediaPlatformSource.YOUTUBE_SHORTS
                    },
                    text = { Text("Social Apps", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // Google Photos Picker Tab
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
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = Color(0xFF60A5FA),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Select Photos & Video Clips from Google Photos",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Use Android Photo Picker to pick photos or video clips directly from your cloud and on-device photo library without broad permissions.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        androidx.activity.result.PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("google_photos_picker_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Open Google Photos Library", fontWeight = FontWeight.Bold)
                            }

                            if (userPickedMediaUri != null || (project.backgroundMediaSource == "GOOGLE_PHOTOS" && project.backgroundMediaUri != null)) {
                                Spacer(Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF065F46)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Photo successfully assigned as video background!", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Google Search Tab
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Photos & Video Clips") },
                            placeholder = { Text("e.g. Charminar, sunset, tech code, nature...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        // Category chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Charminar", "Araku", "Tech AI", "Studio", "Sunset", "All")) { query ->
                                val isSelected = if (query == "All") searchQuery.isBlank() else searchQuery.equals(query, ignoreCase = true)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { searchQuery = if (query == "All") "" else query },
                                    label = { Text(query, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Results
                        Text(
                            text = "Search Results (${searchResults.size}):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            searchResults.forEach { item ->
                                MediaSourceCard(
                                    item = item,
                                    onSetAsBackground = { onSetBackgroundMedia(item.sampleUri, item.mediaType, item.platform.name) },
                                    onImportAsset = { onImportAsset(item) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Integrated Social Media Apps Tab
                    Column {
                        Text(
                            text = "Filter by Social Media Platform:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(
                                listOf(
                                    MediaPlatformSource.YOUTUBE_SHORTS,
                                    MediaPlatformSource.INSTAGRAM_REELS,
                                    MediaPlatformSource.TIKTOK,
                                    MediaPlatformSource.TWITTER_X
                                )
                            ) { plat ->
                                val isSel = selectedSocialPlatform == plat
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedSocialPlatform = if (isSel) null else plat },
                                    label = { Text(plat.displayName, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(plat.badgeColor),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Import from Social URL Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Import Clip via Social Link:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = socialUrlInput,
                                        onValueChange = { socialUrlInput = it },
                                        placeholder = { Text("Paste YouTube Shorts / Reel URL...", fontSize = 11.sp) },
                                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (socialUrlInput.isNotBlank()) {
                                                onSetBackgroundMedia(socialUrlInput, "VIDEO_CLIP", "SOCIAL_MEDIA")
                                                socialUrlInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Import", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Trending Social Media Clips:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            searchResults.forEach { item ->
                                MediaSourceCard(
                                    item = item,
                                    onSetAsBackground = { onSetBackgroundMedia(item.sampleUri, item.mediaType, item.platform.name) },
                                    onImportAsset = { onImportAsset(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaSourceCard(
    item: MediaSourceItem,
    onSetAsBackground: () -> Unit,
    onImportAsset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(item.gradientColors.first), Color(item.gradientColors.second))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.iconEmoji, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(item.platform.badgeColor)
                            ) {
                                Text(
                                    text = item.platform.displayName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${item.mediaType} • ${item.durationLabel} • ${item.resolutionLabel}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                maxLines = 2
            )

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSetAsBackground,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Set as Background", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onImportAsset,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add to Assets", fontSize = 11.sp)
                }
            }
        }
    }
}
