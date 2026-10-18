package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CloudVideoProject(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val videoUri: String = "",
    val audioUri: String? = null,
    val originalDurationMs: Long = 30_000L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 30_000L,
    val aspectRatio: String = "9:16",
    val colorFilter: String = "NORMAL",
    val filterIntensity: Double = 0.8,
    val originalAudioVolume: Double = 1.0,
    val isOriginalAudioMuted: Boolean = false,
    val voiceoverVolume: Double = 1.0,
    val voiceClarityEnhanced: Boolean = true,
    val overlayText: String = "",
    val overlayTextColorHex: String = "#FFFFFF",
    val overlayTextBgStyle: String = "PILL",
    val overlayTextPosition: String = "BOTTOM",
    val overlayTextSize: Long = 22L,
    val language: String = "te",
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
    val isExported: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)
