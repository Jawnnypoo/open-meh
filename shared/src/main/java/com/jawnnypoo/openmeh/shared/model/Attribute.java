package com.jawnnypoo.openmeh.shared.model;

import android.os.Parcelable;

/**
 * I dunno...
 */
public class Attribute implements Parcelable {

    public Attribute(){}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    protected Attribute(android.os.Parcel in) {
    }

    public static final Parcelable.Creator<Attribute> CREATOR = new Parcelable.Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(android.os.Parcel source) {
            return new Attribute(source);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };
}
