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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoProject
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SunsetAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextOverlayToolSection(
    project: VideoProject,
    onUpdateOverlay: (String, String, String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember(project.overlayText) { mutableStateOf(project.overlayText) }

    val colors = listOf(
        "#FFFFFF" to "White",
        "#F59E0B" to "Amber",
        "#06B6D4" to "Cyan",
        "#F43F5E" to "Pink",
        "#10B981" to "Emerald",
        "#E2E8F0" to "Silver",
        "#0F172A" to "Black"
    )

    val positions = listOf(
        "TOP" to "Top Banner",
        "CENTER" to "Center Focus",
        "BOTTOM" to "Bottom Subtitle"
    )

    val styles = listOf(
        "PILL" to "Rounded Pill",
        "BANNER" to "Full Banner",
        "OUTLINE" to "Outline Box",
        "NONE" to "Clean Text"
    )

    val quickHooks = listOf(
        "🔥 చివరి వరకు చూడండి!",
        "✨ సీక్రెట్ రెసిపీ!",
        "⚡ 2026 Best Feature!",
        "💡 Don't Miss This Tip",
        "❤️ Watch Till 00:07!"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("text_overlay_tool_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = null,
                    tint = SunsetAmber,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Customizable Text Overlay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            // Main Text Field
            OutlinedTextField(
                value = textInput,
                onValueChange = {
                    textInput = it
                    onUpdateOverlay(
                        it,
                        project.overlayTextColorHex,
                        project.overlayTextBgStyle,
                        project.overlayTextPosition,
                        project.overlayTextSize
                    )
                },
                label = { Text("Overlay Title / Caption (Telugu or English)") },
                placeholder = { Text("e.g. 🔥 హైదరాబాదీ దమ్ బిర్యానీ సీక్రెట్!") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("overlay_text_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonIndigo,
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                maxLines = 2
            )

            Spacer(Modifier.height(10.dp))

            // Quick Hook suggestions chips
            Text("Quick Hook Templates (Telugu & English):", fontSize = 12.sp, color = Color(0xFF94A3B8))
            Spacer(Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickHooks.forEach { hook ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.clickable {
                            textInput = hook
                            onUpdateOverlay(
                                hook,
                                project.overlayTextColorHex,
                                project.overlayTextBgStyle,
                                project.overlayTextPosition,
                                project.overlayTextSize
                            )
                        }
                    ) {
                        Text(
                            text = hook,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Position & Background Style
            Text("Position On Screen:", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                positions.forEach { (posKey, posLabel) ->
                    FilterChip(
                        selected = project.overlayTextPosition == posKey,
                        onClick = {
                            onUpdateOverlay(
                                textInput,
                                project.overlayTextColorHex,
                                project.overlayTextBgStyle,
                                posKey,
                                project.overlayTextSize
                            )
                        },
                        label = { Text(posLabel, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text("Background Badge Style:", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                styles.forEach { (styleKey, styleLabel) ->
                    FilterChip(
                        selected = project.overlayTextBgStyle == styleKey,
                        onClick = {
                            onUpdateOverlay(
                                textInput,
                                project.overlayTextColorHex,
                                styleKey,
                                project.overlayTextPosition,
                                project.overlayTextSize
                            )
                        },
                        label = { Text(styleLabel, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Color Palette Row
            Text("Text Color:", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEach { (hex, name) ->
                    val c = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = project.overlayTextColorHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) SunsetAmber else Color(0xFF475569),
                                shape = CircleShape
                            )
                            .clickable {
                                onUpdateOverlay(
                                    textInput,
                                    hex,
                                    project.overlayTextBgStyle,
                                    project.overlayTextPosition,
                                    project.overlayTextSize
                                )
                            }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Font Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Font Size: ${project.overlayTextSize}sp", fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
            Slider(
                value = project.overlayTextSize.toFloat(),
                onValueChange = {
                    onUpdateOverlay(
                        textInput,
                        project.overlayTextColorHex,
                        project.overlayTextBgStyle,
                        project.overlayTextPosition,
                        it.toInt()
                    )
                },
                valueRange = 14f..36f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = SunsetAmber,
                    activeTrackColor = SunsetAmber
                )
            )
        }
    }
}
