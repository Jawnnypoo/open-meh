package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcelable;

/**
 * The video of the day
 */
public class Video implements Parcelable {
    String id;
    String startDate;
    String title;
    String url;
    Topic topic;

    public Video() {}

    public String getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Topic getTopic() {
        return topic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.startDate);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeParcelable(this.topic, flags);
    }

    protected Video(android.os.Parcel in) {
        this.id = in.readString();
        this.startDate = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.topic = in.readParcelable(Topic.class.getClassLoader());
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(android.os.Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
