package com.jawnnypoo.openmeh.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jawnnypoo.openmeh.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * The Activity to rule them all
 */
abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val KEY_THEME = "theme"
    }

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        job = Job()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
