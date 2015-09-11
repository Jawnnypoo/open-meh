package com.jawnnypoo.openmeh.util;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Tree that prints to Crashlytics
 * Created by Jawn on 9/10/2015.
 */
public class CrashlyticsTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        Crashlytics.log(message);
    }

}
