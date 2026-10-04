package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.CurriculumData
import com.example.ui.AppScreen
import com.example.ui.EduMentorViewModel

data class SearchResultItem(
    val title: String,
    val subtitle: String,
    val category: String, // "Subject", "Note", "Document", "Action"
    val onSelect: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchDialog(
    viewModel: EduMentorViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val userProfile by viewModel.userProfile.collectAsState()
    val grade = userProfile?.grade ?: "Class 10"
    val stream = userProfile?.stream ?: "MPC"
    val notes by viewModel.studyNotes.collectAsState()
    val documents by viewModel.documents.collectAsState()

    val allSubjects = remember(grade, stream) {
        CurriculumData.getSubjectsFor(grade, stream)
    }

    // Build unified search results
    val results = remember(searchQuery, allSubjects, notes, documents) {
        val query = searchQuery.trim().lowercase()
        val list = mutableListOf<SearchResultItem>()

        if (query.isEmpty()) {
            // Default quick command actions
            list.add(SearchResultItem("Ask JARVIS a Question", "Instant AI Doubt Solving", "Command") {
                viewModel.isJarvisSheetOpen.value = true
                onDismiss()
            })
            list.add(SearchResultItem("Start Practice Quiz", "Generate tailored tests for your current chapter", "Command") {
                viewModel.navigateTo(AppScreen.PRACTICE_QUIZ)
                onDismiss()
            })
            list.add(SearchResultItem("Open Pomodoro Study Planner", "Track daily tasks and focus sessions", "Command") {
                viewModel.navigateTo(AppScreen.STUDY_PLANNER)
                onDismiss()
            })
            list.add(SearchResultItem("View Mistake Notebook", "Review and resolve past traps and errors", "Command") {
                viewModel.navigateTo(AppScreen.MISTAKE_NOTEBOOK)
                onDismiss()
            })
            list.add(SearchResultItem("Open Study Documents", "Analyze textbook PDFs and lecture notes", "Command") {
                viewModel.navigateTo(AppScreen.DOCUMENTS)
                onDismiss()
            })
            list.add(SearchResultItem("Digital Textbooks Library", "NCERT & State Board eBooks and reader", "Command") {
                viewModel.navigateTo(AppScreen.TEXTBOOKS)
                onDismiss()
            })
            list.add(SearchResultItem("Previous Papers & AI Trends", "Board PYQs, blueprints and predicted papers", "Command") {
                viewModel.navigateTo(AppScreen.PREVIOUS_PAPERS)
                onDismiss()
            })
            list.add(SearchResultItem("AI Intelligence & Prerequisites", "Concept gaps, readiness index and traps", "Command") {
                viewModel.navigateTo(AppScreen.LEARNING_PATH_INTELLIGENCE)
                onDismiss()
            })
            list.add(SearchResultItem("State Education Command Center", "Institutional metrics and district sync", "Command") {
                viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                onDismiss()
            })
        } else {
            // Search across subjects and chapters
            for (sub in allSubjects) {
                if (sub.name.lowercase().contains(query)) {
                    list.add(SearchResultItem(sub.name, "${sub.chapters.size} Chapters • $grade", "Subject") {
                        viewModel.selectedSubject.value = sub
                        viewModel.selectedChapter.value = sub.chapters.firstOrNull()
                        viewModel.navigateTo(AppScreen.CURRICULUM)
                        onDismiss()
                    })
                }
                for (ch in sub.chapters) {
                    if (ch.title.lowercase().contains(query)) {
                        list.add(SearchResultItem(ch.title, "Subject: ${sub.name}", "Chapter") {
                            viewModel.selectedSubject.value = sub
                            viewModel.selectedChapter.value = ch
                            viewModel.navigateTo(AppScreen.CURRICULUM)
                            onDismiss()
                        })
                    }
                }
            }

            // Search across notes
            for (note in notes) {
                if (note.title.lowercase().contains(query) || note.content.lowercase().contains(query)) {
                    list.add(SearchResultItem(note.title, "Note in ${note.subject}", "Note") {
                        viewModel.navigateTo(AppScreen.NOTES_FLASHCARDS)
                        onDismiss()
                    })
                }
            }

            // Search across documents
            for (doc in documents) {
                if (doc.title.lowercase().contains(query) || doc.content.lowercase().contains(query)) {
                    list.add(SearchResultItem(doc.title, "Document in ${doc.subject}", "Document") {
                        viewModel.navigateTo(AppScreen.DOCUMENTS)
                        onDismiss()
                    })
                }
            }
        }

        list
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Global Search & Command Center",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search subjects, chapters, notes, documents...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(results) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onSelect() }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (item.category) {
                                "Subject", "Chapter" -> Icons.Default.MenuBook
                                "Document" -> Icons.Default.Description
                                "Note" -> Icons.Default.AutoAwesome
                                else -> Icons.Default.AutoAwesome
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
