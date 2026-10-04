package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen

data class WebNavItem(
    val screen: AppScreen,
    val title: String,
    val icon: ImageVector,
    val section: String = "Main"
)

@Composable
fun AppWebSideNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        WebNavItem(AppScreen.HOME, "Dashboard", Icons.Default.Home, "Learning"),
        WebNavItem(AppScreen.TUTOR_CHAT, "AI Tutor (JARVIS)", Icons.Default.AutoAwesome, "Learning"),
        WebNavItem(AppScreen.CURRICULUM, "Curriculum Roadmap", Icons.Default.Book, "Learning"),
        WebNavItem(AppScreen.PRACTICE_QUIZ, "Practice & Quizzes", Icons.Default.Quiz, "Learning"),
        WebNavItem(AppScreen.STUDY_PLANNER, "Planner & Pomodoro", Icons.Default.DateRange, "Learning"),

        WebNavItem(AppScreen.TEXTBOOKS, "Digital Textbooks", Icons.Default.School, "Resources"),
        WebNavItem(AppScreen.PREVIOUS_PAPERS, "Previous Papers (PYQ)", Icons.Default.HistoryEdu, "Resources"),
        WebNavItem(AppScreen.DOCUMENTS, "Document Tutor", Icons.Default.Book, "Resources"),

        WebNavItem(AppScreen.LEARNING_PATH_INTELLIGENCE, "AI Intelligence & Gaps", Icons.Default.Hub, "Analytics"),
        WebNavItem(AppScreen.PROGRESS, "Progress & Badges", Icons.Default.Psychology, "Analytics"),

        WebNavItem(AppScreen.ADMIN_DASHBOARD, "CM Education Command", Icons.Default.AdminPanelSettings, "Governance"),
        WebNavItem(AppScreen.TEACHER_VIEW, "Teacher Suite", Icons.Default.CastForEducation, "Governance"),
        WebNavItem(AppScreen.PARENT_VIEW, "Parent Portal", Icons.Default.FamilyRestroom, "Governance"),
        WebNavItem(AppScreen.PROFILE, "Academic Profile", Icons.Default.Person, "Settings")
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Institutional Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Vidya AI Logo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Vidya AI",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "National Education Portal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            var lastSection = ""

            items.forEach { item ->
                if (item.section != lastSection) {
                    lastSection = item.section
                    Text(
                        text = item.section.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 4.dp)
                    )
                }

                val isSelected = currentScreen == item.screen
                val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent
                val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bgColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable { onNavigate(item.screen) }
                        .testTag("web_nav_${item.title.lowercase().replace(" ", "_")}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
