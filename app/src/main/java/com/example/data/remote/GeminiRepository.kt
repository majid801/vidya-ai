package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.TeachingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.io.ByteArrayOutputStream

class GeminiRepository(
    private val apiService: GeminiApiService = GeminiApiClient.service
) {
    // Read API key safely from BuildConfig
    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY.ifBlank { "" }

    // Helper: Convert bitmap to base64 jpeg
    fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * 1. Multi-turn AI Tutor Chat / Doubt Solver
     * Uses gemini-3.1-pro-preview for complex tasks, gemini-3.5-flash for general,
     * and gemini-3.1-flash-lite for fast tasks.
     * Supports high thinking mode with gemini-3.1-pro-preview.
     */
    suspend fun sendChatMessage(
        history: List<Pair<String, String>>, // sender ("user" or "model") to text
        userMessage: String,
        grade: String,
        subject: String,
        chapter: String,
        teachingMode: TeachingMode,
        isComplexTask: Boolean = false,
        useThinkingMode: Boolean = false,
        imageBitmap: Bitmap? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val modelName = when {
                useThinkingMode || isComplexTask || imageBitmap != null -> "gemini-3.1-pro-preview"
                teachingMode == TeachingMode.QUICK -> "gemini-3.1-flash-lite"
                else -> "gemini-3.5-flash"
            }

            val systemRolePrompt = """
                You are Vidya AI, a world-class personal AI teacher, academic mentor, and doubt solver.
                Student Context:
                - Grade/Level: $grade
                - Current Subject: $subject
                - Current Chapter/Topic: $chapter
                - Selected Pedagogical Mode: ${teachingMode.displayName}
                
                Pedagogical Rule:
                ${teachingMode.promptInstruction}
                
                Core Teacher Guidelines:
                1. Always explain accurately and age-appropriately for $grade.
                2. Do NOT just give bare answers; teach the intuition and underlying principles.
                3. If solving math/science problems: show clear step-by-step calculations with units.
                4. Highlight any common student misconceptions when relevant.
                5. Check understanding with a quick, engaging follow-up check question when appropriate.
                6. Keep the tone warm, encouraging, inspiring, and professional.
            """.trimIndent()

            val contents = mutableListOf<Content>()

            // Append prior history turns (limited to last 8 for prompt budget)
            val recentHistory = history.takeLast(8)
            for ((sender, text) in recentHistory) {
                contents.add(
                    Content(
                        role = if (sender == "user") "user" else "model",
                        parts = listOf(Part(text = text))
                    )
                )
            }

            // Current user message parts
            val currentParts = mutableListOf<Part>()
            currentParts.add(Part(text = userMessage))

            if (imageBitmap != null) {
                val base64 = bitmapToBase64(imageBitmap)
                currentParts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64)))
            }

            contents.add(Content(role = "user", parts = currentParts))

            val genConfig = if (useThinkingMode) {
                GenerationConfig(
                    temperature = 0.4f,
                    thinkingConfig = ThinkingConfig(thinkingLevel = "high")
                )
            } else {
                GenerationConfig(temperature = 0.6f)
            }

            val request = GenerateContentRequest(
                contents = contents,
                generationConfig = genConfig,
                systemInstruction = Content(parts = listOf(Part(text = systemRolePrompt)))
            )

            val response = apiService.generateContent(
                model = modelName,
                apiKey = apiKey,
                request = request
            )

            val responseText = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull { it.text != null }
                ?.text ?: "I am ready to help you learn! What topic or question would you like to explore?"

            Result.success(responseText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 2. Search Grounding using gemini-3.5-flash with googleSearch tool
     */
    suspend fun searchGroundedQuery(
        query: String,
        grade: String,
        subject: String
    ): Result<Pair<String, List<String>>> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = "You are an educational research tutor for $grade studying $subject. Provide fact-checked, up-to-date academic explanations grounded in authoritative sources."

            val searchTool = buildJsonObject {
                putJsonObject("googleSearch") {}
            }

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(role = "user", parts = listOf(Part(text = query)))
                ),
                tools = listOf(searchTool),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
            )

            val response = apiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )

            val candidate = response.candidates?.firstOrNull()
            val text = candidate?.content?.parts?.firstOrNull { it.text != null }?.text
                ?: "No search-grounded result obtained."

            val sources = mutableListOf<String>()
            candidate?.groundingMetadata?.groundingChunks?.forEach { chunk ->
                chunk.web?.title?.let { title ->
                    chunk.web.uri?.let { uri ->
                        sources.add("$title: $uri")
                    }
                }
            }

            Result.success(Pair(text, sources))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 3. Text to Speech (TTS) using gemini-3.8-flash-tts
     */
    suspend fun generateSpeech(
        textToSpeak: String,
        voiceName: String = "Kore" // Kore, Puck, Fenrir, Aoede, Charon
    ): Result<ByteArray?> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(role = "user", parts = listOf(Part(text = textToSpeak)))
                ),
                generationConfig = GenerationConfig(
                    responseModalities = listOf("AUDIO"),
                    speechConfig = SpeechConfig(
                        voiceConfig = VoiceConfig(
                            prebuiltVoiceConfig = PrebuiltVoiceConfig(voiceName = voiceName)
                        )
                    )
                )
            )

            val response = apiService.generateContent(
                model = "gemini-3.8-flash-tts",
                apiKey = apiKey,
                request = request
            )

            val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData
            if (inlineData != null) {
                val audioBytes = Base64.decode(inlineData.data, Base64.DEFAULT)
                Result.success(audioBytes)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 4. Audio Transcription using gemini-3.5-transcribe
     */
    suspend fun transcribeAudio(
        audioBase64: String,
        mimeType: String = "audio/wav"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = "Transcribe this student's spoken audio question or academic doubt accurately into clear text."),
                            Part(inlineData = InlineData(mimeType = mimeType, data = audioBase64))
                        )
                    )
                )
            )

            val response = apiService.generateContent(
                model = "gemini-3.5-transcribe",
                apiKey = apiKey,
                request = request
            )

            val transcribedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                ?: "Could not transcribe audio."
            Result.success(transcribedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 5. Live Voice Conversation using gemini-3.8-live
     */
    suspend fun sendLiveVoiceTurn(
        transcript: String,
        grade: String,
        subject: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemInstruction = "You are a real-time conversational voice tutor for $grade $subject. Respond concisely, warmly, and clearly in 1-3 spoken sentences that are easy to hear and understand."
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(role = "user", parts = listOf(Part(text = transcript)))
                ),
                systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
            )

            val response = apiService.generateContent(
                model = "gemini-3.8-live",
                apiKey = apiKey,
                request = request
            )

            val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                ?: "I hear you! How can I assist you with your lesson?"
            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 6. Create & Edit Educational Images using gemini-3.1-flash-image-preview
     */
    suspend fun createOrEditImage(
        prompt: String,
        baseImageBitmap: Bitmap? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val parts = mutableListOf<Part>()
            parts.add(Part(text = prompt))
            if (baseImageBitmap != null) {
                val b64 = bitmapToBase64(baseImageBitmap)
                parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = b64)))
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(role = "user", parts = parts)),
                generationConfig = GenerationConfig(
                    responseModalities = listOf("TEXT", "IMAGE")
                )
            )

            val response = apiService.generateContent(
                model = "gemini-3.1-flash-image-preview",
                apiKey = apiKey,
                request = request
            )

            val imgData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData?.data
            if (imgData != null) {
                Result.success(imgData)
            } else {
                val textReply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text ?: ""
                Result.success(textReply)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 7. High-Quality Image Generation with size affordance (1K, 2K, 4K) using gemini-3-pro-image-preview
     */
    suspend fun generateHighQualityImage(
        prompt: String,
        imageSize: String = "1K", // "1K", "2K", "4K"
        aspectRatio: String = "1:1"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    imageConfig = ImageConfig(aspectRatio = aspectRatio, imageSize = imageSize),
                    responseModalities = listOf("TEXT", "IMAGE")
                )
            )

            val response = apiService.generateContent(
                model = "gemini-3-pro-image-preview",
                apiKey = apiKey,
                request = request
            )

            val imgData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData?.data
            if (imgData != null) {
                Result.success(imgData)
            } else {
                val textReply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text ?: ""
                Result.success(textReply)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 8. Text-to-Video and Photo-to-Video using veo-3.1-fast-generate-preview
     * Aspect ratio must be "16:9" (landscape) or "9:16" (portrait).
     */
    suspend fun generateVideo(
        prompt: String,
        sourceBitmap: Bitmap? = null,
        aspectRatio: String = "16:9" // "16:9" or "9:16"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inlineImg = sourceBitmap?.let {
                InlineData(mimeType = "image/jpeg", data = bitmapToBase64(it))
            }

            val request = GenerateVideosRequest(
                prompt = prompt,
                image = inlineImg,
                config = VeoConfig(
                    numberOfVideos = 1,
                    resolution = "720p",
                    aspectRatio = if (aspectRatio == "9:16") "9:16" else "16:9"
                )
            )

            val response = apiService.generateVideos(
                model = "veo-3.1-fast-generate-preview",
                apiKey = apiKey,
                request = request
            )

            val opName = response["name"]?.toString() ?: "Operation started"
            Result.success("Veo animation started successfully! Task ID: $opName")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 9. Analyze Images (Textbook questions, handwritten problems, diagrams) using gemini-3.1-pro-preview
     */
    suspend fun analyzeImageQuestion(
        bitmap: Bitmap,
        grade: String,
        subject: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val base64 = bitmapToBase64(bitmap)
            val prompt = """
                Analyze this educational photo carefully.
                1. Identify the textbook or handwritten question, diagram, or formula.
                2. Transcribe the question accurately.
                3. Identify the academic subject, chapter, and topic.
                4. Provide the step-by-step solution with clear formulas and reasoning suitable for $grade $subject.
                5. Highlight any potential traps or misconceptions students might encounter.
                6. Generate 2 similar practice questions with brief hints so the student can verify their learning.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = prompt),
                            Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64))
                        )
                    )
                )
            )

            val response = apiService.generateContent(
                model = "gemini-3.1-pro-preview",
                apiKey = apiKey,
                request = request
            )

            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                ?: "Unable to analyze the image."
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 10. Generate AI Quiz & Questions (using gemini-3.1-flash-lite for rapid generation)
     */
    suspend fun generateQuiz(
        grade: String,
        subject: String,
        chapter: String,
        difficulty: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Generate a 5-question multiple choice quiz for $grade student studying $subject chapter: "$chapter".
                Difficulty: $difficulty.
                Format your response clearly as:
                Q1: [Question text]
                A) [Option A]
                B) [Option B]
                C) [Option C]
                D) [Option D]
                Correct: [A/B/C/D]
                Explanation: [Concise pedagogical explanation]
                TopicTag: [Specific topic name]

                Repeat for Q2, Q3, Q4, Q5. Ensure questions are academically accurate according to standard syllabus.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(role = "user", parts = listOf(Part(text = prompt)))
                )
            )

            val response = apiService.generateContent(
                model = "gemini-3.1-flash-lite",
                apiKey = apiKey,
                request = request
            )

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                ?: "Quiz generation failed."
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
