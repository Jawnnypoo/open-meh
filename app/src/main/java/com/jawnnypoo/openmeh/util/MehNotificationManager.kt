package com.jawnnypoo.openmeh.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder

import com.jawnnypoo.openmeh.activity.MehActivity
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.shared.api.MehResponse
import com.jawnnypoo.openmeh.shared.model.Deal
import com.jawnnypoo.openmeh.shared.model.Theme

/**
 * Manages notification stuff
 */
object MehNotificationManager {

    private val UNIQUE_ID = 42

    fun postDailyNotification(context: Context, response: MehResponse, icon: Bitmap) {
        postIt(context, response, icon)
    }

    private fun postIt(context: Context, response: MehResponse, icon: Bitmap?) {
        val deal = response.deal
        val theme = deal.theme
        val priceString = if (deal.isSoldOut)
            context.getString(R.string.sold_out)
        else
            deal.priceRange
        val notificationBuilder = NotificationCompat.Builder(context)
                .setContentTitle(deal.title)
                .setTicker(deal.priceRange)
                .setContentText(priceString)
                .setColor(theme.backgroundColor)
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
            notificationBuilder.setSmallIcon(if (theme.foreground == Theme.FOREGROUND_DARK)
                R.drawable.ic_meh_black
            else
                R.drawable.ic_meh)
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }

        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(context, MehActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        // Adds the back stack
        //stackBuilder.addParentStack(JournalActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(UNIQUE_ID, notificationBuilder.build())
    }

    fun removeAllNotifications(context: Context) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancelAll()
    }
}
