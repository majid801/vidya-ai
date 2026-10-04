package com.example.data.ai

/**
 * FutureProvider stub for future AI integrations (e.g. on-device Edge AI, custom backends).
 */
interface FutureProvider : AIProvider {
    val futureProviderName: String
}

class FallbackProvider(
    private val cloudProvider: GeminiProvider = GeminiProvider(),
    private val localProvider: LocalEducationalProvider = LocalEducationalProvider()
) : AIProvider {

    override val providerType: AIProviderType = AIProviderType.HYBRID_FALLBACK

    override fun isAvailable(): Boolean = true

    override suspend fun generateResponse(request: AIRequest): Result<AIResponse> {
        // Attempt cloud provider first if available
        if (cloudProvider.isAvailable()) {
            val cloudResult = cloudProvider.generateResponse(request)
            if (cloudResult.isSuccess) {
                return cloudResult
            }
        }

        // If cloud fails or is unavailable, seamlessly use Local Curriculum Engine
        val localResult = localProvider.generateResponse(request)
        return if (localResult.isSuccess) {
            val originalLocal = localResult.getOrThrow()
            val notice = if (!cloudProvider.isAvailable()) {
                "Operating via Local Offline Educational Provider (Cloud API key not configured)."
            } else {
                "Operating via Local Offline Educational Provider (Cloud AI temporarily unreachable). Content is verified against syllabus."
            }
            Result.success(originalLocal.copy(errorNotice = notice))
        } else {
            localResult
        }
    }

    override suspend fun generateSpeech(text: String, voiceName: String): Result<ByteArray?> {
        val cloudSpeech = cloudProvider.generateSpeech(text, voiceName)
        if (cloudSpeech.isSuccess && cloudSpeech.getOrNull() != null) {
            return cloudSpeech
        }
        return localProvider.generateSpeech(text, voiceName)
    }

    override suspend fun transcribeAudio(audioBase64: String): Result<String> {
        val cloudTranscribe = cloudProvider.transcribeAudio(audioBase64)
        if (cloudTranscribe.isSuccess) {
            return cloudTranscribe
        }
        return localProvider.transcribeAudio(audioBase64)
    }
}
