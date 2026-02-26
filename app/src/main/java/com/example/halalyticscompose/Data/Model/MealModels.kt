package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class MealAnalysisRequest(
    @SerializedName("image") val image: String // Base64 string
)

data class MealAnalysisResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MealData?,
    @SerializedName("message") val message: String?
)

data class MealData(
    @SerializedName("food_name") val mealName: String = "Unknown Meal",
    @SerializedName("description") val description: String = "",
    @SerializedName("ingredients_detected") val ingredientsDetected: List<String> = emptyList(),
    @SerializedName("halal_analysis") val halalAnalysis: MealHalalAnalysis = MealHalalAnalysis(),
    @SerializedName("nutrition") val nutrition: MealNutrition = MealNutrition(),
    @SerializedName("health_score") val healthScore: Int = 0,
    @SerializedName("health_grade") val healthGrade: String = "Unknown",
    @SerializedName("portion_advice") val portionAdvice: String = ""
)

data class MealHalalAnalysis(
    @SerializedName("status") val status: String = "unknown", // halal, syubhat, haram
    @SerializedName("reason") val reason: String = "",
    @SerializedName("risk_factors") val riskFactors: List<String> = emptyList()
)

data class MealNutrition(
    @SerializedName("calories") val calories: Int = 0,
    @SerializedName("protein") val protein: Double = 0.0,
    @SerializedName("fat") val fat: Double = 0.0,
    @SerializedName("carbs") val carbs: Double = 0.0,
    @SerializedName("sugar") val sugar: Double = 0.0
)

data class AiAnalysisState(
    val isLoading: Boolean = false,
    val data: MealData? = null,
    val error: String? = null
)
