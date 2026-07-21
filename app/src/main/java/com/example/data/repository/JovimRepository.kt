package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.*
import com.example.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class JovimRepository(
    private val accountDao: AccountDao,
    private val postDao: PostDao,
    private val chatMessageDao: ChatMessageDao,
    private val brandKitDao: BrandKitDao,
    private val mediaItemDao: MediaItemDao,
    private val userDao: UserDao,
    private val socialProfileDao: SocialProfileDao,
    private val authenticationLogDao: AuthenticationLogDao,
    private val geminiService: GeminiApiService = RetrofitClient.service
) {
    // Flows from database
    val allAccounts: Flow<List<Account>> = accountDao.getAllAccounts()
    val allPosts: Flow<List<Post>> = postDao.getAllPosts()
    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()
    val brandKitFlow: Flow<BrandKit?> = brandKitDao.getBrandKitFlow()
    val allMediaItems: Flow<List<MediaItem>> = mediaItemDao.getAllMediaItems()
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allSocialProfiles: Flow<List<SocialProfile>> = socialProfileDao.getAllSocialProfiles()
    val allAuthLogs: Flow<List<AuthenticationLog>> = authenticationLogDao.getAllAuthLogs()

    suspend fun insertAuthLog(log: AuthenticationLog) = withContext(Dispatchers.IO) {
        authenticationLogDao.insertAuthLog(log)
    }

    // SocialProfile Operations
    suspend fun insertSocialProfile(profile: SocialProfile) = withContext(Dispatchers.IO) {
        socialProfileDao.insertSocialProfile(profile)
    }

    suspend fun updateSocialProfile(profile: SocialProfile) = withContext(Dispatchers.IO) {
        socialProfileDao.updateSocialProfile(profile)
    }

    suspend fun deleteSocialProfileById(id: Int) = withContext(Dispatchers.IO) {
        socialProfileDao.deleteSocialProfileById(id)
    }

    // User Operations
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun updatePassword(email: String, newHash: String) = withContext(Dispatchers.IO) {
        userDao.updatePassword(email, newHash)
    }

    suspend fun deleteUserById(id: Int) = withContext(Dispatchers.IO) {
        userDao.deleteUserById(id)
    }

    // Core Database Operations
    suspend fun insertAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccountById(id: Int) = withContext(Dispatchers.IO) {
        accountDao.deleteAccountById(id)
    }

    suspend fun insertPost(post: Post): Long = withContext(Dispatchers.IO) {
        postDao.insertPost(post)
    }

    suspend fun updatePost(post: Post) = withContext(Dispatchers.IO) {
        postDao.updatePost(post)
    }

    suspend fun deletePostById(id: Int) = withContext(Dispatchers.IO) {
        postDao.deletePostById(id)
    }

    suspend fun insertMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun clearChatHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.clearHistory()
    }

    suspend fun getBrandKit(): BrandKit {
        return withContext(Dispatchers.IO) {
            val kit = brandKitDao.getBrandKit()
            if (kit == null) {
                val defaultKit = BrandKit()
                brandKitDao.insertBrandKit(defaultKit)
                defaultKit
            } else {
                kit
            }
        }
    }

    suspend fun saveBrandKit(brandKit: BrandKit) = withContext(Dispatchers.IO) {
        brandKitDao.insertBrandKit(brandKit)
    }

    suspend fun insertMediaItem(mediaItem: MediaItem) = withContext(Dispatchers.IO) {
        mediaItemDao.insertMediaItem(mediaItem)
    }

    suspend fun deleteMediaItemById(id: Int) = withContext(Dispatchers.IO) {
        mediaItemDao.deleteMediaItemById(id)
    }

    // Populate default database values for premium first launch experience
    suspend fun prepopulateDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val currentAccounts = allAccounts.first()
        if (currentAccounts.isEmpty()) {
            // Add default accounts
            listOf(
                Account(platformName = "Facebook", accountName = "JO Social Hub Page", isConnected = true, followerCount = 12450, status = "Connected", isProfessional = true),
                Account(platformName = "Instagram", accountName = "@ir_jospin_kanani_christoph", isConnected = true, followerCount = 8200, status = "Connected", isProfessional = true),
                Account(platformName = "TikTok", accountName = "@jo_social_hub", isConnected = false, followerCount = 0, status = "Disconnected", isProfessional = true),
                Account(platformName = "YouTube", accountName = "JO Hub Channel", isConnected = true, followerCount = 3400, status = "Connected", isProfessional = true),
                Account(platformName = "LinkedIn", accountName = "JO Corp", isConnected = true, followerCount = 1540, status = "Connected", isProfessional = true),
                Account(platformName = "X (Twitter)", accountName = "@JoSocial", isConnected = false, followerCount = 0, status = "Disconnected", isProfessional = true)
            ).forEach { accountDao.insertAccount(it) }
        }

        val currentProfiles = allSocialProfiles.first()
        if (currentProfiles.isEmpty()) {
            listOf(
                SocialProfile(platformName = "Facebook", profileUrl = "https://facebook.com"),
                SocialProfile(platformName = "Instagram", profileUrl = "https://instagram.com"),
                SocialProfile(platformName = "LinkedIn", profileUrl = "https://linkedin.com/in/kanani-jospin-a55825346/"),
                SocialProfile(platformName = "TikTok", profileUrl = "https://tiktok.com"),
                SocialProfile(platformName = "YouTube", profileUrl = "https://youtube.com"),
                SocialProfile(platformName = "X", profileUrl = "https://x.com"),
                SocialProfile(platformName = "Threads", profileUrl = "https://threads.net"),
                SocialProfile(platformName = "Pinterest", profileUrl = "https://pinterest.com"),
                SocialProfile(platformName = "WordPress", profileUrl = "https://wordpress.com"),
                SocialProfile(platformName = "Blogger", profileUrl = "https://blogger.com"),
                SocialProfile(platformName = "Medium", profileUrl = "https://medium.com")
            ).forEach { socialProfileDao.insertSocialProfile(it) }
        }

        val currentMedia = allMediaItems.first()
        if (currentMedia.isEmpty()) {
            listOf(
                MediaItem(title = "Logo JO Social Hub Premium", mediaUrl = "logo_jo_placeholder", mediaType = "IMAGE", category = "Banners"),
                MediaItem(title = "Bannière Lancement d'été", mediaUrl = "banner_summer_placeholder", mediaType = "IMAGE", category = "Campagnes"),
                MediaItem(title = "Vidéo teaser Hub V1", mediaUrl = "teaser_video_placeholder", mediaType = "VIDEO", category = "Ressources"),
                MediaItem(title = "Infographie engagement", mediaUrl = "info_engagement_placeholder", mediaType = "IMAGE", category = "Social")
            ).forEach { mediaItemDao.insertMediaItem(it) }
        }

        val currentPosts = allPosts.first()
        if (currentPosts.isEmpty()) {
            listOf(
                Post(
                    contentText = "Nous sommes ravis d'annoncer la sortie officielle de JO Social Hub ! Centralisez tous vos réseaux en un seul endroit. [Lien de téléchargement] #JO #SocialHub #Branding #Tech",
                    platforms = "Facebook,Instagram,LinkedIn",
                    status = "PUBLISHED",
                    qualityScore = 92,
                    isAdapted = true,
                    adaptedContent = "Adaptation Facebook: Post engageant et direct.\nAdaptation Instagram: Des visuels et un style accrocheur.\nAdaptation LinkedIn: Un ton professionnel soulignant l'impact business."
                ),
                Post(
                    contentText = "Comment l'Intelligence Artificielle transforme le métier de Community Manager ? Rendez-vous lundi pour notre article exclusif. [Lien vers l'article]",
                    platforms = "LinkedIn,Facebook",
                    status = "SCHEDULED",
                    scheduledTime = System.currentTimeMillis() + 86400000, // Demain
                    qualityScore = 88
                )
            ).forEach { postDao.insertPost(it) }
        }

        // Check Brand Kit
        getBrandKit()

        // Insert first administrator account safely hashed
        val adminUser = userDao.getUserByEmail("jospin.kanani")
        if (adminUser == null) {
            userDao.insertUser(
                User(
                    fullName = "Jospin Kanani",
                    email = "jospin.kanani",
                    passwordHash = PasswordHasher.hashPassword("chrisjospin2003@@"),
                    role = "Administrateur",
                    isGoogleConnected = false,
                    isEmailVerified = true
                )
            )
        }
    }

    // --- GEMINI AI SERVICES ---

    /**
     * Ask JO AI Assistant (General Assistant conversation)
     */
    suspend fun askJovimAi(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("JovimRepository", "Gemini API key is not configured. Falling back to local offline simulation.")
            return@withContext getOfflineAiFallback(prompt)
        }

        val brandKit = getBrandKit()
        val systemInstructionText = """
            Tu es "JO AI Assistant", l'assistant d'intelligence artificielle intégré de l'application "JO Social Hub".
            Ton rôle est d'aider les community managers, créateurs de contenu et entreprises à rédiger, optimiser, formuler et adapter des publications pour tous leurs réseaux sociaux.
            
            Informations sur l'entreprise de l'utilisateur (Mémoire de marque) :
            - Nom : ${brandKit.companyName}
            - Slogan : ${brandKit.slogan}
            - Services/Produits : ${brandKit.services}
            - Audience cible : ${brandKit.targetAudience}
            - Style de communication souhaité : ${brandKit.communicationStyle}
            
            CONSIGNE CRITIQUE DE STYLE : Ne mets JAMAIS d'émojis ni de symboles pictographiques (comme des émoticônes ou des petits dessins) dans tes réponses ou tes rédactions de posts. Propose plutôt des structures prêtes à l'emploi avec des listes à puces claires, des titres en gras bien structurés et des hashtags pertinents. Réponds toujours de manière professionnelle, sobre, claire, concise et moderne, en français.
        """.trimIndent()

        // Gather chat history to maintain context (maximum last 10 messages)
        val history = allMessages.first().takeLast(10)
        val contents = mutableListOf<Content>()
        
        history.forEach {
            contents.add(Content(parts = listOf(Part(text = it.text))))
        }
        contents.add(Content(parts = listOf(Part(text = prompt))))

        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Désolé, je n'ai pas pu générer de réponse pour le moment."
        } catch (e: Exception) {
            Log.e("JovimRepository", "Error calling Gemini API", e)
            "Erreur de connexion avec l'IA. Voici une réponse locale : ${getOfflineAiFallback(prompt)}"
        }
    }

    /**
     * Adapt publication content specifically for various networks
     */
    suspend fun adaptContentForPlatforms(baseText: String, targetPlatforms: List<String>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineAdaptationFallback(baseText, targetPlatforms)
        }

        val brandKit = getBrandKit()
        val platformsString = targetPlatforms.joinToString(", ")
        val prompt = """
            Adapte la publication suivante pour les plateformes demandées : $platformsString.
            Texte de base : "$baseText"
            
            Identité de marque :
            - Nom : ${brandKit.companyName}
            - Style : ${brandKit.communicationStyle}
            
            CONSIGNE CRITIQUE : Ne génère AUCUN émoji ou symbole pictographique sous aucun prétexte. Remplace-les par une rédaction soignée et de qualité professionnelle.
            
            Pour chaque plateforme sélectionnée, écris une version sur mesure en respectant ses codes professionnels (longueur, structure, hashtags, ton) :
            - Facebook : Texte engageant, fluide, professionnel et aéré avec appel à l'action clair.
            - Instagram : Style dynamique, visuel, bloc de hashtags séparé en bas, pas d'émojis.
            - LinkedIn : Ton professionnel, structuré, mettant en valeur l'expertise ou l'impact commercial.
            - TikTok : Accroche ultra-courte, percutante, hashtags tendances.
            - YouTube : Titre accrocheur et description structurée avec mots-clés SEO.
            - WordPress : Style d'article de blog concis avec structure (Titre H1, Introduction, points clés).
            - Email : Structure de newsletter professionnelle et sobre avec objet d'e-mail accrocheur et salutations.
            
            Sépare chaque plateforme clairement avec un titre en gras (ex: **Facebook**, **Instagram**, etc.).
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "Tu es un copywriter expert en réseaux sociaux."))),
            generationConfig = GenerationConfig(temperature = 0.6f)
        )

        try {
            val response = geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Impossible d'adapter le contenu actuellement."
        } catch (e: Exception) {
            Log.e("JovimRepository", "Error adapting content via Gemini", e)
            getOfflineAdaptationFallback(baseText, targetPlatforms)
        }
    }

    /**
     * Analyze content pre-publication to calculate a score and return suggestions
     */
    suspend fun analyzePostContent(baseText: String): PostAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineAnalysisFallback(baseText)
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

        try {
            val response = geminiService.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Analyse indisponible."
            
            // Extract a score from text (ex: Score: 85 or 85/100)
            val scorePattern = "\\b([0-9]{2,3})\\b".toRegex()
            val matches = scorePattern.findAll(responseText)
            val score = matches.firstOrNull { match -> 
                val value = match.value.toIntOrNull() ?: 0
                value in 40..100
            }?.value?.toIntOrNull() ?: 78

            PostAnalysisResult(
                score = score,
                rawAnalysis = responseText
            )
        } catch (e: Exception) {
            Log.e("JovimRepository", "Error analyzing post", e)
            getOfflineAnalysisFallback(baseText)
        }
    }

    // --- OFFLINE SIMULATION FALLBACKS (Ensure a seamless offline experience when key is missing) ---

    private fun getOfflineAiFallback(prompt: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("idé") || p.contains("inspi") -> {
                "**Idées de publications pour vous :**\n\n1. **Coulisses (Behind-the-scenes)** : Montrez l'équipe ou le processus de création de JO Social Hub. Idéal pour Instagram/TikTok.\n2. **Tutoriel rapide** : Une astuce courte liée à votre domaine d'activité. Idéal pour LinkedIn/YouTube.\n3. **Question ouverte** : Demandez l'avis de votre communauté sur un sujet tendance pour booster l'engagement."
            }
            p.contains("hashtag") || p.contains("tag") -> {
                "**Hashtags optimisés suggérés :**\n#DigitalMarketing #SocialMediaHub #JOSquad #ContentCreator #GrowthMarketing #SMM #CommunityManager"
            }
            p.contains("slogan") -> {
                "**Propositions de slogans modernes :**\n\n1. *« JO Social Hub : Votre voix, partout, à l'unisson. »*\n2. *« Unifiez votre communication, démultipliez votre impact. »*\n3. *« L'intelligence artificielle au service de vos réseaux. »*"
            }
            else -> {
                "Bonjour ! Je suis **JO AI Assistant**.\n\nJe suis prêt à rédiger des publications professionnelles, à corriger vos textes ou à générer une newsletter sur mesure.\n\n*Note : L'API Gemini est actuellement en mode local. Connectez votre clé API dans les secrets de l'application pour des réponses 100% dynamiques.*"
            }
        }
    }

    private fun getOfflineAdaptationFallback(baseText: String, targetPlatforms: List<String>): String {
        val sb = StringBuilder()
        sb.append("**Adaptation intelligente par JO AI Assistant (Mode local)**\n\n")
        sb.append("Texte de base : *\"$baseText\"*\n\n")

        targetPlatforms.forEach { platform ->
            sb.append("### **$platform**\n")
            when (platform) {
                "Facebook" -> {
                    sb.append("$baseText\n\nQu'en pensez-vous ? Laissez-nous un commentaire ! #JOSocialHub #Hub #Com\n\n")
                }
                "Instagram" -> {
                    sb.append("$baseText\n\n.\n.\n.\n#JOSocialHub #InstaTech #VisualBranding #MarketingHub\n\n")
                }
                "LinkedIn" -> {
                    sb.append("**Digital Hub : Simplification de flux**\n\n$baseText\n\nCette approche permet d'optimiser l'engagement de +24% en moyenne. Quel est votre retour d'expérience ?\n\n#SocialMediaStrategy #Productivity #BusinessTech\n\n")
                }
                "TikTok" -> {
                    sb.append("POV: Tu centralises toute ta com' digitale en 1 clic\n\n#JO #foryou #growthhack #techhack\n\n")
                }
                "YouTube" -> {
                    sb.append("**[TITRE] Centraliser sa communication avec JO Social Hub**\n\nDescription :\nDécouvrez comment optimiser vos flux réseaux avec notre assistant de création de posts.\n\nTags: #JO, #SocialMediaManager, #ContentCreator\n\n")
                }
                "WordPress" -> {
                    sb.append("**[H1] Optimisation de votre communication multi-canal**\n\nDans cet article court, nous abordons la problématique suivante : $baseText.\n\n### Points clés :\n- Centralisation des comptes de marque\n- Adaptation automatique par IA\n- Gain de temps hebdomadaire estimé à 10 heures.\n\n")
                }
                "Email" -> {
                    sb.append("**Objet : Unifiez vos réseaux sociaux avec JO Social Hub !**\n\nBonjour,\n\n$baseText\n\nDécouvrez notre démo exclusive dès aujourd'hui.\n\nCordialement,\nL'équipe JO\n\n")
                }
                else -> {
                    sb.append("**$platform** : $baseText #SocialMedia\n\n")
                }
            }
        }
        return sb.toString()
    }

    private fun getOfflineAnalysisFallback(baseText: String): PostAnalysisResult {
        val score = if (baseText.length > 50) 84 else 62
        val analysis = """
            **Audit de Publication (Mode Local)**
            
            - **Qualité du texte** : Le message est clair. ${if (baseText.length > 50) "La longueur est idéale et apporte de la valeur." else "C'est un peu court, l'ajout de détails capterait mieux l'audience."}
            - **Hashtags** : ${if (baseText.contains("#")) "Présence de hashtags pertinents pour le ciblage." else "Aucun hashtag détecté. Nous vous recommandons d'en ajouter."}
            - **Potentiel d'engagement** : ${if (baseText.length > 50) "Excellent" else "Moyen"}
            
            **Suggestions de JO AI Assistant :**
            1. Ajoutez un appel à l'action clair (ex: « Donnez votre avis ! »).
            2. Aérez le texte en insérant des sauts de ligne pour faciliter la lecture.
            3. Associez un visuel professionnel de qualité pour attirer l'attention.
        """.trimIndent()
        
        return PostAnalysisResult(score = score, rawAnalysis = analysis)
    }
}

data class PostAnalysisResult(
    val score: Int,
    val rawAnalysis: String
)
