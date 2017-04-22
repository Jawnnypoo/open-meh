package com.jawnnypoo.openmeh.activity

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 * The Activity to father them all
 */
open class BaseActivity : RxAppCompatActivity() {
    companion object {
        val EXTRA_THEME = "EXTRA_THEME"
    }
}
