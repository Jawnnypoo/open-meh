package com.jawnnypoo.openmeh.shared.model

import com.squareup.moshi.Json

/**
 * The entire deal!
 */
class Deal {
    @Json(name = "features") var features: String? = null
    @Json(name = "id") var id: String? = null
    @Json(name = "items") var items: MutableList<Item>? = null
    @Json(name = "photos") var photos: MutableList<String>? = null
    @Json(name = "title") var title: String? = null
    //Null if not sold out
    @Json(name = "soldOutAt") var soldOutAt: String? = null
    @Json(name = "story") var story: Story? = null
    @Json(name = "theme")  var theme: Theme? = null
    @Json(name = "topic") var topic: Topic? = null
    @Json(name = "url") var url: String? = null
}
