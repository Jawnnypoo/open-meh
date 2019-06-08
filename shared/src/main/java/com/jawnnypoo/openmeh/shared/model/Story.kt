package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * Cool story brah
 */
class Story {
    @Json(name = "title") var title: String? = null
    @Json(name = "body") var body: String? = null
}
