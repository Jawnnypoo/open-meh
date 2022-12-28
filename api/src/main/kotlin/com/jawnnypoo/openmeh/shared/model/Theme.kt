package com.jawnnypoo.openmeh.shared.model

/**
 * Pretty cool theme. Tells us all about the colors we want to show
 */
data class Theme(
    val accentColor: String,
    val foreground: String,
    val backgroundColor: String,
    val backgroundImage: String
) {
    companion object {
        const val FOREGROUND_LIGHT = "light"
    }
}
