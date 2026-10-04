package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EduMentorViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JarvisAssistantSheet(
    viewModel: EduMentorViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val userProfile by viewModel.userProfile.collectAsState()
    val tasks by viewModel.studyTasks.collectAsState()
    val weakTopics by viewModel.weakTopics.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()

    val studentName = userProfile?.name ?: "Student"
    val grade = userProfile?.grade ?: "Class 10"
    val unfinishedTasksCount = tasks.count { !it.isCompleted }

    // Time-aware greeting
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingTime = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    var inputPrompt by remember { mutableStateOf("") }
    var jarvisOutput by remember { mutableStateOf<String?>(null) }
    var jarvisProviderBadge by remember { mutableStateOf("JARVIS AI Ready") }
    var isThinking by remember { mutableStateOf(false) }
    var speechSpeed by remember { mutableStateOf(1.0f) }
    var isSpeaking by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "JARVIS",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "JARVIS",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "AI Personal Mentor",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = jarvisProviderBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("jarvis_close_btn")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close JARVIS")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Context Pill
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active Context: $grade • ${selectedSubject?.name ?: "All Subjects"} • ${selectedChapter?.title ?: "All Chapters"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dynamic Session Briefing Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "$greetingTime, $studentName!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val briefText = buildString {
                                append("You have $unfinishedTasksCount study tasks pending today. ")
                                if (weakTopics.isNotEmpty()) {
                                    append("I recommend reinforcing ${weakTopics.first().topicName} (~${weakTopics.first().accuracy}% accuracy). ")
                                } else {
                                    append("Your practice performance is solid across recent topics! ")
                                }
                                append("How can I assist you right now?")
                            }
                            Text(
                                text = briefText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Quick Actions Flow
                item {
                    Text(
                        text = "JARVIS Quick Actions",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val quickActions = listOf(
                        "Explain Step-by-Step",
                        "Solve This Problem",
                        "Socratic Hint",
                        "Exam Scoring Tips",
                        "Teach-Back Mode",
                        "Real-Life Analogy",
                        "Make Revision Notes",
                        "Generate Quiz",
                        "Find Weak Areas",
                        "Daily Reflection"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickActions.forEach { action ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable {
                                        inputPrompt = "JARVIS, please $action for ${selectedChapter?.title ?: "current topic"}"
                                    }
                            ) {
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // AI Response Area (if any)
                if (jarvisOutput != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "JARVIS Response",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Voice Controls Row
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Speed Toggle
                                        IconButton(onClick = {
                                            speechSpeed = when (speechSpeed) {
                                                0.75f -> 1.0f
                                                1.0f -> 1.25f
                                                1.25f -> 1.5f
                                                else -> 0.75f
                                            }
                                            viewModel.audioHelper.setSpeechRate(speechSpeed)
                                        }) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Speed, contentDescription = "Speed", modifier = Modifier.size(16.dp))
                                                Text("${speechSpeed}x", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }

                                        // Play / Stop Speech
                                        IconButton(onClick = {
                                            if (isSpeaking) {
                                                viewModel.audioHelper.stop()
                                                isSpeaking = false
                                            } else {
                                                isSpeaking = true
                                                viewModel.audioHelper.speak(jarvisOutput ?: "")
                                            }
                                        }) {
                                            Icon(
                                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = if (isSpeaking) "Stop" else "Speak",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = jarvisOutput ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Prompt Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = { Text("Ask JARVIS anything...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jarvis_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            inputPrompt = "JARVIS, teach me the core principles of ${selectedChapter?.title ?: "this topic"}"
                        }) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Input")
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val query = inputPrompt.trim()
                        if (query.isNotEmpty()) {
                            isThinking = true
                            coroutineScope.launch {
                                val currentSub = selectedSubject?.name ?: "Mathematics"
                                val currentCh = selectedChapter?.title ?: "General Studies"
                                val mode = viewModel.selectedTeachingMode.value

                                val history = viewModel.chatMessages.value.takeLast(4).map { it.sender to it.text }
                                val result = viewModel.geminiRepo.sendChatMessage(
                                    history = history,
                                    userMessage = query,
                                    grade = grade,
                                    subject = currentSub,
                                    chapter = currentCh,
                                    teachingMode = mode,
                                    isComplexTask = true
                                )
                                isThinking = false
                                result.onSuccess { reply ->
                                    jarvisOutput = reply
                                    jarvisProviderBadge = "Gemini Cloud • Verified"
                                }.onFailure { err ->
                                    // Local fallback
                                    jarvisOutput = "### JARVIS (Offline Curriculum Mode)\n" +
                                            "Regarding: \"$query\"\n\n" +
                                            "For $grade $currentSub ($currentCh):\n" +
                                            "1. Focus on core formulas and fundamental definitions.\n" +
                                            "2. Break multi-step derivations into clear stages.\n" +
                                            "3. Always check units and verify answers against given constraints."
                                    jarvisProviderBadge = "Local Engine • Offline Mode"
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isThinking && inputPrompt.isNotBlank(),
                    modifier = Modifier.testTag("jarvis_send_btn")
                ) {
                    if (isThinking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}
