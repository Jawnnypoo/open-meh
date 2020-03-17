package com.jawnnypoo.openmeh.worker

import android.content.Context
import androidx.work.*
import com.jawnnypoo.openmeh.util.MehNotificationManager
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class ReminderWorker(
        private val context: Context,
        private val workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val TAG = "Reminder"

        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"

        suspend fun schedule(context: Context, hour: Int, minute: Int, isRepeatSchedule: Boolean = false) {
            val data = Data.Builder()
                    .putInt(KEY_HOUR, hour)
                    .putInt(KEY_MINUTE, minute)
                    .build()

            val now = LocalDateTime.now()
            var timeToNotify = now
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(0)
            if (isRepeatSchedule || timeToNotify.isBefore(now)) {
                timeToNotify = timeToNotify.plusDays(1)
            }
            val delay = now.until(timeToNotify, ChronoUnit.MILLIS)

            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(TAG).await()

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInputData(data)
                    .addTag(TAG)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()

            workManager.enqueue(request)
        }

        suspend fun cancel(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(TAG)
                    .await()
        }
    }

    override fun doWork(): Result {
        val data = workerParams.inputData
        val hour = data.getInt(KEY_HOUR, -1)
        val minute = data.getInt(KEY_HOUR, -1)
        MehNotificationManager.postDailyNotification(context)
        lau
        schedule(context, hour, minute, true)
        return Result.success()
    }
}