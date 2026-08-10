package com.example.domain.usecase

import com.example.data.repository.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for PasswordHasher — BCrypt implementation.
 * Pure JVM tests, no Android framework needed.
 */
class PasswordHasherTest {

    // -------------------------------------------------------------------------
    // BCrypt hash format
    // -------------------------------------------------------------------------

    @Test
    fun `hashPassword returns a bcrypt hash starting with dollar 2a dollar`() {
        val hash = PasswordHasher.hashPassword("secret123")
        assertTrue("Expected BCrypt format", hash.startsWith("\$2a\$"))
    }

    @Test
    fun `hashPassword returns a non-empty string`() {
        val hash = PasswordHasher.hashPassword("mypassword")
        assertTrue(hash.isNotEmpty())
    }

    @Test
    fun `hashPassword produces different hashes for same password due to random salt`() {
        val hash1 = PasswordHasher.hashPassword("samepassword")
        val hash2 = PasswordHasher.hashPassword("samepassword")
        // BCrypt uses a random salt — two calls must never produce the same hash
        assertNotEquals(
            "BCrypt must produce different hashes for same password (random salt)",
            hash1, hash2
        )
    }

    @Test
    fun `hashPassword produces different hashes for different passwords`() {
        val hash1 = PasswordHasher.hashPassword("password_one")
        val hash2 = PasswordHasher.hashPassword("password_two")
        assertNotEquals(hash1, hash2)
    }

    // -------------------------------------------------------------------------
    // BCrypt verification
    // -------------------------------------------------------------------------

    @Test
    fun `verifyPassword returns true for correct BCrypt password`() {
        val password = "correct_password"
        val hash = PasswordHasher.hashPassword(password)
        assertTrue(PasswordHasher.verifyPassword(password, hash))
    }

    @Test
    fun `verifyPassword returns false for wrong password against BCrypt hash`() {
        val hash = PasswordHasher.hashPassword("correct_password")
        assertFalse(PasswordHasher.verifyPassword("wrong_password", hash))
    }

    @Test
    fun `verifyPassword returns false for empty password against BCrypt hash`() {
        val hash = PasswordHasher.hashPassword("secure")
        assertFalse(PasswordHasher.verifyPassword("", hash))
    }

    @Test
    fun `verifyPassword returns false for tampered BCrypt hash`() {
        val hash = PasswordHasher.hashPassword("password")
        // Corrupt the last characters of the hash
        val tampered = hash.dropLast(4) + "XXXX"
        assertFalse(PasswordHasher.verifyPassword("password", tampered))
    }

    // -------------------------------------------------------------------------
    // Legacy SHA-256 migration support
    // -------------------------------------------------------------------------

    @Test
    fun `isLegacyHash returns false for BCrypt hash`() {
        val bcryptHash = PasswordHasher.hashPassword("anypassword")
        assertFalse(PasswordHasher.isLegacyHash(bcryptHash))
    }

    @Test
    fun `isLegacyHash returns true for 64-char hex SHA-256 hash`() {
        // Simulate an old SHA-256 hash (64 lowercase hex chars)
        val legacyHash = "a".repeat(64)
        assertTrue(PasswordHasher.isLegacyHash(legacyHash))
    }

    @Test
    fun `isLegacyHash returns false for random garbage string`() {
        assertFalse(PasswordHasher.isLegacyHash("notahash"))
    }

    @Test
    fun `verifyPassword accepts legacy SHA-256 hash for migration`() {
        // This is the exact legacy hash for "chrisjospin2003@@" + "JOVIM_SECURE_SALT_2026"
        // We compute it here to stay independent of the legacy implementation
        val legacyHash = buildLegacySha256Hash("chrisjospin2003@@")
        assertTrue(
            "Legacy SHA-256 hash must still verify during migration period",
            PasswordHasher.verifyPassword("chrisjospin2003@@", legacyHash)
        )
    }

    @Test
    fun `verifyPassword rejects wrong password against legacy SHA-256 hash`() {
        val legacyHash = buildLegacySha256Hash("correct_password")
        assertFalse(PasswordHasher.verifyPassword("wrong_password", legacyHash))
    }

    // -------------------------------------------------------------------------
    // Cost factor
    // -------------------------------------------------------------------------

    @Test
    fun `hashPassword embeds cost factor 12 in the hash`() {
        val hash = PasswordHasher.hashPassword("any")
        // BCrypt format: $2a$12$...
        assertTrue("Expected cost factor 12 in hash", hash.startsWith("\$2a\$12\$"))
    }

    // -------------------------------------------------------------------------
    // Helper — reproduces the legacy SHA-256 logic for test assertions only
    // -------------------------------------------------------------------------

    private fun buildLegacySha256Hash(password: String): String {
        val salt = "JOVIM_SECURE_SALT_2026"
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest((password + salt).toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
