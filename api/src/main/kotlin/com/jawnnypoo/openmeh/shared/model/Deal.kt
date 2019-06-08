package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The entire deal!
 */
data class Deal(
        @Json(name = "features") val features: String,
        @Json(name = "id") val id: String,
        @Json(name = "items") val items: List<Item>,
        @Json(name = "photos") val photos: List<String>,
        @Json(name = "title") val title: String,
        /**
         * Null if not sold out
         */
        @Json(name = "soldOutAt") val soldOutAt: String? = null,
        @Json(name = "story") val story: Story,
        @Json(name = "theme") val theme: Theme,
        @Json(name = "topic") val topic: Topic,
        @Json(name = "url") val url: String
)
