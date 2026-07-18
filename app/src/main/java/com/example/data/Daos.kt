package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY platformName ASC")
    fun getAllAccounts(): Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccountById(id: Int)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    @Update
    suspend fun updatePost(post: Post)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePostById(id: Int)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface BrandKitDao {
    @Query("SELECT * FROM brand_kits WHERE id = 1 LIMIT 1")
    fun getBrandKitFlow(): Flow<BrandKit?>

    @Query("SELECT * FROM brand_kits WHERE id = 1 LIMIT 1")
    suspend fun getBrandKit(): BrandKit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrandKit(brandKit: BrandKit)
}

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items ORDER BY timestamp DESC")
    fun getAllMediaItems(): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItem)

    @Update
    suspend fun updateMediaItem(mediaItem: MediaItem)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteMediaItemById(id: Int)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("UPDATE users SET passwordHash = :newHash WHERE email = :email")
    suspend fun updatePassword(email: String, newHash: String)

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Int)
}

@Dao
interface SocialProfileDao {
    @Query("SELECT * FROM social_profiles ORDER BY platformName ASC")
    fun getAllSocialProfiles(): Flow<List<SocialProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialProfile(profile: SocialProfile)

    @Update
    suspend fun updateSocialProfile(profile: SocialProfile)

    @Query("DELETE FROM social_profiles WHERE id = :id")
    suspend fun deleteSocialProfileById(id: Int)
}

@Dao
interface AuthenticationLogDao {
    @Query("SELECT * FROM authentication_logs ORDER BY timestamp DESC")
    fun getAllAuthLogs(): Flow<List<AuthenticationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthLog(log: AuthenticationLog)
}

