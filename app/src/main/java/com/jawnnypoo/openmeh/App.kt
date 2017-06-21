package com.jawnnypoo.openmeh

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.jawnnypoo.openmeh.github.GitHubClient
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
    }
}
