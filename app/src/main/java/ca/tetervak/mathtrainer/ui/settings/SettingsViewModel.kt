package ca.tetervak.mathtrainer.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.tetervak.mathtrainer.domain.model.ProblemGeneration
import ca.tetervak.mathtrainer.domain.model.UserPreferences
import ca.tetervak.mathtrainer.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
class SettingsViewModel(
    private val repository: PreferencesRepository
): ViewModel() {

    val uiState: StateFlow<UserPreferences> = repository.getUserPreferencesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = runBlocking {
                repository.getUserPreferences()
            }
        )


    fun saveNumberOfProblems(numberOfProblems: Int) {
        viewModelScope.launch {
            repository.saveNumberOfProblems(numberOfProblems)
        }
    }

    fun saveProblemGeneration(problemGeneration: ProblemGeneration) {
        viewModelScope.launch {
            repository.saveProblemGeneration(problemGeneration)
        }
    }
}