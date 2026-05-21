package com.jawnnypoo.openmeh.shared.model

import kotlinx.serialization.Serializable


/**
 * The story of the deal
 */
@Serializable
data class Story(
    val title: String,
    val body: String
)
