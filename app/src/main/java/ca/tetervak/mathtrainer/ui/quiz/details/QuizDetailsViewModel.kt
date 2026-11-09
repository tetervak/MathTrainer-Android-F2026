package ca.tetervak.mathtrainer.ui.quiz.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.model.QuizStatus
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import ca.tetervak.mathtrainer.ui.QuizDetailsRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

sealed interface QuizDetailsUiState {
    object Loading : QuizDetailsUiState
    data class Success(
        val quiz: Quiz,
        val quizStatus: QuizStatus,
        val firstProblemId: String?
    ) : QuizDetailsUiState
}

class QuizDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: QuizRepository
) : ViewModel() {

    val quizDetailsRoute: QuizDetailsRoute = savedStateHandle.toRoute<QuizDetailsRoute>()
    val quizId: String = quizDetailsRoute.quizId

    val uiState: StateFlow<QuizDetailsUiState> =
        combine(
            repository.getQuizByIdFlow(quizId).filterNotNull(),
            repository.getQuizStatusFlow(quizId).filterNotNull()
        )
        { quiz, quizStatus ->
            val firstProblemId = repository.getFirstProblemId(quizId)
            QuizDetailsUiState.Success(
                quiz = quiz,
                quizStatus = quizStatus,
                firstProblemId = firstProblemId
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = QuizDetailsUiState.Loading
            )

    fun deleteQuiz() {
        repository.deleteQuizWithProblems(quizId = quizId)
    }
}