package com.jawnnypoo.openmeh.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager to manage access to all of the preferences in the app
 */
object Prefs {

    private const val SHARED_PREFS = "meh_shared_prefs"
    private const val KEY_VIBRATE = "notification_vibrate"
    private const val KEY_SOUND = "notification_sound"
    private const val KEY_ENABLED = "notification_enabled"
    private const val KEY_HOUR = "notification_hour"
    private const val KEY_MINUTE = "notification_minute"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    var isNotificationSound: Boolean
        get() = sharedPreferences
                .getBoolean(KEY_SOUND, false)
        set(value) {
            sharedPreferences.edit()
                    .putBoolean(KEY_SOUND, value)
                    .apply()
        }

    var isNotificationVibrate: Boolean
        get() = sharedPreferences
                .getBoolean(KEY_VIBRATE, false)
        set(value) {
            sharedPreferences.edit()
                    .putBoolean(KEY_VIBRATE, value)
                    .apply()
        }

    var notificationHour: Int
        get() = sharedPreferences
                .getInt(KEY_HOUR, 18)
        set(value) {
            sharedPreferences.edit()
                    .putInt(KEY_HOUR, value)
                    .apply()
        }

    var notificationMinute: Int
        get() = sharedPreferences
                .getInt(KEY_MINUTE, 30)
        set(value) {
            sharedPreferences.edit()
                    .putInt(KEY_MINUTE, value)
                    .apply()
        }

    var areNotificationsEnabled: Boolean
        get() = sharedPreferences
                .getBoolean(KEY_ENABLED, false)
        set(removed) {
            sharedPreferences.edit()
                    .putBoolean(KEY_ENABLED, removed)
                    .apply()
        }
}

