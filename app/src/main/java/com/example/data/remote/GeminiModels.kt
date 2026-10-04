package com.example.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<JsonObject>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String? = null, // "user", "model"
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String // Base64
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: JsonObject? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val thinkingConfig: ThinkingConfig? = null,
    val imageConfig: ImageConfig? = null,
    val responseModalities: List<String>? = null,
    val speechConfig: SpeechConfig? = null
)

@Serializable
data class ThinkingConfig(
    val thinkingLevel: String // "high"
)

@Serializable
data class ImageConfig(
    val aspectRatio: String = "1:1", // "1:1", "16:9", "4:3"
    val imageSize: String = "1K"     // "1K", "2K", "4K"
)

@Serializable
data class SpeechConfig(
    val voiceConfig: VoiceConfig
)

@Serializable
data class VoiceConfig(
    val prebuiltVoiceConfig: PrebuiltVoiceConfig
)

@Serializable
data class PrebuiltVoiceConfig(
    val voiceName: String = "Kore" // Kore, Puck, Fenrir, Aoede, Charon
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null,
    val usageMetadata: UsageMetadata? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null,
    val groundingMetadata: GroundingMetadata? = null
)

@Serializable
data class GroundingMetadata(
    val webSearchQueries: List<String>? = null,
    val searchEntryPoint: SearchEntryPoint? = null,
    val groundingChunks: List<GroundingChunk>? = null
)

@Serializable
data class SearchEntryPoint(
    val renderedContent: String? = null
)

@Serializable
data class GroundingChunk(
    val web: GroundingWeb? = null
)

@Serializable
data class GroundingWeb(
    val uri: String? = null,
    val title: String? = null
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)

// Veo Request & Response
@Serializable
data class GenerateVideosRequest(
    val prompt: String,
    val image: InlineData? = null,
    val config: VeoConfig? = null
)

@Serializable
data class VeoConfig(
    val numberOfVideos: Int = 1,
    val resolution: String = "720p",
    val aspectRatio: String = "16:9" // "16:9" or "9:16"
)

@Serializable
data class VeoOperationResponse(
    val name: String? = null,
    val done: Boolean = false,
    val error: JsonObject? = null,
    val response: JsonObject? = null
)
