package com.jawnnypoo.bleh.util;

import android.graphics.Color;
import android.os.Build;
import android.view.Window;

/**
 * Created by John on 4/20/2015.
 */
public class ColorUtil {

    private static float[] hsv = new float[3];

    public static int getDarkerColor(int color) {
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public static void setStatusBarAndNavBarColor(Window window, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }
}
