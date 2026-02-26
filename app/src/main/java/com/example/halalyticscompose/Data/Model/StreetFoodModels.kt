package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Street Food Models for AI Food Recognition
 */

// ========== RESPONSE MODELS ==========

data class StreetFoodSearchResponse(
    @SerializedName("response_code")
    val responseCode: Int = 0,
    
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("content")
    val content: StreetFoodSearchContent? = null
)

data class StreetFoodSearchContent(
    @SerializedName("data")
    val data: List<StreetFood>? = null,
    
    @SerializedName("total")
    val total: Int = 0
)

data class StreetFood(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("name_en")
    val nameEn: String? = null,
    
    @SerializedName("slug")
    val slug: String? = null,
    
    @SerializedName("category")
    val category: String = "",
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("calories_typical")
    val caloriesTypical: Double = 0.0,
    
    @SerializedName("calories_range")
    val caloriesRange: String? = null,
    
    @SerializedName("protein")
    val protein: Double = 0.0,
    
    @SerializedName("carbs")
    val carbs: Double = 0.0,
    
    @SerializedName("fat")
    val fat: Double = 0.0,
    
    @SerializedName("serving_description")
    val servingDescription: String? = null,
    
    @SerializedName("halal_status")
    val halalStatus: String = "halal_umum",
    
    @SerializedName("halal_status_label")
    val halalStatusLabel: String? = null,
    
    @SerializedName("health_score")
    val healthScore: Int = 50,
    
    @SerializedName("health_category")
    val healthCategory: String? = null,
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    @SerializedName("is_popular")
    val isPopular: Boolean = false,
    
    @SerializedName("variants")
    val variants: List<FoodVariant>? = null
)

data class FoodVariant(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("variant_name")
    val variantName: String = "",
    
    @SerializedName("variant_type")
    val variantType: String = "",
    
    @SerializedName("calories_modifier")
    val caloriesModifier: Double = 0.0,
    
    @SerializedName("protein_modifier")
    val proteinModifier: Double = 0.0,
    
    @SerializedName("is_default")
    val isDefault: Boolean = false
)

// ========== ANALYSIS MODELS ==========

data class FoodAnalysisResponse(
    @SerializedName("response_code")
    val responseCode: Int = 0,
    
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("content")
    val content: FoodAnalysis? = null
)

data class FoodAnalysis(
    @SerializedName("food_name")
    val foodName: String = "",
    
    @SerializedName("base_food")
    val baseFood: String? = null,
    
    @SerializedName("variant")
    val variant: String? = null,
    
    @SerializedName("portion")
    val portion: Double = 1.0,
    
    @SerializedName("serving_size")
    val servingSize: ServingSize? = null,
    
    @SerializedName("nutrition")
    val nutrition: NutritionInfo? = null,
    
    @SerializedName("halal_info")
    val halalInfo: FoodHalalInfo? = null,
    
    @SerializedName("health_info")
    val healthInfo: FoodHealthInfo? = null,
    
    @SerializedName("disclaimer")
    val disclaimer: String? = null
)

data class ServingSize(
    @SerializedName("grams")
    val grams: Int = 0,
    
    @SerializedName("description")
    val description: String = ""
)

data class NutritionInfo(
    @SerializedName("calories")
    val calories: Int = 0,
    
    @SerializedName("protein")
    val protein: Double = 0.0,
    
    @SerializedName("carbs")
    val carbs: Double = 0.0,
    
    @SerializedName("fat")
    val fat: Double = 0.0,
    
    @SerializedName("fiber")
    val fiber: Double = 0.0,
    
    @SerializedName("sugar")
    val sugar: Double = 0.0,
    
    @SerializedName("sodium")
    val sodium: Double = 0.0
)

data class FoodHalalInfo(
    @SerializedName("status")
    val status: String = "halal_umum",
    
    @SerializedName("status_label")
    val statusLabel: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null
)

data class FoodHealthInfo(
    @SerializedName("score")
    val score: Int = 50,
    
    @SerializedName("category")
    val category: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("tags")
    val tags: List<String>? = null,
    
    @SerializedName("recommendations")
    val recommendations: List<String>? = null
)

// ========== REQUEST MODELS ==========

data class FoodSearchRequest(
    val query: String
)

data class FoodAnalysisRequest(
    @SerializedName("food_id")
    val foodId: Int,
    
    @SerializedName("variant_id")
    val variantId: Int? = null,
    
    @SerializedName("portion")
    val portion: Double = 1.0,
    
    @SerializedName("input_method")
    val inputMethod: String = "text",
    
    @SerializedName("ai_confidence")
    val aiConfidence: Double? = null,
    
    @SerializedName("meal_type")
    val mealType: String? = null
)

// ========== RECOGNITION MODELS ==========

data class FoodRecognitionResponse(
    @SerializedName("image_path")
    val imagePath: String? = null,
    
    @SerializedName("matches")
    val matches: List<FoodMatch> = emptyList()
)

data class FoodMatch(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("confidence")
    val confidence: Double = 0.0,
    
    @SerializedName("category")
    val category: String = "",
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    @SerializedName("source")
    val source: String? = null
)
