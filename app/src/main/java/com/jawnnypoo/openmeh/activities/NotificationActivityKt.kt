package com.jawnnypoo.openmeh.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.jawnnypoo.openmeh.R

/**
 * Notification activity, rewritten in Kotlin
 * Created by John on 12/8/15.
 */
open class NotificationActivityKt : android.support.v7.app.AppCompatActivity() {

    var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        mToolbar = findViewById(R.id.toolbar) as Toolbar?
    }
}
