package com.jawnnypoo.openmeh.shared.response

import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Video
import com.squareup.moshi.Json

/**
 * The actual response we get when pulling in the meh deal
 */
class MehResponse {
    @Json(name = "deal") var deal: Deal? = null
    @Json(name = "video") var video: Video? = null
}
