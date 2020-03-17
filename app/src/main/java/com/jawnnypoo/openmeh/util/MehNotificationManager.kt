package com.jawnnypoo.openmeh.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.activity.MehActivity
import com.jawnnypoo.openmeh.image.CoilCompat
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.shared.response.MehResponse
import timber.log.Timber
import java.util.*

/**
 * Manages notification stuff
 */
object MehNotificationManager {

    private const val UNIQUE_ID = 42

    /**
     * Fetches the daily deal and posts a notification about it
     */
    suspend fun postDailyNotification(context: Context) {
        val response: MehResponse
        try {
            response = App.get().meh.meh()
        } catch (error: Exception) {
            Timber.e(error)
            return
        }


        val deal = response.deal
        var icon: Bitmap? = null
        // Shoot for the highest resolution
        // http://graphicdesign.stackexchange.com/questions/15776/issues-with-creating-a-hi-res-large-icon-for-android-notifications-in-jelly-bean
        try {
            val url = deal.photos.firstOrNull()
            if (url != null) {
                icon = (CoilCompat.getBlocking(url) as? BitmapDrawable)?.bitmap
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        Timber.d("Posting daily notification for ${DateFormat.getDateFormat(context).format(Date())}")
        postIt(context, response, icon)
    }

    private fun postIt(context: Context, response: MehResponse, icon: Bitmap?) {
        val deal = response.deal
        val theme = ParsedTheme.fromTheme(deal.theme)
        val color = theme?.safeBackgroundColor() ?: Color.WHITE
        val priceString = if (deal.isSoldOut()) {
            context.getString(R.string.sold_out)
        } else {
            deal.getPriceRange()
        }
        val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.notification_channel_reminders))
                .setContentTitle(deal.title)
                .setTicker(priceString)
                .setContentText(priceString)
                .setColor(color)
                .setAutoCancel(true)
        if (icon != null) {
            notificationBuilder.setLargeIcon(icon)
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigPicture(icon)
                    .setBigContentTitle(deal.title)
                    .setSummaryText(priceString))
        }

        if (Prefs.getNotificationSound()) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        if (Prefs.getNotificationVibrate()) {
            notificationBuilder.setVibrate(longArrayOf(100, 200, 100, 200))
        }

        notificationBuilder.setSmallIcon(
                if (theme?.foreground == Theme.FOREGROUND_LIGHT)
                    R.drawable.ic_meh_black
                else
                    R.drawable.ic_meh
        )


        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(context, MehActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultPendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(UNIQUE_ID, notificationBuilder.build())
    }
}
