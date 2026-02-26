package com.example.halalyticscompose.healthcare.model

import java.util.Date

data class HealthProfile(
    val id: String,
    val userId: String,
    val conditions: List<String>,
    val allergies: List<String>,
    val dietaryGoals: List<String>,
    val additionalNotes: String
)

data class ScannedIngredient(
    val name: String,
    val amount: String?,
    val isWarning: Boolean,
    val warningReason: String?
)

data class HealthAnalysis(
    val productId: String,
    val productName: String,
    val overallSafety: SafetyLevel,
    val warnings: List<HealthWarning>,
    val recommendations: List<AlternativeProduct>,
    val disclaimer: String = "This analysis is AI-generated and should not replace professional medical advice."
)

enum class SafetyLevel {
    SAFE, CAUTION, DANGER
}

data class HealthWarning(
    val title: String,
    val message: String,
    val severity: WarningSeverity
)

enum class WarningSeverity {
    LOW, MEDIUM, HIGH
}

data class AlternativeProduct(
    val id: String,
    val name: String,
    val brand: String,
    val reason: String
)

data class HealthScanResult(
    val id: String,
    val timestamp: Date,
    val productName: String,
    val ingredients: List<ScannedIngredient>,
    val analysis: HealthAnalysis
)
