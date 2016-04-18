package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcelable;

/**
 * Topic of discussion for the day
 */
public class Topic implements Parcelable {
    Integer commentCount;
    String createdAt; //date
    String id;
    Integer replyCount;
    String url;
    Integer voteCount;

    public Topic(){}

    public Integer getCommentCount() {
        return commentCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public String getUrl() {
        return url;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(this.commentCount);
        dest.writeString(this.createdAt);
        dest.writeString(this.id);
        dest.writeValue(this.replyCount);
        dest.writeString(this.url);
        dest.writeValue(this.voteCount);
    }

    protected Topic(android.os.Parcel in) {
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createdAt = in.readString();
        this.id = in.readString();
        this.replyCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.url = in.readString();
        this.voteCount = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        @Override
        public Topic createFromParcel(android.os.Parcel source) {
            return new Topic(source);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
