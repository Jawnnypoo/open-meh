package com.jawnnypoo.openmeh.shared.communication;

import com.google.gson.annotations.SerializedName;
import com.jawnnypoo.openmeh.shared.api.MehResponse;

/**
 * A smaller version of the {@link com.jawnnypoo.openmeh.shared.api.MehResponse} to send to wearables
 */
public class TinyMehResponse {

    public static TinyMehResponse create(MehResponse response) {
        TinyMehResponse tinyMehResponse = new TinyMehResponse();
        tinyMehResponse.mTitle = response.getDeal().getTitle();
        tinyMehResponse.mPriceRange = response.getDeal().getPriceRange();
        return tinyMehResponse;
    }

    @SerializedName("title")
    protected String mTitle;
    @SerializedName("price_range")
    protected String mPriceRange;

    protected TinyMehResponse() {
        //for json
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPriceRange() {
        return mPriceRange;
    }
}
