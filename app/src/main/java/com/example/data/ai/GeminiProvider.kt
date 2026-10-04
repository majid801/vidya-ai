package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.InlineData
import com.example.data.remote.Part
import com.example.data.remote.PrebuiltVoiceConfig
import com.example.data.remote.SpeechConfig
import com.example.data.remote.ThinkingConfig
import com.example.data.remote.VoiceConfig
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class GeminiProvider(
    private val apiService: GeminiApiService = GeminiApiClient.service
) : AIProvider {

    override val providerType: AIProviderType = AIProviderType.GEMINI_CLOUD

    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY.trim()

    override fun isAvailable(): Boolean = apiKey.isNotBlank()

    override suspend fun generateResponse(request: AIRequest): Result<AIResponse> = withContext(Dispatchers.IO) {
        if (!isAvailable()) {
            return@withContext Result.failure(
                AppError(
                    kind = AppErrorKind.AUTH_ERROR,
                    userMessage = "Gemini API key is not configured. Please enter your API key in AI Studio Secrets panel."
                )
            )
        }

        // Ordered list of candidate models with automatic fallback if 404 is encountered
        val candidateModels = when {
            request.useThinkingMode || request.isComplexTask -> listOf(
                "gemini-3.1-pro-preview",
                "gemini-2.5-pro",
                "gemini-1.5-pro",
                "gemini-2.5-flash"
            )
            request.teachingModeName.equals("Quick", ignoreCase = true) -> listOf(
                "gemini-3.1-flash-lite",
                "gemini-2.5-flash",
                "gemini-1.5-flash"
            )
            else -> listOf(
                "gemini-3.5-flash",
                "gemini-2.5-flash",
                "gemini-1.5-flash"
            )
        }

        val systemRolePrompt = request.systemRoleOverride ?: """
            You are JARVIS / Vidya AI, a world-class personal AI teacher, academic mentor, and doubt solver.
            Student Context:
            - Grade/Level: ${request.grade}
            - Current Subject: ${request.subject}
            - Current Chapter: ${request.chapter}
            - Selected Pedagogical Mode: ${request.teachingModeName}
            
            Pedagogical Rule:
            ${request.teachingModePrompt}
            
            Core Guidelines:
            1. Explain accurately and age-appropriately for ${request.grade}.
            2. Never just give bare answers; teach intuition, reasoning, and underlying principles.
            3. For math and science, show step-by-step calculations with units.
            4. Highlight common misconceptions and pitfalls when relevant.
            5. Always maintain an inspiring, encouraging, structured, and helpful tone.
        """.trimIndent()

        val contents = mutableListOf<Content>()
        // Append history
        for ((sender, text) in request.history.takeLast(10)) {
            contents.add(
                Content(
                    role = if (sender == "user") "user" else "model",
                    parts = listOf(Part(text = text))
                )
            )
        }

        // User parts
        val userParts = mutableListOf<Part>()
        userParts.add(Part(text = request.prompt))
        if (request.imageBitmap != null) {
            val base64 = bitmapToBase64(request.imageBitmap)
            userParts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64)))
        }
        contents.add(Content(role = "user", parts = userParts))

        var lastError: Exception? = null

        for (model in candidateModels) {
            try {
                val genConfig = if (request.useThinkingMode && model.contains("pro")) {
                    GenerationConfig(
                        temperature = 0.4f,
                        thinkingConfig = ThinkingConfig(thinkingLevel = "high")
                    )
                } else {
                    GenerationConfig(temperature = 0.6f)
                }

                val reqBody = GenerateContentRequest(
                    contents = contents,
                    generationConfig = genConfig,
                    systemInstruction = Content(parts = listOf(Part(text = systemRolePrompt)))
                )

                val response = apiService.generateContent(
                    model = model,
                    apiKey = apiKey,
                    request = reqBody
                )

                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                if (!responseText.isNullOrBlank()) {
                    return@withContext Result.success(
                        AIResponse(
                            text = responseText,
                            providerUsed = AIProviderType.GEMINI_CLOUD,
                            modelName = model
                        )
                    )
                }
            } catch (e: HttpException) {
                lastError = e
                // If model not found (404), continue to next candidate model
                if (e.code() == 404) {
                    continue
                } else if (e.code() == 429) {
                    return@withContext Result.failure(
                        AppError(AppErrorKind.RATE_LIMIT, "API rate limit reached. Please wait a moment and try again.")
                    )
                } else if (e.code() in 400..403) {
                    return@withContext Result.failure(
                        AppError(AppErrorKind.AUTH_ERROR, "API key authentication failed. Check your Gemini API key.")
                    )
                } else {
                    break
                }
            } catch (e: IOException) {
                lastError = e
                return@withContext Result.failure(
                    AppError(AppErrorKind.NETWORK_ERROR, "Network connection error. Check your mobile internet connection.")
                )
            } catch (e: Exception) {
                lastError = e
                break
            }
        }

        Result.failure(
            AppError(
                kind = AppErrorKind.AI_PROVIDER_ERROR,
                userMessage = "Could not generate response from cloud AI models: ${lastError?.message}",
                technicalDetails = lastError?.localizedMessage
            )
        )
    }

    override suspend fun generateSpeech(text: String, voiceName: String): Result<ByteArray?> = withContext(Dispatchers.IO) {
        if (!isAvailable()) return@withContext Result.success(null)
        try {
            val request = GenerateContentRequest(
                contents = listOf(Content(role = "user", parts = listOf(Part(text = text)))),
                generationConfig = GenerationConfig(
                    responseModalities = listOf("AUDIO"),
                    speechConfig = SpeechConfig(
                        voiceConfig = VoiceConfig(prebuiltVoiceConfig = PrebuiltVoiceConfig(voiceName = voiceName))
                    )
                )
            )

            // Try gemini-3.8-flash-tts, fallback to gemini-2.5-flash if needed
            val models = listOf("gemini-3.8-flash-tts", "gemini-2.5-flash")
            for (model in models) {
                try {
                    val response = apiService.generateContent(model = model, apiKey = apiKey, request = request)
                    val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData
                    if (inlineData != null) {
                        val audioBytes = Base64.decode(inlineData.data, Base64.DEFAULT)
                        return@withContext Result.success(audioBytes)
                    }
                } catch (e: HttpException) {
                    if (e.code() == 404) continue
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.success(null)
        }
    }

    override suspend fun transcribeAudio(audioBase64: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isAvailable()) return@withContext Result.failure(AppError(AppErrorKind.AUTH_ERROR, "API key required"))
        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = "Transcribe the student's spoken audio query accurately into text:"),
                            Part(inlineData = InlineData(mimeType = "audio/wav", data = audioBase64))
                        )
                    )
                )
            )
            val models = listOf("gemini-3.5-transcribe", "gemini-2.5-flash", "gemini-1.5-flash")
            for (model in models) {
                try {
                    val response = apiService.generateContent(model = model, apiKey = apiKey, request = request)
                    val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.text != null }?.text
                    if (!text.isNullOrBlank()) return@withContext Result.success(text)
                } catch (e: HttpException) {
                    if (e.code() == 404) continue
                }
            }
            Result.failure(AppError(AppErrorKind.AI_PROVIDER_ERROR, "Could not transcribe audio"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}
