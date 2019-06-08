package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The story of the deal
 */
class Story {
    @Json(name = "title") var title: String? = null
    @Json(name = "body") var body: String? = null
}
