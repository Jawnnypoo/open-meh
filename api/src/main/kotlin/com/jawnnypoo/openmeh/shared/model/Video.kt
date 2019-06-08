package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The video of the day
 */
data class Video(
        @Json(name = "id") val id: String,
        @Json(name = "title") val title: String,
        @Json(name = "url") val url: String
)
