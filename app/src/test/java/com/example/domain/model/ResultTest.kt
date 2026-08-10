package com.example.domain.model

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the Result sealed class and its extension functions.
 */
class ResultTest {

    // -------------------------------------------------------------------------
    // Convenience properties
    // -------------------------------------------------------------------------

    @Test
    fun `Success isSuccess returns true`() {
        assertTrue(Result.Success("data").isSuccess)
    }

    @Test
    fun `Error isError returns true`() {
        assertTrue(Result.Error(Exception("fail")).isError)
    }

    @Test
    fun `Loading isLoading returns true`() {
        assertTrue(Result.Loading.isLoading)
    }

    @Test
    fun `Success isError returns false`() {
        assertFalse(Result.Success("data").isError)
    }

    // -------------------------------------------------------------------------
    // getOrNull
    // -------------------------------------------------------------------------

    @Test
    fun `getOrNull returns data for Success`() {
        assertEquals("hello", Result.Success("hello").getOrNull())
    }

    @Test
    fun `getOrNull returns null for Error`() {
        assertNull(Result.Error(Exception()).getOrNull())
    }

    @Test
    fun `getOrNull returns null for Loading`() {
        assertNull(Result.Loading.getOrNull())
    }

    // -------------------------------------------------------------------------
    // getOrThrow
    // -------------------------------------------------------------------------

    @Test
    fun `getOrThrow returns data for Success`() {
        assertEquals(42, Result.Success(42).getOrThrow())
    }

    @Test(expected = RuntimeException::class)
    fun `getOrThrow throws for Error`() {
        Result.Error(RuntimeException("boom")).getOrThrow()
    }

    // -------------------------------------------------------------------------
    // onSuccess extension
    // -------------------------------------------------------------------------

    @Test
    fun `onSuccess block is called for Success`() {
        var called = false
        Result.Success("value").onSuccess { called = true }
        assertTrue(called)
    }

    @Test
    fun `onSuccess block is NOT called for Error`() {
        var called = false
        Result.Error(Exception()).onSuccess { called = true }
        assertFalse(called)
    }

    @Test
    fun `onSuccess returns same Result for chaining`() {
        val original = Result.Success("chain")
        val returned = original.onSuccess { }
        assertEquals(original, returned)
    }

    // -------------------------------------------------------------------------
    // onError extension
    // -------------------------------------------------------------------------

    @Test
    fun `onError block is called for Error`() {
        var called = false
        Result.Error(Exception("oops")).onError { _, _ -> called = true }
        assertTrue(called)
    }

    @Test
    fun `onError block receives correct message`() {
        var receivedMessage = ""
        Result.Error(Exception("network failure")).onError { msg, _ -> receivedMessage = msg }
        assertEquals("network failure", receivedMessage)
    }

    @Test
    fun `onError block is NOT called for Success`() {
        var called = false
        Result.Success("ok").onError { _, _ -> called = true }
        assertFalse(called)
    }

    // -------------------------------------------------------------------------
    // safeCall
    // -------------------------------------------------------------------------

    @Test
    fun `safeCall returns Success when block succeeds`() = runTest {
        val result = safeCall { "computed value" }
        assertTrue(result is Result.Success)
        assertEquals("computed value", (result as Result.Success).data)
    }

    @Test
    fun `safeCall returns Error when block throws`() = runTest {
        val result = safeCall<String> { throw IllegalStateException("something broke") }
        assertTrue(result is Result.Error)
        assertEquals("something broke", (result as Result.Error).message)
    }

    @Test
    fun `safeCall Error message falls back to default when exception message is null`() = runTest {
        val result = safeCall<String> { throw Exception() }
        assertTrue(result is Result.Error)
        assertEquals("Une erreur inattendue est survenue.", (result as Result.Error).message)
    }

    // -------------------------------------------------------------------------
    // Chaining
    // -------------------------------------------------------------------------

    @Test
    fun `onSuccess and onError can be chained`() {
        var successCalled = false
        var errorCalled = false

        Result.Success("value")
            .onSuccess { successCalled = true }
            .onError   { _, _ -> errorCalled = true }

        assertTrue(successCalled)
        assertFalse(errorCalled)
    }
}
