package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * Pretty cool theme. Tells us all about the colors we want to show
 */
data class Theme(
        @Json(name = "accentColor") var accentColor: String,
        @Json(name = "foreground") var foreground: String,
        @Json(name = "backgroundColor") var backgroundColor: String,
        @Json(name = "backgroundImage") var backgroundImage: String
) {
    companion object {
        const val FOREGROUND_LIGHT = "light"
    }
}
