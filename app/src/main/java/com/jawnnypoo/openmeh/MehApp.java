package com.jawnnypoo.openmeh;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.jawnnypoo.openmeh.util.CrashlyticsTree;
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Meh
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
        SimpleChromeCustomTabs.initialize(this);
    }

}
