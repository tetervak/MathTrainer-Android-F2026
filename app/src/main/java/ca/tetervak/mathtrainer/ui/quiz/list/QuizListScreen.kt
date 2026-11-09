package ca.tetervak.mathtrainer.ui.quiz.list

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.tetervak.mathtrainer.R
import ca.tetervak.mathtrainer.domain.model.Quiz
import ca.tetervak.mathtrainer.domain.model.QuizScore
import ca.tetervak.mathtrainer.ui.common.HomeButton
import ca.tetervak.mathtrainer.ui.common.QuizTopBar
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme

@Composable
fun QuizListScreen(
    viewModel: QuizListViewModel,
    toHome: () -> Unit,
    toQuiz: (String) -> Unit,
    toBack: () -> Unit,
    onShowHelp: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()
    val stateList = uiState.value.stateList

    QuizListScreenBody(
        stateList = stateList,
        onAddNewQuizClick = viewModel::addNewQuiz,
        toHome = toHome,
        toQuiz = toQuiz,
        toBack = toBack,
        onShowHelp = onShowHelp
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreenBody(
    stateList: List<QuizListItemUiState>,
    onAddNewQuizClick: () -> Unit,
    toHome: () -> Unit,
    toQuiz: (String) -> Unit,
    toBack: () -> Unit,
    onShowHelp: () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            QuizTopBar(
                title = stringResource(R.string.quiz_list),
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
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items = stateList) { itemState ->
                    QuizListItem(
                        onClick = { toQuiz(itemState.quiz.id) },
                        quiz = itemState.quiz,
                        quizScore = itemState.quizScore
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                HomeButton(
                    onClick = toHome,
                )
                OutlinedButton(
                    onClick = onAddNewQuizClick,
                ){
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.add_new_quiz),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizListItem(
    onClick: () -> Unit,
    quiz: Quiz,
    quizScore: QuizScore,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(width = 2.dp, color = Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.quiz_n, quiz.quizNumber),
                fontSize = 24.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(R.string.score, quizScore.rightAnswers, quizScore.problemCount),
                fontSize = 18.sp,

            )
        }
    }
}

@Composable
@Preview
fun QuizListItemPreview() {
    MathTrainerTheme{
        QuizListItem(
            onClick = {},
            quiz = Quiz.Preview,
            quizScore = QuizScore.Preview,
        )
    }
}

@Composable
@Preview
fun QuizListScreenBodyPreview() {
    MathTrainerTheme {
        QuizListScreenBody(
            stateList = List( size = 5){ index ->
                QuizListItemUiState(
                    quiz = Quiz.Preview,
                    quizScore = QuizScore(
                        problemCount = index + 5,
                        rightAnswers = index + 4
                    ),
                )
            },
            onAddNewQuizClick = {},
            toHome = {},
            toQuiz = {},
            toBack = {},
            onShowHelp = {}
        )
    }
}