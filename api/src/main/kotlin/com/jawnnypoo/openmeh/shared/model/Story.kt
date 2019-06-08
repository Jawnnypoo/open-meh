package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The story of the deal
 */
data class Story(
        @Json(name = "title") val title: String,
        @Json(name = "body") val body: String
)
