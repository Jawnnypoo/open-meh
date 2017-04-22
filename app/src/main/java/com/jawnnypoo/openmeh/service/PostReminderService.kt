package com.jawnnypoo.openmeh.service

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.shared.api.MehResponse
import com.jawnnypoo.openmeh.util.MehNotificationManager
import com.jawnnypoo.openmeh.util.Prefs
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.ExecutionException

/**
 * Service that pretty much just posts a notification then goes away
 */
class PostReminderService : IntentService(PostReminderService.TAG) {

    companion object {
        private val TAG = PostReminderService::class.java.simpleName
    }

    override fun onHandleIntent(intent: Intent?) {
        postNotification()
    }

    private fun postNotification() {
        if (!Prefs.getNotificationsPreference(applicationContext)) {
            //Notifications disabled, go away
            return
        }
        var response: MehResponse? = null
        try {
            response = App.get().meh.getMeh()
                    .blockingGet()
        } catch (error: Exception) {
            Timber.e(error)
        }

        if (response == null || response.deal == null) {
            Timber.e("Response was null or deal was null. Will not notify")
            return
        }


        val deal = response.deal
        var icon: Bitmap? = null
        //Shoot for the highest resolution
        //http://graphicdesign.stackexchange.com/questions/15776/issues-with-creating-a-hi-res-large-icon-for-android-notifications-in-jelly-bean
        try {
            icon = Glide.with(applicationContext)
                    .load(deal.photos[0])
                    .asBitmap()
                    .centerCrop()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
        } catch (e: ExecutionException) {
            Timber.e(e)
        } catch (e: InterruptedException) {
            Timber.e(e)
        }

        MehNotificationManager.postDailyNotification(applicationContext, response, icon!!)
    }
}
