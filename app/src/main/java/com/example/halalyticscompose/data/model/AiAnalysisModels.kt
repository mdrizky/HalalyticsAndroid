package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for AI Analysis
 */
data class AiAnalysisRequest(
    @SerializedName("ingredients_text")
    val ingredientsText: String,
    
    @SerializedName("user_profile")
    val userProfile: Map<String, Any>? = null,

    @SerializedName("family_id")
    val familyId: Int? = null
)

/**
 * Response model for AI Analysis
 */
data class AiAnalysisResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("content")
    val content: AiAnalysisContent? = null,
    
    @SerializedName("message")
    val message: String? = null
)

data class AiAnalysisContent(
    @SerializedName("status")
    val status: String, // Halal, Haram, Syubhat, Unknown
    
    @SerializedName("confidence")
    val confidence: Int = 0,
    
    @SerializedName("analysis")
    val analysis: String, // Detailed explanation
    
    @SerializedName("red_flags")
    val redFlags: List<String> = emptyList(),
    
    @SerializedName("health_risk")
    val healthRisk: String = "safe" // safe, low, high
)
