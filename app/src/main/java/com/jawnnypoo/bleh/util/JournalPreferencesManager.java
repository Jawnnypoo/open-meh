package com.jawnnypoo.bleh.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jawnnypoo.bleh.services.PostReminderService;

import java.util.Calendar;

/**
 * Manager to manage access to all of the preferences in the app
 * Created by Jawn on 8/28/2014.
 */
public class JournalPreferencesManager {

    public static final String SHARED_PREFS = "meh_shared_prefs";
    private static final String SHARED_PREFS_NOTIFICATION_STATUS = "notification_status";
    private static final String SHARED_PREFS_NOTIFICATION_HOUR = "notification_hour";
    private static final String SHARED_PREFS_NOTIFICATION_MINUTE = "notification_minute";
    private static final String SHARED_PREFS_NOTIFICATION_VIBRATE = "notification_vibrate";
    private static final String SHARED_PREFS_NOTIFICATION_SOUND = "notification_sound";

    private static final int DEFAULT_HOUR = 18;
    private static final int DEFAULT_MINUTE = 30;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public static void turnOnDefaultReminder(Context context) {
        Intent myIntent = new Intent(context, PostReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
        setReminder(context, DEFAULT_HOUR, DEFAULT_MINUTE, pendingIntent);
    }

    public static void setReminder(Context context, int hour, int minute, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar alarmTime = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(System.currentTimeMillis());
        currentTime.setTimeInMillis(System.currentTimeMillis());

        //Repeat once a day, at a certain time
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        //Add another day if the time has already happened
        if (alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DATE, 1);
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC, alarmTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        //Repeat ever 10 seconds, for testing
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, mPendingIntent);
        setNotificationsPreference(context, true, hour, minute);
    }

    public static void setNotificationPreferenceHour(Context context, int hour) {
        getSharedPreferences(context).edit().putInt(SHARED_PREFS_NOTIFICATION_HOUR, hour).apply();
    }

    public static int getNotificationPreferenceHour(Context context) {
        return getSharedPreferences(context).getInt(SHARED_PREFS_NOTIFICATION_HOUR, DEFAULT_HOUR);
    }

    public static void setNotificationPreferenceMinute(Context context, int minute) {
        getSharedPreferences(context).edit().putInt(SHARED_PREFS_NOTIFICATION_MINUTE, minute).apply();
    }

    public static int getNotificationPreferenceMinute(Context context) {
        return getSharedPreferences(context).getInt(SHARED_PREFS_NOTIFICATION_MINUTE, DEFAULT_MINUTE);
    }

    public static boolean getNotificationsPreference(Context context) {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_STATUS, true);
    }

    public static void setNotificationsPreference(Context context, boolean value) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREFS_NOTIFICATION_STATUS, value).apply();
    }

    public static void setNotificationsPreference(Context context, boolean value, int hour, int minute) {
        setNotificationsPreference(context, value);
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (hour > 0 && minute > 0) {
            editor.putInt(SHARED_PREFS_NOTIFICATION_HOUR, hour);
            editor.putInt(SHARED_PREFS_NOTIFICATION_MINUTE, minute);
        }
        editor.apply();
    }

    public static boolean getNotificationSound(Context context) {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_SOUND, false);
    }

    public static void setNotificationSound(Context context, boolean value) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREFS_NOTIFICATION_SOUND, value).apply();
    }

    public static boolean getNotificationVibrate(Context context) {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_VIBRATE, false);
    }

    public static void setNotificationVibrate(Context context, boolean value) {
        getSharedPreferences(context).edit().putBoolean(SHARED_PREFS_NOTIFICATION_VIBRATE, value).apply();
    }
}

