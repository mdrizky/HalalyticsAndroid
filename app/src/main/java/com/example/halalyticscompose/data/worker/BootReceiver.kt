package com.example.halalyticscompose.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d("BootReceiver", "Device booted or app updated, scheduling medicine reminders")
                
                // Schedule medicine reminders
                MedicineReminderScheduler().scheduleReminders(context)
                
                // Also schedule other periodic tasks
                com.example.halalyticscompose.data.worker.SyncManager.scheduleSync(context)
                com.example.halalyticscompose.data.notification.NotificationWorker.schedulePeriodicWork(context)
            }
        }
    }
}
