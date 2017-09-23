package com.jawnnypoo.openmeh.github

import com.squareup.moshi.Json

/**
 * GitHub contributor
 */
class Contributor {
    var login: String? = null
    var contributions: Int = 0
    @field:Json(name = "avatar_url")
    var avatarUrl: String? = null
}
