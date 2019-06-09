package com.jawnnypoo.openmeh.job

import android.graphics.Bitmap
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.MehNotificationManager
import timber.log.Timber
import java.util.*

/**
 * Daily jobs seems to be flawed, so this is a daily job that just keeps rescheduling itself.
 */
class ReminderJob : Job() {

    companion object {
        const val TAG = "ReminderJob"

        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"

        fun schedule(hourOfDay: Int, minute: Int) {

            val extras = PersistableBundleCompat()
            extras.putInt(KEY_HOUR, hourOfDay)
            extras.putInt(KEY_MINUTE, minute)

            val now = Calendar.getInstance()

            val at = Calendar.getInstance()
            at.set(Calendar.HOUR_OF_DAY, hourOfDay)
            at.set(Calendar.MINUTE, minute)

            if (at.time.before(now.time)) {
                at.add(Calendar.MINUTE, 24 * 60)
            }

            val exactInMs = at.timeInMillis - now.timeInMillis

            JobRequest.Builder(TAG)
                    .setExact(exactInMs)
                    .build()
                    .schedule()
        }

        fun cancel() {
            JobManager.instance()
                    .cancelAllForTag(TAG)
        }
    }


    override fun onRunJob(params: Params): Result {
        reschedule(params.extras)
        val response: MehResponse
        try {
            response = App.get().meh.getMeh()
                    .blockingGet()
        } catch (error: Exception) {
            Timber.e(error)
            reschedule(params.extras)
            return Result.SUCCESS
        }


        val deal = response.deal
        var icon: Bitmap? = null
        // Shoot for the highest resolution
        // http://graphicdesign.stackexchange.com/questions/15776/issues-with-creating-a-hi-res-large-icon-for-android-notifications-in-jelly-bean
        try {
            icon = Glide.with(context)
                    .asBitmap()
                    .load(deal.photos.firstOrNull())
                    .submit()
                    .get()
        } catch (e: Exception) {
            Timber.e(e)
        }
        Timber.d("Posting daily notification for ${DateFormat.getDateFormat(context).format(Date())}")
        MehNotificationManager.postDailyNotification(context, response, icon)
        return Result.SUCCESS
    }

    private fun reschedule(extras: PersistableBundleCompat) {
        val hourOfDay = extras.getInt(KEY_HOUR, 0)
        val minute = extras.getInt(KEY_MINUTE, 0)
        schedule(hourOfDay, minute)
    }
}