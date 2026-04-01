package com.example.halalyticscompose.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.halalyticscompose.data.database.ProductHistoryDao
import com.example.halalyticscompose.data.api.ProductApiService
import com.example.halalyticscompose.utils.SessionManager
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OfflineSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val productHistoryDao: ProductHistoryDao,
    private val productApiService: ProductApiService,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("OfflineSyncWorker", "Starting sync work...")

        val token = sessionManager.getBearerToken() ?: return Result.failure()
        val unsyncedProducts = productHistoryDao.getUnsyncedProducts()

        if (unsyncedProducts.isEmpty()) {
            Log.d("OfflineSyncWorker", "No unsynced products found.")
            return Result.success()
        }

        return try {
            val payload = unsyncedProducts.map { product ->
                mapOf(
                    "barcode" to product.barcode,
                    "product_name" to product.name,
                    "halal_status" to normalizeHalalStatus(product.status),
                    "ai_analysis" to (product.sources ?: ""),
                    "scanned_at" to product.timestamp
                )
            }

            val response = productApiService.syncScanLogs(
                token = token,
                request = mapOf("logs" to payload)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                unsyncedProducts.forEach { product ->
                    productHistoryDao.markAsSynced(product.barcode)
                }

                Log.d("OfflineSyncWorker", "Sync complete. Synced ${unsyncedProducts.size} products.")
                Result.success()
            } else {
                Log.e("OfflineSyncWorker", "Batch sync failed: ${response.message()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("OfflineSyncWorker", "Exception during batch sync: ${e.message}", e)
            Result.retry()
        }
    }

    private fun normalizeHalalStatus(status: String): String {
        return when (status.trim().lowercase()) {
            "halal" -> "halal"
            "haram", "tidak halal", "non-halal" -> "haram"
            else -> "syubhat"
        }
    }
}
