package com.jawnnypoo.openmeh.util

/**
 * Cross-platform URL/share entry points. Each target provides a thin adapter to the platform's
 * intent/share APIs.
 */
expect fun openUrl(url: String)

/**
 * Opens a URL with a hint at the chrome color to use (Android Custom Tabs honors this; other
 * targets may ignore it).
 */
expect fun openUrl(url: String, toolbarColor: Int)

expect fun shareText(text: String, subject: String)
