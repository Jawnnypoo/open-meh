package com.jawnnypoo.openmeh.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jawnnypoo.openmeh.ui.navigation.OpenMehNavHost
import com.jawnnypoo.openmeh.ui.theme.OpenMehTheme
import com.jawnnypoo.openmeh.util.IntentUtil

class MehActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            OpenMehTheme {
                val useDarkStatusBarIcons = MaterialTheme.colorScheme.surface.luminance() > 0.5f
                SideEffect {
                    WindowInsetsControllerCompat(window, window.decorView)
                        .isAppearanceLightStatusBars = useDarkStatusBarIcons
                }

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
