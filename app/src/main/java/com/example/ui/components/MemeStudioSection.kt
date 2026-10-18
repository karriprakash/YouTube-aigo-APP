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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemeRepository
import com.example.data.model.MemeTemplate
import com.example.data.model.VideoProject
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemeStudioSection(
    project: VideoProject,
    currentPlayheadMs: Long,
    onApplyMeme: (
        memeId: String,
        title: String,
        topText: String,
        bottomText: String,
        imageUri: String?,
        position: String,
        startMs: Long,
        endMs: Long
    ) -> Unit,
    onRemoveMeme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTemplate by remember { mutableStateOf<MemeTemplate?>(MemeRepository.templates.firstOrNull { it.id == project.activeMemeId } ?: MemeRepository.templates.first()) }

    var topText by remember(selectedTemplate, project.activeMemeId) {
        mutableStateOf(project.memeTopText ?: selectedTemplate?.defaultTopText ?: "WHEN THE CODE RUNS")
    }
    var bottomText by remember(selectedTemplate, project.activeMemeId) {
        mutableStateOf(project.memeBottomText ?: selectedTemplate?.defaultBottomText ?: "ON FIRST ATTEMPT")
    }

    var customImageUri by remember { mutableStateOf<Uri?>(project.memeImageUri?.let { Uri.parse(it) }) }
    var memePosition by remember { mutableStateOf(project.memePosition) }
    var memeDurationSec by remember { mutableFloatStateOf(5f) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customImageUri = uri
            selectedTemplate = null
        }
    }

    val categories = listOf("All", "Trending", "Telugu Classics", "Reactions", "Gaming & Tech")
    val filteredTemplates = if (selectedCategory == "All") {
        MemeRepository.templates
    } else {
        MemeRepository.templates.filter { it.category == selectedCategory }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("meme_studio_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131722)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF23293D))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with status
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
                            .background(SunsetAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = null,
                            tint = SunsetAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Meme & Sticker Studio",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Add Viral Telugu & Global Reaction Memes to Video",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                if (project.activeMemeId != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF065F46)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Active on Video", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Live Meme Preview Card (Impact Style!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (project.activeMemeId != null) NeonCyan else Color(0xFF334155))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(selectedTemplate?.bgGradientHex?.first ?: 0xFF1E293B),
                                    Color(selectedTemplate?.bgGradientHex?.second ?: 0xFF0F172A)
                                )
                            )
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Emoji / Icon Illustration
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedTemplate?.emoji ?: "🖼️",
                            fontSize = 48.sp
                        )
                        if (selectedTemplate?.punchlineTe?.isNotBlank() == true) {
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x99000000)
                            ) {
                                Text(
                                    text = selectedTemplate?.punchlineTe ?: "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SunsetAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Meme Top Text
                    if (topText.isNotBlank()) {
                        Text(
                            text = topText.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .background(Color(0xB3000000), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    // Meme Bottom Text
                    if (bottomText.isNotBlank()) {
                        Text(
                            text = bottomText.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(Color(0xB3000000), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Action: Load Custom Meme from Photos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (customImageUri != null) "Custom Image Selected" else "Load Custom Meme",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (project.activeMemeId != null) {
                    Button(
                        onClick = onRemoveMeme,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Remove", fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Categories Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonIndigo,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Curated Templates Grid / Carousel
            Text("Select Template:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            Spacer(Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredTemplates) { template ->
                    val isCurrent = selectedTemplate?.id == template.id
                    Card(
                        modifier = Modifier
                            .width(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedTemplate = template
                                topText = template.defaultTopText
                                bottomText = template.defaultBottomText
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) Color(0xFF27354F) else Color(0xFF161C2C)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = if (isCurrent) NeonCyan else Color(0xFF2C354D)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(template.emoji, fontSize = 28.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = template.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = template.category,
                                fontSize = 9.sp,
                                color = SunsetAmber
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Text Inputs
            OutlinedTextField(
                value = topText,
                onValueChange = { topText = it },
                label = { Text("Meme Top Punchline") },
                placeholder = { Text("e.g. WHEN THE PRODUCTION RELEASE...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = bottomText,
                onValueChange = { bottomText = it },
                label = { Text("Meme Bottom Punchline") },
                placeholder = { Text("e.g. WORKS ON THE FIRST RUN!") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Placement Selector
            Text("Video Canvas Placement:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "BOTTOM_RIGHT" to "PiP (Bottom)",
                    "TOP_RIGHT" to "Top Right",
                    "CENTER" to "Center",
                    "FULL_OVERLAY" to "Full Cutaway"
                ).forEach { (pos, label) ->
                    FilterChip(
                        selected = memePosition == pos,
                        onClick = { memePosition = pos },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color(0xFF0F172A)
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Duration Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show for Duration:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                Text("${memeDurationSec.toInt()}s from Playhead (${currentPlayheadMs / 1000}s)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunsetAmber)
            }
            Slider(
                value = memeDurationSec,
                onValueChange = { memeDurationSec = it },
                valueRange = 2f..15f,
                steps = 12,
                colors = SliderDefaults.colors(thumbColor = SunsetAmber, activeTrackColor = SunsetAmber)
            )

            Spacer(Modifier.height(14.dp))

            // Apply Button
            Button(
                onClick = {
                    val memeId = selectedTemplate?.id ?: "custom_meme_${System.currentTimeMillis()}"
                    val title = selectedTemplate?.name ?: "Custom Meme"
                    val startMs = currentPlayheadMs
                    val endMs = currentPlayheadMs + (memeDurationSec * 1000L).toLong()
                    onApplyMeme(
                        memeId,
                        title,
                        topText,
                        bottomText,
                        customImageUri?.toString(),
                        memePosition,
                        startMs,
                        endMs
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_meme_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Apply Meme to Video Creation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
