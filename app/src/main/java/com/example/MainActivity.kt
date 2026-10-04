package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.ui.AppScreen
import com.example.ui.EduMentorViewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.components.AppWebSideNav
import com.example.ui.components.GlobalSearchDialog
import com.example.ui.components.JarvisAssistantSheet
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CurriculumScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.ErrorRecoveryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearningPathIntelligenceScreen
import com.example.ui.screens.MediaLabScreen
import com.example.ui.screens.MistakeNotebookScreen
import com.example.ui.screens.NotesAndFlashcardsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.PracticeQuizScreen
import com.example.ui.screens.PreviousPapersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressAndGamificationScreen
import com.example.ui.screens.StudyPlannerScreen
import com.example.ui.screens.SubjectSelectionScreen
import com.example.ui.screens.TeacherDashboardScreen
import com.example.ui.screens.TextbookLibraryScreen
import com.example.ui.screens.TutorChatScreen
import com.example.ui.screens.VoiceLiveScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: EduMentorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: EduMentorViewModel) {
    // Simple state variable to control Subject Selection screen visibility
    var isSubjectSelectionVisible by remember { mutableStateOf(false) }

    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isJarvisOpen by viewModel.isJarvisSheetOpen.collectAsState()
    val isSearchOpen by viewModel.isSearchDialogOpen.collectAsState()

    // Using simple state variable and if/else logic in the main Activity as requested
    if (isSubjectSelectionVisible) {
        SubjectSelectionScreen(
            viewModel = viewModel,
            onBack = {
                isSubjectSelectionVisible = false
            },
            onSubjectChosen = { chosenSubject, _ ->
                viewModel.selectedSubject.value = chosenSubject
                viewModel.selectedChapter.value = chosenSubject.chapters.firstOrNull()
                isSubjectSelectionVisible = false
                viewModel.navigateTo(AppScreen.CURRICULUM)
            }
        )
    } else {
        // Handle Android system Back button
        BackHandler(enabled = currentScreen != AppScreen.HOME) {
            viewModel.navigateBack()
        }

        val topBarTitle = when (currentScreen) {
            AppScreen.ONBOARDING -> "Academic Setup"
            AppScreen.HOME -> "Vidya AI"
            AppScreen.TUTOR_CHAT -> "AI Personal Tutor"
            AppScreen.CURRICULUM -> "Curriculum"
            AppScreen.PRACTICE_QUIZ -> "Practice & Quizzes"
            AppScreen.STUDY_PLANNER -> "Study Planner"
            AppScreen.VOICE_LIVE -> "Live Voice Tutor"
            AppScreen.NOTES_FLASHCARDS -> "Notes & Flashcards"
            AppScreen.MEDIA_LAB -> "AI Media Lab"
            AppScreen.PROGRESS -> "Progress & Badges"
            AppScreen.PARENT_VIEW -> "Parent Guardian"
            AppScreen.TEACHER_VIEW -> "Teacher Suite"
            AppScreen.PROFILE -> "Student Profile"
            AppScreen.DOCUMENTS -> "Document Intelligence"
            AppScreen.MISTAKE_NOTEBOOK -> "Mistake Notebook"
            AppScreen.ERROR_RECOVERY -> "Diagnostics & Recovery"
            AppScreen.TEXTBOOKS -> "Textbook Library"
            AppScreen.PREVIOUS_PAPERS -> "Previous Papers & Trends"
            AppScreen.ADMIN_DASHBOARD -> "State Public Education Center"
            AppScreen.LEARNING_PATH_INTELLIGENCE -> "AI Learning Intelligence"
        }

        val isPrimaryScreen = currentScreen in listOf(
            AppScreen.HOME,
            AppScreen.TUTOR_CHAT,
            AppScreen.CURRICULUM,
            AppScreen.PRACTICE_QUIZ,
            AppScreen.STUDY_PLANNER
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideWebLayout = maxWidth >= 720.dp

            if (isWideWebLayout && currentScreen != AppScreen.ONBOARDING) {
                // Wide-Screen Desktop / Web Browser Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    AppWebSideNav(
                        currentScreen = currentScreen,
                        onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )

                    Scaffold(
                        modifier = Modifier.weight(1f),
                        topBar = {
                            AppTopBar(
                                title = topBarTitle,
                                gradeBadge = userProfile?.grade,
                                xpCount = userProfile?.xp ?: 0,
                                canNavigateBack = currentScreen != AppScreen.HOME,
                                onBackClick = { viewModel.navigateBack() },
                                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                                onSubjectSelectClick = { isSubjectSelectionVisible = true },
                                onSearchClick = { viewModel.isSearchDialogOpen.value = true }
                            )
                        },
                        floatingActionButton = {
                            if (currentScreen != AppScreen.VOICE_LIVE) {
                                ExtendedFloatingActionButton(
                                    onClick = { viewModel.isJarvisSheetOpen.value = true },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Psychology,
                                            contentDescription = "Ask JARVIS",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    text = { Text("Ask JARVIS", fontWeight = FontWeight.Bold) },
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.testTag("universal_jarvis_fab")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            ScreenContent(
                                currentScreen = currentScreen,
                                viewModel = viewModel,
                                onOpenSubjectSelection = { isSubjectSelectionVisible = true }
                            )
                        }
                    }
                }
            } else {
                // Mobile / Compact Layout
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentScreen != AppScreen.ONBOARDING) {
                            AppTopBar(
                                title = topBarTitle,
                                gradeBadge = userProfile?.grade,
                                xpCount = userProfile?.xp ?: 0,
                                canNavigateBack = currentScreen != AppScreen.HOME,
                                onBackClick = { viewModel.navigateBack() },
                                onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                                onSubjectSelectClick = { isSubjectSelectionVisible = true },
                                onSearchClick = { viewModel.isSearchDialogOpen.value = true }
                            )
                        }
                    },
                    bottomBar = {
                        if (isPrimaryScreen) {
                            AppBottomNav(
                                currentScreen = currentScreen,
                                onTabSelected = { screen -> viewModel.navigateTo(screen) }
                            )
                        }
                    },
                    floatingActionButton = {
                        if (currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.VOICE_LIVE) {
                            ExtendedFloatingActionButton(
                                onClick = { viewModel.isJarvisSheetOpen.value = true },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "Ask JARVIS",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                text = { Text("Ask JARVIS", fontWeight = FontWeight.Bold) },
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.testTag("universal_jarvis_fab")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ScreenContent(
                            currentScreen = currentScreen,
                            viewModel = viewModel,
                            onOpenSubjectSelection = { isSubjectSelectionVisible = true }
                        )
                    }
                }
            }
        }

        // Global JARVIS Assistant Sheet
        if (isJarvisOpen) {
            JarvisAssistantSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.isJarvisSheetOpen.value = false }
            )
        }

        // Global Search & Command Center Dialog
        if (isSearchOpen) {
            GlobalSearchDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isSearchDialogOpen.value = false }
            )
        }
    }
}

