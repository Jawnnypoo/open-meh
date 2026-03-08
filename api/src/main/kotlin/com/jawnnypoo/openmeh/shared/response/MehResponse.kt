package com.jawnnypoo.openmeh.shared.response

import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Video
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The actual response we get when pulling in the meh deal
 */
@Serializable
data class MehResponse(
    @SerialName("deal") var deal: Deal,
    @SerialName("video") var video: Video? = null
)
