package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The video of the day
 */
class Video {
    @Json(name = "id") var id: String? = null
    @Json(name = "title") var title: String? = null
    @Json(name = "url") var url: String? = null
}
