package com.jawnnypoo.bleh.data;

/**
 * Created by John on 4/17/2015.
 */
public class Video {
    String id;
    String startDate;
    String title;
    String url;
    Topic topic;

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
