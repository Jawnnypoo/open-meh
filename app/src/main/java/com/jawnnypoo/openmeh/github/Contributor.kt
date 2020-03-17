package com.jawnnypoo.openmeh.github

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * GitHub contributor
 */
@JsonClass(generateAdapter = true)
data class Contributor(
        @Json(name = "avatar_url")
        var avatarUrl: String? = null
)
