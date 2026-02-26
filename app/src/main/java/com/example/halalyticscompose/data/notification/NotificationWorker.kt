package com.example.halalyticscompose.data.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.halalyticscompose.data.api.NotificationApiService
import com.example.halalyticscompose.utils.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationApiService: NotificationApiService,
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        val token = sessionManager.getAuthToken() ?: return Result.success()
        val bearerToken = "Bearer $token"

        return try {
            // Check for new products
            val response = notificationApiService.getNewProducts(bearerToken)
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.forEach { product ->
                    notificationService.showNewProductNotification(
                        productName = product.name,
                        productBrand = product.brand,
                        productId = product.id.toString()
                    )
                }
            }
            
            // Check for status changes
            val statusResponse = notificationApiService.getStatusUpdates(bearerToken)
            if (statusResponse.isSuccessful && statusResponse.body()?.success == true) {
                statusResponse.body()?.data?.forEach { update ->
                    notificationService.showStatusChangedNotification(
                        productName = update.productName,
                        oldStatus = update.oldStatus,
                        newStatus = update.newStatus,
                        productId = update.productId
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    companion object {
        const val WORK_NAME = "HalalyticsNotificationWorker"
        
        fun schedulePeriodicWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                15, // Repeat interval (15 minutes)
                TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
        
        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
