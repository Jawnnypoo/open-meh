package com.jawnnypoo.openmeh.job

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.MehNotificationManager
import timber.log.Timber
import java.lang.Exception

/**
 * Just to test reminders posting as a job
 */
class ReminderTestJob : Job() {

    companion object {
        const val TAG = "ReminderTestJob"

        fun scheduleNow() {
            JobRequest.Builder(TAG)
                    .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .startNow()
                    .build()
                    .schedule()
        }
    }

    override fun onRunJob(params: Params): Result {
        var response: MehResponse? = null
        try {
            response = App.get().meh.getMeh()
                    .blockingGet()
        } catch (error: Exception) {
            Timber.e(error)
        }

        if (response?.deal == null) {
            Timber.e("Response was null or deal was null. Will not notify")
            return Result.FAILURE
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
        return Result.SUCCESS
    }

}