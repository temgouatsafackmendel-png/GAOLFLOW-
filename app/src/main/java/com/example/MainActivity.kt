package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.GoalTemplate
import com.example.ui.components.AntiTemptationShieldDialog
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.GratificationPhilosophyDialog
import com.example.ui.components.SmartGuideDialog
import com.example.ui.components.TemplatesDialog
import com.example.ui.screens.AddEditGoalScreen
import com.example.ui.screens.DisciplineJourneyScreen
import com.example.ui.screens.GoalDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StatsAchievementsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.GoalViewModel

sealed class Screen {
    object Home : Screen()
    data class Detail(val goalId: Long) : Screen()
    data class AddEdit(val goalId: Long? = null, val template: GoalTemplate? = null) : Screen()
    object DisciplineJourney : Screen()
    object Stats : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GoalApp()
            }
        }
    }
}

@Composable
fun GoalApp(viewModel: GoalViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showTemplatesDialog by remember { mutableStateOf(false) }
    var showSmartGuideDialog by remember { mutableStateOf(false) }
    var showAntiTemptationDialog by remember { mutableStateOf(false) }
    var showGratificationPhilosophyDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val isRootScreen = currentScreen is Screen.Home ||
            currentScreen is Screen.DisciplineJourney ||
            currentScreen is Screen.Stats

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (isRootScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is Screen.Home,
                            onClick = { currentScreen = Screen.Home },
                            icon = { Icon(Icons.Default.TrackChanges, contentDescription = "Aujourd'hui") },
                            label = { Text("Aujourd'hui", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryIndigo.copy(alpha = 0.15f),
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.DisciplineJourney,
                            onClick = { currentScreen = Screen.DisciplineJourney },
                            icon = { Icon(Icons.Default.Shield, contentDescription = "5 Niveaux") },
                            label = { Text("5 Niveaux", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryIndigo.copy(alpha = 0.15f),
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo
                            ),
                            modifier = Modifier.testTag("nav_discipline_journey")
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Stats,
                            onClick = { currentScreen = Screen.Stats },
                            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Trophées") },
                            label = { Text("Trophées", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryIndigo.copy(alpha = 0.15f),
                                selectedIconColor = PrimaryIndigo,
                                selectedTextColor = PrimaryIndigo
                            ),
                            modifier = Modifier.testTag("nav_stats")
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { goalId ->
                            currentScreen = Screen.Detail(goalId)
                        },
                        onNavigateToAddGoal = {
                            currentScreen = Screen.AddEdit()
                        },
                        onOpenTemplates = {
                            showTemplatesDialog = true
                        },
                        onOpenStats = {
                            currentScreen = Screen.Stats
                        },
                        onOpenSmartGuide = {
                            showSmartGuideDialog = true
                        },
                        onOpenDisciplineJourney = {
                            currentScreen = Screen.DisciplineJourney
                        },
                        onOpenAntiTemptation = {
                            showAntiTemptationDialog = true
                        },
                        onOpenGratificationPhilosophy = {
                            showGratificationPhilosophyDialog = true
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                is Screen.Detail -> {
                    GoalDetailScreen(
                        goalId = screen.goalId,
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.Home },
                        onNavigateToEdit = { id ->
                            currentScreen = Screen.AddEdit(goalId = id)
                        }
                    )
                }
                is Screen.AddEdit -> {
                    AddEditGoalScreen(
                        goalId = screen.goalId,
                        initialTemplate = screen.template,
                        viewModel = viewModel,
                        onNavigateBack = {
                            currentScreen = if (screen.goalId != null) Screen.Detail(screen.goalId) else Screen.Home
                        }
                    )
                }
                is Screen.DisciplineJourney -> {
                    DisciplineJourneyScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.Home }
                    )
                }
                is Screen.Stats -> {
                    StatsAchievementsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.Home }
                    )
                }
            }
        }

        // Templates Selection Dialog
        if (showTemplatesDialog) {
            TemplatesDialog(
                onSelectTemplate = { template ->
                    showTemplatesDialog = false
                    currentScreen = Screen.AddEdit(template = template)
                },
                onDismiss = { showTemplatesDialog = false }
            )
        }

        // S.M.A.R.T. Methodology Guide
        if (showSmartGuideDialog) {
            SmartGuideDialog(
                onDismiss = { showSmartGuideDialog = false }
            )
        }

        // Anti-Temptation Shield Dialog (Instant Gratification Stopper)
        if (showAntiTemptationDialog) {
            AntiTemptationShieldDialog(
                onResistConfirmed = { temptationName ->
                    viewModel.recordResistedTemptation(temptationName)
                },
                onDismiss = { showAntiTemptationDialog = false }
            )
        }

        // Philosophy of Delayed Gratification Dialog
        if (showGratificationPhilosophyDialog) {
            GratificationPhilosophyDialog(
                onDismiss = { showGratificationPhilosophyDialog = false }
            )
        }

        // Celebration dialog when an objective is achieved!
        uiState.celebratedGoal?.let { achieved ->
            CelebrationDialog(
                goalWithDetails = achieved,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }
}
