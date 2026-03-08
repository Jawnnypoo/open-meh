package com.jawnnypoo.openmeh.shared.model

import kotlinx.serialization.Serializable

/**
 * An item of the deal
 */
@Serializable
data class Item(
    val id: String,
    val price: Float
)
