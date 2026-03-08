package com.jawnnypoo.openmeh.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.worker.ReminderWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber

data class DealUiState(
    val isLoading: Boolean = false,
    val response: MehResponse? = null,
    val hasError: Boolean = false,
)

class MehViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as App).mehRepository

    private val _uiState = MutableStateFlow(DealUiState(isLoading = true))
    val uiState: StateFlow<DealUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    hasError = false,
                )
            }
            try {
                val response = repository.currentDeal()
                _uiState.value = DealUiState(
                    isLoading = false,
                    response = response,
                    hasError = false,
                )
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasError = true,
                    )
                }
            }
        }
    }

    fun postDebugNotification() {
        viewModelScope.launch {
            val now = LocalDateTime.now().plusMinutes(1)
            ReminderWorker.schedule(
                context = getApplication(),
                hour = now.hour,
                minute = now.minute,
            )
        }
    }
}
