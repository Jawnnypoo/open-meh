package com.jawnnypoo.openmeh.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jawnnypoo.openmeh.App
import com.jawnnypoo.openmeh.github.Contributor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class AboutUiState(
    val isLoading: Boolean = false,
    val contributors: List<Contributor> = emptyList(),
    val hasError: Boolean = false,
)

class AboutViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val REPO_USER = "Jawnnypoo"
        private const val REPO_NAME = "open-meh"
    }

    private val repository = (application as App).gitHubRepository

    private val _uiState = MutableStateFlow(AboutUiState(isLoading = true))
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

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
                val contributors = repository.contributors(REPO_USER, REPO_NAME)
                _uiState.value = AboutUiState(
                    isLoading = false,
                    contributors = contributors,
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
}
