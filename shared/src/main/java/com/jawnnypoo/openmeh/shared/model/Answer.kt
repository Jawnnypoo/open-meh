package com.jawnnypoo.openmeh.shared.model

import org.parceler.Parcel

/**
 * Answer to a poll
 */
@Parcel(Parcel.Serialization.BEAN)
class Answer {

    var id: String? = null
    var text: String? = null
    var voteCount: Int = 0
}
