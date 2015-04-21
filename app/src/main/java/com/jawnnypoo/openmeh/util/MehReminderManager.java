package com.jawnnypoo.openmeh.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jawnnypoo.openmeh.services.PostReminderService;

import java.util.Calendar;

/**
 * Created by Jawn on 8/28/2014.
 */
public class MehReminderManager {

    private static final String TAG = MehReminderManager.class.getSimpleName();

    private static PendingIntent getReminderServicePendingIntent(Context context) {
        return PendingIntent.getService(context, 0, new Intent(context, PostReminderService.class), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void scheduleDailyReminder(Context context, int hour, int minute) {
        //First, cancel all previous reminders
        cancelPendingReminders(context);

        Calendar alarmTime = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(System.currentTimeMillis());
        currentTime.setTimeInMillis(System.currentTimeMillis());

        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        //Add another day if the time has already happened
        if (alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DATE, 1);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, alarmTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, getReminderServicePendingIntent(context));

        //Repeat ever 10 seconds, for testing
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
        //        TimeUnit.SECONDS.toMillis(10), getReminderServicePendingIntent(context));
    }

    /**
     * Restore the daily reminder, with the times already in preferences. Useful for
     * device reboot or switched on from the settings screen
     * @param context
     */
    public static void restoreReminderPreference(Context context) {
        int hour = MehPreferencesManager.getNotificationPreferenceHour(context);
        int minute = MehPreferencesManager.getNotificationPreferenceMinute(context);
        scheduleDailyReminder(context, hour, minute);
    }

    public static void cancelPendingReminders(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(getReminderServicePendingIntent(context));
    }
}
