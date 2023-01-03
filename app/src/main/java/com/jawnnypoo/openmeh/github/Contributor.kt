package com.jawnnypoo.openmeh.github

import com.squareup.moshi.Json

/**
 * GitHub contributor
 */
data class Contributor(
    @Json(name = "avatar_url")
    var avatarUrl: String? = null
)
