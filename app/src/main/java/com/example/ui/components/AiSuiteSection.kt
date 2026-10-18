package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.InstagramGradientPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.YouTubeRed
import com.example.util.SocialShareHelper
import com.example.viewmodel.AiUiState

@Composable
fun AiSuiteSection(
    project: VideoProject,
    aiState: AiUiState,
    onGenerate: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onUpdateLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var notesInput by remember(project.rawTopicNotes) { mutableStateOf(project.rawTopicNotes) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_suite_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with AI Branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonIndigo, Color(0xFFA855F7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Google Gemini Media AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Summaries & Social Multi-Format Generator",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "OFFLINE SYNCED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Language Preference Selector
            Text("Input Spoken Audio / Video Language:", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "te" to "Telugu (తెలుగు)",
                    "en" to "English",
                    "auto" to "Bilingual / Auto"
                ).forEach { (langKey, langLabel) ->
                    FilterChip(
                        selected = project.language == langKey,
                        onClick = { onUpdateLanguage(langKey) },
                        label = { Text(langLabel, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Raw Audio/Video Context Notes Input
            OutlinedTextField(
                value = notesInput,
                onValueChange = {
                    notesInput = it
                    onUpdateNotes(it)
                },
                label = { Text("Raw Video Context / Spoken Dialog Notes") },
                placeholder = { Text("Describe spoken Telugu/English audio, key visuals, or paste transcript...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_notes_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonIndigo,
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                minLines = 2,
                maxLines = 4
            )

            Spacer(Modifier.height(12.dp))

            // Action Button
            Button(
                onClick = { onGenerate(notesInput) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_ai_btn"),
                enabled = aiState !is AiUiState.Loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonIndigo,
                    contentColor = Color.White
                )
            ) {
                if (aiState is AiUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Analyzing with Gemini 3.8 Flash...", fontSize = 14.sp)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (project.aiSummaryEn == null) "Generate Summary & Social Kit" else "Regenerate Content with Gemini",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Error Display
            if (aiState is AiUiState.Error) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x33EF4444),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = aiState.message,
                            color = Color(0xFFFCA5A5),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onGenerate(notesInput) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Display Generated Results (or saved offline content)
            if (project.aiSummaryEn != null || project.aiSummaryTe != null) {
                Spacer(Modifier.height(16.dp))

                // Card 1: Content Summary (Bilingual)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📝 Video Content Summary",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunsetAmber
                            )
                            IconButton(
                                onClick = {
                                    val fullSummary = "${project.aiSummaryTe ?: ""}\n\n${project.aiSummaryEn ?: ""}"
                                    SocialShareHelper.copyToClipboard(context, "Video Summary", fullSummary)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }

                        if (!project.aiSummaryTe.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF161B26),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "ముఖ్యాంశాలు (Telugu Highlights):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = project.aiSummaryTe,
                                        fontSize = 13.sp,
                                        color = Color.White,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }

                        if (!project.aiSummaryEn.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF161B26),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Key Takeaways (English):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF93C5FD)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = project.aiSummaryEn,
                                        fontSize = 13.sp,
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Card 2: YouTube Social Package
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = YouTubeRed
                                ) {
                                    Text("YouTube", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("Title & Chapter Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            IconButton(
                                onClick = {
                                    SocialShareHelper.copyToClipboard(context, "YouTube Metadata", "${project.youtubeTitle}\n\n${project.youtubeDescription}\n\nChapters:\n${project.youtubeChapters}")
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = project.youtubeTitle ?: project.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFDE047)
                        )

                        if (!project.youtubeChapters.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = project.youtubeChapters,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Card 3: Instagram & TikTok Hooks
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = InstagramGradientPink
                                ) {
                                    Text("Reels & TikTok", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("Viral Hooks & Hashtags", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            IconButton(
                                onClick = {
                                    SocialShareHelper.copyToClipboard(context, "Instagram Caption", "${project.instagramCaption}\n\n${project.instagramHashtags}")
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }

                        if (!project.tiktokHook.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "⚡ Hook: \"${project.tiktokHook}\"",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TikTokCyan
                            )
                        }

                        if (!project.instagramCaption.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = project.instagramCaption,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        if (!project.instagramHashtags.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = project.instagramHashtags,
                                fontSize = 11.sp,
                                color = Color(0xFF60A5FA)
                            )
                        }
                    }
                }
            }
        }
    }
}
