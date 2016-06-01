package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The entire deal!
 */
public class Deal implements Parcelable {
    private static NumberFormat PRICE_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
    private static final String PATH_CHECKOUT = "/checkout";

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.features);
        dest.writeString(this.id);
        dest.writeTypedList(this.items);
        dest.writeStringList(this.photos);
        dest.writeString(this.title);
        dest.writeString(this.soldOutAt);
        dest.writeString(this.specifications);
        dest.writeParcelable(this.story, flags);
        dest.writeParcelable(this.theme, flags);
        dest.writeParcelable(this.topic, flags);
        dest.writeString(this.url);
    }

    protected Deal(Parcel in) {
        this.features = in.readString();
        this.id = in.readString();
        this.items = in.createTypedArrayList(Item.CREATOR);
        this.photos = in.createStringArrayList();
        this.title = in.readString();
        this.soldOutAt = in.readString();
        this.specifications = in.readString();
        this.story = in.readParcelable(Story.class.getClassLoader());
        this.theme = in.readParcelable(Theme.class.getClassLoader());
        this.topic = in.readParcelable(Topic.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Creator<Deal> CREATOR = new Creator<Deal>() {
        @Override
        public Deal createFromParcel(Parcel source) {
            return new Deal(source);
        }

        @Override
        public Deal[] newArray(int size) {
            return new Deal[size];
        }
    };
}
