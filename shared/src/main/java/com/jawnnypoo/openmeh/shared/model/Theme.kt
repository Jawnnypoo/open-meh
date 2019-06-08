package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * Pretty cool theme. Tells us all about the colors we want to show
 */
class Theme {
    @Json(name = "accentColor") var accentColor: String? = null
    @Json(name = "foreground") var foreground: String? = null
    @Json(name = "backgroundColor") var backgroundColor: String? = null
    @Json(name = "backgroundImage") var backgroundImage: String? = null

    companion object {
        const val FOREGROUND_LIGHT = "light"
    }
}
