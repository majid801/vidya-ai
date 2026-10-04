package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class PrerequisiteNode(
    val conceptId: String,
    val conceptName: String,
    val subject: String,
    val chapter: String,
    val prerequisites: List<String>,
    val importance: String, // "Foundational", "Critical", "Supplementary"
    val isMastered: Boolean = false,
    val diagnosticSummary: String
)

@Serializable
data class ChapterPriority(
    val chapterTitle: String,
    val subject: String,
    val marksWeightage: Int,
    val historicalFrequency: String, // "High", "Medium", "Moderate"
    val priorityRank: Int, // 1 (Highest), 2, 3
    val recommendedHours: Double,
    val keyTrapConcept: String
)

@Serializable
data class ExamReadinessMetric(
    val subject: String,
    val overallReadinessScore: Int, // 0 - 100
    val syllabusCoveragePercentage: Int,
    val mockAccuracyPercentage: Int,
    val revisionHealthStatus: String, // "On Track", "Needs Immediate Review", "Excellent"
    val highYieldGaps: List<String>,
    val predictedScoreBand: String // "72 - 78 out of 80"
)

@Serializable
data class MisconceptionItem(
    val id: String,
    val subject: String,
    val chapter: String,
    val commonMyth: String,
    val scientificallyAccurateFact: String,
    val testExample: String,
    val jarvisExplanation: String
)

@Serializable
data class RevisionScheduleItem(
    val title: String,
    val durationText: String,
    val targetChapters: List<String>,
    val mode: String, // "1-Hour Rapid", "1-Day Intensive", "7-Day Countdown", "Pre-Exam"
    val checklist: List<String>
)

@Serializable
data class InstitutionalStateMetrics(
    val stateName: String = "Telangana & CBSE All-India",
    val activeDistrictsCount: Int = 33,
    val totalEnrolledStudentsSample: String = "1,248,500+",
    val syllabusSynchronizedRate: Int = 98,
    val officialQuestionBankCount: Int = 18450,
    val verifiedTextbooksCount: Int = 240,
    val dailyLearningHoursLogged: String = "425,000+ hrs",
    val aiProviderAvailabilityPercentage: Double = 99.98
)

object AdvancedEducationIntelligence {

    /**
     * Institutional Chief Minister Education Platform State Metrics
     */
    val stateMetrics = InstitutionalStateMetrics()

    /**
     * Knowledge Gap & Concept Dependency Graph
     */
    fun getPrerequisitesFor(subject: String, chapter: String): List<PrerequisiteNode> {
        return when {
            subject.contains("Math", ignoreCase = true) -> listOf(
                PrerequisiteNode(
                    conceptId = "math_pre_1",
                    conceptName = "Linear Equations & Factorization",
                    subject = "Mathematics",
                    chapter = "Prerequisites for Quadratic Equations",
                    prerequisites = listOf("Basic Algebra", "Middle-Term Splitting"),
                    importance = "Foundational",
                    isMastered = true,
                    diagnosticSummary = "Mastery in middle-term splitting is essential to solve quadratic equations ax² + bx + c = 0 in under 60 seconds."
                ),
                PrerequisiteNode(
                    conceptId = "math_pre_2",
                    conceptName = "Radicals & Square Roots Properties",
                    subject = "Mathematics",
                    chapter = "Number Systems",
                    prerequisites = listOf("Rational Numbers", "Prime Factorization"),
                    importance = "Critical",
                    isMastered = true,
                    diagnosticSummary = "Understanding √b² - 4ac and discriminant nature requires fluency in non-negative square root operations."
                ),
                PrerequisiteNode(
                    conceptId = "math_pre_3",
                    conceptName = "Cartesian Coordinate System",
                    subject = "Mathematics",
                    chapter = "Coordinate Geometry Foundation",
                    prerequisites = listOf("Number Line", "Distance Formula"),
                    importance = "Critical",
                    isMastered = false,
                    diagnosticSummary = "Crucial for graphing parabolas and finding roots as x-intercepts."
                )
            )
            subject.contains("Physics", ignoreCase = true) || subject.contains("Science", ignoreCase = true) -> listOf(
                PrerequisiteNode(
                    conceptId = "phy_pre_1",
                    conceptName = "Vectors & Trigonometric Ratios",
                    subject = "Physics",
                    chapter = "Mathematical Tools in Physics",
                    prerequisites = listOf("sin, cos, tan fundamentals", "Component Resolution"),
                    importance = "Critical",
                    isMastered = true,
                    diagnosticSummary = "Essential for solving Snell's law, ray diagrams, and wave equations."
                ),
                PrerequisiteNode(
                    conceptId = "phy_pre_2",
                    conceptName = "Cartesian Sign Convention",
                    subject = "Physics",
                    chapter = "Light & Optics",
                    prerequisites = listOf("Object distance (-u)", "Focal length signs"),
                    importance = "Foundational",
                    isMastered = false,
                    diagnosticSummary = "Over 68% of student exam errors in optics happen due to flipped negative signs on focal length or image distance."
                )
            )
            else -> listOf(
                PrerequisiteNode(
                    conceptId = "gen_pre_1",
                    conceptName = "Core Terminology & Definitions",
                    subject = subject,
                    chapter = chapter,
                    prerequisites = listOf("Basic Scientific Method", "SI Units"),
                    importance = "Foundational",
                    isMastered = true,
                    diagnosticSummary = "Clear conceptual grasp of standard units and definitions ensures full step-marks."
                )
            )
        }
    }

