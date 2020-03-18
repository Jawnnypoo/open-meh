package com.jawnnypoo.openmeh.worker

import android.content.Context
import androidx.work.*
import com.jawnnypoo.openmeh.util.MehNotificationManager
import com.jawnnypoo.openmeh.util.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReminderWorker(
        private val context: Context,
        workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {

        private const val TAG = "Reminder"

        suspend fun schedule(context: Context, hour: Int, minute: Int, isRepeatSchedule: Boolean = false) {

            cancel(context)

            val now = LocalDateTime.now()
            var timeToNotify = now
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(0)
            if (isRepeatSchedule || timeToNotify.isBefore(now)) {
                Timber.d("Time is before now, adding 1 day")
                timeToNotify = timeToNotify.plusDays(1)
            }
            val delay = now.until(timeToNotify, ChronoUnit.SECONDS)

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .addTag(TAG)
                    .setInitialDelay(delay, TimeUnit.SECONDS)
                    .build()
            Timber.d("Reminder scheduled for $timeToNotify")

            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(request)
        }

        suspend fun cancel(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(TAG)
                    .await()
        }
    }

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            Timber.d("Now reminding...")
            MehNotificationManager.postDailyNotification(context)
            val hour = Prefs.notificationHour
            val minute = Prefs.notificationMinute
            schedule(context, hour, minute, true)
        }
        return Result.success()
    }
}