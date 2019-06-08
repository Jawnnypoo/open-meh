package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Pretty cool theme. Tells us all about the colors we want to show
 */
@JsonClass(generateAdapter = true)
data class Theme(
        @Json(name = "accentColor") val accentColor: String,
        @Json(name = "foreground") val foreground: String,
        @Json(name = "backgroundColor") val backgroundColor: String,
        @Json(name = "backgroundImage") val backgroundImage: String
) {
    companion object {
        const val FOREGROUND_LIGHT = "light"
    }
}
