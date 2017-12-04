package com.jawnnypoo.openmeh.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import java.util.*

/**
 * This should be better named or something... meh
 */
object MehUtil {

    fun isYouTubeInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo("com.google.android.youtube", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getQueryMap(url: String): Map<String, String> {
        val query = Uri.parse(url).query
        val params = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val map = HashMap<String, String>()
        for (param in params) {
            val name = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val value = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            map.put(name, value)
        }
        return map
    }

    /**
     * Extracts the YouTube video id from a YouTube video. Kinda clunky

     * @param videoUrl url to YouTube video
     * *
     * @return YouTube video id, for use with YouTube API
     */
    fun getYouTubeIdFromUrl(videoUrl: String): String? {
        if (videoUrl.contains("youtube")) {
            return MehUtil.getQueryMap(videoUrl)["v"]
        } else if (videoUrl.contains("youtu.be")) {
            return videoUrl.substring(videoUrl.indexOf(".be/") + ".be/".length)
        }
        return null
    }
}
