package com.example.data.ai

import android.graphics.Bitmap

/**
 * Standard error classification for Vidya AI.
 */
enum class AppErrorKind {
    NETWORK_ERROR,
    AUTH_ERROR,
    API_ERROR,
    AI_PROVIDER_ERROR,
    NOT_FOUND,
    VALIDATION_ERROR,
    FILE_ERROR,
    PERMISSION_ERROR,
    DATABASE_ERROR,
    TIMEOUT,
    RATE_LIMIT,
    UNKNOWN_ERROR
}

data class AppError(
    val kind: AppErrorKind,
    val userMessage: String,
    val technicalDetails: String? = null,
    val isRecoverable: Boolean = true
) : Exception(userMessage)

enum class AIProviderType {
    GEMINI_CLOUD,
    LOCAL_OFFLINE,
    HYBRID_FALLBACK
}

data class AIRequest(
    val prompt: String,
    val history: List<Pair<String, String>> = emptyList(),
    val grade: String = "Class 10",
    val subject: String = "Mathematics",
    val chapter: String = "General",
    val teachingModeName: String = "Normal",
    val teachingModePrompt: String = "",
    val isComplexTask: Boolean = false,
    val useThinkingMode: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val systemRoleOverride: String? = null
)

data class AIResponse(
    val text: String,
    val providerUsed: AIProviderType,
    val modelName: String,
    val isOfflineFallback: Boolean = false,
    val errorNotice: String? = null,
    val citations: List<String> = emptyList()
)

interface AIProvider {
    val providerType: AIProviderType
    suspend fun generateResponse(request: AIRequest): Result<AIResponse>
    suspend fun generateSpeech(text: String, voiceName: String = "Kore"): Result<ByteArray?>
    suspend fun transcribeAudio(audioBase64: String): Result<String>
    fun isAvailable(): Boolean
}
