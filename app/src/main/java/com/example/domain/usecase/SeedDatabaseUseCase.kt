package com.example.domain.usecase

import com.example.data.Account
import com.example.data.MediaItem
import com.example.data.Post
import com.example.data.SocialProfile
import com.example.data.User
import com.example.data.repository.JovimRepository
import com.example.data.repository.PasswordHasher
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Seeds the database with default demo data on first launch.
 * Extracted from JovimRepository — seeding is app-level initialisation
 * logic, not a data-access responsibility.
 */
class SeedDatabaseUseCase @Inject constructor(
    private val repository: JovimRepository
) {
    suspend operator fun invoke() {
        seedAccounts()
        seedSocialProfiles()
        seedMediaItems()
        seedPosts()
        repository.getBrandKit() // ensures default BrandKit is created if absent
        seedAdminUser()
    }

    private suspend fun seedAccounts() {
        if (repository.allAccounts.first().isNotEmpty()) return
        listOf(
            Account(platformName = "Facebook",    accountName = "JO Social Hub Page",          isConnected = true,  followerCount = 12450, status = "Connected",    isProfessional = true),
            Account(platformName = "Instagram",   accountName = "@ir_jospin_kanani_christoph",  isConnected = true,  followerCount = 8200,  status = "Connected",    isProfessional = true),
            Account(platformName = "TikTok",      accountName = "@jo_social_hub",               isConnected = false, followerCount = 0,     status = "Disconnected", isProfessional = true),
            Account(platformName = "YouTube",     accountName = "JO Hub Channel",               isConnected = true,  followerCount = 3400,  status = "Connected",    isProfessional = true),
            Account(platformName = "LinkedIn",    accountName = "JO Corp",                      isConnected = true,  followerCount = 1540,  status = "Connected",    isProfessional = true),
            Account(platformName = "X (Twitter)", accountName = "@JoSocial",                    isConnected = false, followerCount = 0,     status = "Disconnected", isProfessional = true)
        ).forEach { repository.insertAccount(it) }
    }

    private suspend fun seedSocialProfiles() {
        if (repository.allSocialProfiles.first().isNotEmpty()) return
        listOf(
            SocialProfile(platformName = "Facebook",  profileUrl = "https://facebook.com"),
            SocialProfile(platformName = "Instagram", profileUrl = "https://instagram.com"),
            SocialProfile(platformName = "LinkedIn",  profileUrl = "https://linkedin.com/in/kanani-jospin-a55825346/"),
            SocialProfile(platformName = "TikTok",    profileUrl = "https://tiktok.com"),
            SocialProfile(platformName = "YouTube",   profileUrl = "https://youtube.com"),
            SocialProfile(platformName = "X",         profileUrl = "https://x.com"),
            SocialProfile(platformName = "Threads",   profileUrl = "https://threads.net"),
            SocialProfile(platformName = "Pinterest", profileUrl = "https://pinterest.com"),
            SocialProfile(platformName = "WordPress", profileUrl = "https://wordpress.com"),
            SocialProfile(platformName = "Blogger",   profileUrl = "https://blogger.com"),
            SocialProfile(platformName = "Medium",    profileUrl = "https://medium.com")
        ).forEach { repository.insertSocialProfile(it) }
    }

    private suspend fun seedMediaItems() {
        if (repository.allMediaItems.first().isNotEmpty()) return
        listOf(
            MediaItem(title = "Logo JO Social Hub Premium",  mediaUrl = "logo_jo_placeholder",      mediaType = "IMAGE", category = "Banners"),
            MediaItem(title = "Bannière Lancement d'été",    mediaUrl = "banner_summer_placeholder", mediaType = "IMAGE", category = "Campagnes"),
            MediaItem(title = "Vidéo teaser Hub V1",         mediaUrl = "teaser_video_placeholder",  mediaType = "VIDEO", category = "Ressources"),
            MediaItem(title = "Infographie engagement",      mediaUrl = "info_engagement_placeholder",mediaType = "IMAGE", category = "Social")
        ).forEach { repository.insertMediaItem(it) }
    }

    private suspend fun seedPosts() {
        if (repository.allPosts.first().isNotEmpty()) return
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
                scheduledTime = System.currentTimeMillis() + 86400000,
                qualityScore = 88
            )
        ).forEach { repository.insertPost(it) }
    }

    private suspend fun seedAdminUser() {
        if (repository.getUserByEmail("jospin.kanani") != null) return
        // TODO: Change the default admin password on first launch via the Settings screen.
        // The password below is hashed with BCrypt — never store plaintext passwords in code.
        // To change it: log in with this account and use the "Modifier le mot de passe" option.
        repository.insertUser(
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
