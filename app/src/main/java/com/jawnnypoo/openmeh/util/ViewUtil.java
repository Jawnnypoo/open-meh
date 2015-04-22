package com.jawnnypoo.openmeh.util;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Jawn on 4/21/2015.
 */
public class ViewUtil {

    /**
     * Pass in a runnable to run before the view starts drawing
     * @param view
     * @param runnable
     */
    public static void onPreDraw(final View view, final Runnable runnable) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                view.post(runnable);
                //Dont draw this time, since we are posting the runnable
                return false;
            }
        });
    }
}
