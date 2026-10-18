package com.example.data

import com.example.data.model.AudioPresetsRepository
import com.example.data.model.MediaPlatformSource
import com.example.data.model.MediaSourcingRepository
import com.example.data.model.MemeRepository
import com.example.data.model.TranscriptSegment
import com.example.data.model.TranscriptionParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AudioAndMemeFeaturesTest {

    @Test
    fun testTranscriptionParserJsonRoundtrip() {
        val originalSegments = listOf(
            TranscriptSegment(1, 0L, 4000L, "నమస్కారం స్వాగతం", "Speaker 1"),
            TranscriptSegment(2, 4000L, 8500L, "ఈ వీడియోలో వైరల్ టిప్స్ తెలుసుకుందాం", "Speaker 1")
        )

        val json = TranscriptionParser.toJson(originalSegments)
        assertTrue(json.contains("నమస్కారం స్వాగతం"))

        val parsed = TranscriptionParser.fromJson(json)
        assertEquals(2, parsed.size)
        assertEquals(originalSegments[0].text, parsed[0].text)
        assertEquals(originalSegments[1].endMs, parsed[1].endMs)
    }

    @Test
    fun testAudioPresetsRepository() {
        val presets = AudioPresetsRepository.presets
        assertTrue(presets.isNotEmpty())
        val teluguPreset = presets.firstOrNull { it.language == "te" }
        assertNotNull(teluguPreset)
        assertTrue(teluguPreset!!.segments.isNotEmpty())
        assertTrue(teluguPreset.durationMs > 0)
    }

    @Test
    fun testMemeRepository() {
        val memes = MemeRepository.templates
        assertTrue(memes.isNotEmpty())
        val brahmi = memes.firstOrNull { it.id == "brahmi_shock" }
        assertNotNull(brahmi)
        assertEquals("Telugu Classics", brahmi?.category)
        assertTrue(brahmi?.punchlineTe?.isNotBlank() == true)

        val drake = memes.firstOrNull { it.id == "drake_hotline" }
        assertNotNull(drake)
    }

    @Test
    fun testMediaSourcingRepositorySearch() {
        val allClips = MediaSourcingRepository.curatedClips
        assertTrue(allClips.isNotEmpty())

        val hydClips = MediaSourcingRepository.searchClips("charminar")
        assertEquals(1, hydClips.size)
        assertEquals(MediaPlatformSource.GOOGLE_SEARCH, hydClips[0].platform)

        val shortsClips = MediaSourcingRepository.searchClips("", MediaPlatformSource.YOUTUBE_SHORTS)
        assertTrue(shortsClips.isNotEmpty())
        assertEquals(MediaPlatformSource.YOUTUBE_SHORTS, shortsClips[0].platform)
    }
}
