package com.jawnnypoo.openmeh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.jawnnypoo.openmeh.data.GitHubRepository
import com.jawnnypoo.openmeh.github.Contributor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AboutUiState(
    val isLoading: Boolean = false,
    val contributors: List<Contributor> = emptyList(),
    val hasError: Boolean = false,
)

@Inject
@ContributesIntoMap(AppScope::class)
@ViewModelKey
class AboutViewModel(
    private val repository: GitHubRepository,
) : ViewModel() {

    companion object {
        private const val REPO_USER = "Jawnnypoo"
        private const val REPO_NAME = "open-meh"
    }

    private val log = Logger.withTag("AboutViewModel")

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
                log.e(e) { "Failed to load contributors" }
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
