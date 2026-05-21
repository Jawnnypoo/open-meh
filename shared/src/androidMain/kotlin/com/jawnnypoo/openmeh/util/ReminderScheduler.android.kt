package com.jawnnypoo.openmeh.util

import android.content.Context
import com.jawnnypoo.openmeh.worker.ReminderWorker

private lateinit var applicationContext: Context

fun initializeReminderScheduler(context: Context) {
    applicationContext = context.applicationContext
}

actual object ReminderScheduler {
    actual suspend fun schedule(hour: Int, minute: Int) {
        ReminderWorker.schedule(applicationContext, hour, minute)
    }

    actual suspend fun cancel() {
        ReminderWorker.cancel(applicationContext)
    }
}
