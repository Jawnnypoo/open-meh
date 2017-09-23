package com.jawnnypoo.openmeh.shared.response

import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Video
import org.parceler.Parcel

/**
 * The actual response we get when pulling in the meh deal
 */
@Parcel(Parcel.Serialization.BEAN)
class MehResponse {
    var deal: Deal? = null
    var video: Video? = null
}
