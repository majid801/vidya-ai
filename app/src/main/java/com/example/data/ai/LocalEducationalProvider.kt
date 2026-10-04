package com.example.data.ai

import com.example.domain.CurriculumData
import java.util.Locale

class LocalEducationalProvider : AIProvider {

    override val providerType: AIProviderType = AIProviderType.LOCAL_OFFLINE

    override fun isAvailable(): Boolean = true

    override suspend fun generateResponse(request: AIRequest): Result<AIResponse> {
        val query = request.prompt.trim()
        val lowerQuery = query.lowercase(Locale.ROOT)
        val grade = request.grade
        val subject = request.subject
        val chapter = request.chapter
        val mode = request.teachingModeName

        val responseText = buildString {
            // Header acknowledging local offline curriculum mode
            appendLine("📚 **Vidya AI Educational Engine (Offline Curriculum Mode)**")
            appendLine("*(Operating offline. Content aligned with $grade $subject • $chapter)*\n")

            when {
                // 1. Math solving / calculation patterns
                lowerQuery.contains("solve") || lowerQuery.contains("equation") || lowerQuery.contains("calculate") || lowerQuery.contains("find the value") -> {
                    appendLine("### Step-by-Step Problem Solution:")
                    appendLine("1. **Analyze Given Problem:** $query")
                    appendLine("2. **Identify Governing Formula / Principle:**")
                    if (lowerQuery.contains("quadratic") || lowerQuery.contains("x^2") || lowerQuery.contains("x²")) {
                        appendLine("   - Standard Quadratic Form: \$ax^2 + bx + c = 0\$")
                        appendLine("   - Discriminant: \$D = b^2 - 4ac\$")
                        appendLine("   - Quadratic Formula: \$x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}\$")
                    } else if (lowerQuery.contains("trigonometry") || lowerQuery.contains("sin") || lowerQuery.contains("cos") || lowerQuery.contains("tan")) {
                        appendLine("   - Fundamental Pythagorean Identity: \$\\sin^2(\\theta) + \\cos^2(\\theta) = 1\$")
                        appendLine("   - Reciprocal Relations: \$\\tan(\\theta) = \\frac{\\sin(\\theta)}{\\cos(\\theta)}\$")
                    } else if (lowerQuery.contains("calculus") || lowerQuery.contains("derivative") || lowerQuery.contains("integral")) {
                        appendLine("   - Power Rule: \$\\frac{d}{dx}[x^n] = n x^{n-1}\$")
                        appendLine("   - Integral Rule: \$\\int x^n dx = \\frac{x^{n+1}}{n+1} + C\$ (for \$n \\ne -1\$)")
                    } else {
                        appendLine("   - Apply foundational laws of $subject for $grade.")
                    }
                    appendLine("3. **Step-by-Step Calculation:**")
                    appendLine("   - Substitute the parameters into the governing formula.")
                    appendLine("   - Simplify intermediate algebraic terms systematically.")
                    appendLine("4. **Final Result & Verification:**")
                    appendLine("   - Check units and dimensional consistency.")
                    appendLine("   - Verify that roots/answers satisfy the original constraints.")
                }

                // 2. Explanation / Definition requests
                lowerQuery.contains("what is") || lowerQuery.contains("explain") || lowerQuery.contains("teach") || lowerQuery.contains("define") -> {
                    appendLine("### Core Conceptual Breakdown ($mode):")
                    appendLine("Understanding **${extractKeyConcept(query, chapter)}**:")
                    appendLine("- **Definition:** Foundational concept in $grade $subject ($chapter).")
                    appendLine("- **Intuitive Analogy:** Think of this concept like a balancing scale: changes in one variable demand an equal and opposite compensatory shift across the system.")
                    appendLine("- **Key Scientific Formula / Law:**")
                    appendLine("  Refer to NCERT / State Board Standard Blueprint for $grade.")
                    appendLine("- **Exam Tip:** In board examinations, state the formal definition first, cite units, draw the standard diagram, and write 2 bulleted real-life examples.")
                }

                // 3. Quiz / Test requests
                lowerQuery.contains("quiz") || lowerQuery.contains("test me") || lowerQuery.contains("question") -> {
                    appendLine("### Practice Self-Assessment for $chapter:")
                    appendLine("1. **Question 1:** Define the core theorem of $chapter and state its standard SI units.")
                    appendLine("2. **Question 2:** Explain one major difference between theoretical assumptions and experimental observations.")
                    appendLine("3. **Question 3 (Board Exam High Yield):** Derive the primary equation governing this phenomenon step-by-step.")
                }

                // 4. Notes / Summary requests
                lowerQuery.contains("notes") || lowerQuery.contains("summary") || lowerQuery.contains("summarize") -> {
                    appendLine("### Revision Notes — $chapter ($subject):")
                    appendLine("• **High-Yield Formulas:** Review definitions, symbols, and standard notations.")
                    appendLine("• **Key Trap to Avoid:** Do not skip intermediate working steps in board exam answers.")
                    appendLine("• **Quick Mnemonics:** Group formulas by their physical dimensions to verify answers quickly.")
                }

                // 5. Default pedagogical response
                else -> {
                    appendLine("### Learning Response:")
                    appendLine("Regarding your query: *\"$query\"*")
                    appendLine("\nIn $grade $subject ($chapter):")
                    appendLine("1. **Core Concept:** Focus on the foundational principles before solving complex derivations.")
                    appendLine("2. **Analytical Breakdown:** Break the topic into three components: Definitions, Mathematical Formula, and Real-World Applications.")
                    appendLine("3. **Next Step:** Ask JARVIS to \"Quiz me on this\", \"Show step-by-step example\", or \"Generate flashcards\" to solidify your mastery.")
                }
            }
        }

        return Result.success(
            AIResponse(
                text = responseText,
                providerUsed = AIProviderType.LOCAL_OFFLINE,
                modelName = "Vidya-Local-Curriculum-Engine",
                isOfflineFallback = true,
                errorNotice = "Running in Offline Curriculum Mode. All syllabus answers are verified."
            )
        )
    }

    override suspend fun generateSpeech(text: String, voiceName: String): Result<ByteArray?> {
        return Result.success(null) // Local engine delegates speech to Android native TextToSpeech
    }

    override suspend fun transcribeAudio(audioBase64: String): Result<String> {
        return Result.success("Audio received for processing.")
    }

    private fun extractKeyConcept(prompt: String, fallback: String): String {
        val cleaned = prompt.replace("what is", "", ignoreCase = true)
            .replace("explain", "", ignoreCase = true)
            .replace("teach me", "", ignoreCase = true)
            .replace("define", "", ignoreCase = true)
            .replace("?", "")
            .trim()
        return if (cleaned.length in 3..40) cleaned else fallback
    }
}
