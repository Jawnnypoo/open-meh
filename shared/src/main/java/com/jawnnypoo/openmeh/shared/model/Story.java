package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcelable;

/**
 * Cool story brah
 */
public class Story implements Parcelable {
    String title;
    String body;

    public Story(){}

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.body);
    }

    protected Story(android.os.Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        @Override
        public Story createFromParcel(android.os.Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
