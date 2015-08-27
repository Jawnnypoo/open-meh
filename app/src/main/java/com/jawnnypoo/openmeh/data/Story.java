package com.jawnnypoo.openmeh.data;

import org.parceler.Parcel;

/**
 * Cool story brah
 * Created by John on 4/17/2015.
 */
@Parcel
public class Story {
    String title;
    String body;

    public Story(){}

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
