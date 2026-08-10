package com.example.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database migrations — one object per version bump.
 *
 * History (reconstructed from the current schema at version 5):
 *
 *  v1 → v2 : Added `isProfessional` column to `accounts`
 *  v2 → v3 : Added `users` table and `social_profiles` table
 *  v3 → v4 : Added `authentication_logs` table
 *             Added `isGoogleConnected`, `isEmailVerified`, `verificationCode` to `users`
 *  v4 → v5 : Added `adaptedContent` column to `posts`
 *             Added `fileSize` column to `media_items`
 *
 * When you add a new column or table in the future:
 *  1. Bump `version` in AppDatabase
 *  2. Add a new MIGRATION_X_Y object here
 *  3. Add it to the `ALL_MIGRATIONS` list
 *  4. Done — no data loss.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Added isProfessional to accounts (default TRUE to preserve existing rows)
        db.execSQL(
            "ALTER TABLE accounts ADD COLUMN isProfessional INTEGER NOT NULL DEFAULT 1"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create users table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                fullName TEXT NOT NULL,
                email TEXT NOT NULL,
                passwordHash TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'Utilisateur',
                isGoogleConnected INTEGER NOT NULL DEFAULT 0,
                isEmailVerified INTEGER NOT NULL DEFAULT 0,
                verificationCode TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
        )

        // Create social_profiles table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS social_profiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                platformName TEXT NOT NULL,
                profileUrl TEXT NOT NULL,
                isActive INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create authentication_logs table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS authentication_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                email TEXT NOT NULL,
                actionType TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                details TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Added adaptedContent to posts (default empty string)
        db.execSQL(
            "ALTER TABLE posts ADD COLUMN adaptedContent TEXT NOT NULL DEFAULT ''"
        )

        // Added fileSize to media_items (default placeholder)
        db.execSQL(
            "ALTER TABLE media_items ADD COLUMN fileSize TEXT NOT NULL DEFAULT '1.5 MB'"
        )
    }
}

/**
 * Provide all migrations as a single array to pass into Room's builder.
 * Order does not matter — Room selects the right path automatically.
 */
val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5
)
