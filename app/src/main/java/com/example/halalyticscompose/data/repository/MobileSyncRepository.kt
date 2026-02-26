package com.example.halalyticscompose.data.repository

import android.util.Log
import com.example.halalyticscompose.data.remote.MobileSyncService
import com.example.halalyticscompose.data.remote.ScanData
import com.example.halalyticscompose.data.remote.ScanSyncRequest
import com.example.halalyticscompose.data.remote.UserData
import com.example.halalyticscompose.data.remote.UserSyncRequest
import com.example.halalyticscompose.data.remote.AdminProduct
import com.example.halalyticscompose.data.remote.AdminCategory

/**
 * Repository for handling mobile synchronization with Laravel admin system
 */
class MobileSyncRepository(
    private val syncService: MobileSyncService,
    private val tokenProvider: () -> String
) {
    
    private val TAG = "MobileSyncRepository"
    
    /**
     * Sync scan data to admin system
     */
    suspend fun syncScans(
        userId: Int,
        scans: List<ScanData>
    ): Result<SyncResult> {
        return try {
            val token = tokenProvider()
            val request = ScanSyncRequest(userId, scans)
            val response = syncService.syncScanData("Bearer $token", request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "Successfully synced ${body.synced_count} scans")
                    Result.success(SyncResult(
                        success = true,
                        syncedCount = body.synced_count,
                        errors = body.errors,
                        message = body.message
                    ))
                } else {
                    Log.e(TAG, "Sync failed: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Unknown sync error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Sync request failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Sync failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during sync", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sync user data to admin system
     */
    suspend fun syncUsers(users: List<UserData>): Result<SyncResult> {
        return try {
            val token = tokenProvider()
            val request = UserSyncRequest(users)
            val response = syncService.syncUserData("Bearer $token", request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "Successfully synced ${body.synced_count} users")
                    Result.success(SyncResult(
                        success = true,
                        syncedCount = body.synced_count,
                        errors = body.errors,
                        message = body.message
                    ))
                } else {
                    Log.e(TAG, "User sync failed: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Unknown user sync error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "User sync request failed: ${response.code()} - $errorBody")
                Result.failure(Exception("User sync failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during user sync", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get products from admin system
     */
    suspend fun getProducts(
        search: String? = null,
        categoryId: Int? = null,
        status: String? = null
    ): Result<List<AdminProduct>> {
        return try {
            val token = tokenProvider()
            val response = syncService.getProducts("Bearer $token", search, categoryId, status)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "Successfully retrieved ${body.data.size} products")
                    Result.success(body.data)
                } else {
                    Log.e(TAG, "Get products failed: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Get products request failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Get products failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during get products", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get categories from admin system
     */
    suspend fun getCategories(): Result<List<AdminCategory>> {
        return try {
            val token = tokenProvider()
            val response = syncService.getCategories("Bearer $token")
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "Successfully retrieved ${body.data.size} categories")
                    Result.success(body.data)
                } else {
                    Log.e(TAG, "Get categories failed: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Get categories request failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Get categories failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during get categories", e)
            Result.failure(e)
        }
    }
}

// Result data classes moved to remote or consolidated
data class SyncResult(
    val success: Boolean,
    val syncedCount: Int,
    val errors: List<String>,
    val message: String
)
