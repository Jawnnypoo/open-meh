package com.jawnnypoo.openmeh.extension

import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.lightStatusBar(enabled: Boolean) {
    val windowInsetsController = WindowInsetsControllerCompat(window, this.window.decorView)
    windowInsetsController.isAppearanceLightStatusBars = enabled
}