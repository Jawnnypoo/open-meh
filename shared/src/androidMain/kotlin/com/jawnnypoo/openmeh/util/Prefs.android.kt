package com.jawnnypoo.openmeh.util

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings

private const val SHARED_PREFS = "meh_shared_prefs"

fun initializePrefs(context: Context) {
    val sharedPreferences = context.applicationContext
        .getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    Prefs.initialize(SharedPreferencesSettings(sharedPreferences))
}
