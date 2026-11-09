package ca.tetervak.mathtrainer.ui.quiz.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.tetervak.mathtrainer.R
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.model.QuizStatus
import ca.tetervak.mathtrainer.ui.common.HomeButton
import ca.tetervak.mathtrainer.ui.common.QuizTopBar
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme
import ca.tetervak.mathtrainer.ui.theme.Purple40

@Composable
fun QuizDetailsScreen(
    viewModel: QuizDetailsViewModel,
    quizId: String,
    toHome: () -> Unit,
    toProblem: (String) -> Unit,
    toProblemList: (String, String?) -> Unit,
    toBack: () -> Unit,
    onShowHelp: () -> Unit
) {
    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }
    val uiState = viewModel.uiState.collectAsState()
    val state: QuizDetailsUiState = uiState.value

    if(state is QuizDetailsUiState.Success){
        QuizDetailsScreenBody(
            state = state,
            toHome = toHome,
            toProblem = toProblem,
            toProblemList = toProblemList,
            toBack = toBack,
            onShowHelp = onShowHelp,
            onDeleteQuizClick = {
                viewModel.deleteQuiz()
                toHome()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailsScreenBody(
    state: QuizDetailsUiState.Success,
    toHome: () -> Unit,
    toProblem: (String) -> Unit,
    toProblemList: (String, String?) -> Unit,
    toBack: () -> Unit,
    onShowHelp: () -> Unit,
    onDeleteQuizClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            QuizTopBar(
                title = stringResource(
                    id = R.string.quiz_n,
                    state.quiz.quizNumber
                ),
                scrollBehavior = scrollBehavior,
                onHelpClick = onShowHelp,
                onBackClick = toBack
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(48.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string.quiz_n,
                    state.quiz.quizNumber
                ),
                fontSize = 32.sp,
                color = Purple40
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { state.firstProblemId?.let { toProblem(it) }}
            ) {

                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.first_problem)
                )
                Icon(
                    imageVector = Icons.Filled.Start,
                    contentDescription = stringResource(R.string.first)
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { toProblemList(state.quiz.id, state.firstProblemId) }
            ) {

                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.list_all_problems)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.list)
                )
            }
            QuizStatusCard(
                quizStatus = state.quizStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)

            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDeleteQuizClick
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.delete_quiz),
                    color = Color.Red
                )
            }
            Spacer(Modifier.weight(1f))
            HomeButton(
                onClick = toHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun QuizStatusCard(quizStatus: QuizStatus, modifier: Modifier = Modifier) {
    Card(modifier = modifier){
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(all = 16.dp)) {
                Row {
                    Text(text = "Problem Count:")
                    Text(
                        text = quizStatus.problemCount.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    Text(text = "Right Answers:")
                    Text(
                        text = quizStatus.rightAnswers.toString(),
                        color = if(quizStatus.rightAnswers > 0) Color.Green else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    Text(text = "Wrong Answers:")
                    Text(
                        text = quizStatus.wrongAnswers.toString(),
                        color = if(quizStatus.wrongAnswers > 0) Color.Red else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    Text(text = "Invalid Inputs:")
                    Text(
                        text = quizStatus.invalidInputs.toString(),
                        color = if(quizStatus.invalidInputs > 0) Color.Red else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    Text(text = "Not Answered:")
                    Text(
                        text = quizStatus.notAnswered.toString(),
                        color = if(quizStatus.notAnswered > 0) Color.Blue else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row {
                    Text(text = "Answered:")
                    Text(
                        text = quizStatus.answeredCount.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }


            }
    }
}

@Composable
@Preview
fun QuizStatusPreview(){
    MathTrainerTheme {
        QuizStatusCard(
            quizStatus = QuizStatus.Preview
        )
    }
}

@Composable
@Preview
fun QuizDetailsScreenBodyPreview(){
    MathTrainerTheme {
        QuizDetailsScreenBody(
            state = QuizDetailsUiState.Success(
                quiz = Quiz.Preview,
                quizStatus = QuizStatus.Preview,
                firstProblemId = null
            ),
            toHome = {},
            toProblem = {},
            toProblemList = { _, _->},
            toBack = {},
            onShowHelp = {},
            onDeleteQuizClick = {}
        )
    }
}