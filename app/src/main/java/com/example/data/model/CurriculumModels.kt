package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TeachingMode(val displayName: String, val promptInstruction: String) {
    BEGINNER("Beginner (ELI5)", "Explain using very simple everyday analogies, simple language, and intuitive visual examples as if explaining to a beginner or younger student."),
    NORMAL("Academic Normal", "Provide a clear, balanced academic explanation suitable for the student's grade, aligning with standard textbook curriculum."),
    EXAM_MODE("Exam Scoring Mode", "Structure the answer strictly for maximum marks in board/school exams: Definition, Key Formula/Principle, Step-by-Step Derivation, Common Traps, and Model Mark Distribution."),
    DEEP_LEARNING("Deep Concept (First Principles)", "Explain the underlying intuition, origins, mathematical proofs, and real-world scientific applications from first principles."),
    QUICK("Quick Summary", "Give a crisp, 2-3 bullet point summary of the essential core concept and key takeaway formula."),
    SOCRATIC("Socratic Tutor", "Do NOT give away the final answer directly! Guide the student through thoughtful questions, hints, and checks of their understanding one step at a time."),
    PRACTICE("Practice & Solve", "Teach primarily through illustrative solved problems, followed by a quick test question for the student to solve."),
    REVISION("Revision & Formulas", "Highlight memory mnemonics, formula sheets, rapid-fire recall questions, and common board exam questions."),
    TEACH_BACK("Teach-Back Mode", "Ask the student to explain the concept back to you step-by-step. Critique their explanation constructively and gently correct any gaps."),
    REAL_LIFE("Real-Life Examples", "Ground every single idea in vivid everyday real-world engineering, natural phenomena, or daily-life applications.")
}

@Serializable
data class SubjectInfo(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val chapters: List<ChapterInfo>
)

@Serializable
data class ChapterInfo(
    val id: String,
    val title: String,
    val estimatedHours: Int = 3,
    val topics: List<String>,
    val commonMisconceptions: List<String> = emptyList(),
    val keyFormulas: List<String> = emptyList()
)

@Serializable
data class GradeStream(
    val gradeName: String,
    val streams: List<String>,
    val defaultSubjects: List<SubjectInfo>
)

@Serializable
data class QuizQuestion(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val topicTag: String
)

@Serializable
data class GeneratedQuiz(
    val title: String,
    val subject: String,
    val chapter: String,
    val difficulty: String,
    val questions: List<QuizQuestion>
)
