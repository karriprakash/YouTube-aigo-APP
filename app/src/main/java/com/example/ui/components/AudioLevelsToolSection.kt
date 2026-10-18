package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@Composable
fun AudioLevelsToolSection(
    project: VideoProject,
    onUpdateAudio: (Float, Boolean, Float, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_levels_tool_card"),
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
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Audio Levels & Track Mixer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = if (project.isOriginalAudioMuted) "ORIGINAL MUTED" else "ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (project.isOriginalAudioMuted) Color(0xFFEF4444) else NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Track 1: Original Audio Track
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2433), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = SunsetAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = "Original Video Audio",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${(project.originalAudioVolume * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (project.isOriginalAudioMuted) Color(0xFF94A3B8) else SunsetAmber,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        FilledIconToggleButton(
                            checked = project.isOriginalAudioMuted,
                            onCheckedChange = { isMuted ->
                                onUpdateAudio(
                                    project.originalAudioVolume,
                                    isMuted,
                                    project.voiceoverVolume,
                                    project.voiceClarityEnhanced
                                )
                            },
                            modifier = Modifier.size(34.dp),
                            colors = IconButtonDefaults.filledIconToggleButtonColors(
                                containerColor = Color(0xFF2C324B),
                                checkedContainerColor = Color(0xFFEF4444)
                            )
                        ) {
                            Icon(
                                imageVector = if (project.isOriginalAudioMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "Mute",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Slider(
                    value = if (project.isOriginalAudioMuted) 0f else project.originalAudioVolume,
                    onValueChange = { vol ->
                        onUpdateAudio(
                            vol,
                            false,
                            project.voiceoverVolume,
                            project.voiceClarityEnhanced
                        )
                    },
                    valueRange = 0f..2.0f,
                    modifier = Modifier.testTag("original_volume_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = SunsetAmber,
                        activeTrackColor = SunsetAmber,
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )

                // Simulated dB Peak Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0% (Silence)", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text("100% (Normal)", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text("200% (Boost)", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Track 2: Background Music / Voiceover Track
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2433), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = NeonIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = "BGM / Telugu Voiceover Track",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "${(project.voiceoverVolume * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonIndigo
                    )
                }

                Slider(
                    value = project.voiceoverVolume,
                    onValueChange = { vol ->
                        onUpdateAudio(
                            project.originalAudioVolume,
                            project.isOriginalAudioMuted,
                            vol,
                            project.voiceClarityEnhanced
                        )
                    },
                    valueRange = 0f..2.0f,
                    modifier = Modifier.testTag("voiceover_volume_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = NeonIndigo,
                        activeTrackColor = NeonIndigo,
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mute", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text("Balanced BGM (100%)", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text("Vocal Priority (200%)", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }

            Spacer(Modifier.height(14.dp))

            // Voice Clarity & AI Noise Clean Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1F2C), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.size(10.dp))
                    Column {
                        Text(
                            text = "AI Voice Clarity & Denoise",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Enhances Telugu spoken dialog & removes wind/room hum",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Switch(
                    checked = project.voiceClarityEnhanced,
                    onCheckedChange = { enabled ->
                        onUpdateAudio(
                            project.originalAudioVolume,
                            project.isOriginalAudioMuted,
                            project.voiceoverVolume,
                            enabled
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonCyan
                    )
                )
            }
        }
    }
}
