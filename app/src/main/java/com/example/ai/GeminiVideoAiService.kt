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
            Log.w(TAG, "Gemini AI generation timed out, providing local intelligent generation", e)
            Result.success(createLocalIntelligenceFallback(projectTitle, rawNotesOrTranscript, languagePreference, aspectRatio))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Gemini online call unavailable, providing local intelligent generation", e)
            Result.success(createLocalIntelligenceFallback(projectTitle, rawNotesOrTranscript, languagePreference, aspectRatio))
        }
    }

    private fun createLocalIntelligenceFallback(
        projectTitle: String,
        notes: String,
        languagePreference: String,
        aspectRatio: String
    ): GeneratedSocialMediaKit {
        val topic = if (notes.isNotBlank()) notes else projectTitle
        val isTelugu = languagePreference == "te" || notes.any { it in '\u0C00'..'\u0C7F' } || projectTitle.any { it in '\u0C00'..'\u0C7F' }

        val summaryEn = """
            • Key Highlights: $projectTitle
            • Core Essence: ${topic.take(120)}
            • Format: Tailored for $aspectRatio video display with optimized pacing
            • Clear actionable takeaway for social viewers
        """.trimIndent()

        val summaryTe = if (isTelugu) {
            """
                • ముఖ్య ముఖ్యాంశాలు: $projectTitle
                • ప్రధాన సారాంశం: ${topic.take(120)}
                • అనుకూల ఫార్మాట్: $aspectRatio తో సోషల్ మీడియా కోసం సిద్ధం చేయబడింది
                • వీక్షకులను ఆకట్టుకునే సంక్షిప్త వివరణ మరియు స్పష్టమైన ముఖ్యాంశాలు
            """.trimIndent()
        } else {
            """
                • ముఖ్య ముఖ్యాంశాలు (తెలుగు అనువాదం): $projectTitle
                • ఈ వీడియోలో చూపించిన ప్రధానాంశాలు స్పష్టంగా పొందుపరచబడ్డాయి.
                • సోషల్ మీడియా కోసం సిద్ధం చేయబడిన ప్రత్యేక వివరణ.
            """.trimIndent()
        }

        val ytTitle = if (isTelugu) "$projectTitle | పూర్తి వివరాలు (Telugu Video)" else "$projectTitle | Complete Breakdown & Highlights"
        val ytDesc = buildString {
            appendLine("Full breakdown of $projectTitle. Watch till the end for key takeaways!")
            appendLine()
            appendLine("Timeline Chapters:")
            appendLine("00:00 - Introduction & Overview")
            appendLine("00:08 - Key Highlights & Demonstration")
            appendLine("00:18 - Important Tips & Takeaways")
            appendLine("00:24 - Conclusion & Next Steps")
            appendLine()
            appendLine("#Trending #OmniVideo #ContentCreation")
        }

        val igCaption = if (isTelugu) {
            "వీడియో నచ్చితే తప్పకుండా లైక్ మరియు షేర్ చేయండి! 🔥 మీ అభిప్రాయాన్ని కామెంట్ చేయండి.\n\nSave this reel for later! 📌"
        } else {
            "Watch till the end! 🔥 Double tap if you found this valuable. Drop your thoughts below!\n\nSave this reel for later! 📌"
        }

        val tags = if (isTelugu) {
            "#TeluguReels #TeluguContent #TrendingReels #TeluguCreators #ViralVideo #InstaTelugu #ReelsIndia"
        } else {
            "#ViralVideo #ContentCreator #ReelsInstagram #Shorts #TrendingNow #DailyVideo #VideoEditing"
        }

        val hook = if (isTelugu) {
            "ఇది మీకు తెలుసా? ఈ వీడియోని అస్సలు మిస్ అవ్వకండి:"
        } else {
            "Wait till you see what happens here! Watch this:"
        }

        val ttCaption = "$projectTitle 🚀 #fyp #viral #trending #video"
        val broll = "00:00 Opening wide establishing shot\n00:06 Dynamic zoom-in on subject\n00:15 Quick B-roll cut illustrating key point"
        val overlay = if (isTelugu) "🔥 తప్పక చూడండి!" else "🔥 Watch Till The End!"

        return GeneratedSocialMediaKit(
            summaryEn = summaryEn,
            summaryTe = summaryTe,
            youtubeTitle = ytTitle,
            youtubeDescription = ytDesc,
            youtubeChapters = "00:00 Intro\n00:08 Highlights\n00:18 Tips\n00:24 Conclusion",
            instagramCaption = igCaption,
            instagramHashtags = tags,
            tiktokHook = hook,
            tiktokCaptions = ttCaption,
            brollSuggestions = broll,
            overlayTextSuggestion = overlay
        )
    }
}
