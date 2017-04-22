package com.jawnnypoo.openmeh.util

import android.content.res.ColorStateList

/**
 * Color util!
 */
object ColorUtil {

    fun createColorStateList(color: Int, pressed: Int): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()), intArrayOf(pressed, color))
    }
}
