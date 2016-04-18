package com.jawnnypoo.openmeh.shared.communication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.jawnnypoo.openmeh.shared.api.MehResponse;
import com.jawnnypoo.openmeh.shared.model.Theme;

/**
 * A smaller version of the {@link com.jawnnypoo.openmeh.shared.api.MehResponse} to send to wearables
 */
public class TinyMehResponse implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTitle);
        dest.writeString(this.mPriceRange);
        dest.writeParcelable(this.mTheme, flags);
    }

    protected TinyMehResponse(Parcel in) {
        this.mTitle = in.readString();
        this.mPriceRange = in.readString();
        this.mTheme = in.readParcelable(Theme.class.getClassLoader());
    }

    public static final Parcelable.Creator<TinyMehResponse> CREATOR = new Parcelable.Creator<TinyMehResponse>() {
        @Override
        public TinyMehResponse createFromParcel(Parcel source) {
            return new TinyMehResponse(source);
        }

        @Override
        public TinyMehResponse[] newArray(int size) {
            return new TinyMehResponse[size];
        }
    };
}
