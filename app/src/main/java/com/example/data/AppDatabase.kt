package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

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
    version = 4,
    exportSchema = false
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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jovim_social_hub_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
