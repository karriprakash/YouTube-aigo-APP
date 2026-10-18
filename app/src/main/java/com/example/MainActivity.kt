package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.firebase.configureAppCheck
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

sealed interface AppScreen {
    data object Home : AppScreen
    data object Editor : AppScreen
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with intent before setContent or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppContent(viewModel: MainViewModel) {
    val activeProject by viewModel.activeProject.collectAsState()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    Crossfade(
        targetState = if (activeProject != null && currentScreen == AppScreen.Editor) AppScreen.Editor else AppScreen.Home,
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            AppScreen.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenProject = { project ->
                        viewModel.selectProject(project)
                        currentScreen = AppScreen.Editor
                    }
                )
            }
            AppScreen.Editor -> {
                activeProject?.let { project ->
                    VideoEditorScreen(
                        viewModel = viewModel,
                        project = project,
                        onNavigateBack = {
                            currentScreen = AppScreen.Home
                        }
                    )
                } ?: run {
                    currentScreen = AppScreen.Home
                }
            }
        }
    }
}
