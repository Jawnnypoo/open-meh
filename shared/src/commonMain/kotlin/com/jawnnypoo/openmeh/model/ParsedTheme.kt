package com.jawnnypoo.openmeh.model

import com.jawnnypoo.openmeh.shared.model.Theme

/**
 * [Theme], but parsed into colors that are safe and make sense. Values are ARGB ints so they're
 * usable as both [androidx.compose.ui.graphics.Color] inputs and Android-platform color ints.
 */
data class ParsedTheme(
    val accentColor: String? = null,
    val foreground: String? = null,
    val backgroundColor: String? = null,
    val backgroundImage: String? = null,
) {

    companion object {

        const val BLACK: Int = 0xFF000000.toInt()
        const val WHITE: Int = 0xFFFFFFFF.toInt()

        private fun safeParseColor(color: String?): Int {
            color ?: return BLACK
            val hex = when {
                color.startsWith("#") && color.length == 7 -> color.substring(1)
                color.startsWith("#") && color.length == 4 -> {
                    // Expand #RGB → #RRGGBB.
                    buildString {
                        for (i in 1 until color.length) {
                            append(color[i])
                            append(color[i])
                        }
                    }
                }
                else -> return BLACK
            }
            return runCatching {
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)
                (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }.getOrDefault(BLACK)
        }

        fun fromTheme(theme: Theme?): ParsedTheme? {
            if (theme == null) {
                return null
            }
            return ParsedTheme(
                accentColor = theme.accentColor,
                foreground = theme.foreground,
                backgroundColor = theme.backgroundColor,
                backgroundImage = theme.backgroundImage,
            )
        }
    }

    fun safeForegroundColor(): Int =
        if (foreground == Theme.FOREGROUND_LIGHT) WHITE else BLACK

    fun safeForegroundColorInverse(): Int =
        if (foreground == Theme.FOREGROUND_LIGHT) BLACK else WHITE

    fun safeAccentColor(): Int = safeParseColor(accentColor)

    fun safeBackgroundColor(): Int = safeParseColor(backgroundColor)
}
