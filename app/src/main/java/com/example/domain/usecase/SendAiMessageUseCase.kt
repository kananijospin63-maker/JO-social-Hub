package com.example.domain.usecase

import android.util.Log
import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GEMINI_MODEL
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.GeminiApiService
import com.example.data.api.Part
import com.example.data.repository.JovimRepository
import com.example.domain.model.Result
import com.example.domain.model.safeCall
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Sends a user message to the Gemini AI assistant.
 *
 * Returns Result<String>:
 *  - Success → the AI response text (or offline fallback text)
 *  - Error   → network / API failure with human-readable message
 *
 * The offline fallback is treated as a Success (the user still gets
 * a useful response), not as an Error.
 */
class SendAiMessageUseCase @Inject constructor(
    private val repository: JovimRepository,
    private val geminiService: GeminiApiService
) {
    suspend operator fun invoke(prompt: String): Result<String> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("SendAiMessageUseCase", "Gemini API key not configured — using offline fallback.")
            return Result.Success(offlineFallback(prompt))
        }

        val brandKit = repository.getBrandKit()
        val systemInstruction = """
            Tu es "JO AI Assistant", l'assistant d'intelligence artificielle intégré de l'application "JO Social Hub".
            Ton rôle est d'aider les community managers, créateurs de contenu et entreprises à rédiger, optimiser, formuler et adapter des publications pour tous leurs réseaux sociaux.
            
            Informations sur l'entreprise de l'utilisateur (Mémoire de marque) :
            - Nom : ${brandKit.companyName}
            - Slogan : ${brandKit.slogan}
            - Services/Produits : ${brandKit.services}
            - Audience cible : ${brandKit.targetAudience}
            - Style de communication souhaité : ${brandKit.communicationStyle}
            
            CONSIGNE CRITIQUE DE STYLE : Ne mets JAMAIS d'émojis ni de symboles pictographiques dans tes réponses. Réponds toujours de manière professionnelle, sobre, claire et moderne, en français.
        """.trimIndent()

        val history = repository.allMessages.first().takeLast(10)
        val contents = history.map { msg ->
            Content(parts = listOf(Part(text = msg.text)))
        }.toMutableList()
        contents.add(Content(parts = listOf(Part(text = prompt))))

        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        return safeCall {
            val response = geminiService.generateContent(GEMINI_MODEL, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: offlineFallback(prompt)
        }.also { result ->
            if (result is Result.Error) {
                Log.e("SendAiMessageUseCase", "Gemini API error", result.exception)
            }
        }.let { result ->
            // On network failure, degrade gracefully to offline fallback (still a Success)
            if (result is Result.Error) Result.Success(offlineFallback(prompt)) else result
        }
    }

    private fun offlineFallback(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("idé") || p.contains("inspi") ->
                "**Idées de publications pour vous :**\n\n" +
                "1. **Coulisses (Behind-the-scenes)** : Montrez l'équipe ou le processus de création de JO Social Hub.\n" +
                "2. **Tutoriel rapide** : Une astuce courte liée à votre domaine d'activité.\n" +
                "3. **Question ouverte** : Demandez l'avis de votre communauté pour booster l'engagement."

            p.contains("hashtag") || p.contains("tag") ->
                "**Hashtags optimisés suggérés :**\n" +
                "#DigitalMarketing #SocialMediaHub #JOSquad #ContentCreator #GrowthMarketing #SMM #CommunityManager"

            p.contains("slogan") ->
                "**Propositions de slogans modernes :**\n\n" +
                "1. *« JO Social Hub : Votre voix, partout, à l'unisson. »*\n" +
                "2. *« Unifiez votre communication, démultipliez votre impact. »*\n" +
                "3. *« L'intelligence artificielle au service de vos réseaux. »*"

            else ->
                "Bonjour ! Je suis **JO AI Assistant**.\n\n" +
                "Je suis prêt à rédiger des publications professionnelles, à corriger vos textes ou à générer une newsletter sur mesure.\n\n" +
                "*Note : L'API Gemini est en mode local. Connectez votre clé API pour des réponses 100% dynamiques.*"
        }
    }
}
