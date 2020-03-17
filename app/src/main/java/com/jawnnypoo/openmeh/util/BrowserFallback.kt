package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.google.android.material.snackbar.Snackbar

import com.jawnnypoo.openmeh.R
import com.novoda.simplechromecustomtabs.navigation.NavigationFallback

/**
 * A fallback to open the url in the browser
 */
class BrowserFallback(private val activity: Activity) : NavigationFallback {

    override fun onFallbackNavigateTo(url: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = url
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(activity.window.decorView, R.string.error_no_browser, Snackbar.LENGTH_SHORT)
                    .show()
        }

    }
}
