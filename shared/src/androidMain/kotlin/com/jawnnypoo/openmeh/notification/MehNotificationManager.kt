package com.jawnnypoo.openmeh.notification

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
import co.touchlab.kermit.Logger
import coil3.SingletonImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import com.jawnnypoo.openmeh.activity.MehActivity
import com.jawnnypoo.openmeh.data.MehRepository
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.resources.Res
import com.jawnnypoo.openmeh.resources.sold_out
import com.jawnnypoo.openmeh.resources.tap_to_load_again
import com.jawnnypoo.openmeh.resources.unable_to_load_deal
import com.jawnnypoo.openmeh.shared.R
import com.jawnnypoo.openmeh.shared.extension.getPriceRange
import com.jawnnypoo.openmeh.shared.extension.isSoldOut
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.Prefs
import org.jetbrains.compose.resources.getString

object MehNotificationManager {

    private const val UNIQUE_ID = 42
    private val log = Logger.withTag("MehNotificationManager")

    /**
     * Fetches the daily deal and posts a notification about it. If it fails to load the deal, it
     * will post a notification prompting the user to try again.
     *
     * Repositories/prefs are passed in (instead of read from a global) because Workers aren't
     * Metro-injected — see [com.jawnnypoo.openmeh.worker.ReminderWorker] which pulls them from
     * the [com.jawnnypoo.openmeh.di.AppGraph] via the Application cast.
     */
    suspend fun postDailyNotification(
        context: Context,
        mehRepository: MehRepository,
        prefs: Prefs,
    ) {
        var response: MehResponse? = null
        try {
            response = mehRepository.currentDeal()
        } catch (e: Exception) {
            log.e(e) { "Failed to load deal" }
        }

        val deal = response?.deal
        var icon: Bitmap? = null
        try {
            val url = deal?.photos?.firstOrNull()
            if (url != null) {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .build()
                val result = SingletonImageLoader.get(context).execute(request).image
                    ?.asDrawable(resources = context.resources)
                icon = (result as? BitmapDrawable)?.bitmap
            }
        } catch (e: Exception) {
            log.e(e) { "Failed to load notification icon" }
        }
        log.d { "Posting notification" }
        postIt(context, response, icon, prefs)
    }

    private suspend fun postIt(
        context: Context,
        response: MehResponse?,
        icon: Bitmap?,
        prefs: Prefs,
    ) {
        val deal = response?.deal
        val theme = ParsedTheme.fromTheme(deal?.theme)
        val color = theme?.safeBackgroundColor() ?: Color.WHITE
        val priceString = when {
            deal?.isSoldOut() == true -> getString(Res.string.sold_out)
            deal != null -> deal.getPriceRange()
            else -> null
        }
        val notificationBuilder = NotificationCompat.Builder(
            context,
            NotificationChannels.REMINDERS,
        )
            .setContentTitle(deal?.title ?: getString(Res.string.unable_to_load_deal))
            .setTicker(priceString ?: getString(Res.string.tap_to_load_again))
            .setContentText(priceString ?: getString(Res.string.tap_to_load_again))
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

        if (prefs.isNotificationSound) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
        if (prefs.isNotificationVibrate) {
            notificationBuilder.setVibrate(longArrayOf(100, 200, 100, 200))
        }

        notificationBuilder.setSmallIcon(
            if (theme?.foreground == Theme.FOREGROUND_LIGHT) {
                R.drawable.ic_meh_black
            } else {
                R.drawable.ic_meh
            }
        )

        val resultIntent = Intent(context, MehActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(resultPendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(UNIQUE_ID, notificationBuilder.build())
    }
}
