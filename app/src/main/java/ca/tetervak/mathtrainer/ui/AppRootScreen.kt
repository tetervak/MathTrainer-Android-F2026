/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.tetervak.mathtrainer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ca.tetervak.mathtrainer.ui.common.AboutDialog
import ca.tetervak.mathtrainer.ui.problem.details.ProblemDetailsScreen
import ca.tetervak.mathtrainer.ui.home.HomeScreen
import ca.tetervak.mathtrainer.ui.home.HomeViewModel
import ca.tetervak.mathtrainer.ui.problem.details.ProblemDetailsViewModel
import ca.tetervak.mathtrainer.ui.problem.list.ProblemListScreen
import ca.tetervak.mathtrainer.ui.problem.list.ProblemListViewModel
import ca.tetervak.mathtrainer.ui.quiz.details.QuizDetailsScreen
import ca.tetervak.mathtrainer.ui.quiz.details.QuizDetailsViewModel
import ca.tetervak.mathtrainer.ui.quiz.list.QuizListScreen
import ca.tetervak.mathtrainer.ui.quiz.list.QuizListViewModel
import ca.tetervak.mathtrainer.ui.settings.SettingsScreen
import ca.tetervak.mathtrainer.ui.settings.SettingsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Home

@Serializable
object Settings

@Serializable
data class ProblemList(
    val quizId: String,
    val selectedId: String? = null,
)

@Serializable
data class ProblemDetails(val problemId: String)

@Serializable
data class QuizDetails(
    val quizId: String,
)

@Serializable
object QuizList

@Composable
fun AppRootScreen() {

    var showAboutDialog: Boolean by rememberSaveable {
        mutableStateOf(false)
    }

    val navController = rememberNavController()

    val onShowHelp: () -> Unit = { showAboutDialog = true }
    val onDismissHelp: () -> Unit = { showAboutDialog = false }

    val toBack: () -> Unit = { navController.popBackStack() }
    val toHome: () -> Unit = { navController.popBackStack(route = Home, inclusive = false) }
    val toQuizList: () -> Unit = { navController.navigate(route = QuizList) }
    val toSettings: () -> Unit = { navController.navigate(route = Settings) }
    val toQuiz: (String) -> Unit = { quizId ->
        navController.navigate(route = QuizDetails(quizId = quizId))
    }
    val toProblem: (String) -> Unit = { problemId ->
        navController.navigate(route = ProblemDetails(problemId = problemId))
    }
    val toProblemList: (String, String?) -> Unit = { quizId, selectedId ->
        navController.navigate(route = ProblemList(quizId = quizId, selectedId = selectedId))
    }

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            val viewModel: HomeViewModel = koinViewModel()
            HomeScreen(
                viewModel = viewModel,
                toQuizList = toQuizList,
                toSettings = toSettings,
                onShowHelp = onShowHelp
            )
        }
        composable<QuizList> {
            val viewModel: QuizListViewModel = koinViewModel()
            QuizListScreen(
                viewModel = viewModel,
                toHome = toHome,
                toQuiz = toQuiz,
                toBack = toBack,
                onShowHelp = onShowHelp,
            )
        }
        composable<ProblemList> { backStackEntry ->
            val viewModel: ProblemListViewModel = koinViewModel()
            val problemList: ProblemList = backStackEntry.toRoute()
            val quizId: String = problemList.quizId
            val selectedId: String? = problemList.selectedId
            ProblemListScreen(
                viewModel = viewModel,
                quizId = quizId,
                selectedId = selectedId,
                toProblem = toProblem,
                toHome = toHome,
                onShowHelp = onShowHelp,
                toBack = toBack,
                toQuiz = toQuiz
            )
        }
        composable<ProblemDetails> { backStackEntry ->
            val viewModel: ProblemDetailsViewModel = koinViewModel()
            val problemDetails: ProblemDetails = backStackEntry.toRoute()
            val problemId: String = problemDetails.problemId
            ProblemDetailsScreen(
                viewModel = viewModel,
                problemId = problemId,
                onShowHelp = onShowHelp,
                toHome = toHome,
                toProblemList = toProblemList,
                toProblem = toProblem,
                toQuiz = toQuiz,
                toBack = toBack
            )
        }
        composable<QuizDetails> { backStackEntry ->
            val viewModel: QuizDetailsViewModel = koinViewModel()
            val quizDetails: QuizDetails = backStackEntry.toRoute()
            val quizId: String = quizDetails.quizId
            QuizDetailsScreen(
                viewModel = viewModel,
                quizId = quizId,
                toHome = toHome,
                toProblem = toProblem,
                toProblemList = toProblemList,
                toBack = toBack,
                onShowHelp = onShowHelp
            )
        }
        composable<Settings> {
            val viewModel: SettingsViewModel = koinViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onShowHelp = onShowHelp,
                toHome = toHome,
                toBack = toBack
            )
        }
    }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = onDismissHelp)
    }
}