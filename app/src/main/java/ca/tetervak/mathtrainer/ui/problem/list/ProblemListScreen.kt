package ca.tetervak.mathtrainer.ui.problem.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.tetervak.mathtrainer.R
import ca.tetervak.mathtrainer.domain.model.Problem
import ca.tetervak.mathtrainer.domain.model.AnswerStatus
import ca.tetervak.mathtrainer.ui.common.HomeButton
import ca.tetervak.mathtrainer.ui.common.QuizButton
import ca.tetervak.mathtrainer.ui.common.QuizTopBar
import ca.tetervak.mathtrainer.ui.common.ScoreCard
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme

@Composable
fun ProblemListScreen(
    viewModel: ProblemListViewModel,
    quizId: String,
    selectedId: String?,
    toProblem: (String) -> Unit,
    toQuiz: (String) -> Unit,
    toHome: () -> Unit,
    onShowHelp: () -> Unit,
    toBack: () -> Unit
) {
    LaunchedEffect(quizId) {
        viewModel.loadProblems(quizId)
    }
    val uiState = viewModel.uiState.collectAsState()
    val state: ProblemListUiState = uiState.value

    if (state is ProblemListUiState.Success)
        ProblemListScreenBody(
            state = state,
            selectedId = selectedId,
            toProblem = toProblem,
            toQuiz = toQuiz,
            toHome = toHome,
            onShowHelp = onShowHelp,
            toBack = toBack
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemListScreenBody(
    state: ProblemListUiState.Success,
    selectedId: String?,
    toProblem: (String) -> Unit,
    toQuiz: (String) -> Unit,
    toHome: () -> Unit,
    onShowHelp: () -> Unit,
    toBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = selectedId) {
        if (selectedId != null) {
            val index = state.problemList.indexOfFirst { it.id == selectedId }
            if (index != -1) {
                listState.scrollToItem(index)
            }
        }
    }

    Scaffold(
        topBar = {
            QuizTopBar(
                title = stringResource(R.string.quiz_n_problems, state.quizNumber),
                onHelpClick = onShowHelp,
                onBackClick = toBack,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                state = listState, // scroll to the selected problem
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items = state.problemList) { problem ->
                    ProblemListItem(
                        onClick = { toProblem(problem.id) },
                        problem = problem,
                        selected = problem.id == selectedId
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeButton(
                    onClick = toHome,
                    modifier = Modifier.padding(8.dp)
                )
                QuizButton(
                    quizNumber = state.quizNumber,
                    onClick = { toQuiz(state.problemList.first().quizId) },
                    modifier = Modifier.padding(8.dp)
                )
                ScoreCard(
                    rightAnswers = state.rightAnswers,
                    numberOfProblems = state.problemList.size,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@Composable
fun ProblemListItem(
    problem: Problem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {

    val frameColor = if (selected) Color.DarkGray else Color.LightGray
    val elevation = if (selected) 4.dp else 0.dp

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, frameColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${problem.problemNumber}.",
                fontSize = 24.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
                color = Color.DarkGray
            )
            Text(
                text = problem.text,
                fontSize = 24.sp
            )
            when (problem.answerStatus) {
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

@Preview
@Composable
fun ProblemListItemPreview() {
    MathTrainerTheme {
        ProblemListItem(
            problem = Problem.Preview,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun ProblemListItemSelectedPreview() {
    MathTrainerTheme {
        ProblemListItem(
            problem = Problem.Preview,
            onClick = {},
            selected = true
        )
    }
}

@Preview
@Composable
fun ProblemListScreenBodyPreview() {
    MathTrainerTheme {
        ProblemListScreenBody(
            state = ProblemListUiState.Success(
                problemList = List(5) {
                    Problem.Preview
                },
                quizNumber = 2,
                rightAnswers = 3,
            ),
            selectedId = 3.toString(),
            toProblem = {},
            toHome = {},
            onShowHelp = {},
            toQuiz = {},
            toBack = {}
        )
    }
}