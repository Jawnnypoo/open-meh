package com.jawnnypoo.openmeh.model;

import android.os.Parcelable;

import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

/**
 * The meh response meant for wearables
 */
public class MehWearResponse implements Parcelable {

    protected TinyMehResponse mTinyMehResponse;
    protected String mImageId;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeParcelable(this.mTinyMehResponse, flags);
        dest.writeString(this.mImageId);
    }

    protected MehWearResponse(android.os.Parcel in) {
        this.mTinyMehResponse = in.readParcelable(TinyMehResponse.class.getClassLoader());
        this.mImageId = in.readString();
    }

    public static final Parcelable.Creator<MehWearResponse> CREATOR = new Parcelable.Creator<MehWearResponse>() {
        @Override
        public MehWearResponse createFromParcel(android.os.Parcel source) {
            return new MehWearResponse(source);
        }

        @Override
        public MehWearResponse[] newArray(int size) {
            return new MehWearResponse[size];
        }
    };
}
