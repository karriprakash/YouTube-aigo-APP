package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiVideoAiService"

data class GeneratedSocialMediaKit(
    val summaryEn: String,
    val summaryTe: String,
    val youtubeTitle: String,
    val youtubeDescription: String,
    val youtubeChapters: String,
    val instagramCaption: String,
    val instagramHashtags: String,
    val tiktokHook: String,
    val tiktokCaptions: String,
    val brollSuggestions: String,
    val overlayTextSuggestion: String
)

class GeminiVideoAiService {

    // Define strict JSON schema using Schema.obj as mandated by Firebase AI Logic SDK
    private val socialKitSchema = Schema.obj(
        properties = mapOf(
            "summaryEn" to Schema.string(description = "Concise bullet-point summary of key highlights in English"),
            "summaryTe" to Schema.string(description = "Concise bullet-point summary and highlights in Telugu script (తెలుగులో ముఖ్యాంశాలు)"),
            "youtubeTitle" to Schema.string(description = "Catchy SEO-optimized YouTube title in bilingual format"),
            "youtubeDescription" to Schema.string(description = "Complete YouTube description with chapters and social links"),
            "youtubeChapters" to Schema.string(description = "Video timeline chapters with timestamps (e.g. 00:00 Intro...)"),
            "instagramCaption" to Schema.string(description = "High retention Instagram Reel/Post caption with hook and CTA"),
            "instagramHashtags" to Schema.string(description = "10 to 15 relevant trending hashtags for Instagram"),
            "tiktokHook" to Schema.string(description = "First 3-second viral spoken or on-screen hook line for TikTok"),
            "tiktokCaptions" to Schema.string(description = "Punchy short TikTok caption with viral sound/search keywords"),
            "brollSuggestions" to Schema.string(description = "Suggested camera angles, visual b-rolls and cut timings"),
            "overlayTextSuggestion" to Schema.string(description = "Punchy on-screen title text overlay for the video")
        )
    )

    private val jsonConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = socialKitSchema
    }

    // Lazy initialization so Firebase.ai is evaluated upon first call
    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = jsonConfig
        )
    }

    suspend fun generateMultiFormatContent(
        projectTitle: String,
        rawNotesOrTranscript: String,
        languagePreference: String, // "te", "en", "auto"
        aspectRatio: String,
        durationSeconds: Int
    ): Result<GeneratedSocialMediaKit> = withContext(Dispatchers.IO) {
        val prompt = buildString {
            appendLine("You are an expert viral video producer, multilingual content strategist, and social media director specializing in Telugu and English digital content for YouTube, Instagram, and TikTok.")
            appendLine()
            appendLine("VIDEO PROJECT DETAILS:")
            appendLine("- Working Title: $projectTitle")
            appendLine("- Raw Audio/Video Content Notes or Spoken Transcript: $rawNotesOrTranscript")
            appendLine("- Target Duration: ${durationSeconds}s")
            appendLine("- Primary Format: $aspectRatio")
            appendLine("- Preferred Spoken Language: ${if (languagePreference == "te") "Telugu (తెలుగు)" else "English / Bilingual"}")
            appendLine()
            appendLine("YOUR MISSION:")
            appendLine("1. Extract the core essence and highlights of the video. Create a clear, readable summary capturing main points in English (summaryEn) AND in authentic Telugu script (summaryTe).")
            appendLine("2. Generate tailored social media packages:")
            appendLine("   - YouTube (16:9 Long-form or Shorts): Click-worthy bilingual title, detailed description with timestamps/chapters.")
            appendLine("   - Instagram (9:16 Reels / 1:1 Feed): High-retention opening hook, emotional connect, call-to-action (Comment/Share), and 12+ hashtags.")
            appendLine("   - TikTok (9:16): Ultra-punchy 3-second hook, trending search tags.")
            appendLine("   - B-roll & Pacing Tips: Suggestions on when to cut, zoom, or insert b-roll over the audio.")
            appendLine("   - Suggested on-screen Overlay Text: High-impact text overlay banner in 2-5 words.")
            appendLine()
            appendLine("Ensure high quality, culturally relevant phrasing for Telugu audiences while staying engaging globally.")
        }

        try {
            val response = withTimeout(60_000L) {
                generativeModel.generateContent(prompt)
            }

            val jsonString = response.text
                ?: throw IllegalStateException("Gemini model returned an empty response.")

            Log.d(TAG, "Gemini JSON Response: $jsonString")

            val json = JSONObject(jsonString)
            val kit = GeneratedSocialMediaKit(
                summaryEn = json.optString("summaryEn", "Key highlights generated from video content."),
                summaryTe = json.optString("summaryTe", "వీడియో నుండి ముఖ్యమైన ముఖ్యాంశాలు."),
                youtubeTitle = json.optString("youtubeTitle", "$projectTitle | Best 4K Video"),
                youtubeDescription = json.optString("youtubeDescription", "Full video overview and highlights."),
                youtubeChapters = json.optString("youtubeChapters", "00:00 Intro\n00:05 Highlights\n00:15 Conclusion"),
                instagramCaption = json.optString("instagramCaption", "Watch till the end! 🔥 Comment your thoughts below."),
                instagramHashtags = json.optString("instagramHashtags", "#Video #Reels #Shorts #Trending #TeluguReels"),
                tiktokHook = json.optString("tiktokHook", "Wait till you see what happens here!"),
                tiktokCaptions = json.optString("tiktokCaptions", "Check this out! #fyp #viral #trending"),
                brollSuggestions = json.optString("brollSuggestions", "00:00 Wide establishing shot\n00:08 Zoom-in on subject\n00:18 Quick cuts to reaction"),
                overlayTextSuggestion = json.optString("overlayTextSuggestion", "🔥 Watch Till The End!")
            )
            Result.success(kit)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini AI generation timed out after 60s", e)
            Result.failure(Exception("Generation timed out. Please check your network and try again."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini AI generation failed", e)
            Result.failure(e)
        }
    }
}
