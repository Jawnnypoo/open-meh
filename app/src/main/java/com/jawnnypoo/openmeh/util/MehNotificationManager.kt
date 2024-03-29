package com.jawnnypoo.openmeh.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import coil.Coil
import coil.request.ImageRequest
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.activity.MehActivity
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.shared.response.MehResponse
import timber.log.Timber

/**
 * Manages notification stuff
 */
object MehNotificationManager {

    private const val UNIQUE_ID = 42

    /**
     * Fetches the daily deal and posts a notification about it. If it fails to load the deal, it
     * will post a notification prompting the user to try again.
     */
    suspend fun postDailyNotification(context: Context) {
        var response: MehResponse? = null
        try {
            response = App.get().meh.meh()
        } catch (e: Exception) {
            Timber.e(e)
        }

        val deal = response?.deal
        var icon: Bitmap? = null
        try {
            val url = deal?.photos?.firstOrNull()
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            if (url != null) {
                val result = Coil.imageLoader(context).execute(request)
                icon = (result.drawable as? BitmapDrawable)?.bitmap
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        Timber.d("Posting notification")
        postIt(context, response, icon)
    }

    private fun postIt(context: Context, response: MehResponse?, icon: Bitmap?) {
        val deal = response?.deal
        val theme = ParsedTheme.fromTheme(deal?.theme)
        val color = theme?.safeBackgroundColor() ?: Color.WHITE
        val priceString = if (deal?.isSoldOut() == true) {
            context.getString(R.string.sold_out)
        } else {
            deal?.getPriceRange()
        }
        val notificationBuilder = NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_reminders)
        )
            .setContentTitle(deal?.title ?: context.getString(R.string.unable_to_load_deal))
            .setTicker(priceString ?: context.getString(R.string.tap_to_load_again))
            .setContentText(priceString ?: context.getString(R.string.tap_to_load_again))
            .setColor(color)
            .setAutoCancel(true)
        if (icon != null) {
            notificationBuilder.setLargeIcon(icon)
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .setBigContentTitle(deal?.title)
                    .setSummaryText(priceString)
            )
        }

        if (Prefs.isNotificationSound) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        if (Prefs.isNotificationVibrate) {
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
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(resultPendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(UNIQUE_ID, notificationBuilder.build())
    }
}
