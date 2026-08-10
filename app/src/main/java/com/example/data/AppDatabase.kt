package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database definition.
 *
 * - exportSchema = true  → Room writes a JSON schema file to the schemas/ folder
 *   on each version bump, giving you a permanent record of every schema change.
 *   These files should be committed to version control.
 *
 * - The companion object / manual singleton is removed.
 *   The database instance is now provided exclusively by Hilt (DatabaseModule),
 *   which guarantees a single instance for the app's lifetime.
 */
@Database(
    entities = [
        Account::class,
        Post::class,
        ChatMessage::class,
        BrandKit::class,
        MediaItem::class,
        User::class,
        SocialProfile::class,
        AuthenticationLog::class
    ],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun postDao(): PostDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun brandKitDao(): BrandKitDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun userDao(): UserDao
    abstract fun socialProfileDao(): SocialProfileDao
    abstract fun authenticationLogDao(): AuthenticationLogDao
}
