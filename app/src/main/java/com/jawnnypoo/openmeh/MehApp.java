package com.jawnnypoo.openmeh;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.jawnnypoo.openmeh.util.CrashlyticsTree;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Meh
 * Created by Jawn on 4/20/2015.
 */
public class MehApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsTree());
        }
    }

}
