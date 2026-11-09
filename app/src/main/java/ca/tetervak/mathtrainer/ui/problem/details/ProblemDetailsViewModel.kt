package ca.tetervak.mathtrainer.ui.problem.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.AnswerStatus
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProblemDetailsViewModel(
    private val repository: QuizRepository,
) : ViewModel() {

    private val problemIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProblemDetailsUiState> =
        problemIdFlow.flatMapLatest { problemId ->
            if (problemId == null) {
                flowOf(ProblemDetailsUiState.Loading)
            } else {
                repository.getProblemByIdFlow(problemId)
                    .filterNotNull()
                    .onEach { problem ->
                        answerInput = if (problem.answerStatus == AnswerStatus.RIGHT_ANSWER) {
                            checkNotNull(problem.userAnswer)
                        } else { "" }
                    }
                    .map { problem -> successState(problem) }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProblemDetailsUiState.Loading
        )

    fun loadProblem(problemId: String) {
        problemIdFlow.value = problemId
    }

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

    private suspend fun successState(problem: Problem): ProblemDetailsUiState.Success{
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