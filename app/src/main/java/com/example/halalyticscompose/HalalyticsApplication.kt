package com.example.halalyticscompose

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.halalyticscompose.data.notification.NotificationWorker
import com.example.halalyticscompose.data.worker.MedicineReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HalalyticsApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    
    override fun onCreate() {
        super.onCreate()
        
        // Delay scheduling to ensure WorkManager uses our custom configuration
        // WorkManager.getInstance(this) will now use workManagerConfiguration
        try {
            // Schedule offline data synchronization
            com.example.halalyticscompose.data.worker.SyncManager.scheduleSync(this)
            
            // Schedule periodic notification worker
            NotificationWorker.schedulePeriodicWork(this)
            
            // Schedule medicine reminder worker
            MedicineReminderScheduler().scheduleReminders(this)
        } catch (e: Exception) {
            Log.e("HalalyticsApp", "Error scheduling workers: ${e.message}")
        }
    }
}
