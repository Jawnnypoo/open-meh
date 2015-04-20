package com.jawnnypoo.bleh.data;

import java.util.List;

/**
 * Created by John on 4/17/2015.
 */
public class Deal {
    String features;
    String id;
    List<Item> items;
    List<String> photos;
    String title;
    String soldOutAt; //date
    String specifications;
    Theme theme;
    String url;

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

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSoldOutAt() {
        return soldOutAt;
    }

    public void setSoldOutAt(String soldOutAt) {
        this.soldOutAt = soldOutAt;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSoldOut() {
        //TODO look at date and see if it is sold out
        return false;
    }
}
