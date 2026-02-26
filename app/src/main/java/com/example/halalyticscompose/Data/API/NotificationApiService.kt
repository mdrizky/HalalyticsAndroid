package com.example.halalyticscompose.data.api

import retrofit2.Response
import retrofit2.http.*
import com.example.halalyticscompose.Data.Model.ApiResponse



data class NewProduct(
    val id: Int,
    val name: String,
    val brand: String? = null,
    val barcode: String,
    val status: String,
    val created_at: String
)

data class StatusUpdate(
    val productId: String,
    val productName: String,
    val oldStatus: String,
    val newStatus: String,
    val updated_at: String
)

data class NotificationRequest(
    val userId: String,
    val type: String,
    val title: String,
    val message: String,
    val data: Map<String, String>? = null
)

interface NotificationApiService {
    
    @GET("notifications/new-products")
    suspend fun getNewProducts(
        @Header("Authorization") token: String,
        @Query("last_check") lastCheck: Long? = null
    ): Response<ApiResponse<List<NewProduct>>>
    
    @GET("notifications/status-updates")
    suspend fun getStatusUpdates(
        @Header("Authorization") token: String,
        @Query("last_check") lastCheck: Long? = null
    ): Response<ApiResponse<List<StatusUpdate>>>
    
    @POST("fcm/register")
    suspend fun registerFcmToken(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<ApiResponse<String>>
    
    @POST("notifications/mark-read")
    suspend fun markNotificationRead(
        @Header("Authorization") token: String,
        @Body notificationId: Int
    ): Response<ApiResponse<String>>
    
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<NotificationItem>>>
    
    @DELETE("notifications/{id}")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Path("id") notificationId: Int
    ): Response<ApiResponse<String>>
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val type: String,
    val data: Map<String, String>? = null,
    val read: Boolean = false,
    val created_at: String
)
