package com.jawnnypoo.openmeh.util

/**
 * Schedules a daily reminder notification at the configured time. Each platform plugs in its own
 * scheduling primitive — Android uses `WorkManager` via `ReminderWorker`; iOS would use
 * `UNUserNotificationCenter`.
 */
expect object ReminderScheduler {
    suspend fun schedule(hour: Int, minute: Int)
    suspend fun cancel()
}
