package com.example.domain.usecase

import com.example.data.AuthenticationLog
import com.example.data.User
import com.example.data.repository.JovimRepository
import com.example.data.repository.PasswordHasher
import javax.inject.Inject

/**
 * Handles user login with email + password credentials.
 *
 * Business rules:
 *  - Blank field validation
 *  - User existence check
 *  - Password verification (BCrypt, with transparent SHA-256 migration)
 *  - Email verification gate
 *  - Auth log insertion on success and failure
 *
 * BCrypt migration strategy:
 *  Users whose passwords were hashed with the old SHA-256 scheme are
 *  automatically upgraded to BCrypt on their next successful login —
 *  completely transparent, no extra step required from the user.
 */
class LoginUserUseCase @Inject constructor(
    private val repository: JovimRepository
) {
    sealed class Result {
        data class Success(val user: User) : Result()
        data class Error(val message: String) : Result()
        /** Account exists but email has not been confirmed yet */
        data class EmailNotVerified(val email: String, val verificationCode: String) : Result()
    }

    suspend operator fun invoke(emailInput: String, pass: String): Result {
        val email = emailInput.trim()

        if (email.isBlank() || pass.isBlank())
            return Result.Error("Veuillez remplir tous les champs.")

        val user = repository.getUserByEmail(email)
        if (user == null) {
            repository.insertAuthLog(
                AuthenticationLog(
                    email = email,
                    actionType = "LOGIN_FAILED",
                    details = "Échec connexion: Compte inexistant pour l'e-mail $email"
                )
            )
            return Result.Error("Ce compte n'existe pas encore. Veuillez créer un compte.")
        }

        if (!PasswordHasher.verifyPassword(pass, user.passwordHash)) {
            repository.insertAuthLog(
                AuthenticationLog(
                    email = email,
                    actionType = "LOGIN_FAILED",
                    details = "Échec connexion: Mot de passe incorrect pour $email"
                )
            )
            return Result.Error("Identifiants invalides (mot de passe incorrect).")
        }

        // -----------------------------------------------------------------------
        // Transparent BCrypt migration
        // If the stored hash is a legacy SHA-256 hash, silently upgrade it to
        // BCrypt now that we have the plaintext password available.
        // -----------------------------------------------------------------------
        val migratedUser = if (PasswordHasher.isLegacyHash(user.passwordHash)) {
            val newHash = PasswordHasher.hashPassword(pass)
            val updated = user.copy(passwordHash = newHash)
            repository.updateUser(updated)
            repository.insertAuthLog(
                AuthenticationLog(
                    email = email,
                    actionType = "PASSWORD_MIGRATED",
                    details = "Hash SHA-256 migré vers BCrypt pour $email"
                )
            )
            updated
        } else {
            user
        }

        if (!migratedUser.isEmailVerified) {
            return Result.EmailNotVerified(email, migratedUser.verificationCode)
        }

        repository.insertAuthLog(
            AuthenticationLog(
                email = email,
                actionType = "LOGIN_SUCCESS",
                details = "Connexion réussie de l'utilisateur ${migratedUser.fullName}"
            )
        )

        return Result.Success(migratedUser)
    }
}
