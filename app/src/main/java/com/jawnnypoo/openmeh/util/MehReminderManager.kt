package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jawnnypoo.openmeh.service.PostReminderService
import java.util.*

/**
 * Manages reminder stuff
 */
object MehReminderManager {

    private fun getReminderServicePendingIntent(context: Context): PendingIntent {
        return PendingIntent.getService(context, 0, Intent(context, PostReminderService::class.java), PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        //First, cancel all previous reminders
        cancelPendingReminders(context)

        val alarmTime = Calendar.getInstance()
        val currentTime = Calendar.getInstance()
        alarmTime.timeInMillis = System.currentTimeMillis()
        currentTime.timeInMillis = System.currentTimeMillis()

        alarmTime.set(Calendar.HOUR_OF_DAY, hour)
        alarmTime.set(Calendar.MINUTE, minute)
        //Add another day if the time has already happened
        if (alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DATE, 1)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC, alarmTime.timeInMillis,
                AlarmManager.INTERVAL_DAY, getReminderServicePendingIntent(context))

        //Repeat ever 10 seconds, for testing
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
        //        TimeUnit.SECONDS.toMillis(10), getReminderServicePendingIntent(context));
    }

    /**
     * Restore the daily reminder, with the times already in preferences. Useful for
     * device reboot or switched on from the settings screen
     * @param context
     */
    fun restoreReminderPreference(context: Context) {
        val hour = Prefs.getNotificationPreferenceHour(context)
        val minute = Prefs.getNotificationPreferenceMinute(context)
        scheduleDailyReminder(context, hour, minute)
    }

    fun cancelPendingReminders(context: Context) {
        val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getReminderServicePendingIntent(context))
    }
}
