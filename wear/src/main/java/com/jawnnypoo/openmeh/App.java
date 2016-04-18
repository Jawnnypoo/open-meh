package com.jawnnypoo.openmeh;

import android.app.Application;

import timber.log.Timber;

/**
 * App, for all the cool stuff
 */
public class App extends Application {

    private static App sInstance;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
