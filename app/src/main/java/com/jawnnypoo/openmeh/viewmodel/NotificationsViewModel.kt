package com.jawnnypoo.openmeh.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.util.Prefs
import com.jawnnypoo.openmeh.worker.ReminderWorker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class NotificationsUiState(
    val notificationsEnabled: Boolean = false,
    val soundEnabled: Boolean = false,
    val vibrateEnabled: Boolean = false,
    val hour: Int = 18,
    val minute: Int = 30,
)

sealed interface NotificationsEvent {
    data class Message(val value: String) : NotificationsEvent
}

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        NotificationsUiState(
            notificationsEnabled = Prefs.areNotificationsEnabled,
            soundEnabled = Prefs.isNotificationSound,
            vibrateEnabled = Prefs.isNotificationVibrate,
            hour = Prefs.notificationHour,
            minute = Prefs.notificationMinute,
        )
    )
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NotificationsEvent>()
    val events: SharedFlow<NotificationsEvent> = _events.asSharedFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        Prefs.areNotificationsEnabled = enabled
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        if (enabled) {
            scheduleReminder()
        } else {
            cancelReminder()
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        Prefs.notificationHour = hour
        Prefs.notificationMinute = minute
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
        Prefs.isNotificationSound = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        Prefs.isNotificationVibrate = enabled
        _uiState.update { it.copy(vibrateEnabled = enabled) }
    }

    fun onPermissionDenied() {
        emitEvent(NotificationsEvent.Message("Notification permission is not enabled"))
    }

    private fun scheduleReminder() {
        viewModelScope.launch {
            try {
                ReminderWorker.schedule(
                    context = getApplication(),
                    hour = Prefs.notificationHour,
                    minute = Prefs.notificationMinute,
                )
                emitEvent(NotificationsEvent.Message(getApplication<Application>().getString(R.string.notification_scheduled)))
            } catch (e: Exception) {
                Timber.e(e)
                emitEvent(NotificationsEvent.Message(getApplication<Application>().getString(R.string.error_with_server)))
            }
        }
    }

    private fun cancelReminder() {
        viewModelScope.launch {
            try {
                ReminderWorker.cancel(getApplication())
                emitEvent(NotificationsEvent.Message(getApplication<Application>().getString(R.string.notification_canceled)))
            } catch (e: Exception) {
                Timber.e(e)
                emitEvent(NotificationsEvent.Message(getApplication<Application>().getString(R.string.error_with_server)))
            }
        }
    }

    private fun emitEvent(event: NotificationsEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}
