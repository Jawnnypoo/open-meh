package com.jawnnypoo.openmeh.shared.model

import org.parceler.Parcel

/**
 * Cool story brah
 */
@Parcel(Parcel.Serialization.BEAN)
class Story {
    var title: String? = null
    var body: String? = null
}
