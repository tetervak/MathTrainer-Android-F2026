package ca.tetervak.mathtrainer.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.tetervak.mathtrainer.R
import ca.tetervak.mathtrainer.domain.model.ProblemGeneration
import ca.tetervak.mathtrainer.domain.model.UserPreferences
import ca.tetervak.mathtrainer.ui.common.HomeButton
import ca.tetervak.mathtrainer.ui.common.QuizTopBar
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme
import ca.tetervak.mathtrainer.ui.theme.Purple40

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    toHome: () -> Unit,
    onShowHelp: () -> Unit,
    toBack: () -> Unit
){
    val uiState: UserPreferences by viewModel.uiState.collectAsState()

    SettingsScreenBody(
        numberOfProblems = uiState.numberOfProblems,
        problemGeneration = uiState.problemGeneration,
        onNumberOfProblemsChange = viewModel::saveNumberOfProblems,
        onProblemGenerationChange = viewModel::saveProblemGeneration,
        toHome = toHome,
        onShowHelp = onShowHelp,
        toBack = toBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenBody(
    numberOfProblems: Int,
    problemGeneration: ProblemGeneration,
    onNumberOfProblemsChange: (Int) -> Unit,
    onProblemGenerationChange: (ProblemGeneration) -> Unit,
    toHome: () -> Unit,
    onShowHelp: () -> Unit,
    toBack: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            QuizTopBar(
                title = stringResource(R.string.settings),
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth().padding(48.dp)
            ){
                Text(
                    text = "New Quiz Settings",
                    fontSize = 32.sp,
                    color = Purple40
                )
                NumberOfProblemsInput(
                    numberOfProblems = numberOfProblems,
                    onChange = onNumberOfProblemsChange
                )
                ProblemGenerationInput(
                    problemGeneration = problemGeneration,
                    onChange = onProblemGenerationChange
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
            HomeButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = toHome,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberOfProblemsInput(
    numberOfProblems: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectExpanded: Boolean by remember { mutableStateOf(false) }
    val choices = listOf(5, 10, 15, 20)
    val problems: String = stringResource(R.string.problems)
    val selectOptions: List<String> = choices.map { number -> "$number $problems" }

    val selectedText = selectOptions[choices.indexOf(numberOfProblems)]

    ExposedDropdownMenuBox(
        expanded = selectExpanded,
        onExpandedChange = { selectExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedText,
            onValueChange = { },
            label = {
                Text(
                    text = stringResource(R.string.number_of_problems),
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = selectExpanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = 20.sp,
                color = colorResource(id = R.color.purple_500)
            ),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = selectExpanded,
            onDismissRequest = {
                selectExpanded = false
            }
        ) {
            choices.forEachIndexed { index, choice ->
                DropdownMenuItem(
                    onClick = {
                        selectExpanded = false
                        onChange(choice)
                    },
                    text = {
                        Text(text = selectOptions[index], fontSize = 20.sp)
                    }
                )
            }
        }
    }
}

@Composable
fun ProblemGenerationInput(
    problemGeneration: ProblemGeneration,
    onChange: (ProblemGeneration) -> Unit
) {
    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
        Text(
            text = stringResource(R.string.problem_generation),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onChange(ProblemGeneration.LOCAL) }
        ) {
            RadioButton(
                selected = problemGeneration == ProblemGeneration.LOCAL,
                onClick = null,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(id = R.string.local),
                fontSize = 18.sp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onChange(ProblemGeneration.REMOTE) }
        ) {
            RadioButton(
                selected = problemGeneration == ProblemGeneration.REMOTE,
                onClick = null,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(id = R.string.remote),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
@Preview
fun SettingsScreenBodyPreview(){
    MathTrainerTheme {
        SettingsScreenBody(
            numberOfProblems = 10,
            problemGeneration = ProblemGeneration.LOCAL,
            onNumberOfProblemsChange = {},
            onProblemGenerationChange = {},
            toHome = {},
            onShowHelp = {},
            toBack = {}
        )
    }
}