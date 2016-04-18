package com.jawnnypoo.openmeh.event;

import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

/**
 * Indicates that the meh deal has been fetched
 */
public class FetchMehEvent {

    public TinyMehResponse tinyMehResponse;

    public FetchMehEvent(TinyMehResponse response) {
        tinyMehResponse = response;
    }
}
