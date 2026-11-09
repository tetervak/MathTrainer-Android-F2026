package ca.tetervak.mathtrainer.ui.problem.details

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.tetervak.mathtrainer.R
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.AnswerStatus
import ca.tetervak.mathtrainer.ui.common.QuizButton
import ca.tetervak.mathtrainer.ui.common.QuizTopBar
import ca.tetervak.mathtrainer.ui.common.ScoreCard
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme

@Composable
fun ProblemDetailsScreen(
    viewModel: ProblemDetailsViewModel,
    problemId: String,
    onShowHelp: () -> Unit,
    toHome: () -> Unit,
    toProblemList: (String, String?) -> Unit,
    toProblem: (String) -> Unit,
    toQuiz: (String) -> Unit,
    toBack: () -> Unit
) {
    LaunchedEffect(problemId) {
        viewModel.loadProblem(problemId)
    }
    val detailsUiState: State<ProblemDetailsUiState> = viewModel.uiState.collectAsState()
    val state = detailsUiState.value
    if (state is ProblemDetailsUiState.Success) {
        ProblemDetailsScreenBody(
            state = state,
            userAnswerInput = viewModel.answerInput,
            onChangeUserAnswerInput = viewModel::updateAnswerInput,
            onSubmit = viewModel::onSubmit,
            onShowHelp = onShowHelp,
            toHome = toHome,
            toProblemList = toProblemList,
            toProblem = toProblem,
            toQuiz = toQuiz,
            toBack = toBack
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailsScreenBody(
    state: ProblemDetailsUiState.Success,
    userAnswerInput: String,
    onChangeUserAnswerInput: (String) -> Unit,
    onSubmit: () -> Unit,
    onShowHelp: () -> Unit,
    toHome: () -> Unit,
    toProblemList: (String, String?) -> Unit,
    toProblem: (String) -> Unit,
    toQuiz: (String) -> Unit,
    toBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            QuizTopBar(
                title = stringResource(
                    id = R.string.quiz_n_problem_n,
                    state.quizNumber, state.problem.problemNumber
                ),
                scrollBehavior = scrollBehavior,
                onHelpClick = onShowHelp,
                onBackClick = toBack
            )
        },
        bottomBar = {
            DetailsBottomBar(
                onHomeClick = toHome,
                onListClick = { toProblemList(state.problem.quizId, state.problem.id ) },
                onFirstClick = { state.firstProblemId?.let{toProblem(it)} }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProblemLayout(
                onUserAnswerChanged = onChangeUserAnswerInput,
                problemNumber = state.problem.problemNumber,
                numberOfProblems = state.numberOfProblems,
                userAnswer = userAnswerInput,
                onKeyboardDone = onSubmit,
                currentProblemText = state.problem.text,
                currentProblemStatus = state.problem.answerStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSubmit
                ) {
                    Text(
                        modifier = Modifier.padding(end = 8.dp),
                        text = stringResource(R.string.submit),
                        fontSize = 16.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedButton(
                        onClick = { state.previousProblemId?.let{ toProblem(it) } },
                        modifier = Modifier.weight(1f),
                        enabled = state.problem.problemNumber > 1
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.previous),
                            fontSize = 16.sp
                        )
                    }
                    OutlinedButton(
                        onClick = { state.nextProblemId?.let{ toProblem(it) } },
                        modifier = Modifier.weight(1f),
                        enabled = state.problem.problemNumber < state.numberOfProblems
                    ) {
                        Text(
                            text = stringResource(R.string.next),
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                            contentDescription = null
                        )
                    }
                }

            }
            ScoreCard(
                rightAnswers = state.numberOfRightAnswers,
                numberOfProblems = state.numberOfProblems,
                modifier = Modifier.padding(20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            QuizButton(
                quizNumber = state.quizNumber,
                onClick = { toQuiz(state.problem.quizId) },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
fun DetailsBottomBar(
    onHomeClick: () -> Unit,
    onListClick: () -> Unit,
    onFirstClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.home)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = onListClick,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.list)
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = onFirstClick,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Start,
                    contentDescription = stringResource(R.string.first)
                )
            }
        )
    }
}

@Composable
fun ProblemLayout(
    currentProblemText: String,
    problemNumber: Int,
    numberOfProblems: Int,
    currentProblemStatus: AnswerStatus,
    userAnswer: String,
    onUserAnswerChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
                text = stringResource(R.string.problem_count, problemNumber, numberOfProblems),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = currentProblemText,
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = userAnswer,
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.surface,
                ),
                onValueChange = onUserAnswerChanged,
                label = {
                    Text(stringResource(R.string.enter_your_answer))
                },
                placeholder = {
                    when (currentProblemStatus) {
                        AnswerStatus.WRONG_ANSWER -> {
                            Text(
                                text = stringResource(R.string.wrong_answer_try_again),
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        AnswerStatus.INVALID_INPUT -> {
                            Text(
                                text = stringResource(R.string.invalid_input_try_again),
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        AnswerStatus.NOT_ANSWERED -> {
                            Text(stringResource(R.string.enter_your_answer))
                        }

                        else -> {}
                    }
                },
                isError = currentProblemStatus == AnswerStatus.WRONG_ANSWER ||
                        currentProblemStatus == AnswerStatus.INVALID_INPUT,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() }
                )
            )
            when (currentProblemStatus) {
                AnswerStatus.NOT_ANSWERED -> Text(
                    text = stringResource(R.string.not_answered),
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    color = Color.Blue
                )

                AnswerStatus.RIGHT_ANSWER -> Text(
                    text = stringResource(R.string.right_answer),
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    color = Color.Green
                )

                AnswerStatus.WRONG_ANSWER -> Text(
                    text = stringResource(R.string.wrong_answer),
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    color = Color.Red
                )

                AnswerStatus.INVALID_INPUT -> Text(
                    text = stringResource(R.string.invalid_input),
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp,
                    color = Color.Red
                )

            }
        }
    }
}

/*
 * Creates and shows an AlertDialog with final score.
 */
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalActivity.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = stringResource(R.string.you_scored, score)) },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MathTrainerTheme {
        ProblemDetailsScreenBody(
            state = ProblemDetailsUiState.Success(
                problem = Problem.Preview,
                quizNumber = 2,
                numberOfProblems = 5,
                numberOfRightAnswers = 3,
                firstProblemId = null,
                previousProblemId = null,
                nextProblemId = null
            ),
            userAnswerInput = "",
            onChangeUserAnswerInput = {},
            onSubmit = {},
            onShowHelp = {},
            toHome = {},
            toProblemList = { _, _ ->},
            toProblem = {},
            toQuiz = {},
            toBack = {}
        )
    }
}