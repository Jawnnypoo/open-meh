package com.jawnnypoo.openmeh.model;

import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

import org.parceler.Parcel;

/**
 * The meh response meant for wearables
 */
@Parcel
public class MehWearResponse {

    private TinyMehResponse mTinyMehResponse;
    private String mImageId;

    protected MehWearResponse() {
        //for parceler
    }

    public MehWearResponse(TinyMehResponse tinyMehResponse, String imageId) {
        mTinyMehResponse = tinyMehResponse;
        mImageId = imageId;
    }

    public TinyMehResponse getTinyMehResponse() {
        return mTinyMehResponse;
    }

    public String getImageId() {
        return mImageId;
    }
}
