package ca.tetervak.mathtrainer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.tetervak.mathtrainer.R
import ca.tetervak.mathtrainer.ui.common.QuizTopBar
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme
import ca.tetervak.mathtrainer.ui.theme.Purple40

@Composable
fun HomeScreen(
    toQuizList: () -> Unit,
    toSettings: () -> Unit,
    onShowHelp: () -> Unit,
    viewModel: HomeViewModel,
) {

    val state: HomeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenBody(
        numberOfQuizzes = state.quizCount,
        toQuizList = toQuizList,
        toSettings = toSettings,
        onShowHelp = onShowHelp
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreenBody(
    numberOfQuizzes: Int,
    toQuizList: () -> Unit,
    toSettings: () -> Unit,
    onShowHelp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            QuizTopBar(
                title = stringResource(R.string.home),
                onHelpClick = onShowHelp,
                scrollBehavior = scrollBehavior
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
                .fillMaxWidth()
                .padding(48.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 32.sp,
                color = Purple40
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = toQuizList
            ) {

                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.list_all_quizzes)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.list_all_quizzes)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = toSettings
            ) {

                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.settings)
                )
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.first)
                )
            }
            QuizCountCard(
                numberOfQuizzes = numberOfQuizzes,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}

@Composable
fun QuizCountCard(
    numberOfQuizzes: Int,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.number_of_quizzes_n, numberOfQuizzes),
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    MathTrainerTheme{
        HomeScreenBody(
            numberOfQuizzes = 1,
            toQuizList = {},
            toSettings = {},
            onShowHelp = {}
        )
    }
}