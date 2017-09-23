package com.jawnnypoo.openmeh.shared.model

import android.graphics.Color
import org.parceler.Parcel

/**
 * Pretty cool theme. Tells us all about the colors we want to show
 */
@Parcel(Parcel.Serialization.BEAN)
class Theme {

    companion object {

        const val FOREGROUND_LIGHT = "light"

        private val EXPECTED_FORMAT = "#FFFFFF"

        private fun safeParseColor(color: String?): Int {
            color ?: return Color.BLACK
            if (color.length == EXPECTED_FORMAT.length) {
                return Color.parseColor(color)
            } else {
                //This seems kinda terrible...
                var rrggbb = "#"
                for (i in 1 until color.length) {
                    rrggbb += color[i] + "" + color[i]
                }
                return Color.parseColor(rrggbb)
            }
        }
    }

    var accentColor: String? = null
    var foreground: String? = null
    var backgroundColor: String? = null
    var backgroundImage: String? = null

    fun safeForegroundColor(): Int {
        return if (foreground == FOREGROUND_LIGHT) Color.WHITE else Color.BLACK
    }

    fun safeForegroundColorInverse(): Int {
        return if (foreground == FOREGROUND_LIGHT) Color.BLACK else Color.WHITE
    }

    fun safeAccentColor(): Int {
        return safeParseColor(accentColor)
    }

    fun safeBackgroundColor(): Int {
        return safeParseColor(backgroundColor)
    }
}
