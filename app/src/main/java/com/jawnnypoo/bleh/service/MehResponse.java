package com.jawnnypoo.bleh.service;

import com.jawnnypoo.bleh.data.Deal;
import com.jawnnypoo.bleh.data.Poll;
import com.jawnnypoo.bleh.data.Video;

/**
 * Created by John on 4/17/2015.
 */
public class MehResponse {
    Deal deal;
    Poll poll;
    Video video;

    public Deal getDeal() {
        return deal;
    }

    public Poll getPoll() {
        return poll;
    }

    public Video getVideo() {
        return video;
    }

}
