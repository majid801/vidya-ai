package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = "primary_student",
    val name: String,
    val grade: String, // "Class 10", "Inter 1st Year (MPC)", etc.
    val stream: String = "", // MPC, BiPC, MEC, CEC, General
    val board: String = "CBSE", // CBSE, ICSE, State Board
    val targetExam: String = "Board Exams",
    val country: String = "India",
    val state: String = "Telangana",
    val medium: String = "English",
    val dailyGoalMinutes: Int = 120,
    val xp: Int = 150,
    val level: Int = 1,
    val streakDays: Int = 3,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val isParentMode: Boolean = false,
    val isTeacherMode: Boolean = false,
    val email: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String = "default",
    val sender: String, // "user" or "ai"
    val text: String,
    val subject: String = "",
    val mode: String = "Normal",
    val imageUrl: String? = null,
    val hasThinking: Boolean = false,
    val thinkingText: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val chapter: String,
    val allocatedMinutes: Int = 45,
    val isCompleted: Boolean = false,
    val dateString: String, // YYYY-MM-DD
    val priority: String = "Medium", // High, Medium, Low
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val chapter: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercentage: Int,
    val weakTopicsSummary: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val chapter: String,
    val frontText: String,
    val backText: String,
    val masteryLevel: Int = 0, // 0 = New, 1 = Review, 2 = Mastered
    val lastReviewedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val chapter: String,
    val content: String,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "topic_mastery")
data class TopicMasteryEntity(
    @PrimaryKey val topicKey: String, // e.g. "Math_Quadratic Equations"
    val subject: String,
    val chapter: String,
    val topicName: String,
    val status: String = "Learning", // "Weak", "Learning", "Mastered"
    val accuracy: Int = 50,
    val questionsAttempted: Int = 0,
    val lastPracticedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val content: String,
    val extractedFormulas: String = "",
    val extractedQuestions: String = "",
    val summary: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mistake_notebook")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val chapter: String,
    val question: String,
    val userMistake: String,
    val correctedSolution: String,
    val isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "jarvis_memory")
data class JarvisMemoryEntity(
    @PrimaryKey val key: String,
    val value: String,
    val category: String = "general",
    val timestamp: Long = System.currentTimeMillis()
)
