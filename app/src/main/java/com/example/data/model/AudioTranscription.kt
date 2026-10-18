package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class WordTimestamp(
    val word: String,
    val startMs: Long,
    val endMs: Long
)

data class TranscriptSegment(
    val id: Int,
    val startMs: Long,
    val endMs: Long,
    val text: String,
    val speaker: String = "Speaker 1"
) {
    val formattedTimestamp: String
        get() {
            val startSec = startMs / 1000
            val endSec = endMs / 1000
            return String.format("%02d:%02d - %02d:%02d", startSec / 60, startSec % 60, endSec / 60, endSec % 60)
        }
}

object TranscriptionParser {
    fun toJson(segments: List<TranscriptSegment>): String {
        val array = JSONArray()
        for (seg in segments) {
            val obj = JSONObject()
            obj.put("id", seg.id)
            obj.put("startMs", seg.startMs)
            obj.put("endMs", seg.endMs)
            obj.put("text", seg.text)
            obj.put("speaker", seg.speaker)
            array.put(obj)
        }
        return array.toString()
    }

    fun fromJson(jsonStr: String?): List<TranscriptSegment> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        return try {
            val list = mutableListOf<TranscriptSegment>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TranscriptSegment(
                        id = obj.optInt("id", i),
                        startMs = obj.optLong("startMs", 0L),
                        endMs = obj.optLong("endMs", 3000L),
                        text = obj.optString("text", ""),
                        speaker = obj.optString("speaker", "Speaker")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class AudioSourcePreset(
    val id: String,
    val title: String,
    val category: String, // "Telugu Story", "Motivation", "Comedy Dialogue", "Tech Review"
    val language: String, // "te", "en"
    val durationMs: Long,
    val fullTranscript: String,
    val segments: List<TranscriptSegment>,
    val suggestedAspect: String = "9:16",
    val defaultKeywords: String = "Telugu podcast, viral reels"
)

object AudioPresetsRepository {
    val presets = listOf(
        AudioSourcePreset(
            id = "te_motivation_1",
            title = "విజయం వైపు ప్రయాణం (Telugu Success Journey)",
            category = "Telugu Motivation",
            language = "te",
            durationMs = 24_000L,
            fullTranscript = "ప్రతి రోజు ఒక కొత్త అవకాశం. కష్టపడి పని చేస్తే విజయం ఖచ్చితంగా మీదే అవుతుంది. ఎప్పుడూ వెనకడుగు వేయకండి, మీ లక్ష్యం వైపు ముందుకు సాగండి!",
            segments = listOf(
                TranscriptSegment(1, 0L, 5000L, "ప్రతి రోజు ఒక కొత్త అవకాశం.", "Voice"),
                TranscriptSegment(2, 5000L, 12000L, "కష్టపడి పని చేస్తే విజయం ఖచ్చితంగా మీదే అవుతుంది.", "Voice"),
                TranscriptSegment(3, 12000L, 18000L, "ఎప్పుడూ వెనకడుగు వేయకండి,", "Voice"),
                TranscriptSegment(4, 18000L, 24000L, "మీ లక్ష్యం వైపు నిరంతరం ముందుకు సాగండి!", "Voice")
            ),
            suggestedAspect = "9:16",
            defaultKeywords = "mountain sunrise sunrise goal motivation"
        ),
        AudioSourcePreset(
            id = "te_tech_ai_2",
            title = "ఆర్టిఫిషియల్ ఇంటెలిజెన్స్ విప్లవం (AI Revolution in Telugu)",
            category = "Tech & Innovation",
            language = "te",
            durationMs = 26_000L,
            fullTranscript = "AI టెక్నాలజీ ప్రపంచాన్ని మార్చేస్తోంది. స్మార్ట్‌ఫోన్ల నుండి రోబోటిక్స్ వరకు సరికొత్త మార్పులు వస్తున్నాయి. భవిష్యత్తు ఇప్పుడు మన చేతుల్లోనే ఉంది!",
            segments = listOf(
                TranscriptSegment(1, 0L, 6000L, "AI టెక్నాలజీ ప్రపంచాన్ని పూర్తిగా మార్చేస్తోంది.", "Host"),
                TranscriptSegment(2, 6000L, 14000L, "స్మార్ట్‌ఫోన్ల నుండి రోబోటిక్స్ వరకు సరికొత్త మార్పులు వస్తున్నాయి.", "Host"),
                TranscriptSegment(3, 14000L, 20000L, "భవిష్యత్తు ఇప్పుడు మన చేతుల్లోనే ఉంది,", "Host"),
                TranscriptSegment(4, 20000L, 26000L, "ఈ టెక్నాలజీని అందిపుచ్చుకోవడమే మన కర్తవ్యం!", "Host")
            ),
            suggestedAspect = "9:16",
            defaultKeywords = "technology neural futuristic code neon"
        ),
        AudioSourcePreset(
            id = "te_comedy_punch_3",
            title = "బ్రహ్మానందం ఫన్ కామెంట్స్ (Telugu Comedy Reel)",
            category = "Telugu Comedy",
            language = "te",
            durationMs = 18_000L,
            fullTranscript = "ఏంట్రా బాబు ఇది? అసలు నన్ను ఎవరైనా ప్రశాంతంగా ఉండనిస్తారా? లైక్ కొట్టి సబ్‌స్క్రైబ్ చేసుకోండి ఫ్రెండ్స్!",
            segments = listOf(
                TranscriptSegment(1, 0L, 5000L, "ఏంట్రా బాబు ఇది? అసలు నన్ను చూడనివ్వరా?", "Brahmi"),
                TranscriptSegment(2, 5000L, 11000L, "ఎవరైనా కాస్త ప్రశాంతంగా ఉండనిస్తారా నన్ను?", "Brahmi"),
                TranscriptSegment(3, 11000L, 18000L, "వెంటనే లైక్ కొట్టి మీ ఫ్రెండ్స్‌కి షేర్ చేయండి!", "Voice")
            ),
            suggestedAspect = "9:16",
            defaultKeywords = "funny laughter cinema reaction"
        ),
        AudioSourcePreset(
            id = "en_creator_growth_4",
            title = "3 Secrets to Going Viral on Social Media",
            category = "Creator Growth",
            language = "en",
            durationMs = 22_000L,
            fullTranscript = "Here are the top three secrets to make your videos blow up. First, hook them in under 2 seconds. Second, deliver fast value without fluff. Third, end with a memorable punchline!",
            segments = listOf(
                TranscriptSegment(1, 0L, 4500L, "Here are the top three secrets to make your videos blow up.", "Creator"),
                TranscriptSegment(2, 4500L, 10500L, "First, hook viewers in under 2 seconds.", "Creator"),
                TranscriptSegment(3, 10500L, 16000L, "Second, deliver high value without filler words.", "Creator"),
                TranscriptSegment(4, 16000L, 22000L, "And third, end with a punchy call to action!", "Creator")
            ),
            suggestedAspect = "9:16",
            defaultKeywords = "studio microphone recording lighting creator"
        )
    )
}
