package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber
import com.example.util.FilterMatrixHelper

@Composable
fun VideoPreviewCanvas(
    project: VideoProject,
    currentPlayheadMs: Long,
    isPlaying: Boolean,
    playbackSpeed: Float,
    onTogglePlayback: () -> Unit,
    onSeek: (Long) -> Unit,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val aspectFloat = when (project.aspectRatio) {
        "16:9" -> 16f / 9f
        "9:16" -> 9f / 16f
        "1:1" -> 1f
        "4:5" -> 4f / 5f
        else -> 9f / 16f
    }

    val colorFilter = FilterMatrixHelper.getColorFilter(project.colorFilter, project.filterIntensity)

    // Animated waveform bars when playing
    val transition = rememberInfiniteTransition(label = "audio_bars")
    val barAnim1 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val barAnim2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val barAnim3 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b3"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_preview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
            listOf(Color(0xFF2C324B), Color(0xFF161926))
        ))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Viewport Box constrained to maintain aspect ratio without stretching
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF07090E)),
                contentAlignment = Alignment.Center
            ) {
                // Video aspect-ratio frame
                Box(
                    modifier = Modifier
                        .aspectRatio(aspectFloat)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E1B4B),
                                    Color(0xFF312E81),
                                    Color(0xFF1E293B)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFF3B4261), RoundedCornerShape(8.dp))
                        .drawWithContent {
                            if (colorFilter != null) {
                                drawIntoCanvas { canvas ->
                                    val paint = androidx.compose.ui.graphics.Paint().apply {
                                        this.colorFilter = colorFilter
                                    }
                                    canvas.nativeCanvas.saveLayer(null, paint.asFrameworkPaint())
                                    drawContent()
                                    canvas.nativeCanvas.restore()
                                }
                            } else {
                                drawContent()
                            }
                        }
                ) {
                    // Decorative video simulation backdrop
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x336366F1),
                                        Color(0x110F172A),
                                        Color(0x88000000)
                                    )
                                )
                            )
                    )

                    // Video content cues
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top info badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xCC000000)
                            ) {
                                Text(
                                    text = project.aspectRatio,
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (project.colorFilter != "NORMAL") {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xCC4338CA)
                                ) {
                                    Text(
                                        text = "${project.colorFilter} ${(project.filterIntensity * 100).toInt()}%",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xCC000000)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (project.isOriginalAudioMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = if (project.isOriginalAudioMuted) Color(0xFFEF4444) else SunsetAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "${(project.originalAudioVolume * 100).toInt()}%",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Center dynamic scene simulation / audio visualizer
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isPlaying) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier
                                            .width(4.dp)
                                            .height((16.dp * barAnim1).coerceAtLeast(4.dp))
                                            .background(NeonIndigo, RoundedCornerShape(2.dp))
                                    )
                                    Box(
                                        Modifier
                                            .width(4.dp)
                                            .height((24.dp * barAnim2).coerceAtLeast(4.dp))
                                            .background(NeonCyan, RoundedCornerShape(2.dp))
                                    )
                                    Box(
                                        Modifier
                                            .width(4.dp)
                                            .height((32.dp * barAnim3).coerceAtLeast(4.dp))
                                            .background(SunsetAmber, RoundedCornerShape(2.dp))
                                    )
                                    Box(
                                        Modifier
                                            .width(4.dp)
                                            .height((20.dp * barAnim1).coerceAtLeast(4.dp))
                                            .background(NeonIndigo, RoundedCornerShape(2.dp))
                                    )
                                    Box(
                                        Modifier
                                            .width(4.dp)
                                            .height((12.dp * barAnim2).coerceAtLeast(4.dp))
                                            .background(NeonCyan, RoundedCornerShape(2.dp))
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color(0x66FFFFFF),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        // Customizable Text Overlay rendered right on canvas!
                        if (project.overlayText.isNotBlank()) {
                            val textColor = try {
                                Color(android.graphics.Color.parseColor(project.overlayTextColorHex))
                            } catch (e: Exception) {
                                Color.White
                            }

                            val alignmentModifier = when (project.overlayTextPosition) {
                                "TOP" -> Alignment.TopCenter
                                "CENTER" -> Alignment.Center
                                else -> Alignment.BottomCenter
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = alignmentModifier
                            ) {
                                when (project.overlayTextBgStyle) {
                                    "PILL" -> {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xD9000000),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = project.overlayText,
                                                color = textColor,
                                                fontSize = (project.overlayTextSize * 0.7f).sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                    "BANNER" -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xE60F172A))
                                                .padding(vertical = 4.dp, horizontal = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = project.overlayText,
                                                color = textColor,
                                                fontSize = (project.overlayTextSize * 0.75f).sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                    "OUTLINE" -> {
                                        Text(
                                            text = project.overlayText,
                                            color = textColor,
                                            fontSize = (project.overlayTextSize * 0.75f).sp,
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .border(1.dp, textColor, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = project.overlayText,
                                            color = textColor,
                                            fontSize = (project.overlayTextSize * 0.75f).sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Playhead Timeline Scrubber
            val trimDuration = project.trimmedDurationMs.toFloat().coerceAtLeast(1000f)
            val currentOffset = (currentPlayheadMs - project.trimStartMs).coerceAtLeast(0L).toFloat()
            val progress = (currentOffset / trimDuration).coerceIn(0f, 1f)

            Slider(
                value = progress,
                onValueChange = { frac ->
                    val targetMs = project.trimStartMs + (frac * trimDuration).toLong()
                    onSeek(targetMs)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playhead_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = NeonIndigo,
                    activeTrackColor = NeonIndigo,
                    inactiveTrackColor = Color(0xFF2C324B)
                )
            )

            // Playback Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time position
                val currentSec = (currentPlayheadMs / 1000.0)
                val trimSec = (project.trimmedDurationMs / 1000.0)
                Text(
                    text = String.format("%.1fs / %.1fs", currentSec, trimSec),
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )

                // Main Play/Pause Button
                FilledIconButton(
                    onClick = onTogglePlayback,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("play_pause_button"),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = NeonIndigo,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Speed Selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val speeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)
                    val nextSpeed = speeds[(speeds.indexOf(playbackSpeed) + 1).takeIf { it < speeds.size } ?: 0]
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.clickable { onSpeedChange(nextSpeed) }
                    ) {
                        Text(
                            text = "${playbackSpeed}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
