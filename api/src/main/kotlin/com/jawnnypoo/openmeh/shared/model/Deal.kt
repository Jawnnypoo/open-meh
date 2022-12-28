package com.jawnnypoo.openmeh.shared.model

/**
 * The entire deal!
 */
data class Deal(
    val features: String,
    val id: String,
    val items: List<Item>,
    val photos: List<String>,
    val title: String,
    /**
     * Null if not sold out
     */
    val soldOutAt: String? = null,
    val story: Story,
    val theme: Theme,
    val topic: Topic? = null,
    val url: String
)
