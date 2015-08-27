package com.jawnnypoo.openmeh.data;

import org.parceler.Parcel;

/**
 * Topic of discussion for the day
 * Created by John on 4/17/2015.
 */
@Parcel
public class Topic {
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
}
