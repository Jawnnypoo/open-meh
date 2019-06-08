package com.jawnnypoo.openmeh.activity

import androidx.appcompat.app.AppCompatActivity
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * The Activity to father them all
 */
abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_THEME = "EXTRA_THEME"
    }

    val scopeProvider: AndroidLifecycleScopeProvider by lazy { AndroidLifecycleScopeProvider.from(this) }
}
