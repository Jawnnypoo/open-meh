package com.jawnnypoo.openmeh.util;

import android.content.res.ColorStateList;

/**
 * Created by John on 4/20/2015.
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
