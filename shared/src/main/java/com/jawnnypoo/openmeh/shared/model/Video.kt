package com.jawnnypoo.openmeh.shared.model

import org.parceler.Parcel

/**
 * The video of the day
 */
@Parcel(Parcel.Serialization.BEAN)
class Video {
    var id: String? = null
    var title: String? = null
    var url: String? = null
}
