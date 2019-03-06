package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.google.android.material.snackbar.Snackbar
import android.view.View

import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs
import com.novoda.simplechromecustomtabs.navigation.IntentCustomizer
import com.novoda.simplechromecustomtabs.navigation.SimpleChromeCustomTabsIntentBuilder

/**
 * All the intents
 */
object IntentUtil {

    fun shareDeal(root: View, mehResponse: MehResponse?) {
        val deal = mehResponse?.deal
        if (deal == null) {
            Snackbar.make(root, R.string.error_nothing_to_share, Snackbar.LENGTH_SHORT)
                    .show()
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, root.context.getString(R.string.share_subject))
            shareIntent.putExtra(Intent.EXTRA_TEXT, deal.url)
            root.context.startActivity(Intent.createChooser(shareIntent, root.context.getString(R.string.share_subject)))
        }
    }

    fun openUrl(activity: Activity, url: String, toolbarColor: Int) {
        SimpleChromeCustomTabs.getInstance()
                .withFallback(BrowserFallback(activity))
                .withIntentCustomizer(MehIntentCustomizer(activity, toolbarColor))
                .navigateTo(Uri.parse(url), activity)
    }

    private class MehIntentCustomizer(private val mActivity: Activity, private val mToolbarColor: Int) : IntentCustomizer {

        override fun onCustomiseIntent(simpleChromeCustomTabsIntentBuilder: SimpleChromeCustomTabsIntentBuilder): SimpleChromeCustomTabsIntentBuilder {
            return simpleChromeCustomTabsIntentBuilder.withToolbarColor(mToolbarColor)
                    .withStartAnimations(mActivity, R.anim.fade_in, R.anim.do_nothing)
                    .withExitAnimations(mActivity, R.anim.do_nothing, R.anim.fade_out)
        }
    }

}
