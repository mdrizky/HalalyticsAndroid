package com.example.halalyticscompose.data.api

import retrofit2.Response
import retrofit2.http.*
import com.example.halalyticscompose.data.model.ApiResponse

data class ProductSubmission(
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val ingredients: String? = null,
    val front_image: String? = null,
    val back_image: String? = null,
    val user_id: String
)

data class OCRProductResponse(
    val id: Int,
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val status: String,
    val ingredients: String? = null,
    val front_image: String? = null,
    val back_image: String? = null,
    val verification_status: String,
    val verified_by: String? = null,
    val verified_at: String? = null,
    val created_at: String,
    val updated_at: String
)

data class AdminProduct(
    val id: Int,
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val status: String,
    val ingredients: String? = null,
    val front_image: String? = null,
    val back_image: String? = null,
    val verification_status: String,
    val user_id: String,
    val user_name: String,
    val created_at: String
)

data class VerificationRequest(
    val status: String, // "approved" or "rejected"
    val reason: String? = null,
    val verified_by: String
)

data class Statistics(
    val total_products: Int,
    val pending_products: Int,
    val approved_products: Int,
    val rejected_products: Int,
    val today_submissions: Int,
    val this_week_submissions: Int
)

interface OCRProductApiService {
    
    @POST("ocr/submit")
    suspend fun submitProduct(
        @Header("Authorization") token: String,
        @Body product: ProductSubmission
    ): Response<ApiResponse<OCRProductResponse>>
    
    @POST("ocr/check-duplicate")
    suspend fun checkDuplicate(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<Boolean>>
    
    @GET("ocr/product/{id}")
    suspend fun getProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Int
    ): Response<ApiResponse<OCRProductResponse>>
    
    @PUT("ocr/product/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Int,
        @Body product: ProductSubmission
    ): Response<ApiResponse<OCRProductResponse>>
    
    @GET("ocr/admin/products")
    suspend fun getAdminProducts(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<AdminProduct>>>
    
    @PUT("ocr/admin/product/{id}/verify")
    suspend fun verifyProduct(
        @Header("Authorization") token: String,
        @Path("id") productId: Int,
        @Body verification: VerificationRequest
    ): Response<ApiResponse<String>>
    
    @POST("ocr/favorites/add")
    suspend fun addToFavorites(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<String>>
    
    @DELETE("ocr/favorites/remove/{id}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String,
        @Path("id") productId: Int
    ): Response<ApiResponse<String>>
    
    @GET("ocr/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<OCRProductResponse>>>
    
    @GET("ocr/statistics")
    suspend fun getStatistics(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Statistics>>
}
