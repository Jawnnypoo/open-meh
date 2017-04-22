package com.jawnnypoo.openmeh.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jawnnypoo.openmeh.util.MehReminderManager
import com.jawnnypoo.openmeh.util.Prefs

/**
 * Boot receiver so that we can restore alarms when the phone boots.

 * Keep in mind that the BootReceiver does not work for application
 * installs on external storage
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        restoreAlarms(context)
    }

    private fun restoreAlarms(context: Context) {
        if (Prefs.getNotificationsPreference(context)) {
            MehReminderManager.restoreReminderPreference(context)
        }
    }
}