package com.jawnnypoo.openmeh.data;

import android.text.TextUtils;

import org.parceler.Parcel;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The entire deal!
 */
@Parcel
public class Deal {
    private static NumberFormat PRICE_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
    private static final String PATH_CHECKOUT = "/checkout";
    String features;
    String id;
    List<Item> items;
    List<String> photos;
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


    //Our stuff
    public boolean isSoldOut() {
        return !TextUtils.isEmpty(soldOutAt);
    }
    public String getPriceRange() {
        if (items.size() == 1) {
            return PRICE_FORMATTER.format(items.get(0).getPrice());
        }
        Collections.sort(items);
        Float lowestPrice = items.get(0).getPrice();
        Float highestPrice = items.get(items.size()-1).getPrice();
        //Same price between highest and lowest, just show the one price
        if (lowestPrice.equals(highestPrice)) {
            return PRICE_FORMATTER.format(items.get(0).getPrice());
        }
        return PRICE_FORMATTER.format(items.get(0).getPrice()) + "-"
                + PRICE_FORMATTER.format(items.get(items.size()-1).getPrice());
    }

    @Override
    public String toString() {
        return id + ":" + title;
    }

    public String getCheckoutUrl() {
        return url + PATH_CHECKOUT;
    }
}
