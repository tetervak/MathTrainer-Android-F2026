package ca.tetervak.mathtrainer.data.repository

import ca.tetervak.mathtrainer.data.factory.AlgebraProblemFactory
import ca.tetervak.mathtrainer.domain.model.AlgebraProblem

class QuizGenerationRepositoryLocal(
    private val problemFactory: AlgebraProblemFactory
) {

    fun getRandomQuizProblems(numberOfProblems: Int): List<AlgebraProblem> =
        List(size = numberOfProblems) {
            problemFactory.createRandomProblem()
        }
}