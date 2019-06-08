package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * Stay on topic
 */
data class Topic(
        @Json(name = "url") var url: String
)