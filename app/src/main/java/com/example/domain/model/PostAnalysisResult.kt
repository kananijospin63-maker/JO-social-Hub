package com.example.domain.model

/**
 * Domain model for the result of a post quality analysis.
 * Moved from the data layer — domain models belong in the domain layer,
 * not inside the Repository.
 */
data class PostAnalysisResult(
    val score: Int,
    val rawAnalysis: String
)
