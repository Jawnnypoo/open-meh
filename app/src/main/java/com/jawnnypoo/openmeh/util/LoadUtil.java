package com.jawnnypoo.openmeh.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by John on 4/23/2015.
 */
public class LoadUtil {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
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
