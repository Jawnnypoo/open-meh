package com.commit451.repeater

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object Repeater {

    internal const val KEY_TAG = "tag"

    private var callback: ((id: String) -> Unit)? = null

    fun init(context: Context, callback: (id: String) -> Unit) {
        RepeaterPrefs.init(context)
        this.callback = callback
    }

    fun schedule(context: Context, configuration: RepeaterConfiguration) {

        cancel(context, configuration.tag)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createIntent(context, configuration.tag)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, configuration.hour)
            set(Calendar.MINUTE, configuration.minute)
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        )
        RepeaterPrefs.store(configuration)
    }

    fun cancel(context: Context, tag: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createIntent(context, tag))
        RepeaterPrefs.remove(tag)
    }

    fun config(tag: String): RepeaterConfiguration {
        return RepeaterPrefs.config(tag)
    }

    internal fun triggerCallback(tag: String) {
        callback?.invoke(tag)
    }

    private fun createIntent(context: Context, tag: String): PendingIntent {
        return Intent(context, RepeaterAlarmReceiver::class.java).let { intent ->
            intent.putExtra(KEY_TAG, tag)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }
}
