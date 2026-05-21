package com.jawnnypoo.openmeh.shared.model

import kotlinx.serialization.Serializable

/**
 * The video of the day
 */
@Serializable
data class Video(
    val id: String,
    val title: String,
    val url: String
)
