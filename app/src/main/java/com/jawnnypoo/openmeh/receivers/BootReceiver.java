package com.jawnnypoo.openmeh.receivers;

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
 * Created by john.carlson on 8/19/14.
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