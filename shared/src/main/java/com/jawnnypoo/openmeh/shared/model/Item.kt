package com.jawnnypoo.openmeh.shared.model

import org.parceler.Parcel

/**
 * An item of the deal
 */
@Parcel(Parcel.Serialization.BEAN)
class Item : Comparable<Item> {

    var attributes: List<Attribute>? = null
    var condition: String? = null
    var id: String? = null
    var price: Float? = null
    var photo: String? = null

    override fun compareTo(another: Item): Int {
        return this.price?.compareTo(another.price!!)!!
    }
}
