package com.commit451.repeater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RepeaterAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val tag = intent?.getStringExtra(Repeater.KEY_TAG)
        tag?.let {
            Repeater.triggerCallback(it)
        }
    }
}
