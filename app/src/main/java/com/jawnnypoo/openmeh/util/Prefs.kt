package com.jawnnypoo.openmeh.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager to manage access to all of the preferences in the app
 */
object Prefs {

    private const val SHARED_PREFS = "meh_shared_prefs"
    private const val SHARED_PREFS_NOTIFICATION_VIBRATE = "notification_vibrate"
    private const val SHARED_PREFS_NOTIFICATION_SOUND = "notification_sound"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun getNotificationSound(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_SOUND, false)
    }

    fun setNotificationSound(context: Context, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREFS_NOTIFICATION_SOUND, value).apply()
    }

    fun getNotificationVibrate(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_VIBRATE, false)
    }

    fun setNotificationVibrate(context: Context, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREFS_NOTIFICATION_VIBRATE, value).apply()
    }
}

