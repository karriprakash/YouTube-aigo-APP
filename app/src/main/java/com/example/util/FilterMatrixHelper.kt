package com.example.util

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

enum class VideoFilterType(val displayName: String, val description: String) {
    NORMAL("Normal", "Original natural look"),
    VIBRANT("Vibrant", "Punchy colors & high saturation"),
    CINEMATIC("Cinematic", "Teal & orange moody grade"),
    VINTAGE("Vintage", "Warm nostalgic retro sepia"),
    MONOCHROME("B&W Classic", "Timeless black and white"),
    NOIR("Noir Film", "Deep shadows & high contrast")
}

object FilterMatrixHelper {

    fun getColorFilter(type: String, intensity: Float = 1.0f): ColorFilter? {
        val matrix = getColorMatrix(type, intensity) ?: return null
        return ColorFilter.colorMatrix(matrix)
    }

    fun getColorMatrix(type: String, intensity: Float = 1.0f): ColorMatrix? {
        val clampedIntensity = intensity.coerceIn(0.0f, 1.0f)
        if (type == "NORMAL" || clampedIntensity <= 0.05f) return null

        val baseMatrix = when (type) {
            "VIBRANT" -> ColorMatrix(
                floatArrayOf(
                    1.2f, 0.0f, 0.0f, 0.0f, 10f,
                    0.0f, 1.25f, 0.0f, 0.0f, 10f,
                    0.0f, 0.0f, 1.15f, 0.0f, 5f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0f
                )
            )
            "CINEMATIC" -> ColorMatrix(
                floatArrayOf(
                    1.1f, 0.0f, 0.0f, 0.0f, 15f,   // warm red boost
                    0.0f, 1.0f, 0.0f, 0.0f, 5f,
                    0.0f, 0.1f, 1.3f, 0.0f, 25f,   // teal blue push
                    0.0f, 0.0f, 0.0f, 1.0f, 0f
                )
            )
            "VINTAGE" -> ColorMatrix(
                floatArrayOf(
                    0.393f * 1.2f, 0.769f, 0.189f, 0.0f, 20f,
                    0.349f, 0.686f * 1.1f, 0.168f, 0.0f, 15f,
                    0.272f, 0.534f, 0.131f * 0.9f, 0.0f, 5f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0f
                )
            )
            "MONOCHROME" -> {
                val matrix = ColorMatrix()
                matrix.setToSaturation(0f)
                matrix
            }
            "NOIR" -> ColorMatrix(
                floatArrayOf(
                    1.5f, 0.0f, 0.0f, 0.0f, -40f,
                    0.0f, 1.5f, 0.0f, 0.0f, -40f,
                    0.0f, 0.0f, 1.5f, 0.0f, -40f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0f
                )
            ).apply {
                val sat = ColorMatrix()
                sat.setToSaturation(0f)
                timesAssign(sat)
            }
            else -> return null
        }

        // Blend with identity matrix based on intensity
        if (clampedIntensity >= 0.98f) return baseMatrix

        val identity = ColorMatrix()
        val blended = floatArrayOf(
            lerp(identity.values[0], baseMatrix.values[0], clampedIntensity),
            lerp(identity.values[1], baseMatrix.values[1], clampedIntensity),
            lerp(identity.values[2], baseMatrix.values[2], clampedIntensity),
            lerp(identity.values[3], baseMatrix.values[3], clampedIntensity),
            lerp(identity.values[4], baseMatrix.values[4], clampedIntensity),

            lerp(identity.values[5], baseMatrix.values[5], clampedIntensity),
            lerp(identity.values[6], baseMatrix.values[6], clampedIntensity),
            lerp(identity.values[7], baseMatrix.values[7], clampedIntensity),
            lerp(identity.values[8], baseMatrix.values[8], clampedIntensity),
            lerp(identity.values[9], baseMatrix.values[9], clampedIntensity),

            lerp(identity.values[10], baseMatrix.values[10], clampedIntensity),
            lerp(identity.values[11], baseMatrix.values[11], clampedIntensity),
            lerp(identity.values[12], baseMatrix.values[12], clampedIntensity),
            lerp(identity.values[13], baseMatrix.values[13], clampedIntensity),
            lerp(identity.values[14], baseMatrix.values[14], clampedIntensity),

            lerp(identity.values[15], baseMatrix.values[15], clampedIntensity),
            lerp(identity.values[16], baseMatrix.values[16], clampedIntensity),
            lerp(identity.values[17], baseMatrix.values[17], clampedIntensity),
            lerp(identity.values[18], baseMatrix.values[18], clampedIntensity),
            lerp(identity.values[19], baseMatrix.values[19], clampedIntensity)
        )
        return ColorMatrix(blended)
    }

    private fun lerp(start: Float, stop: Float, fraction: Float): Float {
        return start + (stop - start) * fraction
    }
}
