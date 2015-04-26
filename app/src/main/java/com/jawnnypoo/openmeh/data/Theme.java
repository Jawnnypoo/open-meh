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
        return safeParseColor(accentColor);
    }

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
}
