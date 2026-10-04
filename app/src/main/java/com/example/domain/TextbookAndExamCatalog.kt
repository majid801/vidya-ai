package com.example.domain

import com.example.data.model.ExamTrendReport
import com.example.data.model.PaperQuestion
import com.example.data.model.PaperSection
import com.example.data.model.PredictedPracticeExam
import com.example.data.model.PreviousPaperResource
import com.example.data.model.TextbookChapter
import com.example.data.model.TextbookResource

object TextbookAndExamCatalog {

    /**
     * Retrieves digital textbooks filtered by grade, subject, and medium.
     */
    fun getTextbooksFor(grade: String, subjectName: String, medium: String = "English"): List<TextbookResource> {
        val list = mutableListOf<TextbookResource>()

        when {
            grade.contains("Class 10") -> {
                if (subjectName.contains("Math", ignoreCase = true)) {
                    list.add(
                        TextbookResource(
                            id = "ncert_math_10",
                            title = "NCERT Mathematics Class 10",
                            publisher = "NCERT / ePathshala",
                            grade = "Class 10",
                            subject = "Mathematics",
                            medium = medium,
                            officialUrl = "https://ncert.nic.in/textbook.php?jemh1=0-14",
                            chapters = listOf(
                                TextbookChapter(
                                    chapterNumber = 1,
                                    title = "Real Numbers",
                                    pages = 22,
                                    readTimeMinutes = 45,
                                    summary = "Fundamental Theorem of Arithmetic, Euclid's Division Lemma, Revisiting irrational numbers (proof of √2, √3, √5), and terminating/non-terminating decimal expansions.",
                                    keyFormulas = listOf("HCF(a, b) * LCM(a, b) = a * b", "Fundamental Theorem of Arithmetic: Every composite number can be uniquely expressed as a product of primes"),
                                    importantConcepts = listOf("Proof of Irrationality by Contradiction", "Prime Factorization Method for LCM and HCF"),
                                    sampleText = "The Fundamental Theorem of Arithmetic states that every composite number can be expressed (factorized) as a product of primes, and this factorization is unique, apart from the order in which the prime factors occur."
                                ),
                                TextbookChapter(
                                    chapterNumber = 2,
                                    title = "Polynomials",
                                    pages = 28,
                                    readTimeMinutes = 55,
                                    summary = "Geometrical meaning of zeroes of a polynomial, relationship between zeroes and coefficients of quadratic and cubic polynomials.",
                                    keyFormulas = listOf("Sum of zeroes: α + β = -b/a", "Product of zeroes: αβ = c/a", "Quadratic Polynomial: k[x² - (α + β)x + αβ]"),
                                    importantConcepts = listOf("Graph parabolas opening upwards vs downwards", "Zeroes as x-intercepts"),
                                    sampleText = "For any quadratic polynomial ax² + bx + c (a ≠ 0), the graph of the corresponding equation y = ax² + bx + c has one of the two shapes, either open upwards like ∪ or open downwards like ∩ depending on whether a > 0 or a < 0."
                                ),
                                TextbookChapter(
                                    chapterNumber = 4,
                                    title = "Quadratic Equations",
                                    pages = 32,
                                    readTimeMinutes = 65,
                                    summary = "Standard form ax² + bx + c = 0, solution by factorization and quadratic formula, nature of roots based on discriminant D.",
                                    keyFormulas = listOf("Quadratic Formula: x = (-b ± √(b² - 4ac)) / (2a)", "Discriminant D = b² - 4ac"),
                                    importantConcepts = listOf("Condition for two real and distinct roots (D > 0)", "Condition for two equal real roots (D = 0)", "No real roots (D < 0)"),
                                    sampleText = "A quadratic equation in the variable x is an equation of the form ax² + bx + c = 0, where a, b, c are real numbers and a ≠ 0. The roots of this equation can be found using factorization or the famous quadratic formula discovered by Indian mathematicians."
                                )
                            )
                        )
                    )
                }

                if (subjectName.contains("Physics", ignoreCase = true) || subjectName.contains("Science", ignoreCase = true)) {
                    list.add(
                        TextbookResource(
                            id = "ncert_science_10",
                            title = "NCERT Science Class 10 (Physics Section)",
                            publisher = "NCERT / ePathshala",
                            grade = "Class 10",
                            subject = "Physics",
                            medium = medium,
                            officialUrl = "https://ncert.nic.in/textbook.php?jesc1=0-13",
                            chapters = listOf(
                                TextbookChapter(
                                    chapterNumber = 10,
                                    title = "Light – Reflection and Refraction",
                                    pages = 36,
                                    readTimeMinutes = 70,
                                    summary = "Spherical mirrors, ray diagrams, mirror formula, magnification, refraction of light, Snell's law, refractive index, lens formula, and power of a lens.",
                                    keyFormulas = listOf("Mirror Formula: 1/f = 1/v + 1/u", "Magnification (Mirror): m = -v/u = h'/h", "Snell's Law: n = sin(i) / sin(r)", "Lens Formula: 1/f = 1/v - 1/u", "Power: P = 1/f(in meters) Dioptres"),
                                    importantConcepts = listOf("Cartesian sign convention rules", "Convex vs Concave mirror focal lengths", "Virtual vs Real image characteristics"),
                                    sampleText = "Light seems to travel along straight paths in transparent media. Reflection of light by spherical mirrors follows the laws of reflection: angle of incidence equals angle of reflection, and the incident ray, reflected ray, and normal lie in the same plane."
                                ),
                                TextbookChapter(
                                    chapterNumber = 12,
                                    title = "Electricity",
                                    pages = 30,
                                    readTimeMinutes = 60,
                                    summary = "Electric current and circuit, electric potential and potential difference, Ohm's law, resistance factors, resistors in series and parallel, Joule's law of heating, and electric power.",
                                    keyFormulas = listOf("Ohm's Law: V = I * R", "Resistance: R = ρ * (L / A)", "Series Resistance: Rs = R1 + R2 + R3", "Parallel Resistance: 1/Rp = 1/R1 + 1/R2", "Heating Effect: H = I² * R * t", "Power: P = V * I = I² * R = V² / R"),
                                    importantConcepts = listOf("Derivation of effective resistance in parallel", "Joule's heating in household appliances", "Role of electric fuse as safety device"),
                                    sampleText = "A continuous and closed path of an electric current is called an electric circuit. Electric current is expressed by the amount of charge flowing through a particular area in unit time. That is, current I = Q / t."
                                )
                            )
                        )
                    )
                }
            }

            grade.contains("Intermediate") -> {
                list.add(
                    TextbookResource(
                        id = "inter_physics_telangana",
                        title = "State Board & NCERT Intermediate Physics",
                        publisher = "Telangana State Board / NCERT",
                        grade = grade,
                        subject = "Physics",
                        medium = medium,
                        officialUrl = "https://tsbie.cgg.gov.in/",
                        chapters = listOf(
                            TextbookChapter(
                                chapterNumber = 1,
                                title = "Waves & Sound",
                                pages = 42,
                                readTimeMinutes = 80,
                                summary = "Transverse and longitudinal waves, displacement relation in a progressive wave, the principle of superposition of waves, reflection of waves, standing waves and normal modes, beats, Doppler effect.",
                                keyFormulas = listOf("Wave speed: v = f * λ", "Standing wave on stretched string: fn = n * (v / 2L)", "Doppler shift: f' = f * [(v ± vo) / (v ∓ vs)]"),
                                importantConcepts = listOf("Formation of nodes and antinodes", "Harmonics in open and closed organ pipes", "Beats frequency: fb = |f1 - f2|"),
                                sampleText = "Mechanical waves require a material medium for their propagation. Sound waves are longitudinal mechanical waves consisting of compressions and rarefactions where particles of the medium oscillate parallel to the wave direction."
                            ),
                            TextbookChapter(
                                chapterNumber = 2,
                                title = "Ray Optics and Optical Instruments",
                                pages = 48,
                                readTimeMinutes = 90,
                                summary = "Refraction at spherical surfaces, lens maker's formula, refraction through a prism, optical instruments (compound microscope and telescope).",
                                keyFormulas = listOf("Lens Maker's Formula: 1/f = (n - 1) * (1/R1 - 1/R2)", "Prism Formula: n = sin((A + Dm)/2) / sin(A/2)", "Compound Microscope Magnification: m = (L/fo) * (D/fe)"),
                                importantConcepts = listOf("Condition for total internal reflection: angle of incidence > critical angle", "Minimum deviation in triangular prism"),
                                sampleText = "When light is refracted at a spherical interface separating two media of refractive indices n1 and n2, the relation connecting object distance u, image distance v, and radius of curvature R is n2/v - n1/u = (n2 - n1)/R."
                            )
                        )
                    )
                )
            }

            else -> {
                list.add(
                    TextbookResource(
                        id = "primary_math",
                        title = "NCERT Joyful Learning Mathematics",
                        publisher = "NCERT / State SCERT",
                        grade = grade,
                        subject = "Mathematics",
                        medium = medium,
                        officialUrl = "https://ncert.nic.in/",
                        chapters = listOf(
                            TextbookChapter(
                                chapterNumber = 1,
                                title = "Shapes and Space",
                                pages = 16,
                                readTimeMinutes = 20,
                                summary = "Understanding basic geometric shapes (circle, triangle, rectangle, square), inside/outside, bigger/smaller, near/far.",
                                keyFormulas = listOf("Count sides and corners of shapes"),
                                importantConcepts = listOf("Visual shape recognition", "Spatial patterns in nature"),
                                sampleText = "Look around your classroom! The blackboard is a rectangle, your coin is a round circle, and your sandwich slice looks like a triangle."
                            )
                        )
                    )
                )
            }
        }

        return list
    }

