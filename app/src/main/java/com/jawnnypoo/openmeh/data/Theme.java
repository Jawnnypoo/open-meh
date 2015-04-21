package com.jawnnypoo.openmeh.data;

import android.graphics.Color;

/**
 * Created by John on 4/17/2015.
 */
public class Theme {

    public static final int FOREGROUND_DARK = 0;
    public static final int FOREGROUND_LIGHT = 1;

    String accentColor;
    String foreground;
    String backgroundColor;
    String backgroundImage;

    public int getAccentColor() {
        return Color.parseColor(accentColor);
    }

    public int getForeground() {
        return foreground.equals("light") ? FOREGROUND_LIGHT :
                FOREGROUND_DARK;
    }

    public int getBackgroundColor() {
        return Color.parseColor(backgroundColor);
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }
}
