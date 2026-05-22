package com.jawnnypoo.openmeh.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import co.touchlab.kermit.Logger
import com.jawnnypoo.openmeh.OpenMehApplication
import com.jawnnypoo.openmeh.notification.MehNotificationManager
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {

        private const val TAG = "Reminder"
        private val log = Logger.withTag("ReminderWorker")

        suspend fun schedule(
            context: Context,
            hour: Int,
            minute: Int,
            isRepeatSchedule: Boolean = false,
        ) {
            cancel(context)

            val tz = TimeZone.currentSystemDefault()
            val now = Clock.System.now()
            val nowLocal = now.toLocalDateTime(tz)
            val targetLocal = LocalDateTime(
                year = nowLocal.year,
                month = nowLocal.month,
                day = nowLocal.day,
                hour = hour,
                minute = minute,
                second = 0,
                nanosecond = 0,
            )
            var targetInstant = targetLocal.toInstant(tz)
            if (isRepeatSchedule || targetInstant < now) {
                log.d { "Time is before now (or repeat), adding 1 day" }
                targetInstant = targetInstant.plus(1.days)
            }
            val delaySeconds = (targetInstant - now).inWholeSeconds

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .addTag(TAG)
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .build()
            log.d { "Reminder scheduled for ${targetInstant.toLocalDateTime(tz)}" }

            WorkManager.getInstance(context).enqueue(request)
        }

        suspend fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelAllWorkByTag(TAG)
                .await()
        }
    }

    override suspend fun doWork(): Result {
        // WorkManager constructs Workers itself, so we pull dependencies from the graph via the
        // Application cast. Switching to a WorkManager Configuration with a Metro-backed
        // WorkerFactory would let us @Inject this directly — future cleanup.
        val graph = (applicationContext as OpenMehApplication).graph
        val prefs = graph.prefs

        withContext(Dispatchers.IO) {
            log.d { "Now reminding..." }
            MehNotificationManager.postDailyNotification(
                context = context,
                mehRepository = graph.mehRepository,
                prefs = prefs,
            )
            schedule(
                context = context,
                hour = prefs.notificationHour,
                minute = prefs.notificationMinute,
                isRepeatSchedule = true,
            )
        }
        return Result.success()
    }
}
