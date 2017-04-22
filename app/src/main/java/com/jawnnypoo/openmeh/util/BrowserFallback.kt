package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar

import com.jawnnypoo.openmeh.R
import com.novoda.simplechromecustomtabs.navigation.NavigationFallback

/**
 * A fallback to open the url in the browser
 */
class BrowserFallback(private val mActivity: Activity) : NavigationFallback {

    override fun onFallbackNavigateTo(url: Uri) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = url
        try {
            mActivity.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(mActivity.window.decorView, R.string.error_no_browser, Snackbar.LENGTH_SHORT)
                    .show()
        }

    }
}
