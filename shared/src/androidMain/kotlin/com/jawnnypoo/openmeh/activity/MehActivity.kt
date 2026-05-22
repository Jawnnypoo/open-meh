package com.jawnnypoo.openmeh.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jawnnypoo.openmeh.OpenMehApplication
import com.jawnnypoo.openmeh.ui.LocalIsDebug
import com.jawnnypoo.openmeh.ui.navigation.OpenMehNavHost
import com.jawnnypoo.openmeh.ui.theme.OpenMehTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

class MehActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val graph = (application as OpenMehApplication).graph

        setContent {
            CompositionLocalProvider(
                LocalMetroViewModelFactory provides graph.metroViewModelFactory,
                LocalIsDebug provides graph.isDebug,
            ) {
                OpenMehTheme {
                    val useDarkStatusBarIcons = MaterialTheme.colorScheme.surface.luminance() > 0.5f
                    SideEffect {
                        WindowInsetsControllerCompat(window, window.decorView)
                            .isAppearanceLightStatusBars = useDarkStatusBarIcons
                    }
                    OpenMehNavHost(
                        onFinish = { finish() },
                    )
                }
            }
        }
    }
}
