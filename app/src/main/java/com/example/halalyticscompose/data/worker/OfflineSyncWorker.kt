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

        var successCount = 0
        for (product in unsyncedProducts) {
            try {
                val request = mapOf(
                    "product_id" to (product.barcode), // Using barcode as identifier or if ID exists
                    "nama_produk" to product.name,
                    "barcode" to product.barcode,
                    "status_halal" to product.status,
                    "status_kesehatan" to "sehat",
                    "tanggal_scan" to java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(product.timestamp))
                )

                val response = productApiService.addToScanHistory(token, request)

                if (response.isSuccessful && response.body()?.success == true) {
                    productHistoryDao.markAsSynced(product.barcode)
                    successCount++
                    Log.d("OfflineSyncWorker", "Synced product: ${product.name}")
                } else {
                    Log.e("OfflineSyncWorker", "Failed to sync product: ${product.name}. Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("OfflineSyncWorker", "Exception during sync for ${product.name}: ${e.message}")
            }
        }

        Log.d("OfflineSyncWorker", "Sync complete. Synced $successCount products.")
        return if (successCount == unsyncedProducts.size) Result.success() else Result.retry()
    }
}
