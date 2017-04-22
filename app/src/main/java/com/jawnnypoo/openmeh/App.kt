package com.jawnnypoo.openmeh

import android.app.Application

import com.crashlytics.android.Crashlytics
import com.jawnnypoo.openmeh.util.CrashlyticsTree
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs

import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Meh
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
            Timber.plant(CrashlyticsTree())
        }
        SimpleChromeCustomTabs.initialize(this)
    }
}
