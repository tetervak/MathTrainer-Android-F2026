package ca.tetervak.mathtrainer.ui.problem.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.AnswerStatus
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import ca.tetervak.mathtrainer.ui.ProblemDetailsRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ProblemDetailsUiState {
    object Loading : ProblemDetailsUiState
    data class Success(
        val problem: Problem,
        val quizNumber: Int,
        val numberOfProblems: Int,
        val numberOfRightAnswers: Int,
        val firstProblemId: String?,
        val nextProblemId: String?,
        val previousProblemId: String?
    ) : ProblemDetailsUiState
}

class ProblemDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: QuizRepository,
) : ViewModel() {

    val problemDetailsRoute: ProblemDetailsRoute = savedStateHandle.toRoute<ProblemDetailsRoute>()
    val problemId: String = problemDetailsRoute.problemId

    val uiState: StateFlow<ProblemDetailsUiState> =

        repository.getProblemByIdFlow(problemId)
            .filterNotNull()
            .onEach { problem ->
                answerInput = if (problem.answerStatus == AnswerStatus.RIGHT_ANSWER) {
                    checkNotNull(problem.userAnswer)
                } else {
                    ""
                }
            }
            .map { problem -> successState(problem) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ProblemDetailsUiState.Loading
            )

    var answerInput: String by mutableStateOf("")
        private set

    fun updateAnswerInput(input: String) {
        answerInput = input
    }

    fun onSubmit() {
        val uiState = uiState.value
        if (uiState !is ProblemDetailsUiState.Success) return

        val problem = uiState.problem
        val userAnswer = answerInput
        viewModelScope.launch {
            repository.updateProblem(
                problemId = problem.id,
                userAnswer = userAnswer,
                answerStatus = AnswerStatus.getStatus(
                    correctAnswer = problem.correctAnswer,
                    userAnswer = userAnswer
                )
            )
            answerInput = ""
        }
    }

    private suspend fun successState(problem: Problem): ProblemDetailsUiState.Success {
        val numberOfProblems = repository.getQuizProblemCount(problem.quizId)
        val quizNumber = repository.getQuizNumber(problem.quizId)
        val numberOfRightAnswers =
            repository.getNumberOfRightAnswers(problem.quizId)
        val firstProblemId = if (problem.problemNumber > 1) {
            repository.getFirstProblemId(problem.quizId)
        } else {
            null
        }
        val nextProblemId = if (problem.problemNumber < numberOfProblems) {
            repository.getNextProblemId(problem)
        } else {
            null
        }
        val previousProblemId = if (problem.problemNumber > 1) {
            repository.getPreviousProblemId(problem)
        } else {
            null
        }
        return ProblemDetailsUiState.Success(
            problem = problem,
            quizNumber = quizNumber,
            numberOfProblems = numberOfProblems,
            numberOfRightAnswers = numberOfRightAnswers,
            firstProblemId = firstProblemId,
            nextProblemId = nextProblemId,
            previousProblemId = previousProblemId
        )
    }

}
