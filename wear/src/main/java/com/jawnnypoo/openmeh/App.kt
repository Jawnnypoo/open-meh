package com.jawnnypoo.openmeh

import android.app.Application
import timber.log.Timber

/**
 * App, for all the cool stuff
 */
class App : Application() {

    companion object {

        var instance: App? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
