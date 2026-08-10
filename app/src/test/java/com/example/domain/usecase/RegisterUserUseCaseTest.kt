package com.example.domain.usecase

import com.example.data.User
import com.example.data.repository.JovimRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for RegisterUserUseCase.
 *
 * Tests all validation branches and success path.
 * Repository is mocked — no DB or Android runtime needed.
 */
class RegisterUserUseCaseTest {

    private lateinit var repository: JovimRepository
    private lateinit var useCase: RegisterUserUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = RegisterUserUseCase(repository)
    }

    // -------------------------------------------------------------------------
    // Blank field validation
    // -------------------------------------------------------------------------

    @Test
    fun `returns error when fullName is blank`() = runTest {
        val result = useCase("", "test@example.com", "pass123", "pass123", false, "")
        assertTrue(result is RegisterUserUseCase.Result.Error)
        assertEquals("Veuillez remplir tous les champs.", (result as RegisterUserUseCase.Result.Error).message)
    }

    @Test
    fun `returns error when email is blank`() = runTest {
        val result = useCase("John", "", "pass123", "pass123", false, "")
        assertTrue(result is RegisterUserUseCase.Result.Error)
    }

    @Test
    fun `returns error when password is blank`() = runTest {
        val result = useCase("John", "test@example.com", "", "", false, "")
        assertTrue(result is RegisterUserUseCase.Result.Error)
    }

    // -------------------------------------------------------------------------
    // Email validation
    // -------------------------------------------------------------------------

    @Test
    fun `returns error for email without @ symbol`() = runTest {
        val result = useCase("John", "invalidemail.com", "pass123", "pass123", false, "")
        assertError(result, "invalide")
    }

    @Test
    fun `returns error for email with double @`() = runTest {
        val result = useCase("John", "a@@b.com", "pass123", "pass123", false, "")
        assertError(result, "invalide")
    }

    @Test
    fun `returns error for email with double dots`() = runTest {
        val result = useCase("John", "a..b@example.com", "pass123", "pass123", false, "")
        assertError(result, "invalide")
    }

    @Test
    fun `returns error for disposable email`() = runTest {
        val result = useCase("John", "test@yopmail.com", "pass123", "pass123", false, "")
        assertTrue(result is RegisterUserUseCase.Result.Error)
        assertTrue((result as RegisterUserUseCase.Result.Error).message.contains("jetable"))
    }

    // -------------------------------------------------------------------------
    // Password confirmation
    // -------------------------------------------------------------------------

    @Test
    fun `returns error when passwords do not match`() = runTest {
        val result = useCase("John", "test@example.com", "pass123", "pass456", false, "")
        assertTrue(result is RegisterUserUseCase.Result.Error)
        assertTrue((result as RegisterUserUseCase.Result.Error).message.contains("correspondent pas"))
    }

    // -------------------------------------------------------------------------
    // Duplicate account
    // -------------------------------------------------------------------------

    @Test
    fun `returns error when email already registered`() = runTest {
        whenever(repository.getUserByEmail("existing@example.com"))
            .thenReturn(User(fullName = "Existing", email = "existing@example.com", passwordHash = "hash"))

        val result = useCase("John", "existing@example.com", "pass123", "pass123", false, "")
        assertTrue(result is RegisterUserUseCase.Result.Error)
        assertTrue((result as RegisterUserUseCase.Result.Error).message.contains("déjà utilisée"))
    }

    // -------------------------------------------------------------------------
    // Google verification gate
    // -------------------------------------------------------------------------

    @Test
    fun `returns NeedsGoogleVerification for unverified Gmail`() = runTest {
        whenever(repository.getUserByEmail(any())).thenReturn(null)

        val result = useCase("John", "test@gmail.com", "pass123", "pass123",
            googleVerified = false, verifyingGoogleEmail = "")

        assertTrue(result is RegisterUserUseCase.Result.NeedsGoogleVerification)
        assertEquals("test@gmail.com", (result as RegisterUserUseCase.Result.NeedsGoogleVerification).email)
    }

    @Test
    fun `proceeds past Google check when already verified for this email`() = runTest {
        whenever(repository.getUserByEmail("test@gmail.com")).thenReturn(null)
        whenever(repository.insertUser(any())).thenReturn(1L)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        val result = useCase("John", "test@gmail.com", "pass123", "pass123",
            googleVerified = true, verifyingGoogleEmail = "test@gmail.com")

        assertTrue(result is RegisterUserUseCase.Result.Success)
    }

    // -------------------------------------------------------------------------
    // Success path
    // -------------------------------------------------------------------------

    @Test
    fun `returns Success with 6-digit verification code for valid non-gmail`() = runTest {
        whenever(repository.getUserByEmail(any())).thenReturn(null)
        whenever(repository.insertUser(any())).thenReturn(1L)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        val result = useCase("Jane Doe", "jane@example.com", "secure123", "secure123", false, "")

        assertTrue(result is RegisterUserUseCase.Result.Success)
        val success = result as RegisterUserUseCase.Result.Success
        assertEquals("jane@example.com", success.user.email)
        assertEquals("Jane Doe", success.user.fullName)
        assertEquals(6, success.verificationCode.length)
        assertTrue(success.verificationCode.all { it.isDigit() })
    }

    @Test
    fun `success path inserts user and auth log into repository`() = runTest {
        whenever(repository.getUserByEmail(any())).thenReturn(null)
        whenever(repository.insertUser(any())).thenReturn(1L)
        whenever(repository.insertAuthLog(any())).thenReturn(Unit)

        useCase("Jane", "jane@example.com", "pass123", "pass123", false, "")

        verify(repository).insertUser(any())
        verify(repository).insertAuthLog(any())
    }

    @Test
    fun `failure path never inserts user into repository`() = runTest {
        val result = useCase("", "bad-email", "pass", "pass", false, "")

        assertTrue(result is RegisterUserUseCase.Result.Error)
        verify(repository, never()).insertUser(any())
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun assertError(result: RegisterUserUseCase.Result, containsText: String) {
        assertTrue(result is RegisterUserUseCase.Result.Error)
        assertTrue((result as RegisterUserUseCase.Result.Error).message.contains(containsText, ignoreCase = true))
    }
}
