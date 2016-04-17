package com.jawnnypoo.openmeh.shared.communication;

import com.google.gson.annotations.SerializedName;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.model.Theme;

import org.parceler.Parcel;

/**
 * A smaller version of the {@link com.jawnnypoo.openmeh.shared.api.MehResponse} to send to wearables
 */
@Parcel
public class TinyMehResponse {

    public static TinyMehResponse create(MehResponse response) {
        TinyMehResponse tinyMehResponse = new TinyMehResponse();
        tinyMehResponse.mTitle = response.getDeal().getTitle();
        tinyMehResponse.mPriceRange = response.getDeal().getPriceRange();
        tinyMehResponse.mTheme = response.getDeal().getTheme();
        return tinyMehResponse;
    }

    @SerializedName("title")
    protected String mTitle;
    @SerializedName("price_range")
    protected String mPriceRange;
    @SerializedName("theme")
    protected Theme mTheme;

    protected TinyMehResponse() {
        //for json
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPriceRange() {
        return mPriceRange;
    }

    public Theme getTheme() {
        return mTheme;
    }
}
