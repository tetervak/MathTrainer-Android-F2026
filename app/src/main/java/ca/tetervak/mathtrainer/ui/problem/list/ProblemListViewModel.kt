package ca.tetervak.mathtrainer.ui.problem.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import ca.tetervak.mathtrainer.ui.ProblemListRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface ProblemListUiState{
    object Loading: ProblemListUiState
    data class Success(
        val quizNumber: Int,
        val problemList: List<Problem>,
        val rightAnswers: Int
    ): ProblemListUiState
}

class ProblemListViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: QuizRepository

) : ViewModel() {

    private val problemListRoute: ProblemListRoute = savedStateHandle.toRoute<ProblemListRoute>()
    private val quizId: String = problemListRoute.quizId

    val uiState: StateFlow<ProblemListUiState> =

        repository.getQuizProblemsFlow(quizId)
            .map { problems ->
                val quiz: Quiz = repository.getQuizByIdFlow(quizId).filterNotNull().first()
                val rightAnswers = repository.getNumberOfRightAnswers(quizId)
                ProblemListUiState.Success(
                    quizNumber = quiz.quizNumber,
                    problemList = problems,
                    rightAnswers = rightAnswers
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ProblemListUiState.Loading
            )
}