    /**
     * High-Yield Exam Priority Matrix
     */
    fun getChapterPrioritiesFor(grade: String, subject: String): List<ChapterPriority> {
        return when {
            subject.contains("Math", ignoreCase = true) -> listOf(
                ChapterPriority(
                    chapterTitle = "Quadratic Equations",
                    subject = "Mathematics",
                    marksWeightage = 12,
                    historicalFrequency = "High",
                    priorityRank = 1,
                    recommendedHours = 4.5,
                    keyTrapConcept = "Rejecting negative speed/time in word problems"
                ),
                ChapterPriority(
                    chapterTitle = "Trigonometry & Heights/Distances",
                    subject = "Mathematics",
                    marksWeightage = 14,
                    historicalFrequency = "High",
                    priorityRank = 1,
                    recommendedHours = 6.0,
                    keyTrapConcept = "Angle of elevation vs depression line of sight"
                ),
                ChapterPriority(
                    chapterTitle = "Circles & Tangents",
                    subject = "Mathematics",
                    marksWeightage = 10,
                    historicalFrequency = "High",
                    priorityRank = 2,
                    recommendedHours = 3.5,
                    keyTrapConcept = "Radius perpendicular to tangent theorem proof"
                ),
                ChapterPriority(
                    chapterTitle = "Real Numbers & Polynomials",
                    subject = "Mathematics",
                    marksWeightage = 8,
                    historicalFrequency = "Moderate",
                    priorityRank = 3,
                    recommendedHours = 2.5,
                    keyTrapConcept = "Proof of √3 irrationality by contradiction"
                )
            )
            subject.contains("Physics", ignoreCase = true) || subject.contains("Science", ignoreCase = true) -> listOf(
                ChapterPriority(
                    chapterTitle = "Light - Reflection & Refraction",
                    subject = "Physics",
                    marksWeightage = 14,
                    historicalFrequency = "High",
                    priorityRank = 1,
                    recommendedHours = 5.0,
                    keyTrapConcept = "Cartesian sign conventions in lens formula (1/f = 1/v - 1/u)"
                ),
                ChapterPriority(
                    chapterTitle = "Electricity & Circuits",
                    subject = "Physics",
                    marksWeightage = 13,
                    historicalFrequency = "High",
                    priorityRank = 1,
                    recommendedHours = 5.5,
                    keyTrapConcept = "Parallel resistor reciprocal addition (1/Rp)"
                ),
                ChapterPriority(
                    chapterTitle = "Magnetic Effects of Electric Current",
                    subject = "Physics",
                    marksWeightage = 9,
                    historicalFrequency = "Medium",
                    priorityRank = 2,
                    recommendedHours = 3.5,
                    keyTrapConcept = "Fleming's Left-Hand vs Right-Hand rule context"
                )
            )
            else -> listOf(
                ChapterPriority(
                    chapterTitle = "Core Foundation Principles",
                    subject = subject,
                    marksWeightage = 15,
                    historicalFrequency = "High",
                    priorityRank = 1,
                    recommendedHours = 4.0,
                    keyTrapConcept = "Standard units and definition derivations"
                )
            )
        }
    }

    /**
     * Real-time Exam Readiness Calculation
     */
    fun calculateReadiness(subject: String, practiceCount: Int, accuracy: Int): ExamReadinessMetric {
        val coverage = minOf(95, maxOf(40, 50 + (practiceCount * 2)))
        val readinessScore = ((coverage * 0.4) + (accuracy * 0.6)).toInt()
        val health = when {
            readinessScore >= 80 -> "Excellent"
            readinessScore >= 60 -> "On Track"
            else -> "Needs Immediate Review"
        }
        val band = when {
            readinessScore >= 85 -> "72 - 78 / 80"
            readinessScore >= 70 -> "60 - 70 / 80"
            else -> "45 - 58 / 80"
        }
        val gaps = if (accuracy < 75) {
            listOf("Sign convention in numericals", "Proof step completeness", "Units & dimensions in final answer")
        } else {
            listOf("Speed optimization for 5-mark long questions")
        }

        return ExamReadinessMetric(
            subject = subject,
            overallReadinessScore = readinessScore,
            syllabusCoveragePercentage = coverage,
            mockAccuracyPercentage = accuracy,
            revisionHealthStatus = health,
            highYieldGaps = gaps,
            predictedScoreBand = band
        )
    }

