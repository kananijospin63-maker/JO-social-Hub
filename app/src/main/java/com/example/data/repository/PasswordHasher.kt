package com.example.data.repository

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Password hashing using BCrypt (cost factor 12).
 *
 * Why BCrypt instead of SHA-256 :
 *  - BCrypt is intentionally slow — cost factor 12 ≈ 300ms on a modern phone,
 *    which is acceptable for a user login but makes brute-force attacks
 *    computationally expensive (thousands of years on GPU clusters).
 *  - Each hash embeds a unique random salt, so two identical passwords produce
 *    different hashes. Rainbow tables are useless.
 *  - The cost factor is stored inside the hash string itself, so future upgrades
 *    (e.g. cost factor 13) can be done transparently at next login.
 *  - SHA-256 with a static salt is equivalent to no salt at all — attackable in
 *    seconds with a GPU and a wordlist.
 *
 * Cost factor guidance :
 *  - 10 : ~100ms  — minimum acceptable
 *  - 12 : ~300ms  — recommended (2026 standard)
 *  - 14 : ~1.2s   — high security, noticeable delay
 *
 * Migration :
 *  - Legacy SHA-256 hashes (64 hex chars) are detected by their format.
 *  - verifyPassword() returns true for valid legacy hashes so existing accounts
 *    keep working. The caller (LoginUserUseCase) must re-hash and save the new
 *    bcrypt hash after a successful legacy login to complete the migration.
 */
object PasswordHasher {

    /** BCrypt cost factor — increase over time as hardware gets faster. */
    private const val COST = 12

    /**
     * Hashes [password] with BCrypt and a unique random salt.
     * The returned string contains the algorithm, cost, salt and hash — all
     * needed for verification are embedded in the single string.
     *
     * Format: `$2a$12$<22-char salt><31-char hash>`
     */
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray())
    }

    /**
     * Verifies [password] against [hash].
     *
     * Supports two hash formats for seamless migration :
     *  1. BCrypt hashes   — starts with `$2a$`, `$2b$`, or `$2y$`
     *  2. Legacy SHA-256  — 64-character hex string (old PasswordHasher format)
     *
     * After a successful legacy verification, the caller should re-hash the
     * password and save the new BCrypt hash to complete the migration.
     */
    fun verifyPassword(password: String, hash: String): Boolean {
        return when {
            isBcryptHash(hash) -> {
                val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
                result.verified
            }
            isLegacySha256Hash(hash) -> {
                // Legacy path — kept only for migrating existing accounts
                legacySha256Hash(password) == hash
            }
            else -> false
        }
    }

    /**
     * Returns true if [hash] is a legacy SHA-256 hash that should be migrated.
     * Use this in LoginUserUseCase to trigger re-hashing after successful login.
     */
    fun isLegacyHash(hash: String): Boolean = isLegacySha256Hash(hash)

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun isBcryptHash(hash: String): Boolean =
        hash.startsWith("\$2a\$") || hash.startsWith("\$2b\$") || hash.startsWith("\$2y\$")

    private fun isLegacySha256Hash(hash: String): Boolean =
        hash.length == 64 && hash.all { it.isDigit() || it in 'a'..'f' }

    /**
     * Legacy SHA-256 implementation — used only for migration verification.
     * Do NOT use for new passwords.
     */
    private fun legacySha256Hash(password: String): String {
        val salt = "JOVIM_SECURE_SALT_2026"
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest((password + salt).toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
