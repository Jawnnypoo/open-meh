package com.jawnnypoo.openmeh.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.commit451.addendum.design.snackbar
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.shared.response.MehResponse

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
            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                root.context.getString(R.string.share_subject)
            )
            shareIntent.putExtra(Intent.EXTRA_TEXT, deal.url)
            root.context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    root.context.getString(R.string.share_subject)
                )
            )
        }
    }

    fun openUrl(activity: Activity, url: String, toolbarColor: Int) {
        val builder = CustomTabsIntent.Builder()
        val defaultColors = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor)
            .build()
        builder.setDefaultColorSchemeParams(defaultColors)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }
}
