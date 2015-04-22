package com.jawnnypoo.openmeh;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Jawn on 4/20/2015.
 */
public class MehApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
