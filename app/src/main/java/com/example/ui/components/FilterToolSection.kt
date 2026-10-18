package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber
import com.example.util.VideoFilterType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterToolSection(
    project: VideoProject,
    onUpdateFilter: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        Triple(VideoFilterType.NORMAL, listOf(Color(0xFF64748B), Color(0xFF475569)), "Original natural feed"),
        Triple(VideoFilterType.VIBRANT, listOf(Color(0xFFF59E0B), Color(0xFFEF4444)), "High saturation & warm glow"),
        Triple(VideoFilterType.CINEMATIC, listOf(Color(0xFF06B6D4), Color(0xFFF97316)), "Teal & orange movie look"),
        Triple(VideoFilterType.VINTAGE, listOf(Color(0xFFD97706), Color(0xFF78350F)), "Retro nostalgic sepia"),
        Triple(VideoFilterType.MONOCHROME, listOf(Color(0xFFE2E8F0), Color(0xFF334155)), "Classic high tonal B&W"),
        Triple(VideoFilterType.NOIR, listOf(Color(0xFF94A3B8), Color(0xFF0F172A)), "Deep dramatic contrast")
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("filter_tool_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = NeonIndigo,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Video Color Filters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "${project.colorFilter} ${(project.filterIntensity * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunsetAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Filter Presets Grid
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filters.forEach { (type, gradientColors, description) ->
                    val isSelected = project.colorFilter == type.name
                    Card(
                        modifier = Modifier
                            .weight(1f, fill = true)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onUpdateFilter(type.name, project.filterIntensity) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) NeonIndigo else Color(0xFF2C324B),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF23283E) else Color(0xFF161926)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Brush.linearGradient(gradientColors))
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = type.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Filter Intensity Slider (active when not Normal)
            if (project.colorFilter != "NORMAL") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter Intensity", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Text("${(project.filterIntensity * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SunsetAmber)
                }
                Slider(
                    value = project.filterIntensity,
                    onValueChange = { onUpdateFilter(project.colorFilter, it) },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = SunsetAmber,
                        activeTrackColor = SunsetAmber,
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )
            }
        }
    }
}
