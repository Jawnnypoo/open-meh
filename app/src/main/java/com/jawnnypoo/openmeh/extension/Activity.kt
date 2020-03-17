package com.jawnnypoo.openmeh.extension

import android.app.Activity
import android.os.Build
import android.view.View

fun Activity.lightStatusBar(enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val view = window.decorView
        var flags: Int = view.systemUiVisibility
        flags = if (enabled) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        view.systemUiVisibility = flags
    }
}