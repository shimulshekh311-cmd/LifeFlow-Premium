package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeFlowViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    LifeFlowAppScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "tab_dashboard"),
    HABIT("Habit", Icons.Default.CheckCircle, "tab_habit"),
    STUDY("Study", Icons.Default.Timer, "tab_study"),
    EXERCISE("Exercise", Icons.Default.FitnessCenter, "tab_exercise"),
    STATISTICS("Statistics", Icons.Default.BarChart, "tab_statistics")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeFlowAppScreen(modifier: Modifier = Modifier) {
    val viewModel: LifeFlowViewModel = viewModel()

    // Collect Room database flows with full lifecycle safety
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val studySessions by viewModel.studySessions.collectAsStateWithLifecycle()
    val exerciseSessions by viewModel.exerciseSessions.collectAsStateWithLifecycle()

    // Timer state
    val timerSecondsLeft by viewModel.studyTimerSecondsLeft.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isStudyTimerRunning.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedStudySubject.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }

    // Pre-calculate metrics for dashboard display
    val habitsCompletedCount = habits.count { it.isCompletedToday }
    val habitsTotalCount = habits.size
    val totalStudyMinutesToday = studySessions.sumOf { it.durationMinutes }
    val exerciseCountToday = exerciseSessions.size

    Column(
        modifier = modifier
            .background(WarmBg)
            .testTag("main_container")
    ) {
        // Top App Bar conforming to Material 3 standard
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = currentTab.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnBgCharcoal,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.testTag("top_bar_title")
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { /* Open menu or info */ },
                    modifier = Modifier.testTag("top_bar_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = OnBgCharcoal
                    )
                }
            },
            actions = {
                Surface(
                    shape = CircleShape,
                    color = SecondaryBadge,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = "Mindful",
                            tint = PolishPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = WarmBg
            )
        )

        // Tab Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        habitsCompletedCount = habitsCompletedCount,
                        habitsTotalCount = habitsTotalCount,
                        totalStudyMinutes = totalStudyMinutesToday,
                        exerciseSessionsCount = exerciseCountToday,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                AppTab.HABIT -> {
                    HabitScreen(
                        habits = habits,
                        onAddHabit = { name, desc, category -> viewModel.addHabit(name, desc, category) },
                        onToggleHabit = { habit -> viewModel.toggleHabit(habit) },
                        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                AppTab.STUDY -> {
                    StudyScreen(
                        studySessions = studySessions,
                        timerSecondsLeft = timerSecondsLeft,
                        isTimerRunning = isTimerRunning,
                        selectedSubject = selectedSubject,
                        onSelectSubject = { viewModel.selectStudySubject(it) },
                        onSetDuration = { viewModel.setStudyTimerDuration(it) },
                        onToggleTimer = { viewModel.toggleStudyTimer() },
                        onResetTimer = { viewModel.resetStudyTimer() },
                        onDeleteSession = { id -> viewModel.deleteStudySession(id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                AppTab.EXERCISE -> {
                    ExerciseScreen(
                        exerciseSessions = exerciseSessions,
                        onAddSession = { name, cat, dur -> viewModel.addExerciseSession(name, cat, dur) },
                        onDeleteSession = { id -> viewModel.deleteExerciseSession(id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                AppTab.STATISTICS -> {
                    StatisticsScreen(
                        habits = habits,
                        studySessions = studySessions,
                        exerciseSessions = exerciseSessions,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Bottom Navigation Bar with Standard M3 Layout and safe drawing padding
        NavigationBar(
            containerColor = BottomNavBg,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .testTag("bottom_nav")
        ) {
            AppTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { currentTab = tab },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnSecondaryBadge,
                        selectedTextColor = OnBgCharcoal,
                        indicatorColor = SecondaryBadge,
                        unselectedIconColor = SupportText.copy(alpha = 0.7f),
                        unselectedTextColor = SupportText.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag(tab.tag)
                )
            }
        }
    }
}
