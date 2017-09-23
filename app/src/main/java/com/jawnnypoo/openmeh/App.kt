package com.jawnnypoo.openmeh

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.crashlytics.android.Crashlytics
import com.evernote.android.job.JobManager
import com.jawnnypoo.openmeh.github.GitHubClient
import com.jawnnypoo.openmeh.job.MehJobCreator
import com.jawnnypoo.openmeh.shared.api.MehClient
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * MehService
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
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
        }
        meh = MehClient.create(BuildConfig.OPEN_MEH_MEH_API_KEY, BuildConfig.DEBUG)
        GitHubClient.init()
        SimpleChromeCustomTabs.initialize(this)

        setupNotificationChannelsIfNeeded(this)

        JobManager.create(this)
                .addJobCreator(MehJobCreator())
    }

    fun setupNotificationChannelsIfNeeded(context: Context) {
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
