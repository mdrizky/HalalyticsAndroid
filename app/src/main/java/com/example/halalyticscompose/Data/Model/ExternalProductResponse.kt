package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Base Response dari Laravel
 */
data class BaseResponse<T>(
    @SerializedName("response_code")
    val responseCode: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("content")
    val content: T?
)

/**
 * Response untuk search products
 */
data class ExternalSearchResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("page")
    val page: Int?,
    
    @SerializedName("page_size")
    val pageSize: Int?,
    
    @SerializedName("filter")
    val filter: String?,
    
    @SerializedName("products")
    val products: List<ProductItem>
)

/**
 * Product Item dari OpenFoodFacts
 */
data class ProductItem(
    @SerializedName("_id")
    val id: String?,
    
    @SerializedName("code")
    val code: String?,
    
    @SerializedName("product_name")
    val productName: String?,
    
    @SerializedName("product_name_en")
    val productNameEn: String?,
    
    @SerializedName("brands")
    val brands: String?,
    
    @SerializedName("brands_tags")
    val brandsTags: List<String>?,
    
    @SerializedName("quantity")
    val quantity: String?,
    
    @SerializedName("categories")
    val categories: String?,
    
    @SerializedName("categories_tags")
    val categoriesTags: List<String>?,
    
    @SerializedName("countries")
    val countries: String?,
    
    @SerializedName("countries_tags")
    val countriesTags: List<String>?,
    
    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("image_front_url")
    val imageFrontUrl: String?,
    
    @SerializedName("image_front_small_url")
    val imageFrontSmallUrl: String?,
    
    @SerializedName("image_front_thumb_url")
    val imageFrontThumbUrl: String?,
    
    @SerializedName("nutriscore_grade")
    val nutriscoreGrade: String?,
    
    @SerializedName("nutriscore_score")
    val nutriscoreScore: Int?,
    
    @SerializedName("nova_group")
    val novaGroup: Int?,
    
    @SerializedName("ingredients_text")
    val ingredientsText: String?,
    
    @SerializedName("ingredients_text_en")
    val ingredientsTextEn: String?,
    
    @SerializedName("allergens")
    val allergens: String?,
    
    @SerializedName("allergens_tags")
    val allergensTags: List<String>?,
    
    @SerializedName("labels")
    val labels: String?,
    
    @SerializedName("labels_tags")
    val labelsTags: List<String>?,
    
    @SerializedName("manufacturing_places")
    val manufacturingPlaces: String?,
    
    @SerializedName("origin")
    val origin: String?,
    
    @SerializedName("packaging")
    val packaging: String?,
    
    @SerializedName("stores")
    val stores: String?,
    
    @SerializedName("halal_analysis")
    val halalAnalysis: HalalAnalysis?
) {
    /**
     * Get best available image
     */
    fun getBestImageUrl(): String? {
        return imageFrontSmallUrl 
            ?: imageFrontThumbUrl 
            ?: imageFrontUrl 
            ?: imageUrl
    }
    
    /**
     * Get display name (prioritize non-null)
     */
    fun getDisplayName(): String {
        return productName?.takeIf { it.isNotBlank() }
            ?: productNameEn?.takeIf { it.isNotBlank() }
            ?: "Unknown Product"
    }
    
    /**
     * Check if product has halal label
     */
    fun isHalal(): Boolean {
        val halalLabels = listOf("halal", "en:halal")
        return labelsTags?.any { tag -> 
            halalLabels.any { halal -> tag.contains(halal, ignoreCase = true) }
        } ?: false
    }
    
    /**
     * Check if vegetarian
     */
    fun isVegetarian(): Boolean {
        val vegLabels = listOf("vegetarian", "en:vegetarian")
        return labelsTags?.any { tag -> 
            vegLabels.any { veg -> tag.contains(veg, ignoreCase = true) }
        } ?: false
    }
    
    /**
     * Check if vegan
     */
    fun isVegan(): Boolean {
        val veganLabels = listOf("vegan", "en:vegan")
        return labelsTags?.any { tag -> 
            veganLabels.any { vegan -> tag.contains(vegan, ignoreCase = true) }
        } ?: false
    }
    
    /**
     * Get nutriscore color
     */
    fun getNutriscoreColor(): android.graphics.Color? {
        return when (nutriscoreGrade?.uppercase()) {
            "A" -> android.graphics.Color.valueOf(0f, 0.627f, 0f, 1f) // Green
            "B" -> android.graphics.Color.valueOf(0.522f, 0.765f, 0.243f, 1f) // Light Green
            "C" -> android.graphics.Color.valueOf(1f, 0.843f, 0f, 1f) // Yellow
            "D" -> android.graphics.Color.valueOf(1f, 0.549f, 0f, 1f) // Orange
            "E" -> android.graphics.Color.valueOf(1f, 0f, 0f, 1f) // Red
            else -> null
        }
    }
}

/**
 * Halal Analysis dari backend
 */
data class HalalAnalysis(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("is_potentially_halal")
    val isPotentiallyHalal: Boolean,
    
    @SerializedName("suspicious_ingredients")
    val suspiciousIngredients: List<String>,
    
    @SerializedName("recommendation")
    val recommendation: String
)
