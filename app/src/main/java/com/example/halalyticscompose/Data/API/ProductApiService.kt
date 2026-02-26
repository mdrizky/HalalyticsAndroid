package com.example.halalyticscompose.data.api

import retrofit2.Response
import retrofit2.http.*
import com.example.halalyticscompose.Data.Model.ApiResponse

data class Product(
    val id: Int,
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val status: String,
    val halal_info: HalalInfo? = null,
    val ingredients: String? = null,
    val nutrition_facts: NutritionFacts? = null,
    val image_url: String? = null,
    val source: String = "local",
    val created_at: String,
    val updated_at: String
)

data class HalalInfo(
    val halal_status: String,
    val certification_body: String? = null,
    val certificate_number: String? = null,
    val valid_until: String? = null,
    val last_checked: String? = null
)

data class NutritionFacts(
    val energy: NutritionItem? = null,
    val fat: NutritionItem? = null,
    val saturated_fat: NutritionItem? = null,
    val carbohydrates: NutritionItem? = null,
    val sugars: NutritionItem? = null,
    val fiber: NutritionItem? = null,
    val proteins: NutritionItem? = null,
    val salt: NutritionItem? = null
)

data class NutritionItem(
    val name: String,
    val per_100g: Double,
    val unit: String
)

data class ProductSearchResponse(
    val products: List<Product>,
    val total: Int,
    val page: Int,
    val total_pages: Int
)

data class HalalAlternativeResponse(
    val problematic_ingredients_reason: String,
    val halal_alternatives: List<HalalAlternativeItem>,
    val explanation: String
)

data class HalalAlternativeItem(
    val name: String,
    val manufacturer: String,
    val brand: String? = null,
    val reason_it_is_better: String
)

interface ProductApiService {
    
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<ProductSearchResponse>>
    
    @GET("products/barcode/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<Product>>
    
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<Product>>
    
    @GET("products/popular")
    suspend fun getPopularProducts(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<Product>>>
    
    @GET("products/recent")
    suspend fun getRecentProducts(
        @Query("limit") limit: Int = 10,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<List<Product>>>
    
    @POST("products/scan-history")
    suspend fun addToScanHistory(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<String>>
    
    @GET("products/scan-history")
    suspend fun getScanHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Product>>>
    
    @POST("products/{id}/favorite")
    suspend fun addToFavorites(
        @Path("id") productId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<String>>
    
    @DELETE("products/{id}/favorite")
    suspend fun removeFromFavorites(
        @Path("id") productId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<String>>
    
    @GET("products/favorites")
    suspend fun getFavoriteProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Product>>>

    @GET("products/alternatives/{barcode}")
    suspend fun getProductAlternatives(
        @Path("barcode") barcode: String,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<HalalAlternativeResponse>>
}
