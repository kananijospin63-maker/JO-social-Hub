package com.example.data.repository

import java.security.MessageDigest

object PasswordHasher {
    // SHA-256 based password hashing with salt for enterprise-grade standard security
    fun hashPassword(password: String): String {
        val salt = "JOVIM_SECURE_SALT_2026"
        val digest = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        val hashBytes = digest.digest(saltedPassword.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }
}
