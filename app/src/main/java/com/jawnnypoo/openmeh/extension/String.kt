package com.jawnnypoo.openmeh.extension

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * Assures HTML is formatted the same way pre and post Android N
 */
@Suppress("DEPRECATION")
fun String.formatAsHtml(imageGetter: Html.ImageGetter? = null, tagHandler: Html.TagHandler? = null): Spanned {
    return if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler)
    } else {
        Html.fromHtml(this, imageGetter, tagHandler)
    }
}