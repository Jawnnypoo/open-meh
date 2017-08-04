package com.jawnnypoo.openmeh.shared.api;

import com.jawnnypoo.openmeh.shared.model.Deal;
import com.jawnnypoo.openmeh.shared.model.Video;

/**
 * The actual response we get when pulling in the meh deal
 */
public class MehResponse {
    Deal deal;
    Video video;

    public MehResponse() {}

    public Deal getDeal() {
        return deal;
    }

    public Video getVideo() {
        return video;
    }
}
