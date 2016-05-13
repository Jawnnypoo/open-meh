package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Poll. Hmmm
 */
public class Poll implements Parcelable {
    ArrayList<Answer> answers;
    String id;
    String startDate;
    String title;
    Topic topic;

    public Poll(){}

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public String getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public Topic getTopic() {
        return topic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.answers);
        dest.writeString(this.id);
        dest.writeString(this.startDate);
        dest.writeString(this.title);
        dest.writeParcelable(this.topic, flags);
    }

    protected Poll(Parcel in) {
        this.answers = in.createTypedArrayList(Answer.CREATOR);
        this.id = in.readString();
        this.startDate = in.readString();
        this.title = in.readString();
        this.topic = in.readParcelable(Topic.class.getClassLoader());
    }

    public static final Creator<Poll> CREATOR = new Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel source) {
            return new Poll(source);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
}
