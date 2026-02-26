package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class OCRProductData(
    @SerializedName("id") val id: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("brand") val brand: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("status") val status: String, // pending, verified, etc
    @SerializedName("halal_status") val halalStatus: String, // halal, haram, syubhat
    @SerializedName("confidence_level") val confidenceLevel: Int,
    @SerializedName("front_image_path") val frontImagePath: String?,
    @SerializedName("back_image_path") val backImagePath: String?,
    @SerializedName("front_image_url") val frontImageUrl: String?, // For new implementation
    @SerializedName("back_image_url") val backImageUrl: String?,   // For new implementation
    @SerializedName("ingredients") val ingredients: List<OCRIngredient>? = null,
    @SerializedName("ai_analysis") val aiAnalysis: OCRAiAnalysis? = null,
    @SerializedName("created_at") val createdAt: String?
)

data class OCRAiAnalysis(
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("ingredients") val ingredients: List<OCRAiIngredient>?,
    @SerializedName("nutrition_estimate") val nutritionEstimate: OCRAiNutrition?,
    @SerializedName("health_warnings") val healthWarnings: List<String>?,
    @SerializedName("personal_warnings") val personalWarnings: List<String>?,
    @SerializedName("status_halal") val statusHalal: String?,
    @SerializedName("status_kesehatan") val statusKesehatan: String?,
    @SerializedName("skor_kesehatan") val skorKesehatan: Int?,
    @SerializedName("ringkasan") val ringkasan: String?
)

data class OCRAiIngredient(
    @SerializedName("name") val name: String,
    @SerializedName("halal_status") val halalStatus: String,
    @SerializedName("safety_level") val safetyLevel: String,
    @SerializedName("description") val description: String?,
    @SerializedName("health_impact") val healthImpact: String?,
    @SerializedName("is_personal_allergen") val isPersonalAllergen: Boolean? = false
)

data class OCRAiNutrition(
    @SerializedName("sugar_g") val sugarG: String?,
    @SerializedName("sodium_mg") val sodiumMg: String?,
    @SerializedName("calories") val calories: String?,
    @SerializedName("fat_g") val fatG: String?
)

data class OCRIngredient(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("pivot") val pivot: OCRIngredientPivot?
)

data class OCRIngredientPivot(
    @SerializedName("status") val status: String,
    @SerializedName("risk_level") val riskLevel: String
)

data class OCRStatisticsData(
    @SerializedName("total_scans") val totalScans: Int,
    @SerializedName("pending_review") val pendingReview: Int,
    @SerializedName("approved_today") val approvedToday: Int,
    @SerializedName("rejected_today") val rejectedToday: Int,
    @SerializedName("processing_accuracy") val processingAccuracy: Double
)
