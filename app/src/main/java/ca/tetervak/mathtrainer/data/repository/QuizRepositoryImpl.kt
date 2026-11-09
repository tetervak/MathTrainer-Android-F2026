package ca.tetervak.mathtrainer.data.repository

import ca.tetervak.mathtrainer.domain.model.AlgebraProblem
import ca.tetervak.mathtrainer.domain.model.AnswerStatus
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.model.QuizScore
import ca.tetervak.mathtrainer.domain.model.QuizStatus
import ca.tetervak.mathtrainer.domain.repository.QuizRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizRepositoryImpl(
    private val quizStorageRepository: QuizStorageRepository,
    private val randomQuizRepository: QuizGenerationRepository,
    private val externalScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) : QuizRepository {

    @OptIn(DelicateCoroutinesApi::class)
    constructor(
        quizStorageRepository: QuizStorageRepository,
        randomQuizRepository: QuizGenerationRepository
    ) : this(
        quizStorageRepository = quizStorageRepository,
        randomQuizRepository = randomQuizRepository,
        externalScope = GlobalScope,
        dispatcher = Dispatchers.IO
    )

    override fun getUserQuizzesFlow(): Flow<List<Quiz>> =
        quizStorageRepository.getUserQuizzesFlow()

    override fun getQuizProblemsFlow(quizId: String): Flow<List<Problem>> =
        quizStorageRepository.getQuizProblemsFlow(quizId)

    override fun getProblemByIdFlow(problemId: String): Flow<Problem?> =
        quizStorageRepository.getProblemByIdFlow(problemId)

    override suspend fun updateProblem(problemId: String, userAnswer: String?, answerStatus: AnswerStatus) =
        quizStorageRepository.updateProblem(problemId, userAnswer, answerStatus)

    override suspend fun getQuizScore(quizId: String): QuizScore =
        withContext(context = dispatcher) {
            val problemCount = quizStorageRepository.getQuizProblemCount(quizId)!!
            val rightAnswers = quizStorageRepository.getNumberOfRightAnswers(quizId)
            QuizScore(problemCount = problemCount, rightAnswers)
        }

    override fun getQuizStatusFlow(quizId: String): Flow<QuizStatus> =
        quizStorageRepository.getQuizStatusDataFlow(quizId)

    override fun addNewGeneratedQuiz() {
        externalScope.launch(context = dispatcher) {
            val problems: List<AlgebraProblem> = randomQuizRepository.getRandomQuizProblems()
            quizStorageRepository.insertQuizWithProblems(problems)
        }
    }

    override fun deleteQuizWithProblems(quizId: String) {
        externalScope.launch(context = dispatcher) {
            quizStorageRepository.deleteQuizWithProblems(quizId)
        }
    }

    override suspend fun getNextProblemId(problem: Problem): String? =
        quizStorageRepository.getNextProblemId(problem)

    override suspend fun getPreviousProblemId(problem: Problem): String? =
        quizStorageRepository.getPreviousProblemId(problem)

    override suspend fun getFirstProblemId(quizId: String): String? =
        quizStorageRepository.getFirstProblemId(quizId)

    override suspend fun getQuizProblemCount(quizId: String): Int =
        quizStorageRepository.getQuizProblemCount(quizId)

    override suspend fun getNumberOfRightAnswers(quizId: String): Int =
        quizStorageRepository.getNumberOfRightAnswers(quizId)

    override suspend fun getQuizNumber(quizId: String): Int =
        quizStorageRepository.getQuizNumber(quizId)

    override fun getQuizByIdFlow(quizId: String): Flow<Quiz?> =
        quizStorageRepository.getQuizByIdFlow(quizId)

    override fun getQuizCountFlow(): Flow<Int> =
        quizStorageRepository.getQuizCountFlow()

}