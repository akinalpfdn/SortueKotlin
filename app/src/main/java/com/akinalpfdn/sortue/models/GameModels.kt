package com.akinalpfdn.sortue.models

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

// Raw RGB Data structure to ensure mathematical precision
data class RGBData(
    val r: Double,
    val g: Double,
    val b: Double
) {
    // Helper to convert to Compose Color for display
    val color: Color
        get() = Color(r.toFloat(), g.toFloat(), b.toFloat())

    // Helper to check similarity
    fun isSimilar(other: RGBData): Boolean {
        val threshold = 0.05
        return abs(r - other.r) < threshold &&
                abs(g - other.g) < threshold &&
                abs(b - other.b) < threshold
    }

    // Helper to check distance between colors
    fun distance(other: RGBData): Double {
        val dr = r - other.r
        val dg = g - other.g
        val db = b - other.b
        return sqrt(dr * dr + dg * dg + db * db)
    }

    companion object {
        // Generate random RGB
        val random: RGBData
            get() = RGBData(
                r = Random.nextDouble(0.0, 1.0),
                g = Random.nextDouble(0.0, 1.0),
                b = Random.nextDouble(0.0, 1.0)
            )

        // Create RGBData from Hue, Saturation, Brightness
        // h: 0.0-1.0, s: 0.0-1.0, b: 0.0-1.0
        fun fromHSB(h: Double, s: Double, b: Double): RGBData {
            val hFloat = (h * 360.0).toFloat() // Android Color.hsvToColor uses 0-360 for hue
            val sFloat = s.toFloat()
            val bFloat = b.toFloat()

            val hsv = floatArrayOf(hFloat, sFloat, bFloat)
            val androidColor = android.graphics.Color.HSVToColor(hsv)

            val red = android.graphics.Color.red(androidColor) / 255.0
            val green = android.graphics.Color.green(androidColor) / 255.0
            val blue = android.graphics.Color.blue(androidColor) / 255.0

            return RGBData(red, green, blue)
        }

        // Bilinear Interpolation logic
        fun interpolated(
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            corners: Corners
        ): RGBData {
            val u = x.toDouble() / (maxOf(1, width - 1)).toDouble()
            val v = y.toDouble() / (maxOf(1, height - 1)).toDouble()

            // Interpolate top edge (horizontal)
            val rTop = lerp(corners.tl.r, corners.tr.r, u)
            val gTop = lerp(corners.tl.g, corners.tr.g, u)
            val bTop = lerp(corners.tl.b, corners.tr.b, u)

            // Interpolate bottom edge (horizontal)
            val rBottom = lerp(corners.bl.r, corners.br.r, u)
            val gBottom = lerp(corners.bl.g, corners.br.g, u)
            val bBottom = lerp(corners.bl.b, corners.br.b, u)

            // Interpolate vertical
            return RGBData(
                r = lerp(rTop, rBottom, v),
                g = lerp(gTop, gBottom, v),
                b = lerp(bTop, bBottom, v)
            )
        }

        private fun lerp(start: Double, end: Double, t: Double): Double {
            return start * (1.0 - t) + end * t
        }
    }
}

data class Corners(
    val tl: RGBData,
    val tr: RGBData,
    val bl: RGBData,
    val br: RGBData
)

data class Tile(
    val id: Int,             // Unique ID
    val correctId: Int,      // The grid index where this tile SHOULD be
    val rgb: RGBData,        // The color data
    val isFixed: Boolean,    // Is this a corner anchor?
    var currentIdx: Int      // Logic helper
)

enum class GameStatus {
    MENU,
    PREVIEW,
    PLAYING,
    ANIMATING,
    WON,
    GAME_OVER
}