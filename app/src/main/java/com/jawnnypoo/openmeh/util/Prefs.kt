package com.jawnnypoo.openmeh.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manager to manage access to all of the preferences in the app
 */
object Prefs {

    const val SHARED_PREFS = "meh_shared_prefs"
    private const val SHARED_PREFS_NOTIFICATION_STATUS = "notification_status"
    private const val SHARED_PREFS_NOTIFICATION_HOUR = "notification_hour"
    private const val SHARED_PREFS_NOTIFICATION_MINUTE = "notification_minute"
    private const val SHARED_PREFS_NOTIFICATION_VIBRATE = "notification_vibrate"
    private const val SHARED_PREFS_NOTIFICATION_SOUND = "notification_sound"

    private const val DEFAULT_HOUR = 18
    private const val DEFAULT_MINUTE = 30

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun setNotificationPreferenceHour(context: Context, hour: Int) {
        getSharedPreferences(context).edit().putInt(SHARED_PREFS_NOTIFICATION_HOUR, hour).apply()
    }

    fun getNotificationPreferenceHour(context: Context): Int {
        return getSharedPreferences(context).getInt(SHARED_PREFS_NOTIFICATION_HOUR, DEFAULT_HOUR)
    }

    fun setNotificationPreferenceMinute(context: Context, minute: Int) {
        getSharedPreferences(context).edit().putInt(SHARED_PREFS_NOTIFICATION_MINUTE, minute).apply()
    }

    fun getNotificationPreferenceMinute(context: Context): Int {
        return getSharedPreferences(context).getInt(SHARED_PREFS_NOTIFICATION_MINUTE, DEFAULT_MINUTE)
    }

    fun getNotificationsPreference(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_STATUS, false)
    }

    fun setNotificationsPreference(context: Context, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREFS_NOTIFICATION_STATUS, value).apply()
    }

    fun setNotificationsPreference(context: Context, value: Boolean, hour: Int, minute: Int) {
        setNotificationsPreference(context, value)
        val editor = getSharedPreferences(context).edit()
        if (hour > 0 && minute > 0) {
            editor.putInt(SHARED_PREFS_NOTIFICATION_HOUR, hour)
            editor.putInt(SHARED_PREFS_NOTIFICATION_MINUTE, minute)
        }
        editor.apply()
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

