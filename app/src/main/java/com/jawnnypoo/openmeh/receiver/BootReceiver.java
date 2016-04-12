package com.jawnnypoo.openmeh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jawnnypoo.openmeh.util.MehPreferencesManager;
import com.jawnnypoo.openmeh.util.MehReminderManager;

/**
 * Boot receiver so that we can restore alarms when the phone boots.
 *
 * Keep in mind that the BootReceiver does not work for application
 * installs on external storage
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        restoreAlarms(context);
    }

    private void restoreAlarms(Context context) {
        if (MehPreferencesManager.getNotificationsPreference(context)) {
            MehReminderManager.restoreReminderPreference(context);
        }
    }
}