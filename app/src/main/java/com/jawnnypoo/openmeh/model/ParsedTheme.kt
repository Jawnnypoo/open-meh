package com.jawnnypoo.openmeh.model

import android.graphics.Color
import android.os.Parcelable
import com.jawnnypoo.openmeh.shared.model.Theme
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParsedTheme(
        val accentColor: String? = null,
        val foreground: String? = null,
        val backgroundColor: String? = null,
        val backgroundImage: String? = null
) : Parcelable {

    companion object {

        private const val EXPECTED_FORMAT = "#FFFFFF"

        private fun safeParseColor(color: String?): Int {
            color ?: return Color.BLACK
            return if (color.length == EXPECTED_FORMAT.length) {
                Color.parseColor(color)
            } else {
                //This seems kinda terrible...
                var rrggbb = "#"
                for (i in 1 until color.length) {
                    rrggbb += color[i] + "" + color[i]
                }
                Color.parseColor(rrggbb)
            }
        }

        fun fromTheme(theme: Theme?): ParsedTheme? {
            if (theme == null) {
                return null
            }
            return ParsedTheme(
                    accentColor = theme.accentColor,
                    foreground = theme.foreground,
                    backgroundColor = theme.backgroundColor,
                    backgroundImage = theme.backgroundImage
            )
        }
    }

    fun safeForegroundColor(): Int {
        return if (foreground == Theme.FOREGROUND_LIGHT) Color.WHITE else Color.BLACK
    }

    fun safeForegroundColorInverse(): Int {
        return if (foreground == Theme.FOREGROUND_LIGHT) Color.BLACK else Color.WHITE
    }

    fun safeAccentColor(): Int {
        return safeParseColor(accentColor)
    }

    fun safeBackgroundColor(): Int {
        return safeParseColor(backgroundColor)
    }
}