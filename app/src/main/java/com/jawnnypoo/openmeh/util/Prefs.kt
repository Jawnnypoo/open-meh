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
    private const val SHARED_PREFS_NOTIFICATION_ENABLED = "notification_enabled"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun getNotificationSound(context: Context): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFS_NOTIFICATION_SOUND, false)
    }

    fun setNotificationSound(context: Context, value: Boolean) {
        sharedPreferences.edit().putBoolean(SHARED_PREFS_NOTIFICATION_SOUND, value).apply()
    }

    fun getNotificationVibrate(context: Context): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFS_NOTIFICATION_VIBRATE, false)
    }

    fun setNotificationVibrate(context: Context, value: Boolean) {
        sharedPreferences.edit().putBoolean(SHARED_PREFS_NOTIFICATION_VIBRATE, value).apply()
    }

    var areNotificationsEnabled: Boolean
        get() = sharedPreferences
                .getBoolean(SHARED_PREFS_NOTIFICATION_ENABLED, false)
        set(removed) {
            sharedPreferences.edit()
                    .putBoolean(SHARED_PREFS_NOTIFICATION_ENABLED, removed)
                    .apply()
        }
}

