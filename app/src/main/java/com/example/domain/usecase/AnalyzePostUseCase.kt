package com.example.domain.usecase

import android.util.Log
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GEMINI_MODEL
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.GeminiApiService
import com.example.data.api.Part
import com.example.domain.model.PostAnalysisResult
import com.example.domain.model.Result
import com.example.domain.model.safeCall
import javax.inject.Inject

/**
 * Analyses a post before publication and returns a quality score + feedback.
 *
 * Returns Result<PostAnalysisResult>:
 *  - Success → analysis with score and raw feedback text
 *  - Error   → only for unexpected failures; offline fallback is treated as Success
 */
class AnalyzePostUseCase @Inject constructor(
    private val geminiService: GeminiApiService
) {
    suspend operator fun invoke(baseText: String): Result<PostAnalysisResult> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return Result.Success(offlineFallback(baseText))
        }

        val prompt = """
            Analyse cette publication pour les réseaux sociaux :
            Texte : "$baseText"
            
            Donne ton analyse sous un format structuré contenant :
            1. Un score de qualité global sur 100 (ex: Score: 85)
            2. La qualité du texte (points forts, clarté)
            3. La pertinence des hashtags utilisés
            4. Le potentiel d'engagement estimé (Faible, Moyen, Excellent)
            5. Des suggestions concrètes d'amélioration (au moins 3 points)
            
            Rédige de façon synthétique et exploitable.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "Tu es un consultant en marketing digital spécialisé en audit d'audience."))),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )

        return safeCall {
            val response = geminiService.generateContent(GEMINI_MODEL, apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Analyse indisponible."

            val score = "\\b([0-9]{2,3})\\b".toRegex()
                .findAll(responseText)
                .firstOrNull { it.value.toIntOrNull() in 40..100 }
                ?.value?.toIntOrNull() ?: 78

            PostAnalysisResult(score = score, rawAnalysis = responseText)
        }.also { result ->
            if (result is Result.Error) {
                Log.e("AnalyzePostUseCase", "Gemini API error", result.exception)
            }
        }.let { result ->
            if (result is Result.Error) Result.Success(offlineFallback(baseText)) else result
        }
    }

    private fun offlineFallback(baseText: String): PostAnalysisResult {
        val score = if (baseText.length > 50) 84 else 62
        val analysis = buildString {
            appendLine("**Audit de Publication (Mode Local)**")
            appendLine()
            append("- **Qualité du texte** : Le message est clair. ")
            appendLine(if (baseText.length > 50) "La longueur est idéale et apporte de la valeur." else "C'est un peu court, l'ajout de détails capterait mieux l'audience.")
            append("- **Hashtags** : ")
            appendLine(if (baseText.contains("#")) "Présence de hashtags pertinents pour le ciblage." else "Aucun hashtag détecté. Nous vous recommandons d'en ajouter.")
            append("- **Potentiel d'engagement** : ")
            appendLine(if (baseText.length > 50) "Excellent" else "Moyen")
            appendLine()
            appendLine("**Suggestions de JO AI Assistant :**")
            appendLine("1. Ajoutez un appel à l'action clair (ex: « Donnez votre avis ! »).")
            appendLine("2. Aérez le texte en insérant des sauts de ligne pour faciliter la lecture.")
            appendLine("3. Associez un visuel professionnel de qualité pour attirer l'attention.")
        }
        return PostAnalysisResult(score = score, rawAnalysis = analysis)
    }
}
