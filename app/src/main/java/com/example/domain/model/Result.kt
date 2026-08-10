package com.example.domain.model

/**
 * Unified result wrapper for all use case operations.
 *
 * Usage in a use case:
 *   return Result.Success(data)
 *   return Result.Error(Exception("message"))
 *
 * Usage in ViewModel:
 *   when (val result = useCase(params)) {
 *       is Result.Success -> { /* use result.data */ }
 *       is Result.Error   -> { /* use result.message */ }
 *       is Result.Loading -> { /* show spinner */ }
 *   }
 *
 * Or with the extension helpers:
 *   result.onSuccess { data -> ... }
 *          .onError  { msg  -> ... }
 */
sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(
        val exception: Exception,
        /** Human-readable message safe to display directly in the UI. */
        val message: String = exception.message ?: "Une erreur inattendue est survenue."
    ) : Result<Nothing>()

    /** Emitted while the operation is in progress. */
    data object Loading : Result<Nothing>()

    // -------------------------------------------------------------------------
    // Convenience properties
    // -------------------------------------------------------------------------

    val isSuccess get() = this is Success
    val isError   get() = this is Error
    val isLoading get() = this is Loading

    /** Returns the wrapped value or null if this is not a Success. */
    fun getOrNull(): T? = (this as? Success)?.data

    /** Returns the wrapped value or throws the contained exception. */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error   -> throw exception
        is Loading -> error("Result is still Loading")
    }
}

// -------------------------------------------------------------------------
// Extension functions for fluent chaining
// -------------------------------------------------------------------------

/** Runs [block] only when the result is a Success. Returns the same Result for chaining. */
inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(data)
    return this
}

/** Runs [block] only when the result is an Error. Returns the same Result for chaining. */
inline fun <T> Result<T>.onError(block: (message: String, exception: Exception) -> Unit): Result<T> {
    if (this is Result.Error) block(message, exception)
    return this
}

/** Runs [block] only when the result is Loading. Returns the same Result for chaining. */
inline fun <T> Result<T>.onLoading(block: () -> Unit): Result<T> {
    if (this is Result.Loading) block()
    return this
}

/**
 * Wraps a suspending [block] in a try/catch and returns Result.Success or Result.Error.
 * Use this in use cases to eliminate repetitive try/catch boilerplate:
 *
 *   return safeCall { geminiService.generateContent(...) }
 */
suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}
