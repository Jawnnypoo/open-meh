package com.jawnnypoo.openmeh.shared.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.jawnnypoo.openmeh.shared.model.Deal;
import com.jawnnypoo.openmeh.shared.model.Poll;
import com.jawnnypoo.openmeh.shared.model.Video;

/**
 * The actual response we get when pulling in the meh deal
 */
public class MehResponse implements Parcelable {
    Deal deal;
    Poll poll;
    Video video;

    public MehResponse() {}

    public Deal getDeal() {
        return deal;
    }

    public Poll getPoll() {
        return poll;
    }

    public Video getVideo() {
        return video;
    }

    @Override
    public String toString() {
        return deal.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.deal, flags);
        dest.writeParcelable(this.poll, flags);
        dest.writeParcelable(this.video, flags);
    }

    protected MehResponse(Parcel in) {
        this.deal = in.readParcelable(Deal.class.getClassLoader());
        this.poll = in.readParcelable(Poll.class.getClassLoader());
        this.video = in.readParcelable(Video.class.getClassLoader());
    }

    public static final Parcelable.Creator<MehResponse> CREATOR = new Parcelable.Creator<MehResponse>() {
        @Override
        public MehResponse createFromParcel(Parcel source) {
            return new MehResponse(source);
        }

        @Override
        public MehResponse[] newArray(int size) {
            return new MehResponse[size];
        }
    };
}
