package com.jawnnypoo.openmeh

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.jawnnypoo.openmeh.di.ApiKey
import com.jawnnypoo.openmeh.di.AppGraph
import com.jawnnypoo.openmeh.notification.NotificationChannels
import com.jawnnypoo.openmeh.util.initializeLinker
import com.jawnnypoo.openmeh.util.initializeReminderScheduler
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.createGraphFactory

class OpenMehApplication : Application() {

    /** Root Metro graph; built once in [onCreate] and used by the rest of the app. */
    lateinit var graph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()

        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        setupImageLoader()
        initializeLinker(this)
        initializeReminderScheduler(this)

        val settings = SharedPreferencesSettings(
            delegate = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE),
        )
        graph = createGraphFactory<AppGraph.Factory>().create(
            apiKey = ApiKey(BuildKonfig.MEH_API_KEY),
            isDebug = isDebug,
            settings = settings,
        )

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

    companion object {
        private const val SHARED_PREFS = "meh_shared_prefs"
    }
}
