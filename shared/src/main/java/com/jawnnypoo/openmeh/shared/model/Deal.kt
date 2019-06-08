package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The entire deal!
 */
data class Deal(
        @Json(name = "features") var features: String,
        @Json(name = "id") var id: String,
        @Json(name = "items") var items: List<Item>,
        @Json(name = "photos") var photos: List<String>,
        @Json(name = "title") var title: String,
        /**
         * Null if not sold out
         */
        @Json(name = "soldOutAt") var soldOutAt: String? = null,
        @Json(name = "story") var story: Story,
        @Json(name = "theme") var theme: Theme,
        @Json(name = "topic") var topic: Topic,
        @Json(name = "url") var url: String
)
