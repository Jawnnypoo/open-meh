package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Answer to a poll
 */
public class Answer implements Parcelable {

    String id;
    String text;
    int voteCount;

    protected Answer() {

    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getVoteCount() {
        return voteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeInt(this.voteCount);
    }

    protected Answer(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.voteCount = in.readInt();
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
