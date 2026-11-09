package ca.tetervak.mathtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ca.tetervak.mathtrainer.ui.AppRootScreen
import ca.tetervak.mathtrainer.ui.theme.MathTrainerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathTrainerTheme {
                AppRootScreen()
            }
        }
    }
}