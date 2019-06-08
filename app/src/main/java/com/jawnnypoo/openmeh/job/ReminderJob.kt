package com.jawnnypoo.openmeh.job

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.evernote.android.job.DailyJob
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.MehNotificationManager
import com.jawnnypoo.openmeh.util.Prefs
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit

class ReminderJob : DailyJob() {

    companion object {
        const val TAG = "ReminderJob"

        fun schedule(hourOfDay: Int, minuteOfDay: Int) {
            cancel()

            val builder = JobRequest.Builder(TAG)
                    .setRequiredNetworkType(JobRequest.NetworkType.ANY)

            val timeInMinutes = hourOfDay * 60 + minuteOfDay
            //give it 10 minutes
            val withinInterval = 10
            // run job between user set time and 10 minutes from then
            schedule(builder, TimeUnit.MINUTES.toMillis(timeInMinutes.toLong()), TimeUnit.MINUTES.toMillis(timeInMinutes.toLong() + withinInterval))
        }

        fun scheduleNow() {
            JobRequest.Builder(TAG)
                    .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .startNow()
                    .build()
                    .schedule()
        }

        fun cancel() {
            val currentJobs = JobManager.instance().getAllJobsForTag(TAG)
            if (currentJobs.isNotEmpty()) {
                currentJobs.forEach { it.cancel() }
            }
        }
    }

    override fun onRunDailyJob(params: Params): DailyJobResult {
        if (!Prefs.getNotificationsPreference(context)) {
            //Notifications disabled, go away
            return DailyJobResult.CANCEL
        }

        var response: MehResponse? = null
        try {
            response = App.get().meh.getMeh()
                    .blockingGet()
        } catch (error: Exception) {
            Timber.e(error)
        }

        if (response?.deal == null) {
            Timber.e("Response was null or deal was null. Will not notify")
            return DailyJobResult.SUCCESS
        }


        val deal = response.deal
        var icon: Bitmap? = null
        //Shoot for the highest resolution
        //http://graphicdesign.stackexchange.com/questions/15776/issues-with-creating-a-hi-res-large-icon-for-android-notifications-in-jelly-bean
        try {
            icon = Glide.with(context)
                    .asBitmap()
                    .load(deal?.photos?.firstOrNull())
                    .submit()
                    .get()
        } catch (e: Exception) {
            Timber.e(e)
        }
        MehNotificationManager.postDailyNotification(context, response, icon)
        return DailyJobResult.SUCCESS
    }
}