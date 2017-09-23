package com.jawnnypoo.openmeh.shared.model

import org.parceler.Parcel

/**
 * The entire deal!
 */
@Parcel(Parcel.Serialization.BEAN)
class Deal {

    var features: String? = null
    var id: String? = null
    var items: MutableList<Item>? = null
    var photos: MutableList<String>? = null
    var title: String? = null
    //Null if not sold out
    var soldOutAt: String? = null
    var specifications: String? = null
    var story: Story? = null
    var theme: Theme? = null
    var topic: Topic? = null
    var url: String? = null
}
