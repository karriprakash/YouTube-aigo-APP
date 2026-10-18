package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.VideoProject
import com.example.util.FilterMatrixHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OmniVideo", appName)
    }

    @Test
    fun testProjectDurationAndTrimming() {
        val project = VideoProject(
            title = "Test Telugu Reel",
            videoUri = "asset://test.mp4",
            originalDurationMs = 30_000L,
            trimStartMs = 5_000L,
            trimEndMs = 20_000L,
            aspectRatio = "9:16",
            language = "te"
        )
        assertEquals(15_000L, project.trimmedDurationMs)
        assertEquals("00:15", project.formattedDuration)
    }

    @Test
    fun testColorMatrixFilterGeneration() {
        val vibrantMatrix = FilterMatrixHelper.getColorMatrix("VIBRANT", 0.8f)
        assertNotNull(vibrantMatrix)

        val cinematicMatrix = FilterMatrixHelper.getColorMatrix("CINEMATIC", 1.0f)
        assertNotNull(cinematicMatrix)

        val monochromeMatrix = FilterMatrixHelper.getColorMatrix("MONOCHROME", 1.0f)
        assertNotNull(monochromeMatrix)
    }
}
