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
import javax.inject.Inject

/**
 * Adapts a post's content for each selected social platform using Gemini AI.
 *
 * Returns Result<String>:
 *  - Success → adapted content text (or offline fallback)
 *  - Error   → only for unexpected failures not covered by the offline fallback
 */
class AdaptContentUseCase @Inject constructor(
    private val repository: JovimRepository,
    private val geminiService: GeminiApiService
) {
    suspend operator fun invoke(baseText: String, targetPlatforms: List<String>): Result<String> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return Result.Success(offlineFallback(baseText, targetPlatforms))
        }

        val brandKit = repository.getBrandKit()
        val platformsString = targetPlatforms.joinToString(", ")

        val prompt = """
            Adapte la publication suivante pour les plateformes demandées : $platformsString.
            Texte de base : "$baseText"
            
            Identité de marque :
            - Nom : ${brandKit.companyName}
            - Style : ${brandKit.communicationStyle}
            
            CONSIGNE CRITIQUE : Ne génère AUCUN émoji ou symbole pictographique.
            
            Pour chaque plateforme, écris une version sur mesure :
            - Facebook : Texte engageant, fluide, professionnel, avec appel à l'action clair.
            - Instagram : Style dynamique, bloc de hashtags séparé en bas.
            - LinkedIn : Ton professionnel, structuré, mettant en valeur l'expertise ou l'impact commercial.
            - TikTok : Accroche ultra-courte, percutante, hashtags tendances.
            - YouTube : Titre accrocheur et description structurée avec mots-clés SEO.
            - WordPress : Style d'article de blog concis (Titre H1, Introduction, points clés).
            - Email : Structure de newsletter professionnelle avec objet d'e-mail accrocheur.
            
            Sépare chaque plateforme avec un titre en gras (ex: **Facebook**, **Instagram**, etc.).
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "Tu es un copywriter expert en réseaux sociaux."))),
            generationConfig = GenerationConfig(temperature = 0.6f)
        )

        return safeCall {
            val response = geminiService.generateContent(GEMINI_MODEL, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: offlineFallback(baseText, targetPlatforms)
        }.also { result ->
            if (result is Result.Error) {
                Log.e("AdaptContentUseCase", "Gemini API error", result.exception)
            }
        }.let { result ->
            if (result is Result.Error) Result.Success(offlineFallback(baseText, targetPlatforms)) else result
        }
    }

    private fun offlineFallback(baseText: String, targetPlatforms: List<String>): String {
        val sb = StringBuilder()
        sb.append("**Adaptation intelligente par JO AI Assistant (Mode local)**\n\n")
        sb.append("Texte de base : *\"$baseText\"*\n\n")
        targetPlatforms.forEach { platform ->
            sb.append("### **$platform**\n")
            when (platform) {
                "Facebook"  -> sb.append("$baseText\n\nQu'en pensez-vous ? Laissez-nous un commentaire ! #JOSocialHub #Hub #Com\n\n")
                "Instagram" -> sb.append("$baseText\n\n.\n.\n.\n#JOSocialHub #InstaTech #VisualBranding #MarketingHub\n\n")
                "LinkedIn"  -> sb.append("**Digital Hub : Simplification de flux**\n\n$baseText\n\nQuel est votre retour d'expérience ?\n\n#SocialMediaStrategy #Productivity #BusinessTech\n\n")
                "TikTok"    -> sb.append("POV: Tu centralises toute ta com' digitale en 1 clic\n\n#JO #foryou #growthhack #techhack\n\n")
                "YouTube"   -> sb.append("**[TITRE] Centraliser sa communication avec JO Social Hub**\n\nDescription :\nDécouvrez comment optimiser vos flux réseaux.\n\nTags: #JO, #SocialMediaManager, #ContentCreator\n\n")
                "WordPress" -> sb.append("**[H1] Optimisation de votre communication multi-canal**\n\n$baseText\n\n### Points clés :\n- Centralisation des comptes\n- Adaptation automatique par IA\n- Gain de temps estimé à 10 heures/semaine.\n\n")
                "Email"     -> sb.append("**Objet : Unifiez vos réseaux sociaux avec JO Social Hub !**\n\nBonjour,\n\n$baseText\n\nCordialement,\nL'équipe JO\n\n")
                else        -> sb.append("**$platform** : $baseText #SocialMedia\n\n")
            }
        }
        return sb.toString()
    }
}
