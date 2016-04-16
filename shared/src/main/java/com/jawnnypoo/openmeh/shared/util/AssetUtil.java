package com.jawnnypoo.openmeh.shared.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Load from assets
 */
public class AssetUtil {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
