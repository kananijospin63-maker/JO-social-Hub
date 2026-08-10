package com.example.domain.usecase

import com.example.data.User
import com.example.data.repository.JovimRepository
import com.example.data.repository.PasswordHasher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for LoginUserUseCase.
 * All paths of the login flow are tested with a mocked repository.
 */
class LoginUserUseCaseTest {

    private lateinit var repository: JovimRepository
    private lateinit var useCase: LoginUserUseCase

    // A pre-hashed user ready to be returned by the mock
    private val verifiedUser = User(
        id = 1,
        fullName = "Jospin Kanani",
        email = "jospin@example.com",
        passwordHash = PasswordHasher.hashPassword("correct_pass"),
        isEmailVerified = true
    )

    private val unverifiedUser = verifiedUser.copy(
        isEmailVerified = false,
        verificationCode = "123456"
    )

    @Before
    fun setUp() {
        repository = mock()
        useCase = LoginUserUseCase(repository)
    }

    // -------------------------------------------------------------------------
    // Blank input
    // -------------------------------------------------------------------------

    @Test
    fun `returns error when email is blank`() = runTest {
        val result = useCase("", "pass")
        assertError(result, "champs")
    }

    @Test
    fun `returns error when password is blank`() = runTest {
        val result = useCase("jospin@example.com", "")
        assertError(result, "champs")
    }

    // -------------------------------------------------------------------------
    // Unknown account
    // -------------------------------------------------------------------------

    @Test
    fun `returns error when account does not exist`() = runTest {
        whenever(repository.getUserByEmail("unknown@example.com")).thenReturn(null)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        val result = useCase("unknown@example.com", "any_pass")

        assertError(result, "n'existe pas")
    }

    @Test
    fun `inserts LOGIN_FAILED log when account does not exist`() = runTest {
        whenever(repository.getUserByEmail(any())).thenReturn(null)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        useCase("ghost@example.com", "pass")

        verify(repository).insertAuthLog(
            com.example.data.AuthenticationLog(
                email = "ghost@example.com",
                actionType = "LOGIN_FAILED",
                details = "Échec connexion: Compte inexistant pour l'e-mail ghost@example.com"
            )
        )
    }

    // -------------------------------------------------------------------------
    // Wrong password
    // -------------------------------------------------------------------------

    @Test
    fun `returns error for wrong password`() = runTest {
        whenever(repository.getUserByEmail("jospin@example.com")).thenReturn(verifiedUser)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        val result = useCase("jospin@example.com", "wrong_pass")

        assertError(result, "incorrect")
    }

    @Test
    fun `inserts LOGIN_FAILED log for wrong password`() = runTest {
        whenever(repository.getUserByEmail(any())).thenReturn(verifiedUser)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        useCase("jospin@example.com", "wrong_pass")

        verify(repository).insertAuthLog(
            com.example.data.AuthenticationLog(
                email = "jospin@example.com",
                actionType = "LOGIN_FAILED",
                details = "Échec connexion: Mot de passe incorrect pour jospin@example.com"
            )
        )
    }

    // -------------------------------------------------------------------------
    // Email not verified
    // -------------------------------------------------------------------------

    @Test
    fun `returns EmailNotVerified when account is not verified`() = runTest {
        whenever(repository.getUserByEmail("jospin@example.com")).thenReturn(unverifiedUser)

        val result = useCase("jospin@example.com", "correct_pass")

        assertTrue(result is LoginUserUseCase.Result.EmailNotVerified)
        val r = result as LoginUserUseCase.Result.EmailNotVerified
        assertEquals("jospin@example.com", r.email)
        assertEquals("123456", r.verificationCode)
    }

    // -------------------------------------------------------------------------
    // Success path
    // -------------------------------------------------------------------------

    @Test
    fun `returns Success with user for valid credentials`() = runTest {
        whenever(repository.getUserByEmail("jospin@example.com")).thenReturn(verifiedUser)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        val result = useCase("jospin@example.com", "correct_pass")

        assertTrue(result is LoginUserUseCase.Result.Success)
        assertEquals("Jospin Kanani", (result as LoginUserUseCase.Result.Success).user.fullName)
    }

    @Test
    fun `inserts LOGIN_SUCCESS log on successful login`() = runTest {
        whenever(repository.getUserByEmail(any())).thenReturn(verifiedUser)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        useCase("jospin@example.com", "correct_pass")

        verify(repository).insertAuthLog(
            com.example.data.AuthenticationLog(
                email = "jospin@example.com",
                actionType = "LOGIN_SUCCESS",
                details = "Connexion réussie de l'utilisateur Jospin Kanani"
            )
        )
    }

    @Test
    fun `trims whitespace from email before lookup`() = runTest {
        whenever(repository.getUserByEmail("jospin@example.com")).thenReturn(verifiedUser)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        val result = useCase("  jospin@example.com  ", "correct_pass")

        assertTrue(result is LoginUserUseCase.Result.Success)
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun assertError(result: LoginUserUseCase.Result, containsText: String) {
        assertTrue(result is LoginUserUseCase.Result.Error)
        assertTrue((result as LoginUserUseCase.Result.Error).message.contains(containsText, ignoreCase = true))
    }
}
