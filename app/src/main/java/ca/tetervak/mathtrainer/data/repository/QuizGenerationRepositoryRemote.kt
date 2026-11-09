package ca.tetervak.mathtrainer.data.repository

import ca.tetervak.mathtrainer.data.remote.RemoteQuiz
import ca.tetervak.mathtrainer.data.remote.RemoteProblem
import ca.tetervak.mathtrainer.data.remote.RandomQuizApi
import ca.tetervak.mathtrainer.domain.model.AlgebraProblem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizGenerationRepositoryRemote(
    private val randomQuizApi: RandomQuizApi
) {

    suspend fun getRandomQuizProblems(numberOfProblems: Int): List<AlgebraProblem> =
        withContext(Dispatchers.IO) {
            val quiz: RemoteQuiz = randomQuizApi.getRandomQuiz(numberOfProblems)
            val problems: List<RemoteProblem> = quiz.problems
            problems.map { problem -> problem.toAlgebraProblem() }
        }

    private fun RemoteProblem.toAlgebraProblem(): AlgebraProblem {
        return AlgebraProblem.fromText(this.text)
    }
}

