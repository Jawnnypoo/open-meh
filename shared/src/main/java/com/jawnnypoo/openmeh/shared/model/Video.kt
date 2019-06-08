package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The video of the day
 */
data class Video(
        @Json(name = "id") var id: String,
        @Json(name = "title") var title: String,
        @Json(name = "url") var url: String
)