    /**
     * Common Student Misconception Buster
     */
    val commonMisconceptions = listOf(
        MisconceptionItem(
            id = "misc_1",
            subject = "Mathematics",
            chapter = "Algebra & Quadratics",
            commonMyth = "√(x² + y²) is equal to x + y.",
            scientificallyAccurateFact = "Square root does not distribute across addition! For example: √(3² + 4²) = √(9 + 16) = √25 = 5, whereas 3 + 4 = 7.",
            testExample = "Calculate √(9 + 16). Correct answer is 5, NOT 3 + 4 = 7.",
            jarvisExplanation = "Think of a right triangle with legs 3 and 4: the hypotenuse is 5, not the sum of both legs!"
        ),
        MisconceptionItem(
            id = "misc_2",
            subject = "Physics",
            chapter = "Electricity",
            commonMyth = "Electric current is 'used up' as it flows through a light bulb or resistor.",
            scientificallyAccurateFact = "Electric charge is completely conserved! The current entering a resistor equals the current leaving it. What is transformed is electrical potential energy into heat and light.",
            testExample = "If 2 Amperes enters an electric heater, how much current exits? Exactly 2 Amperes.",
            jarvisExplanation = "Charges are like delivery trucks: they drop off cargo (energy) at the warehouse, but the trucks themselves return to the battery!"
        ),
        MisconceptionItem(
            id = "misc_3",
            subject = "Physics",
            chapter = "Optics",
            commonMyth = "A concave lens can form a magnified real image on a screen.",
            scientificallyAccurateFact = "A concave lens is a diverging lens and ONLY ever forms virtual, erect, and diminished images for all real object positions.",
            testExample = "Can a concave lens produce an inverted image on a wall? No, never.",
            jarvisExplanation = "Because light rays diverge outward when passing through a concave lens, they can only appear to meet behind the lens when traced backwards."
        ),
        MisconceptionItem(
            id = "misc_4",
            subject = "Chemistry",
            chapter = "Acids, Bases & Salts",
            commonMyth = "pH 0 means there is no acid present.",
            scientificallyAccurateFact = "pH 0 indicates an extremely concentrated and strong acid solution ([H+] = 1 Molar). Negative pH values can even exist for super-concentrated acids!",
            testExample = "Is 1M HCl neutral or acidic? It has pH 0 and is strongly acidic.",
            jarvisExplanation = "Remember the logarithmic scale: lower pH means higher hydrogen ion concentration!"
        )
    )

    /**
     * Smart Revision Routines
     */
    val revisionSchedules = listOf(
        RevisionScheduleItem(
            title = "1-Hour Rapid Sprint",
            durationText = "60 Minutes",
            targetChapters = listOf("High-Yield Formulas", "Governing Laws", "Core Definitions"),
            mode = "1-Hour Rapid",
            checklist = listOf(
                "Review formula sheet without looking at answers",
                "Write down all 5 key lens & mirror formulas",
                "Solve 2 high-frequency numerical questions",
                "Check unit symbols (Joules, Watts, Dioptres)"
            )
        ),
        RevisionScheduleItem(
            title = "1-Day Intensive Exam Mastery",
            durationText = "24 Hours (Full Day)",
            targetChapters = listOf("Top 3 High-Weightage Chapters", "Previous 3-Year PYQs"),
            mode = "1-Day Intensive",
            checklist = listOf(
                "Morning: Complete Section A (MCQs) speed run (20 questions in 30 mins)",
                "Afternoon: Practice 4 long 5-mark derivations with neat diagrams",
                "Evening: Solve 1 complete timed sample paper section",
                "Night: Log mistakes into Mistake Notebook for overnight recall"
            )
        ),
        RevisionScheduleItem(
            title = "7-Day State Exam Countdown",
            durationText = "7 Days",
            targetChapters = listOf("Entire Subject Blueprint (Classes 1-12 & Intermediate)"),
            mode = "7-Day Countdown",
            checklist = listOf(
                "Days 1-2: Priority 1 Chapters deep review and numerical mastery",
                "Days 3-4: Priority 2 Chapters & State Board previous model papers",
                "Day 5: Full 3-Hour Exam Simulation under timed conditions",
                "Day 6: Mistake correction & Socratic Teach-Back with JARVIS",
                "Day 7: Light revision of formula sheets & restful sleep"
            )
        )
    )
}
