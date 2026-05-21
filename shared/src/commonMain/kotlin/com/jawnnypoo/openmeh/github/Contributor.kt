package com.jawnnypoo.openmeh.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GitHub contributor
 */
@Serializable
data class Contributor(
    @SerialName("avatar_url")
    var avatarUrl: String? = null
)
