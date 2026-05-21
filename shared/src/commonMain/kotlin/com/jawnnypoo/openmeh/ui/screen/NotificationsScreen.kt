package com.jawnnypoo.openmeh.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jawnnypoo.openmeh.resources.Res
import com.jawnnypoo.openmeh.resources.action_notifications
import com.jawnnypoo.openmeh.resources.ic_arrow_back_24dp
import com.jawnnypoo.openmeh.resources.notification_permission_disabled
import com.jawnnypoo.openmeh.resources.notifications
import com.jawnnypoo.openmeh.resources.sound
import com.jawnnypoo.openmeh.resources.time_to_notify
import com.jawnnypoo.openmeh.resources.vibrate
import com.jawnnypoo.openmeh.ui.components.NotificationPermissionRequest
import com.jawnnypoo.openmeh.ui.components.TimePickerDialog
import com.jawnnypoo.openmeh.viewmodel.NotificationsEvent
import com.jawnnypoo.openmeh.viewmodel.NotificationsUiState
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    events: Flow<NotificationsEvent>,
    onBack: () -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onVibrateChanged: (Boolean) -> Unit,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    onPermissionDenied: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showTimePicker by remember { mutableStateOf(false) }

    NotificationPermissionRequest(onDenied = onPermissionDenied)

    LaunchedEffect(events) {
        events.collect { event ->
            val text = when (event) {
                is NotificationsEvent.Message -> getString(event.text)
                NotificationsEvent.PermissionDenied -> getString(Res.string.notification_permission_disabled)
            }
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.action_notifications))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_back_24dp),
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNotificationsEnabledChanged(!state.notificationsEnabled)
                    }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.notifications),
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(
                    checked = state.notificationsEnabled,
                    onCheckedChange = onNotificationsEnabledChanged,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.time_to_notify),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = formatTime(hour = state.hour, minute = state.minute),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSoundChanged(!state.soundEnabled)
                    }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.sound),
                    style = MaterialTheme.typography.titleMedium,
                )
                Checkbox(
                    checked = state.soundEnabled,
                    onCheckedChange = onSoundChanged,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onVibrateChanged(!state.vibrateEnabled)
                    }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.vibrate),
                    style = MaterialTheme.typography.titleMedium,
                )
                Checkbox(
                    checked = state.vibrateEnabled,
                    onCheckedChange = onVibrateChanged,
                )
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = state.hour,
            initialMinute = state.minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                showTimePicker = false
                onTimeChanged(hour, minute)
            },
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minuteStr = minute.toString().padStart(2, '0')
    return "$displayHour:$minuteStr $period"
}
