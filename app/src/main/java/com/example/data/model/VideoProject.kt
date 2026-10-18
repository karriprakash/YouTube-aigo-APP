package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val videoUri: String,
    val audioUri: String? = null,
    val originalDurationMs: Long = 30_000L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 30_000L,
    val aspectRatio: String = "9:16", // "16:9", "9:16", "1:1", "4:5"
    val colorFilter: String = "NORMAL", // "NORMAL", "VIBRANT", "CINEMATIC", "VINTAGE", "MONOCHROME", "NOIR"
    val filterIntensity: Float = 0.8f,
    val originalAudioVolume: Float = 1.0f,
    val isOriginalAudioMuted: Boolean = false,
    val voiceoverVolume: Float = 1.0f,
    val voiceClarityEnhanced: Boolean = true,
    val overlayText: String = "",
    val overlayTextColorHex: String = "#FFFFFF",
    val overlayTextBgStyle: String = "PILL", // "PILL", "BANNER", "OUTLINE", "NONE"
    val overlayTextPosition: String = "BOTTOM", // "TOP", "CENTER", "BOTTOM"
    val overlayTextSize: Int = 22,
    val language: String = "te", // "te" (Telugu), "en" (English), "auto"
    val rawTopicNotes: String = "",
    val aiSummaryEn: String? = null,
    val aiSummaryTe: String? = null,
    val youtubeTitle: String? = null,
    val youtubeDescription: String? = null,
    val youtubeChapters: String? = null,
    val instagramCaption: String? = null,
    val instagramHashtags: String? = null,
    val tiktokHook: String? = null,
    val tiktokCaptions: String? = null,
    val brollSuggestions: String? = null,
    val isExported: Boolean = false
) {
    val trimmedDurationMs: Long
        get() = (trimEndMs - trimStartMs).coerceAtLeast(1000L)

    val formattedDuration: String
        get() {
            val totalSeconds = (trimmedDurationMs / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}
