package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * An item of the deal
 */
public class Item implements Comparable<Item>,Parcelable {

    List<Attribute> attributes;
    String condition;
    String id;
    Float price;
    String photo;

    public Item(){}

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeTypedList(attributes);
        dest.writeString(this.condition);
        dest.writeString(this.id);
        dest.writeValue(this.price);
        dest.writeString(this.photo);
    }

    protected Item(android.os.Parcel in) {
        this.attributes = in.createTypedArrayList(Attribute.CREATOR);
        this.condition = in.readString();
        this.id = in.readString();
        this.price = (Float) in.readValue(Float.class.getClassLoader());
        this.photo = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(android.os.Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
