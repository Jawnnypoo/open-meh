package com.jawnnypoo.bleh.data;

import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by John on 4/17/2015.
 */
public class Deal {
    private static final String CURRENCY_SYMBOL = "$";
    String features;
    String id;
    List<Item> items;
    List<String> photos;
    String title;
    String soldOutAt; //null if not sold out
    String specifications;
    Story story;
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

    public List<String> getPhotos() {
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

    public Theme getTheme() {
        return theme;
    }

    public String getUrl() {
        return url;
    }


    //Our stuff
    public boolean isSoldOut() {
        return !TextUtils.isEmpty(soldOutAt);
    }
    public String getPriceRange() {
        if (items.size() == 1) {
            return CURRENCY_SYMBOL + items.get(0).getPrice();
        }
        Collections.sort(items);
        return CURRENCY_SYMBOL + items.get(0).getPrice() + "-" + items.get(items.size()-1).getPrice();
    }
}
