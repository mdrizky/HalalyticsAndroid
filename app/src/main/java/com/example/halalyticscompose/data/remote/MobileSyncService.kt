package com.example.halalyticscompose.data.remote

import retrofit2.Response
import retrofit2.http.*

/**
 * Mobile Sync Service for integrating with Laravel Admin System
 * This service handles synchronization between mobile app and admin dashboard
 */
interface MobileSyncService {
    
    @POST("api/mobile/sync/scans")
    suspend fun syncScanData(
        @Header("Authorization") token: String,
        @Body scanData: ScanSyncRequest
    ): Response<SyncResponse>
    
    @POST("api/mobile/sync/users")
    suspend fun syncUserData(
        @Header("Authorization") token: String,
        @Body userData: UserSyncRequest
    ): Response<SyncResponse>
    
    @GET("api/mobile/products")
    suspend fun getProducts(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("status") status: String? = null
    ): Response<ProductListResponse>
    
    @GET("api/mobile/categories")
    suspend fun getCategories(
        @Header("Authorization") token: String
    ): Response<CategoryListResponse>
}

// Data Transfer Objects
data class ScanSyncRequest(
    val user_id: Int,
    val scan_data: List<ScanData>
)

data class ScanData(
    val barcode: String,
    val product_name: String,
    val scan_time: String,
    val halal_status: String? = null,
    val komposisi: String? = null,
    val nutrition_info: String? = null
)

data class UserSyncRequest(
    val users: List<UserData>
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val scan_count: Int? = null,
    val last_active: String? = null
)

data class SyncResponse(
    val success: Boolean,
    val synced_count: Int,
    val errors: List<String>,
    val message: String
)

data class ProductListResponse(
    val success: Boolean,
    val data: List<AdminProduct>,
    val message: String? = null
)

data class CategoryListResponse(
    val success: Boolean,
    val data: List<AdminCategory>,
    val message: String? = null
)

data class AdminProduct(
    val id_product: Int,
    val nama_product: String,
    val barcode: String,
    val komposisi: String?,
    val info_gizi: String?,
    val status: String,
    val kategori_id: Int?,
    val created_at: String,
    val updated_at: String,
    val kategori: AdminCategory?
)

data class AdminCategory(
    val id_kategori: Int,
    val nama_kategori: String,
    val created_at: String,
    val updated_at: String,
    val products_count: Int = 0
)
