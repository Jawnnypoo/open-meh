package com.jawnnypoo.openmeh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.resources.Res
import com.jawnnypoo.openmeh.resources.source_url
import com.jawnnypoo.openmeh.ui.screen.AboutScreen
import com.jawnnypoo.openmeh.ui.screen.DealScreen
import com.jawnnypoo.openmeh.ui.screen.FullScreenImageViewerScreen
import com.jawnnypoo.openmeh.ui.screen.NotificationsScreen
import com.jawnnypoo.openmeh.util.openUrl
import com.jawnnypoo.openmeh.viewmodel.AboutViewModel
import com.jawnnypoo.openmeh.viewmodel.MehViewModel
import com.jawnnypoo.openmeh.viewmodel.NotificationsViewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data object DealDestination : NavKey

@Serializable
data object NotificationsDestination : NavKey

@Serializable
data object AboutDestination : NavKey

@Serializable
data class ImageViewerDestination(
    val images: List<String>,
    val index: Int,
) : NavKey

@Composable
fun OpenMehNavHost(
    onFinish: () -> Unit,
) {
    val backStack = rememberNavBackStack(DealDestination)

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size <= 1) {
                onFinish()
            } else {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<DealDestination> {
                val viewModel: MehViewModel = viewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                DealScreen(
                    state = state,
                    onRefresh = viewModel::refresh,
                    onOpenNotifications = { backStack.add(NotificationsDestination) },
                    onOpenAbout = { backStack.add(AboutDestination) },
                    onOpenImageViewer = { images, index ->
                        backStack.add(ImageViewerDestination(images, index))
                    },
                    onPostDebugNotification = viewModel::postDebugNotification,
                )
            }
            entry<NotificationsDestination> {
                val viewModel: NotificationsViewModel = viewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                NotificationsScreen(
                    state = state,
                    events = viewModel.events,
                    onBack = { backStack.removeLastOrNull() },
                    onNotificationsEnabledChanged = viewModel::setNotificationsEnabled,
                    onSoundChanged = viewModel::setSoundEnabled,
                    onVibrateChanged = viewModel::setVibrateEnabled,
                    onTimeChanged = viewModel::setNotificationTime,
                    onPermissionDenied = viewModel::onPermissionDenied,
                )
            }
            entry<AboutDestination> {
                val sourceUrl = stringResource(Res.string.source_url)
                val viewModel: AboutViewModel = viewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                AboutScreen(
                    state = state,
                    onBack = { backStack.removeLastOrNull() },
                    onOpenSource = { openUrl(sourceUrl, ParsedTheme.WHITE) },
                    onRetry = viewModel::refresh,
                )
            }
            entry<ImageViewerDestination> { route ->
                FullScreenImageViewerScreen(
                    images = route.images,
                    startIndex = route.index,
                    onClose = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
