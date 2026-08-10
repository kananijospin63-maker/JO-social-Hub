package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.ALL_MIGRATIONS
import com.example.data.AppDatabase
import com.example.data.AccountDao
import com.example.data.AuthenticationLogDao
import com.example.data.BrandKitDao
import com.example.data.ChatMessageDao
import com.example.data.MediaItemDao
import com.example.data.PostDao
import com.example.data.SocialProfileDao
import com.example.data.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides the Room database and all DAOs.
 *
 * Key change: addMigrations(*ALL_MIGRATIONS) replaces fallbackToDestructiveMigration.
 * User data is now preserved across app updates.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "jovim_social_hub_database"
        )
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    @Singleton
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    @Singleton
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()

    @Provides
    @Singleton
    fun provideBrandKitDao(db: AppDatabase): BrandKitDao = db.brandKitDao()

    @Provides
    @Singleton
    fun provideMediaItemDao(db: AppDatabase): MediaItemDao = db.mediaItemDao()

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideSocialProfileDao(db: AppDatabase): SocialProfileDao = db.socialProfileDao()

    @Provides
    @Singleton
    fun provideAuthenticationLogDao(db: AppDatabase): AuthenticationLogDao = db.authenticationLogDao()
}
