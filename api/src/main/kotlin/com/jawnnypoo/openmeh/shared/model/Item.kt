package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * An item of the deal
 */
@JsonClass(generateAdapter = true)
data class Item(
        @Json(name = "id") val id: String,
        @Json(name = "price") val price: Float
)
