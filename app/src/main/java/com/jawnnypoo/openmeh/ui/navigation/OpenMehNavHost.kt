package com.jawnnypoo.openmeh.ui.navigation

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.ui.screen.AboutScreen
import com.jawnnypoo.openmeh.ui.screen.DealScreen
import com.jawnnypoo.openmeh.ui.screen.FullScreenImageViewerScreen
import com.jawnnypoo.openmeh.ui.screen.NotificationsScreen
import com.jawnnypoo.openmeh.viewmodel.AboutViewModel
import com.jawnnypoo.openmeh.viewmodel.MehViewModel
import com.jawnnypoo.openmeh.viewmodel.NotificationsViewModel
import kotlinx.serialization.Serializable

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
    onOpenUrl: (url: String, toolbarColor: Int) -> Unit,
    onShare: (MehResponse?) -> Unit,
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
                    onShare = onShare,
                    onOpenNotifications = { backStack.add(NotificationsDestination) },
                    onOpenAbout = { backStack.add(AboutDestination) },
                    onOpenImageViewer = { images, index ->
                        backStack.add(ImageViewerDestination(images, index))
                    },
                    onOpenExternalUrl = onOpenUrl,
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
                val sourceUrl = stringResource(id = R.string.source_url)
                val viewModel: AboutViewModel = viewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                AboutScreen(
                    state = state,
                    onBack = { backStack.removeLastOrNull() },
                    onOpenSource = { onOpenUrl(sourceUrl, Color.WHITE) },
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
