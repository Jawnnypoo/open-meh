package com.jawnnypoo.openmeh.shared.model;

import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Pretty cool theme. Tells us all about the colors we want to show
 */
public class Theme implements Parcelable {

    public static final int FOREGROUND_DARK = 0;
    public static final int FOREGROUND_LIGHT = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FOREGROUND_LIGHT, FOREGROUND_DARK})
    public @interface Foreground {}

    String accentColor;
    String foreground;
    String backgroundColor;
    String backgroundImage;

    public Theme(){}

    public int getAccentColor() {
        return safeParseColor(accentColor);
    }

    @Foreground
    public int getForeground() {
        return foreground.equals("light") ? FOREGROUND_LIGHT :
                FOREGROUND_DARK;
    }

    public int getForegroundColor() {
        return getForeground() == FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
    }

    public int getBackgroundColor() {
        return safeParseColor(backgroundColor);
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    private static final String EXPECTED_FORMAT = "#FFFFFF";

    private static int safeParseColor(String color) {
        if (color.length() == EXPECTED_FORMAT.length()) {
            return Color.parseColor(color);
        } else {
            //This seems kinda terrible...
            String rrggbb = "#";
            for(int i=1;i<color.length();i++) {
                rrggbb += (color.charAt(i) + "" + color.charAt(i));
            }
            return Color.parseColor(rrggbb);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(this.accentColor);
        dest.writeString(this.foreground);
        dest.writeString(this.backgroundColor);
        dest.writeString(this.backgroundImage);
    }

    protected Theme(android.os.Parcel in) {
        this.accentColor = in.readString();
        this.foreground = in.readString();
        this.backgroundColor = in.readString();
        this.backgroundImage = in.readString();
    }

    public static final Parcelable.Creator<Theme> CREATOR = new Parcelable.Creator<Theme>() {
        @Override
        public Theme createFromParcel(android.os.Parcel source) {
            return new Theme(source);
        }

        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };
}