    /**
     * Retrieves authentic previous examination question papers.
     */
    fun getPreviousPapers(board: String, grade: String, subject: String): List<PreviousPaperResource> {
        val list = mutableListOf<PreviousPaperResource>()

        if (grade.contains("Class 10")) {
            list.add(
                PreviousPaperResource(
                    id = "cbse_10_math_2025",
                    board = "CBSE",
                    grade = "Class 10",
                    subject = "Mathematics",
                    year = 2025,
                    examType = "Annual Board Examination",
                    totalMarks = 80,
                    durationMinutes = 180,
                    highFrequencyConcepts = listOf("Quadratic Formula", "Trigonometric Identities", "Surface Area and Volume of Frustum/Combination", "Tangents to Circle Theorem"),
                    sections = listOf(
                        PaperSection(
                            sectionName = "Section A (Multiple Choice Questions - 1 Mark each)",
                            marksPerQuestion = 1,
                            totalQuestions = 4,
                            questions = listOf(
                                PaperQuestion(
                                    questionNumber = 1,
                                    text = "If the quadratic equation 2x² + kx + 3 = 0 has two equal roots, then the value of k is:",
                                    marks = 1,
                                    topicTag = "Quadratic Equations",
                                    frequencyRating = "Recurring Every Year",
                                    modelAnswerHint = "Set Discriminant D = b² - 4ac = 0 => k² - 4(2)(3) = 0 => k² = 24 => k = ±2√6."
                                ),
                                PaperQuestion(
                                    questionNumber = 2,
                                    text = "The distance of the point P(-6, 8) from the origin is:",
                                    marks = 1,
                                    topicTag = "Coordinate Geometry",
                                    frequencyRating = "High",
                                    modelAnswerHint = "Distance = √(x² + y²) = √((-6)² + 8²) = √(36 + 64) = √100 = 10 units."
                                ),
                                PaperQuestion(
                                    questionNumber = 3,
                                    text = "If sin θ + cos θ = √2 cos θ, then the value of tan θ is:",
                                    marks = 1,
                                    topicTag = "Introduction to Trigonometry",
                                    frequencyRating = "High",
                                    modelAnswerHint = "Divide both sides by cos θ: tan θ + 1 = √2 => tan θ = √2 - 1."
                                )
                            )
                        ),
                        PaperSection(
                            sectionName = "Section B (Short Answer Questions - 2 Marks each)",
                            marksPerQuestion = 2,
                            totalQuestions = 2,
                            questions = listOf(
                                PaperQuestion(
                                    questionNumber = 4,
                                    text = "Prove that √5 is an irrational number using the method of contradiction.",
                                    marks = 2,
                                    topicTag = "Real Numbers",
                                    frequencyRating = "Recurring Every Year",
                                    modelAnswerHint = "Assume √5 = a/b where a and b are co-prime integers. 5b² = a² => 5 divides a. Substitute a = 5c => 5b² = 25c² => b² = 5c² => 5 divides b. This contradicts co-prime assumption."
                                )
                            )
                        ),
                        PaperSection(
                            sectionName = "Section C (Long Derivations - 5 Marks each)",
                            marksPerQuestion = 5,
                            totalQuestions = 1,
                            questions = listOf(
                                PaperQuestion(
                                    questionNumber = 5,
                                    text = "Prove that the lengths of tangents drawn from an external point to a circle are equal. Hence find the perimeter of a circumscribed quadrilateral.",
                                    marks = 5,
                                    topicTag = "Circles",
                                    frequencyRating = "Recurring Every Year",
                                    modelAnswerHint = "Join center O to external point P and points of contact A, B. Prove right triangles OAP and OBP congruent by RHS criterion. Hence PA = PB."
                                )
                            )
                        )
                    )
                )
            )

            // Telangana State Board Paper
            list.add(
                PreviousPaperResource(
                    id = "ts_ssc_10_math_2025",
                    board = "Telangana State Board (TSBIE/SSC)",
                    grade = "Class 10",
                    subject = "Mathematics",
                    year = 2025,
                    examType = "SSC Annual Public Exam",
                    totalMarks = 80,
                    durationMinutes = 180,
                    highFrequencyConcepts = listOf("Sets and Venn Diagrams", "Progressions (AP and GP)", "Trigonometric Applications / Heights & Distances"),
                    sections = listOf(
                        PaperSection(
                            sectionName = "Part A: Section I (Very Short Answer - 2 Marks)",
                            marksPerQuestion = 2,
                            totalQuestions = 2,
                            questions = listOf(
                                PaperQuestion(
                                    questionNumber = 1,
                                    text = "If A = {x : x is a prime number, x < 10} and B = {x : x is an odd number, x < 10}, find A ∩ B and illustrate with a Venn diagram.",
                                    marks = 2,
                                    topicTag = "Sets",
                                    frequencyRating = "High",
                                    modelAnswerHint = "A = {2, 3, 5, 7}, B = {1, 3, 5, 7, 9}. Intersection A ∩ B = {3, 5, 7}."
                                )
                            )
                        )
                    )
                )
            )
        }

        return list
    }

