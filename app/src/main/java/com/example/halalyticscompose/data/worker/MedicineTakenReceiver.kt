package com.example.halalyticscompose.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.halalyticscompose.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicineTakenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "MEDICINE_TAKEN") {
            val reminderId = intent.getIntExtra("reminder_id", -1)
            val sessionManager = SessionManager(context)
            val userId = sessionManager.getUserId()
            val token = sessionManager.getBearerToken().orEmpty()
            
            if (reminderId != -1 && token.isNotBlank()) {
                // Mark medicine as taken in background
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val apiService = com.example.halalyticscompose.Data.Network.ApiConfig.apiService
                        val response = apiService.markMedicineAsTaken(token, reminderId, userId.toString())
                        if (response.isSuccessful) {
                            Log.d("MedicineTaken", "Successfully marked medicine as taken")
                        } else {
                            Log.e("MedicineTaken", "Failed to mark medicine as taken")
                        }
                    } catch (e: Exception) {
                        Log.e("MedicineTaken", "Error marking medicine as taken", e)
                    }
                }
                
                // Show confirmation notification
                showTakenConfirmation(context, reminderId)
            }
        }
    }

    private fun showTakenConfirmation(context: Context, reminderId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        val notification = android.app.Notification.Builder(context, "medicine_reminders")
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle("Medicine Taken")
            .setContentText("Great! Your medicine has been recorded.")
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(reminderId + 10000, notification) // Different ID to avoid conflict
    }
}
