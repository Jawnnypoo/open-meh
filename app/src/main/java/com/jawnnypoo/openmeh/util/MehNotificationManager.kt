package com.jawnnypoo.openmeh.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.activity.MehActivity
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.shared.response.MehResponse

/**
 * Manages notification stuff
 */
object MehNotificationManager {

    private const val UNIQUE_ID = 42

    fun postDailyNotification(context: Context, response: MehResponse, icon: Bitmap?) {
        postIt(context, response, icon)
    }

    private fun postIt(context: Context, response: MehResponse, icon: Bitmap?) {
        val deal = response.deal
        val theme = deal?.theme
        if (deal != null) {
            val color = theme?.safeBackgroundColor() ?: Color.WHITE
            val priceString = if (deal.isSoldOut())
                context.getString(R.string.sold_out)
            else
                deal.getPriceRange()
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

            if (Prefs.getNotificationSound(context)) {
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            }
            if (Prefs.getNotificationVibrate(context)) {
                notificationBuilder.setVibrate(longArrayOf(100, 200, 100, 200))
            }

            if (Build.VERSION.SDK_INT >= 21) {
                notificationBuilder.setSmallIcon(if (theme?.foreground == Theme.FOREGROUND_LIGHT)
                    R.drawable.ic_meh_black
                else
                    R.drawable.ic_meh)
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
            }

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
}