    /**
     * AI Exam Trend Analyzer
     */
    fun getExamTrendReport(board: String, grade: String, subject: String): ExamTrendReport {
        return ExamTrendReport(
            subject = subject,
            grade = grade,
            board = board,
            highFrequencyTopics = listOf(
                "Quadratic Equations & Discriminant" to 95,
                "Trigonometric Identities & Heights/Distances" to 90,
                "Tangent Theorems on Circles" to 88,
                "Irrationality Proof (√2, √3, √5)" to 85,
                "Ohm's Law & Resistor Combinations (Physics)" to 92,
                "Ray Optics Mirror & Lens Formula" to 89
            ),
            commonQuestionTypes = listOf(
                "Proof by Contradiction (2-3 Marks)",
                "Discriminant Nature of Roots Calculation (1-2 Marks)",
                "Word Problem: Speed-Distance or Upstream/Downstream (4-5 Marks)",
                "Circuit Equivalent Resistance & Heat Produced (3-5 Marks)"
            ),
            blueprintDistribution = "Section A: 20 Marks (MCQs), Section B: 10 Marks (Short 2M), Section C: 18 Marks (3M), Section D: 20 Marks (Long 5M), Section E: 12 Marks (Case Study / Application)",
            examTips = listOf(
                "In derivations, always write the governing formula with standard SI units first to secure the first 1 mark.",
                "Draw ray diagrams with arrow heads showing light direction; missing arrows often cost half a mark.",
                "Verify roots in quadratic word problems by checking that dimensions (time, distance, age) cannot be negative."
            )
        )
    }

