package com.example

import com.example.data.repository.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Smoke tests — fast sanity checks that run on the JVM with no dependencies.
 * More thorough tests live in the domain/usecase/ package.
 */
class ExampleUnitTest {

    @Test
    fun `password hash is 64 characters`() {
        assertEquals(64, PasswordHasher.hashPassword("test").length)
    }

    @Test
    fun `correct password verifies successfully`() {
        val hash = PasswordHasher.hashPassword("hello")
        assertTrue(PasswordHasher.verifyPassword("hello", hash))
    }
}
