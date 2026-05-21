package com.jawnnypoo.openmeh.ui.components

import androidx.compose.runtime.Composable

/**
 * Side-effect composable that requests the platform's notification permission if missing.
 * On Android 13+ this triggers the runtime POST_NOTIFICATIONS prompt; on iOS this would request
 * UNUserNotificationCenter authorization. [onDenied] fires only on an explicit denial.
 */
@Composable
expect fun NotificationPermissionRequest(onDenied: () -> Unit)
