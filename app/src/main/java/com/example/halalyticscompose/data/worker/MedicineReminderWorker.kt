package com.example.halalyticscompose.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.halalyticscompose.MainActivity
import com.example.halalyticscompose.R
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.utils.SessionManager
import kotlinx.coroutines.flow.first

class MedicineReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get session manager and API service from application context
            val sessionManager = SessionManager(applicationContext)
            // Fix: Use ApiConfig.apiService property instead of create()
            val apiService = com.example.halalyticscompose.Data.Network.ApiConfig.apiService
            
            val userId = sessionManager.getUserId() ?: return Result.failure()
            val token = sessionManager.getBearerToken() ?: return Result.failure()
            
            // Get next doses from API with proper authorization
            val response = apiService.getNextDose(token, userId.toString())
            
            if (response.isSuccessful) {
                val nextDoses = response.body()?.next_doses ?: emptyList()
                
                // Schedule notifications for upcoming doses
                for (dose in nextDoses) {
                    scheduleNotification(dose)
                }
                
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun scheduleNotification(dose: com.example.halalyticscompose.Data.Model.NextDose) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel (for Android 8.0+)
        val channelId = "medicine_reminders"
        val channelName = "Medicine Reminders"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for medicine reminders"
            enableVibration(true)
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
        
        // Create intent for notification tap
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminder_id", dose.reminder_id)
            putExtra("medicine_name", dose.medicine_name)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            dose.reminder_id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Create notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Medicine Reminder")
            .setContentText("Time to take ${dose.medicine_name}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("It's time to take your medicine: ${dose.medicine_name}${dose.dose_info?.let { "\nDosage: $it" } ?: ""}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_save,
                "Taken",
                createTakenIntent(dose.reminder_id)
            )
            .build()
        
        // Show notification
        notificationManager.notify(dose.reminder_id, notification)
    }

    private fun createTakenIntent(reminderId: Int): PendingIntent {
        val intent = Intent(applicationContext, MedicineTakenReceiver::class.java).apply {
            action = "MEDICINE_TAKEN"
            putExtra("reminder_id", reminderId)
        }
        
        return PendingIntent.getBroadcast(
            applicationContext,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
