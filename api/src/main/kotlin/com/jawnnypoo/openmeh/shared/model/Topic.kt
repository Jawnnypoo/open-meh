package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Stay on topic
 */
@JsonClass(generateAdapter = true)
data class Topic(
        @Json(name = "url") val url: String
)