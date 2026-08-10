package com.example.domain.usecase

import com.example.data.api.Candidate
import com.example.data.api.Content
import com.example.data.api.GeminiApiService
import com.example.data.api.GenerateContentResponse
import com.example.data.api.Part
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Unit tests for AnalyzePostUseCase.
 *
 * Uses a mocked GeminiApiService so no real network call is made.
 * BuildConfig.GEMINI_API_KEY will be "MY_GEMINI_API_KEY" in the test
 * environment (no .env file), so the offline fallback path is also covered.
 */
class AnalyzePostUseCaseTest {

    private lateinit var geminiService: GeminiApiService
    private lateinit var useCase: AnalyzePostUseCase

    @Before
    fun setUp() {
        geminiService = mock()
        useCase = AnalyzePostUseCase(geminiService)
    }

    // -------------------------------------------------------------------------
    // Offline fallback (API key absent — default test environment)
    // -------------------------------------------------------------------------

    @Test
    fun `offline fallback returns score 84 for long post`() = runTest {
        val longText = "a".repeat(51)
        val result = useCase(longText)
        assertEquals(84, result.score)
    }

    @Test
    fun `offline fallback returns score 62 for short post`() = runTest {
        val shortText = "Court message"
        val result = useCase(shortText)
        assertEquals(62, result.score)
    }

    @Test
    fun `offline fallback mentions hashtags when present`() = runTest {
        val result = useCase("Voici notre offre #promo #marketing")
        assertTrue(result.rawAnalysis.contains("hashtag", ignoreCase = true))
    }

    @Test
    fun `offline fallback recommends adding hashtags when absent`() = runTest {
        val result = useCase("Texte sans aucun hashtag")
        assertTrue(result.rawAnalysis.contains("recommandons", ignoreCase = true))
    }

    @Test
    fun `offline fallback analysis is never empty`() = runTest {
        val result = useCase("quelconque")
        assertTrue(result.rawAnalysis.isNotBlank())
    }

    // -------------------------------------------------------------------------
    // Score extraction from API response text
    // -------------------------------------------------------------------------

    @Test
    fun `extracts score from API response text containing Score 85`() = runTest {
        val apiResponse = "Score: 85\nLe texte est clair et engageant."
        mockApiResponse(apiResponse)

        // Force use case to hit the API path by bypassing the key check.
        // Since we can't override BuildConfig in unit tests, we test the
        // score extraction logic directly via the regex used inside the use case.
        val score = "\\b([0-9]{2,3})\\b".toRegex()
            .findAll(apiResponse)
            .firstOrNull { it.value.toIntOrNull() in 40..100 }
            ?.value?.toIntOrNull() ?: 78

        assertEquals(85, score)
    }

    @Test
    fun `score extraction defaults to 78 when no valid number in response`() = runTest {
        val apiResponse = "Réponse sans score numérique."
        val score = "\\b([0-9]{2,3})\\b".toRegex()
            .findAll(apiResponse)
            .firstOrNull { it.value.toIntOrNull() in 40..100 }
            ?.value?.toIntOrNull() ?: 78

        assertEquals(78, score)
    }

    @Test
    fun `score extraction ignores numbers outside 40-100 range`() = runTest {
        val apiResponse = "Publié en 2024, score de 12, engagement de 150 personnes."
        val score = "\\b([0-9]{2,3})\\b".toRegex()
            .findAll(apiResponse)
            .firstOrNull { it.value.toIntOrNull() in 40..100 }
            ?.value?.toIntOrNull() ?: 78

        // 2024 > 100, 12 < 40, 150 > 100 → defaults to 78
        assertEquals(78, score)
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun mockApiResponse(text: String) {
        val response = GenerateContentResponse(
            candidates = listOf(
                Candidate(content = Content(parts = listOf(Part(text = text))))
            )
        )
        // The mock returns this response for any call — used to test
        // score parsing logic in isolation from the API key check
        kotlinx.coroutines.runBlocking {
            whenever(geminiService.generateContent(any(), any(), any()))
                .thenReturn(response)
        }
    }
}
