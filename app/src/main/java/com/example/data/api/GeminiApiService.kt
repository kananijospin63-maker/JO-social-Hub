package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ---------------------------------------------------------------------------
// Gemini model identifier — single source of truth.
// Update this constant when Google releases a newer stable model.
// Current recommendation (August 2026): gemini-2.5-flash
//   - Best price/performance ratio
//   - 1M token context window
//   - Supports systemInstruction and generationConfig
// ---------------------------------------------------------------------------
const val GEMINI_MODEL = "gemini-2.5-flash"

@JsonClass(generateAdapter = true)
data class Part(
    @field:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @field:Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @field:Json(name = "temperature") val temperature: Float? = null,
    @field:Json(name = "topP") val topP: Float? = null,
    @field:Json(name = "topK") val topK: Int? = null,
    @field:Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @field:Json(name = "contents") val contents: List<Content>,
    @field:Json(name = "systemInstruction") val systemInstruction: Content? = null,
    @field:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @field:Json(name = "content") val content: Content? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @field:Json(name = "candidates") val candidates: List<Candidate>? = null
)

interface GeminiApiService {
    /**
     * The model name is injected via the {model} path parameter so it can be
     * changed at runtime (e.g. for testing) without touching the interface.
     */
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}
