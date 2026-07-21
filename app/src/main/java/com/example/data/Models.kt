package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val platformName: String, // "Facebook", "Instagram", "TikTok", "YouTube", "LinkedIn", "X (Twitter)", "WordPress", "Email"
    val accountName: String,
    val isConnected: Boolean = false,
    val avatarUrl: String = "",
    val followerCount: Int = 0,
    val status: String = "Disconnected",
    val isProfessional: Boolean = true
)

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentText: String,
    val mediaUrl: String = "",
    val mediaType: String = "NONE", // "IMAGE", "VIDEO", "NONE"
    val platforms: String, // Comma-separated list: "Facebook,Instagram"
    val status: String = "DRAFT", // "DRAFT", "SCHEDULED", "PUBLISHED"
    val scheduledTime: Long = 0, // Timestamp or 0 if immediate
    val qualityScore: Int = 0,
    val isAdapted: Boolean = false,
    val adaptedContent: String = "" // JSON-like or simple custom representation
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "brand_kits")
data class BrandKit(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "JO Social Hub",
    val slogan: String = "Innover pour le futur numérique",
    val services: String = "Communication digitale, Création automatisée",
    val targetAudience: String = "Entreprises, Créateurs de contenu, Agences",
    val communicationStyle: String = "Professionnel, Inspirant, Dynamique",
    val primaryColorHex: String = "#0F172A",
    val secondaryColorHex: String = "#F59E0B"
)

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val mediaUrl: String,
    val mediaType: String, // "IMAGE", "VIDEO"
    val category: String, // "Social", "Campagnes", "Banners", "Ressources"
    val isFavorite: Boolean = false,
    val fileSize: String = "1.5 MB",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String, // email or username like jospin.kanani
    val passwordHash: String,
    val role: String = "Utilisateur", // "Administrateur", "Utilisateur"
    val isGoogleConnected: Boolean = false,
    val isEmailVerified: Boolean = false,
    val verificationCode: String = ""
)

@Entity(tableName = "social_profiles")
data class SocialProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val platformName: String, // "Facebook", "Instagram", "TikTok", "YouTube", "LinkedIn", "X", "Threads", "Pinterest", "WordPress", "Blogger", "Medium"
    val profileUrl: String,
    val isActive: Boolean = true
)

@Entity(tableName = "authentication_logs")
data class AuthenticationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val actionType: String, // "LOGIN_SUCCESS", "LOGIN_FAILED", "REGISTER_SUCCESS", "GOOGLE_AUTH"
    val timestamp: Long = System.currentTimeMillis(),
    val details: String
)

