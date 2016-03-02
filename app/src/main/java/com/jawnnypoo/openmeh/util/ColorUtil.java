package com.jawnnypoo.openmeh.util;

import android.content.res.ColorStateList;

/**
 * Color util!
 */
public class ColorUtil {

    public static ColorStateList createColorStateList(int color, int pressed) {
        return new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{}
        }, new int[]{
                pressed,
                color
        });
    }
}
