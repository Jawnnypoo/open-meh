package com.commit451.repeater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RepeaterBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            RepeaterPrefs.configs().forEach {
                Repeater.schedule(context, it)
            }
        }
    }
}
