package com.jawnnypoo.openmeh.shared.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * The entire deal!
 */
@Parcel
public class Deal {

    String features;
    String id;
    List<Item> items;
    ArrayList<String> photos;
    String title;
    String soldOutAt; //null if not sold out
    String specifications;
    Story story;
    Theme theme;
    Topic topic;
    String url;

    public Deal(){}

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public String getTitle() {
        return title;
    }

    public String getSoldOutAt() {
        return soldOutAt;
    }

    public String getSpecifications() {
        return specifications;
    }

    public Story getStory() {
        return story;
    }

    public Theme getTheme() {
        return theme;
    }

    public Topic getTopic() { return topic; }

    public String getUrl() {
        return url;
    }
}
