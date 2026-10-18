package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrimToolSection(
    project: VideoProject,
    currentPlayheadMs: Long,
    onUpdateTrim: (Long, Long) -> Unit,
    onSetStartToPlayhead: () -> Unit,
    onSetEndToPlayhead: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalMs = project.originalDurationMs.toFloat().coerceAtLeast(1000f)
    val startFrac = (project.trimStartMs.toFloat() / totalMs).coerceIn(0f, 0.99f)
    val endFrac = (project.trimEndMs.toFloat() / totalMs).coerceIn(startFrac + 0.01f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trim_tool_card"),
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
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = NeonIndigo,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Video Clip Trimmer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "Trimmed: ${String.format("%.1fs", project.trimmedDurationMs / 1000f)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Time stamps display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("START TRIM", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                    Text(
                        text = String.format("%.2fs", project.trimStartMs / 1000f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunsetAmber
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTAL DURATION", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text(
                        text = String.format("%.1fs", project.originalDurationMs / 1000f),
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("END TRIM", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                    Text(
                        text = String.format("%.2fs", project.trimEndMs / 1000f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonIndigo
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // RangeSlider for dual-thumb trimming
            RangeSlider(
                value = startFrac..endFrac,
                onValueChange = { range ->
                    val newStartMs = (range.start * totalMs).toLong()
                    val newEndMs = (range.endInclusive * totalMs).toLong()
                    onUpdateTrim(newStartMs, newEndMs)
                },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trim_range_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = SunsetAmber,
                    activeTrackColor = NeonIndigo,
                    inactiveTrackColor = Color(0xFF2C324B)
                )
            )

            Spacer(Modifier.height(12.dp))

            // Playhead synchronization buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSetStartToPlayhead,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("set_start_playhead_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Set Start at Playhead",
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onSetEndToPlayhead,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("set_end_playhead_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Set End at Playhead",
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Precision Nudge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = { onUpdateTrim((project.trimStartMs - 500L).coerceAtLeast(0L), project.trimEndMs) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("-0.5s", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onUpdateTrim((project.trimStartMs + 500L).coerceAtMost(project.trimEndMs - 1000L), project.trimEndMs) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+0.5s", fontSize = 11.sp)
                    }
                }

                OutlinedButton(
                    onClick = { onUpdateTrim(0L, project.originalDurationMs) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Reset Full", fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = { onUpdateTrim(project.trimStartMs, (project.trimEndMs - 500L).coerceAtLeast(project.trimStartMs + 1000L)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("-0.5s", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onUpdateTrim(project.trimStartMs, (project.trimEndMs + 500L).coerceAtMost(project.originalDurationMs)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("+0.5s", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
