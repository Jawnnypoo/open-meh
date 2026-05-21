package com.jawnnypoo.openmeh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.jawnnypoo.openmeh.Container
import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.jawnnypoo.openmeh.util.ReminderScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DealUiState(
    val isLoading: Boolean = false,
    val response: MehResponse? = null,
    val hasError: Boolean = false,
)

class MehViewModel : ViewModel() {

    private val log = Logger.withTag("MehViewModel")
    private val repository = Container.mehRepository

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
                log.e(e) { "Failed to load deal" }
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
            val instant = Clock.System.now().plus(1.minutes)
            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            ReminderScheduler.schedule(
                hour = local.hour,
                minute = local.minute,
            )
        }
    }
}
