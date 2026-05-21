package com.jawnnypoo.openmeh

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ApplicationInfo
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.jawnnypoo.openmeh.notification.NotificationChannels
import com.jawnnypoo.openmeh.util.initializeLinker
import com.jawnnypoo.openmeh.util.initializePrefs
import com.jawnnypoo.openmeh.util.initializeReminderScheduler

class OpenMehApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        setupImageLoader()
        initializeLinker(this)
        initializePrefs(this)
        initializeReminderScheduler(this)
        Container.initialize(BuildKonfig.MEH_API_KEY, isDebug)
        setupNotificationChannelsIfNeeded()
    }

    private fun setupImageLoader() {
        SingletonImageLoader.setSafe {
            ImageLoader.Builder(this)
                .components {
                    add(KtorNetworkFetcherFactory())
                }
                .build()
        }
    }

    private fun setupNotificationChannelsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                NotificationChannels.REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Reminders of the deal of the day"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
