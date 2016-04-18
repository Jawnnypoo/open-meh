package com.jawnnypoo.openmeh.util;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * holds images
 */
public class ImageCache {

    private static HashMap<String, Bitmap> sImageCache = new HashMap<>();

    public static void putImage(String key, Bitmap bitmap) {
        sImageCache.put(key, bitmap);
    }

    @Nullable
    public static Bitmap getImage(String key) {
        return sImageCache.get(key);
    }
}
