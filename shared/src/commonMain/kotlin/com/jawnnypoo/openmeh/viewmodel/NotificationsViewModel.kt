package com.jawnnypoo.openmeh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.jawnnypoo.openmeh.resources.Res
import com.jawnnypoo.openmeh.resources.error_with_server
import com.jawnnypoo.openmeh.resources.notification_canceled
import com.jawnnypoo.openmeh.resources.notification_scheduled
import com.jawnnypoo.openmeh.util.Prefs
import com.jawnnypoo.openmeh.util.ReminderScheduler
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

data class NotificationsUiState(
    val notificationsEnabled: Boolean = false,
    val soundEnabled: Boolean = false,
    val vibrateEnabled: Boolean = false,
    val hour: Int = 18,
    val minute: Int = 30,
)

sealed interface NotificationsEvent {
    data class Message(val text: StringResource) : NotificationsEvent
    data object PermissionDenied : NotificationsEvent
}

@Inject
@ContributesIntoMap(AppScope::class)
@ViewModelKey
class NotificationsViewModel(
    private val prefs: Prefs,
) : ViewModel() {

    private val log = Logger.withTag("NotificationsViewModel")

    private val _uiState = MutableStateFlow(
        NotificationsUiState(
            notificationsEnabled = prefs.areNotificationsEnabled,
            soundEnabled = prefs.isNotificationSound,
            vibrateEnabled = prefs.isNotificationVibrate,
            hour = prefs.notificationHour,
            minute = prefs.notificationMinute,
        )
    )
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NotificationsEvent>()
    val events: SharedFlow<NotificationsEvent> = _events.asSharedFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.areNotificationsEnabled = enabled
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        if (enabled) {
            scheduleReminder()
        } else {
            cancelReminder()
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        prefs.notificationHour = hour
        prefs.notificationMinute = minute
        _uiState.update {
            it.copy(
                hour = hour,
                minute = minute,
            )
        }
        if (_uiState.value.notificationsEnabled) {
            scheduleReminder()
        } else {
            setNotificationsEnabled(true)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.isNotificationSound = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        prefs.isNotificationVibrate = enabled
        _uiState.update { it.copy(vibrateEnabled = enabled) }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            _events.emit(NotificationsEvent.PermissionDenied)
        }
    }

    private fun scheduleReminder() {
        viewModelScope.launch {
            try {
                ReminderScheduler.schedule(
                    hour = prefs.notificationHour,
                    minute = prefs.notificationMinute,
                )
                _events.emit(NotificationsEvent.Message(Res.string.notification_scheduled))
            } catch (e: Exception) {
                log.e(e) { "Failed to schedule reminder" }
                _events.emit(NotificationsEvent.Message(Res.string.error_with_server))
            }
        }
    }

    private fun cancelReminder() {
        viewModelScope.launch {
            try {
                ReminderScheduler.cancel()
                _events.emit(NotificationsEvent.Message(Res.string.notification_canceled))
            } catch (e: Exception) {
                log.e(e) { "Failed to cancel reminder" }
                _events.emit(NotificationsEvent.Message(Res.string.error_with_server))
            }
        }
    }
}
