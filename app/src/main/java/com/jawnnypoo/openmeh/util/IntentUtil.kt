package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import com.commit451.addendum.design.snackbar

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
            root.snackbar(R.string.error_nothing_to_share)
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
                .withIntentCustomizer(MehIntentCustomizer(toolbarColor))
                .navigateTo(Uri.parse(url), activity)
    }

    private class MehIntentCustomizer(private val toolbarColor: Int) : IntentCustomizer {

        override fun onCustomiseIntent(simpleChromeCustomTabsIntentBuilder: SimpleChromeCustomTabsIntentBuilder): SimpleChromeCustomTabsIntentBuilder {
            return simpleChromeCustomTabsIntentBuilder.withToolbarColor(toolbarColor)
        }
    }

}
