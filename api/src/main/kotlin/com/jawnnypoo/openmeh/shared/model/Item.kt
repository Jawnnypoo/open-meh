package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * An item of the deal
 */
data class Item(
        @Json(name = "id") val id: String,
        @Json(name = "price") val price: Float
)
