package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.domain.CurriculumData
import com.example.ui.AppScreen
import com.example.ui.EduMentorViewModel
import com.example.ui.components.EducationalEmptyState

@Composable
fun PracticeQuizScreen(
    viewModel: EduMentorViewModel,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.currentQuizQuestions.collectAsState()
    val isSubmitted by viewModel.isQuizSubmitted.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val isGenerating by viewModel.isGeneratingQuiz.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val grade = userProfile?.grade ?: "Class 10"
    val subjects = CurriculumData.getSubjectsFor(grade, userProfile?.stream ?: "")
    val currentSubject = selectedSubject ?: subjects.firstOrNull()
    val currentChapter = selectedChapter ?: currentSubject?.chapters?.firstOrNull()

    var selectedDifficulty by remember { mutableStateOf("Medium") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quiz Header & Generator Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${currentSubject?.name ?: "Mathematics"} Practice",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Chapter: ${currentChapter?.title ?: "Core Concepts"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Difficulty selector
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Easy", "Medium", "Hard").forEach { diff ->
                            val isSel = selectedDifficulty == diff
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedDifficulty = diff },
                                label = { Text(diff, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("difficulty_chip_${diff.lowercase()}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (currentSubject != null && currentChapter != null) {
                            viewModel.startChapterQuiz(currentSubject, currentChapter, selectedDifficulty)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_quiz_button"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating Aligned Quiz with Gemini...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Practice Test")
                    }
                }
            }
        }

        // Quiz Content
        if (questions.isEmpty()) {
            EducationalEmptyState(
                icon = Icons.Default.Quiz,
                title = "No Active Practice Test",
                message = "Tap 'Generate AI Practice Test' to test your understanding with adaptive questions tailored to your syllabus.",
                actionButtonText = "Generate Class Quiz",
                onActionClick = {
                    if (currentSubject != null && currentChapter != null) {
                        viewModel.startChapterQuiz(currentSubject, currentChapter, selectedDifficulty)
                    }
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Score Summary Banner (if submitted)
                if (isSubmitted) {
                    item {
                        val correctCount = questions.filterIndexed { index, q ->
                            selectedAnswers[index] == q.correctIndex
                        }.size
                        val total = questions.size
                        val percentage = if (total > 0) (correctCount * 100) / total else 0

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (percentage >= 70) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quiz_result_banner")
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = if (percentage >= 70) "🎉 Outstanding Performance!" else "📚 Needs Practice",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (percentage >= 70) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Score: $correctCount / $total ($percentage%) • Gained +${correctCount * 25 + 20} XP",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { percentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }
                        }
                    }
                }

                // Question Cards
                items(questions.size) { index ->
                    val question = questions[index]
                    val selectedOption = selectedAnswers[index]

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_question_card_$index")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Question ${index + 1} of ${questions.size}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = question.topicTag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = question.questionText,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Options A, B, C, D
                            question.options.forEachIndexed { optIndex, optionText ->
                                val isOptSelected = selectedOption == optIndex
                                val isCorrect = question.correctIndex == optIndex

                                val optionColor = when {
                                    isSubmitted && isCorrect -> MaterialTheme.colorScheme.secondaryContainer
                                    isSubmitted && isOptSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                                    isOptSelected -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = optionColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { viewModel.selectQuizAnswer(index, optIndex) }
                                        .testTag("option_${index}_$optIndex")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isOptSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when (optIndex) {
                                                    0 -> "A"
                                                    1 -> "B"
                                                    2 -> "C"
                                                    else -> "D"
                                                },
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isOptSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (isSubmitted) {
                                            if (isCorrect) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Correct",
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            } else if (isOptSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Incorrect",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Pedagogical Explanation (revealed after submission)
                            if (isSubmitted) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Explanation:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = question.explanation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Submit or Retake Button
                item {
                    if (!isSubmitted) {
                        Button(
                            onClick = { viewModel.submitQuiz() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_quiz_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = selectedAnswers.size == questions.size
                        ) {
                            Text("Submit Test & Evaluate", style = MaterialTheme.typography.titleSmall)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentSubject != null && currentChapter != null) {
                                        viewModel.startChapterQuiz(currentSubject, currentChapter, selectedDifficulty)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("retake_quiz_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retake Test")
                            }

                            Button(
                                onClick = {
                                    viewModel.chatInputText.value = "I just took a quiz on ${currentChapter?.title}. Please help me understand my weak spots and clarify the concepts I missed."
                                    viewModel.navigateTo(AppScreen.TUTOR_CHAT)
                                    viewModel.sendChatMessage()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ask_ai_mistakes_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask Tutor")
                            }
                        }
                    }
                }
            }
        }
    }
}
