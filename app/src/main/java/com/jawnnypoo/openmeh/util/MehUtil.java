package com.jawnnypoo.openmeh.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * This should be better named or something... meh
 */
public class MehUtil {

    public static boolean isYouTubeInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.google.android.youtube", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Map<String, String> getQueryMap(String url)
    {
        String query = Uri.parse(url).getQuery();
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    /**
     * Extracts the YouTube video id from a YouTube video. Kinda clunky
     * @param videoUrl url to YouTube video
     * @return YouTube video id, for use with YouTube API
     */
    public static String getYouTubeIdFromUrl(String videoUrl) {
        if (videoUrl.contains("youtube")) {
            return MehUtil.getQueryMap(videoUrl).get("v");
        } else if (videoUrl.contains("youtu.be")) {
            return videoUrl.substring(videoUrl.indexOf(".be/")+".be/".length());
        }
        return null;
    }
}
