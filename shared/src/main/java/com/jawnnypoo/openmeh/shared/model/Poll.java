package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcelable;

/**
 * Poll. Hmmm
 */
public class Poll implements Parcelable {
    public Poll(){}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    protected Poll(android.os.Parcel in) {
    }

    public static final Parcelable.Creator<Poll> CREATOR = new Parcelable.Creator<Poll>() {
        @Override
        public Poll createFromParcel(android.os.Parcel source) {
            return new Poll(source);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
}
