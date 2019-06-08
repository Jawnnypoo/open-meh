package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * An item of the deal
 */
data class Item(
        @Json(name = "id") var id: String? = null,
        @Json(name = "price") var price: Float? = null
)