@Composable
private fun ScreenContent(
    currentScreen: AppScreen,
    viewModel: EduMentorViewModel,
    onOpenSubjectSelection: () -> Unit
) {
    when (currentScreen) {
        AppScreen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
        AppScreen.HOME -> HomeScreen(
            viewModel = viewModel,
            onOpenSubjectSelection = onOpenSubjectSelection
        )
        AppScreen.TUTOR_CHAT -> TutorChatScreen(viewModel = viewModel)
        AppScreen.CURRICULUM -> CurriculumScreen(viewModel = viewModel)
        AppScreen.PRACTICE_QUIZ -> PracticeQuizScreen(viewModel = viewModel)
        AppScreen.STUDY_PLANNER -> StudyPlannerScreen(viewModel = viewModel)
        AppScreen.VOICE_LIVE -> VoiceLiveScreen(viewModel = viewModel)
        AppScreen.NOTES_FLASHCARDS -> NotesAndFlashcardsScreen(viewModel = viewModel)
        AppScreen.MEDIA_LAB -> MediaLabScreen(viewModel = viewModel)
        AppScreen.PROGRESS -> ProgressAndGamificationScreen(viewModel = viewModel)
        AppScreen.PARENT_VIEW -> ParentDashboardScreen(viewModel = viewModel)
        AppScreen.TEACHER_VIEW -> TeacherDashboardScreen(viewModel = viewModel)
        AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
        AppScreen.DOCUMENTS -> DocumentsScreen(viewModel = viewModel)
        AppScreen.MISTAKE_NOTEBOOK -> MistakeNotebookScreen(viewModel = viewModel)
        AppScreen.ERROR_RECOVERY -> ErrorRecoveryScreen(viewModel = viewModel)
        AppScreen.TEXTBOOKS -> TextbookLibraryScreen(viewModel = viewModel)
        AppScreen.PREVIOUS_PAPERS -> PreviousPapersScreen(viewModel = viewModel)
        AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
        AppScreen.LEARNING_PATH_INTELLIGENCE -> LearningPathIntelligenceScreen(viewModel = viewModel)
    }
}
