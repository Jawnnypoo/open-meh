package com.jawnnypoo.openmeh.shared;

import org.parceler.Parcel;

/**
 * The video of the day
 */
@Parcel
public class Video {
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
}
