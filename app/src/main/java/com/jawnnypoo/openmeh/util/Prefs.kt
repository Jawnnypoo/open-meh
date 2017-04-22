package com.jawnnypoo.openmeh.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.jawnnypoo.openmeh.service.PostReminderService
import java.util.*

/**
 * Manager to manage access to all of the preferences in the app
 */
object Prefs {

    val SHARED_PREFS = "meh_shared_prefs"
    private val SHARED_PREFS_NOTIFICATION_STATUS = "notification_status"
    private val SHARED_PREFS_NOTIFICATION_HOUR = "notification_hour"
    private val SHARED_PREFS_NOTIFICATION_MINUTE = "notification_minute"
    private val SHARED_PREFS_NOTIFICATION_VIBRATE = "notification_vibrate"
    private val SHARED_PREFS_NOTIFICATION_SOUND = "notification_sound"

    private val DEFAULT_HOUR = 18
    private val DEFAULT_MINUTE = 30

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun turnOnDefaultReminder(context: Context) {
        val myIntent = Intent(context, PostReminderService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, myIntent, 0)
        setReminder(context, DEFAULT_HOUR, DEFAULT_MINUTE, pendingIntent)
    }

    fun setReminder(context: Context, hour: Int, minute: Int, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmTime = Calendar.getInstance()
        val currentTime = Calendar.getInstance()
        alarmTime.timeInMillis = System.currentTimeMillis()
        currentTime.timeInMillis = System.currentTimeMillis()

        //Repeat once a day, at a certain time
        alarmTime.set(Calendar.HOUR_OF_DAY, hour)
        alarmTime.set(Calendar.MINUTE, minute)
        //Add another day if the time has already happened
        if (alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DATE, 1)
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC, alarmTime.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

        //Repeat ever 10 seconds, for testing
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, mPendingIntent);
        setNotificationsPreference(context, true, hour, minute)
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

