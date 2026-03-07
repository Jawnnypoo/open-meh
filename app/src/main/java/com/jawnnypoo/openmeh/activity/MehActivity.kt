package com.jawnnypoo.openmeh.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.jawnnypoo.openmeh.ui.navigation.OpenMehNavHost
import com.jawnnypoo.openmeh.ui.theme.OpenMehTheme
import com.jawnnypoo.openmeh.util.IntentUtil

class MehActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            OpenMehTheme {
                OpenMehNavHost(
                    onFinish = { finish() },
                    onOpenUrl = { url, toolbarColor ->
                        IntentUtil.openUrl(
                            activity = this,
                            url = url,
                            toolbarColor = toolbarColor,
                        )
                    },
                    onShare = { response ->
                        IntentUtil.shareDeal(
                            context = this,
                            mehResponse = response,
                        )
                    },
                )
            }
        }
    }
}
