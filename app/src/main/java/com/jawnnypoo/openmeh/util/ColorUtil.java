package com.jawnnypoo.openmeh.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
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

    //TODO fix this, it does not work :(
    public static void setBackgroundColorStates(View view, int colorNormal, int colorPressed) {
        StateListDrawable states = new StateListDrawable();
        Drawable pressed = view.getBackground().mutate();
        pressed.setColorFilter(colorPressed, PorterDuff.Mode.MULTIPLY);
        Drawable normal = view.getBackground().mutate();
        normal.setColorFilter(colorNormal, PorterDuff.Mode.MULTIPLY);
        states.addState(new int[] {android.R.attr.state_pressed},
                pressed);
        //disabled state
        states.addState(new int[] {-android.R.attr.state_enabled},
                pressed);
        states.addState(new int[] { },
                normal);
        setBackgroundDrawable(view, states);
    }

    public static ColorStateList createColorStateList(int color) {
        return ColorStateList.valueOf(color);
    }

    public static ColorStateList createColorStateList(int color, int pressed) {
        return new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{}
        }, new int[]{
                pressed,
                color
        });
    }

    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }
}
