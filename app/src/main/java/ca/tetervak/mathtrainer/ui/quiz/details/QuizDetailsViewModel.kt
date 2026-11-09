package ca.tetervak.mathtrainer.ui.quiz.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class QuizDetailsViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val quizIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<QuizDetailsUiState> =
        quizIdFlow.flatMapLatest { quizId ->
            if (quizId == null) {
                flowOf(QuizDetailsUiState.Loading)
            } else {
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
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizDetailsUiState.Loading
        )

    fun loadQuiz(quizId: String) {
        quizIdFlow.value = quizId
    }

    fun deleteQuiz() {
        quizIdFlow.value?.let { quizId ->
            repository.deleteQuizWithProblems(quizId = quizId)
        }
    }
}