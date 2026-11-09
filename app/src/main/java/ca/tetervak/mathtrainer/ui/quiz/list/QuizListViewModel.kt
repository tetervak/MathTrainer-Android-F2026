package ca.tetervak.mathtrainer.ui.quiz.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.tetervak.mathtrainer.domain.model.QuizScore
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class QuizListViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    val uiState: StateFlow<QuizListUiState> =
        repository.getUserQuizzesFlow()
            .map { quizzes ->
                val stateList = quizzes.map { quiz ->
                    val score: QuizScore = repository.getQuizScore(quizId = quiz.id)
                    QuizListItemUiState(
                        quiz = quiz,
                        quizScore = score
                    )
                }
                QuizListUiState(stateList = stateList)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = QuizListUiState(stateList = emptyList())
            )

    fun addNewQuiz() {
        repository.addNewGeneratedQuiz()
    }

}
