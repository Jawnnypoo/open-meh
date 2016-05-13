package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * I dunno...
 */
public class Attribute implements Parcelable {

    String key;
    String value;

    public Attribute(){}

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    protected Attribute(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Creator<Attribute> CREATOR = new Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel source) {
            return new Attribute(source);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };
}
