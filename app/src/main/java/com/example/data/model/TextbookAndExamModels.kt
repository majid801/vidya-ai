package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TextbookResource(
    val id: String,
    val title: String,
    val publisher: String, // "NCERT", "Telangana SCERT", "CBSE Official"
    val grade: String,
    val subject: String,
    val medium: String = "English",
    val chapters: List<TextbookChapter>,
    val officialUrl: String = "https://ncert.nic.in/textbook.php"
)

@Serializable
data class TextbookChapter(
    val chapterNumber: Int,
    val title: String,
    val pages: Int,
    val readTimeMinutes: Int,
    val summary: String,
    val keyFormulas: List<String> = emptyList(),
    val importantConcepts: List<String> = emptyList(),
    val sampleText: String
)

@Serializable
data class PreviousPaperResource(
    val id: String,
    val board: String, // "CBSE", "Telangana State Board", "Andhra Pradesh"
    val grade: String, // "Class 10", "Intermediate 2nd Year"
    val subject: String,
    val year: Int,
    val examType: String, // "Annual Board Exam", "Model Question Paper", "Half-Yearly"
    val totalMarks: Int = 80,
    val durationMinutes: Int = 180,
    val sections: List<PaperSection>,
    val highFrequencyConcepts: List<String> = emptyList()
)

@Serializable
data class PaperSection(
    val sectionName: String, // "Section A (Objective)", "Section B (Short Answer)", "Section C (Long Questions)"
    val marksPerQuestion: Int,
    val totalQuestions: Int,
    val questions: List<PaperQuestion>
)

@Serializable
data class PaperQuestion(
    val questionNumber: Int,
    val text: String,
    val marks: Int,
    val topicTag: String,
    val frequencyRating: String = "High", // "High", "Medium", "Recurring"
    val modelAnswerHint: String
)

@Serializable
data class ExamTrendReport(
    val subject: String,
    val grade: String,
    val board: String,
    val highFrequencyTopics: List<Pair<String, Int>>, // Topic Name to Frequency Percentage
    val commonQuestionTypes: List<String>,
    val blueprintDistribution: String,
    val examTips: List<String>
)

@Serializable
data class PredictedPracticeExam(
    val title: String,
    val disclaimer: String = "AI-generated practice prediction based on available syllabus and historical patterns. Not an actual or leaked exam paper.",
    val subject: String,
    val grade: String,
    val board: String,
    val expectedQuestions: List<PaperQuestion>
)
