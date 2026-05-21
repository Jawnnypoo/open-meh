package com.jawnnypoo.openmeh.util

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

private lateinit var applicationContext: Application

fun initializeLinker(application: Application) {
    applicationContext = application
}

actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    applicationContext.startActivity(intent)
}

actual fun openUrl(url: String, toolbarColor: Int) {
    val params = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor)
        .build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(params)
        .build()
        .apply { intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    customTabsIntent.launchUrl(applicationContext, url.toUri())
}

actual fun shareText(text: String, subject: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(sendIntent, subject).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    applicationContext.startActivity(chooser)
}