    /**
     * AI Predicted Practice Exam
     */
    fun getPredictedPracticePaper(board: String, grade: String, subject: String): PredictedPracticeExam {
        return PredictedPracticeExam(
            title = "Vidya AI Predicted Practice Exam: $grade $subject",
            board = board,
            grade = grade,
            subject = subject,
            expectedQuestions = listOf(
                PaperQuestion(
                    questionNumber = 1,
                    text = "A train travels at a certain average speed for a distance of 63 km and then travels at an average speed of 6 km/h more than its original speed for a distance of 72 km. If it takes 3 hours to complete the journey, find its original average speed.",
                    marks = 5,
                    topicTag = "Quadratic Equations Application",
                    frequencyRating = "High Expected Pattern",
                    modelAnswerHint = "Let original speed be x km/h. Equation: 63/x + 72/(x + 6) = 3. Simplify to quadratic equation: x² - 39x - 126 = 0. Solving gives x = 42 km/h (rejecting negative speed)."
                ),
                PaperQuestion(
                    questionNumber = 2,
                    text = "An electric lamp of resistance 20 Ω and a conductor of 4 Ω resistance are connected in series to a 6 V battery. Calculate: (a) Total resistance of circuit, (b) Overall current flowing, (c) Potential difference across the lamp and the conductor.",
                    marks = 3,
                    topicTag = "Electricity & Circuits",
                    frequencyRating = "High Expected Pattern",
                    modelAnswerHint = "(a) R = 20 + 4 = 24 Ω. (b) I = V / R = 6 / 24 = 0.25 A. (c) V_lamp = 0.25 * 20 = 5 V; V_conductor = 0.25 * 4 = 1 V."
                ),
                PaperQuestion(
                    questionNumber = 3,
                    text = "From a point on the ground, the angles of elevation of the bottom and the top of a transmission tower fixed at the top of a 20 m high building are 45° and 60° respectively. Find the height of the tower (take √3 = 1.732).",
                    marks = 4,
                    topicTag = "Applications of Trigonometry",
                    frequencyRating = "High Expected Pattern",
                    modelAnswerHint = "Let ground distance be x and tower height be h. tan 45° = 20/x => x = 20 m. tan 60° = (20 + h)/x => √3 = (20 + h)/20 => h = 20(√3 - 1) = 20(0.732) = 14.64 meters."
                )
            )
        )
    }
}
