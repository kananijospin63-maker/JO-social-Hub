package com.example.domain.usecase

import com.example.data.AuthenticationLog
import com.example.data.User
import com.example.data.repository.JovimRepository
import com.example.data.repository.PasswordHasher
import javax.inject.Inject

/**
 * Handles new user registration.
 *
 * Business rules extracted from JovimViewModel:
 *  - Blank field validation
 *  - Email format validation
 *  - Disposable email rejection
 *  - Password confirmation check
 *  - Duplicate account detection
 *  - Password hashing
 *  - Verification code generation
 *  - Auth log insertion
 *
 * Returns a sealed Result so the ViewModel stays simple and testable.
 */
class RegisterUserUseCase @Inject constructor(
    private val repository: JovimRepository
) {
    sealed class Result {
        data class Success(val user: User, val verificationCode: String) : Result()
        data class Error(val message: String) : Result()
        /** Google existence check required before proceeding */
        data class NeedsGoogleVerification(val email: String) : Result()
    }

    suspend operator fun invoke(
        fullName: String,
        emailInput: String,
        pass: String,
        passConfirm: String,
        googleVerified: Boolean,
        verifyingGoogleEmail: String
    ): Result {
        val email = emailInput.trim()

        // --- Validation ---
        if (fullName.isBlank() || email.isBlank() || pass.isBlank())
            return Result.Error("Veuillez remplir tous les champs.")

        if (!isValidEmail(email))
            return Result.Error("Adresse e-mail invalide (format incorrect). Ex: nom@domaine.com")

        if (isDisposableEmail(email))
            return Result.Error("Les adresses e-mails temporaires ou jetables ne sont pas acceptées.")

        if (pass != passConfirm)
            return Result.Error("Les mots de passe ne correspondent pas.")

        // --- Duplicate check ---
        if (repository.getUserByEmail(email) != null)
            return Result.Error("Cette adresse email est déjà utilisée. Veuillez vous connecter ou utiliser une autre adresse.")

        // --- Google existence check for Gmail accounts ---
        val isGmail = email.endsWith("@gmail.com") || email.endsWith("@google.com")
        if (isGmail && (!googleVerified || verifyingGoogleEmail != email))
            return Result.NeedsGoogleVerification(email)

        // --- Create user ---
        val verificationCode = (100000..999999).random().toString()
        val newUser = User(
            fullName = fullName,
            email = email,
            passwordHash = PasswordHasher.hashPassword(pass),
            role = "Utilisateur",
            isGoogleConnected = false,
            isEmailVerified = false,
            verificationCode = verificationCode
        )
        repository.insertUser(newUser)
        repository.insertAuthLog(
            AuthenticationLog(
                email = email,
                actionType = "REGISTER_PENDING",
                details = "Nouvel utilisateur enregistré (en attente d'activation) : $fullName ($email)"
            )
        )

        return Result.Success(newUser, verificationCode)
    }

    private fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.contains("..") || trimmed.contains("@@") || trimmed.count { it == '@' } != 1) return false
        val parts = trimmed.split("@")
        if (parts.size != 2) return false
        val (name, domain) = parts
        if (name.isBlank() || domain.isBlank()) return false
        if (domain.startsWith(".") || domain.endsWith(".") || !domain.contains(".")) return false
        val extension = domain.split(".").last()
        if (extension.length < 2 || extension.length > 6) return false
        return "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex().matches(trimmed)
    }

    private fun isDisposableEmail(email: String): Boolean {
        val domain = email.substringAfter("@").lowercase()
        val blacklist = setOf(
            "tempmail.com", "yopmail.com", "mailinator.com", "dispostable.com",
            "10minutemail.com", "guerrillamail.com", "tempmail.fr", "trashmail.com"
        )
        return domain in blacklist
    }
}
