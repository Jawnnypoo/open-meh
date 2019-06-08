package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The video of the day
 */
@JsonClass(generateAdapter = true)
data class Video(
        @Json(name = "id") val id: String,
        @Json(name = "title") val title: String,
        @Json(name = "url") val url: String
)
