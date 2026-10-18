package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.util.SocialPlatform
import com.example.util.SocialShareHelper

@Composable
fun ExportShareSection(
    project: VideoProject,
    onMarkExported: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("export_share_card"),
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
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = NeonIndigo,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Publish & Social Media Sharing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (project.isExported) {
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
                            Text("READY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Video Specs Quick Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E2433),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TARGET FORMAT", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text(project.aspectRatio, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                    Column {
                        Text("DURATION", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text(project.formattedDuration, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SunsetAmber)
                    }
                    Column {
                        Text("FILTER", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text(project.colorFilter, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("OFFLINE DRAFT", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                        Text("SAVED", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Direct Social Media Share Cards
            Text(
                text = "DIRECT SOCIAL MEDIA PUBLISHING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
            Spacer(Modifier.height(8.dp))

            // 1. YouTube Direct Share Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        SocialShareHelper.shareToSocialPlatform(context, SocialPlatform.YOUTUBE, project)
                    }
                    .testTag("share_youtube_btn"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1414)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FF0000))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = YouTubeRed
                        ) {
                            Text(
                                text = "▶ YouTube",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Share to YouTube", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("16:9 Landscape / Shorts • SEO Title & Chapters prefilled", fontSize = 11.sp, color = Color(0xFFFCA5A5))
                        }
                    }
                    Icon(Icons.Default.IosShare, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // 2. Instagram Direct Share Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        SocialShareHelper.shareToSocialPlatform(context, SocialPlatform.INSTAGRAM, project)
                    }
                    .testTag("share_instagram_btn"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF281122)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66E1306C))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
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
                                        listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFF77737))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("IG", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Share to Instagram", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("9:16 Reels / 1:1 Feed • Captions & Hashtags pre-copied", fontSize = 11.sp, color = Color(0xFFF472B6))
                        }
                    }
                    Icon(Icons.Default.IosShare, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // 3. TikTok Direct Share Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        SocialShareHelper.shareToSocialPlatform(context, SocialPlatform.TIKTOK, project)
                    }
                    .testTag("share_tiktok_btn"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A24)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6600F2FE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF000000)
                        ) {
                            Text(
                                text = "♫ TikTok",
                                color = TikTokCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Share to TikTok", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("9:16 Vertical • 3-sec Viral Hook & keywords pre-copied", fontSize = 11.sp, color = Color(0xFF67E8F9))
                        }
                    }
                    Icon(Icons.Default.IosShare, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            // 4. Universal Share Sheet
            OutlinedButton(
                onClick = {
                    SocialShareHelper.shareToSocialPlatform(context, SocialPlatform.UNIVERSAL, project)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("share_universal_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("More Apps (WhatsApp, Telegram, Drive...)")
            }

            Spacer(Modifier.height(14.dp))

            // One-tap quick copy action buttons
            Text(
                text = "COPY GENERATED CONTENT TO CLIPBOARD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val text = project.aiSummaryTe ?: project.aiSummaryEn ?: project.title
                        SocialShareHelper.copyToClipboard(context, "Summary", text)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Copy Summary", fontSize = 11.sp, maxLines = 1)
                }

                OutlinedButton(
                    onClick = {
                        val text = project.instagramCaption ?: project.title
                        SocialShareHelper.copyToClipboard(context, "Caption", text)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Copy Caption", fontSize = 11.sp, maxLines = 1)
                }

                OutlinedButton(
                    onClick = {
                        val text = project.instagramHashtags ?: "#TeluguReels #Trending"
                        SocialShareHelper.copyToClipboard(context, "Hashtags", text)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Copy Tags", fontSize = 11.sp, maxLines = 1)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Mark Exported Finalize Button
            Button(
                onClick = onMarkExported,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("mark_exported_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.DownloadDone, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Finalize & Save Video Package", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
