package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The story of the deal
 */
@JsonClass(generateAdapter = true)
data class Story(
        @Json(name = "title") val title: String,
        @Json(name = "body") val body: String
)
