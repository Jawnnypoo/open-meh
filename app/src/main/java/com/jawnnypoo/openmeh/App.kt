package com.jawnnypoo.openmeh

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.commit451.firebaseshim.FirebaseShim
import com.commit451.repeater.Repeater
import com.crashlytics.android.Crashlytics
import com.jawnnypoo.openmeh.github.GitHubClient
import com.jawnnypoo.openmeh.shared.api.MehClient
import com.jawnnypoo.openmeh.util.MehNotificationManager
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Meh
 */
class App : Application() {

    companion object {
        private lateinit var instance: App

        fun get(): App {
            return instance
        }
    }

    lateinit var meh: MehClient

    override fun onCreate() {
        super.onCreate()

        instance = this
        FirebaseShim.init(this, BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Repeater.init(this) {
            when(it) {
                TAG_REMINDER -> {
                    MehNotificationManager.postDailyNotification(this)
                }
            }
        }
        meh = MehClient(BuildConfig.MEH_API_KEY, BuildConfig.DEBUG)
        GitHubClient.init()
        SimpleChromeCustomTabs.initialize(this)

        setupNotificationChannelsIfNeeded(this)
    }

    private fun setupNotificationChannelsIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelName = "Reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(context.getString(R.string.notification_channel_reminders), channelName, importance)
            notificationChannel.description = "Reminders of the deal of the day"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
