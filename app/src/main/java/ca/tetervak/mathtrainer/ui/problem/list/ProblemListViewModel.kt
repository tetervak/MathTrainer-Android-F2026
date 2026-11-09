package ca.tetervak.mathtrainer.ui.problem.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProblemListViewModel(
    private val repository: QuizRepository,
) : ViewModel() {

    private val quizIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProblemListUiState> =
        quizIdFlow.flatMapLatest { quizId ->
            if (quizId == null) {
                flowOf(ProblemListUiState.Loading)
            } else {
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
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProblemListUiState.Loading
        )

    fun loadProblems(quizId: String) {
        quizIdFlow.value = quizId
    }
}