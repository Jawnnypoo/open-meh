package com.jawnnypoo.openmeh.github

import com.google.gson.annotations.SerializedName

/**
 * GitHub contributor
 */
class Contributor {
    var login: String? = null
    var contributions: Int = 0
    @SerializedName("avatar_url")
    var avatarUrl: String? = null
}
