package com.example.data.repository

import com.example.data.Account
import com.example.data.AccountDao
import com.example.data.AuthenticationLog
import com.example.data.AuthenticationLogDao
import com.example.data.BrandKit
import com.example.data.BrandKitDao
import com.example.data.ChatMessage
import com.example.data.ChatMessageDao
import com.example.data.MediaItem
import com.example.data.MediaItemDao
import com.example.data.Post
import com.example.data.PostDao
import com.example.data.SocialProfile
import com.example.data.SocialProfileDao
import com.example.data.User
import com.example.data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * JovimRepository — pure data-access layer.
 *
 * Responsibilities:
 *  - Expose Flows from Room DAOs
 *  - Provide suspend functions for CRUD operations
 *  - Switch to Dispatchers.IO for all DB work
 *
 * NOT responsible for:
 *  - Business logic (validation, rules) → use cases
 *  - AI prompt building or Gemini calls → use cases
 *  - Offline fallback logic → use cases
 *  - Database seeding → SeedDatabaseUseCase
 */
class JovimRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val postDao: PostDao,
    private val chatMessageDao: ChatMessageDao,
    private val brandKitDao: BrandKitDao,
    private val mediaItemDao: MediaItemDao,
    private val userDao: UserDao,
    private val socialProfileDao: SocialProfileDao,
    private val authenticationLogDao: AuthenticationLogDao
) {
    // -------------------------------------------------------------------------
    // Flows — observed by the ViewModel via StateFlow
    // -------------------------------------------------------------------------

    val allAccounts: Flow<List<Account>>         = accountDao.getAllAccounts()
    val allPosts: Flow<List<Post>>               = postDao.getAllPosts()
    val allMessages: Flow<List<ChatMessage>>     = chatMessageDao.getAllMessages()
    val brandKitFlow: Flow<BrandKit?>            = brandKitDao.getBrandKitFlow()
    val allMediaItems: Flow<List<MediaItem>>     = mediaItemDao.getAllMediaItems()
    val allUsers: Flow<List<User>>               = userDao.getAllUsers()
    val allSocialProfiles: Flow<List<SocialProfile>>   = socialProfileDao.getAllSocialProfiles()
    val allAuthLogs: Flow<List<AuthenticationLog>>     = authenticationLogDao.getAllAuthLogs()

    // -------------------------------------------------------------------------
    // Accounts
    // -------------------------------------------------------------------------

    suspend fun insertAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccountById(id: Int) = withContext(Dispatchers.IO) {
        accountDao.deleteAccountById(id)
    }

    // -------------------------------------------------------------------------
    // Posts
    // -------------------------------------------------------------------------

    suspend fun insertPost(post: Post): Long = withContext(Dispatchers.IO) {
        postDao.insertPost(post)
    }

    suspend fun updatePost(post: Post) = withContext(Dispatchers.IO) {
        postDao.updatePost(post)
    }

    suspend fun deletePostById(id: Int) = withContext(Dispatchers.IO) {
        postDao.deletePostById(id)
    }

    // -------------------------------------------------------------------------
    // Chat messages
    // -------------------------------------------------------------------------

    suspend fun insertMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun clearChatHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.clearHistory()
    }

    // -------------------------------------------------------------------------
    // Brand kit
    // -------------------------------------------------------------------------

    suspend fun getBrandKit(): BrandKit = withContext(Dispatchers.IO) {
        brandKitDao.getBrandKit() ?: BrandKit().also { brandKitDao.insertBrandKit(it) }
    }

    suspend fun saveBrandKit(brandKit: BrandKit) = withContext(Dispatchers.IO) {
        brandKitDao.insertBrandKit(brandKit)
    }

    // -------------------------------------------------------------------------
    // Media items
    // -------------------------------------------------------------------------

    suspend fun insertMediaItem(mediaItem: MediaItem) = withContext(Dispatchers.IO) {
        mediaItemDao.insertMediaItem(mediaItem)
    }

    suspend fun updateMediaItem(mediaItem: MediaItem) = withContext(Dispatchers.IO) {
        mediaItemDao.updateMediaItem(mediaItem)
    }

    suspend fun deleteMediaItemById(id: Int) = withContext(Dispatchers.IO) {
        mediaItemDao.deleteMediaItemById(id)
    }

    // -------------------------------------------------------------------------
    // Users
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Social profiles
    // -------------------------------------------------------------------------

    suspend fun insertSocialProfile(profile: SocialProfile) = withContext(Dispatchers.IO) {
        socialProfileDao.insertSocialProfile(profile)
    }

    suspend fun updateSocialProfile(profile: SocialProfile) = withContext(Dispatchers.IO) {
        socialProfileDao.updateSocialProfile(profile)
    }

    suspend fun deleteSocialProfileById(id: Int) = withContext(Dispatchers.IO) {
        socialProfileDao.deleteSocialProfileById(id)
    }

    // -------------------------------------------------------------------------
    // Authentication logs
    // -------------------------------------------------------------------------

    suspend fun insertAuthLog(log: AuthenticationLog) = withContext(Dispatchers.IO) {
        authenticationLogDao.insertAuthLog(log)
    }
}
