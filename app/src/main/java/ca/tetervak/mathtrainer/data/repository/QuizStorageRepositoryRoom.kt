package ca.tetervak.mathtrainer.data.repository

import ca.tetervak.mathtrainer.data.database.dao.ProblemDao
import ca.tetervak.mathtrainer.data.database.dao.QuizDao
import ca.tetervak.mathtrainer.data.database.dao.UserDao
import ca.tetervak.mathtrainer.data.database.entity.UserEntity
import ca.tetervak.mathtrainer.domain.model.AlgebraProblem
import ca.tetervak.mathtrainer.domain.model.AnswerStatus
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.model.QuizScore
import ca.tetervak.mathtrainer.domain.model.QuizStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
class QuizStorageRepositoryRoom(
    private val userDao: UserDao,
    private val quizDao: QuizDao,
    private val problemDao: ProblemDao,
    private val dispatcher: CoroutineDispatcher
) : QuizStorageRepository {

    constructor(
        userDao: UserDao,
        quizDao: QuizDao,
        problemDao: ProblemDao
    ) : this(
        userDao = userDao,
        quizDao = quizDao,
        problemDao = problemDao,
        dispatcher = Dispatchers.IO
    )

    private val userId: String = UserEntity.demoUser.id

    private suspend fun ensureUserExist(){
        if(userDao.getUserById(userId) == null){
            userDao.insert(UserEntity.demoUser)
        }
    }

    override fun getUserQuizzesFlow(): Flow<List<Quiz>> =
        quizDao.getUserQuizzesFlow(userId).map { entities ->
            entities.map { entity -> entity.toDomain() }
        }.flowOn(context = dispatcher)

    override fun getQuizByIdFlow(quizId: String): Flow<Quiz?> =
        quizDao.getQuizByIdFlow(quizId).map { entity ->
            entity?.toDomain()
        }.flowOn(context = dispatcher)

    override fun getQuizCountFlow(): Flow<Int> =
        quizDao.getUserQuizCountFlow(userId)
            .flowOn(context = dispatcher)

    override suspend fun insertQuizWithProblems(
        problems: List<AlgebraProblem>
    ) = withContext(context = dispatcher) {
        ensureUserExist()
        val quizId = UUID.randomUUID().toString()
        val problemEntities = problems.mapIndexed { index, problem ->
            problem.toEntity(
                quizId = quizId,
                problemNumber = index + 1,
                userAnswer = null,
                answerStatus = AnswerStatus.NOT_ANSWERED
            )
        }
        quizDao.insertQuizWithProblems(
            userId = userId,
            problems = problemEntities
        )
    }

    override suspend fun getQuizProblemCount(quizId: String): Int =
        withContext(context = dispatcher) {
            quizDao.getQuizProblemCount(quizId) ?: 0
        }

    override fun getQuizProblemCountFlow(quizId: String): Flow<Int> =
        quizDao.getQuizProblemCountFlow(quizId).map { it ?: 0 }
            .flowOn(context = dispatcher)


    override suspend fun deleteQuizWithProblems(quizId: String) =
        withContext(context = dispatcher){
            quizDao.deleteQuizWithProblems(quizId)
        }

    override suspend fun getQuizNumber(quizId: String): Int =
        withContext(context = dispatcher) {
            quizDao.getQuizNumber(quizId) ?: 0
        }

    override fun getQuizProblemsFlow(quizId: String): Flow<List<Problem>> =
        problemDao.getQuizProblemsFlow(quizId = quizId)
            .map { list -> list.map { entity -> entity.toDomain() } }
            .flowOn(context = dispatcher)

    override fun getProblemByIdFlow(problemId: String): Flow<Problem?> =
        problemDao.getProblemFlowById(problemId = problemId)
            .map { localProblem -> localProblem?.toDomain() } 
            .flowOn(context = dispatcher)

    override suspend fun updateProblem(
        problemId: String,
        userAnswer: String?,
        answerStatus: AnswerStatus
    ) {
        withContext(context = dispatcher) {
            problemDao.updateProblem(
                problemId = problemId,
                userAnswer = userAnswer,
                answerStatus = answerStatus
            )
        }
    }

    override suspend fun getNextProblemId(problem: Problem): String? =
        withContext(context = dispatcher) {
            problemDao.getQuizProblemByNumber(problem.quizId, problemNumber = problem.problemNumber + 1)?.id
        }

    override suspend fun getPreviousProblemId(problem: Problem): String? =
        withContext(context = dispatcher) {
            problemDao.getQuizProblemByNumber(problem.quizId, problemNumber = problem.problemNumber - 1)?.id
        }

    override suspend fun getFirstProblemId(quizId: String): String? =
        withContext(context = dispatcher) {
            problemDao.getQuizProblemByNumber(quizId, problemNumber = 1)?.id
        }

    override suspend fun getQuizScore(quizId: String): QuizScore =
        withContext(context = dispatcher) {
            val numberOfProblems = quizDao.getQuizProblemCount(quizId = quizId)
            val numberOfRightAnswers = problemDao.getQuizProblemCountByStatus(
                quizId = quizId,
                answerStatus = AnswerStatus.RIGHT_ANSWER
            )
            QuizScore(
                problemCount = numberOfProblems ?: 0,
                rightAnswers = numberOfRightAnswers
            )
        }

    override fun getQuizScoreFlow(quizId: String): Flow<QuizScore> {
        val quizScoreFlow: Flow<QuizScore> = combine(
            quizDao.getQuizProblemCountFlow(quizId = quizId),
            problemDao.getQuizProblemCountByStatusFlow(
                quizId = quizId,
                answerStatus = AnswerStatus.RIGHT_ANSWER
            ),
        ) { numberOfProblems, rightAnswers ->
            QuizScore(
                problemCount = numberOfProblems ?: 0,
                rightAnswers = rightAnswers
            )
        }
        return quizScoreFlow.flowOn(context = dispatcher)
    }

    override fun getQuizStatusDataFlow(quizId: String): Flow<QuizStatus> {
        val quizStatusFlow: Flow<QuizStatus> = combine(
            quizDao.getQuizProblemCountFlow(quizId = quizId),
            problemDao.getQuizProblemCountByStatusFlow(
                quizId = quizId,
                answerStatus = AnswerStatus.RIGHT_ANSWER
            ),
            problemDao.getQuizProblemCountByStatusFlow(
                quizId = quizId,
                answerStatus = AnswerStatus.NOT_ANSWERED
            ),
            problemDao.getQuizProblemCountByStatusFlow(
                quizId = quizId,
                answerStatus = AnswerStatus.WRONG_ANSWER
            )
        ) { numberOfProblems, rightAnswers, notAnswered, wrongAnswers ->
            QuizStatus(
                problemCount = numberOfProblems ?: 0,
                rightAnswers = rightAnswers,
                notAnswered = notAnswered,
                wrongAnswers = wrongAnswers
            )
        }
        return quizStatusFlow.flowOn(context = dispatcher)
    }


    override suspend fun getNumberOfRightAnswers(quizId: String): Int =
        withContext(context = dispatcher) {
            problemDao.getQuizProblemCountByStatus(
                quizId = quizId,
                answerStatus = AnswerStatus.RIGHT_ANSWER
            )
        }

}
