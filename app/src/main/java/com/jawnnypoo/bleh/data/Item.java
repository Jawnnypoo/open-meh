package com.jawnnypoo.bleh.data;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by John on 4/17/2015.
 */
public class Item implements Comparable<Item>{

    List<Attribute> attributes;
    String condition;
    String id;
    Float price;
    String photo;

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public String getCondition() {
        return condition;
    }

    public String getId() {
        return id;
    }

    public Float getPrice() {
        return price;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public int compareTo(@NonNull Item another) {
        return Float.compare(this.getPrice(), another.getPrice());
    }
}
