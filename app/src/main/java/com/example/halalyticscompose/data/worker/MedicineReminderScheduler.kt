package com.example.halalyticscompose.data.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class MedicineReminderScheduler {

    companion object {
        private const val REMINDER_WORK_NAME = "medicine_reminder_work"
        private const val REPEAT_INTERVAL = 15L // Check every 15 minutes
    }

    fun scheduleReminders(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MedicineReminderWorker>(
            REPEAT_INTERVAL,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancelReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME)
    }

    fun isScheduled(context: Context): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(REMINDER_WORK_NAME)
        
        return try {
            workInfos.get().any { !it.state.isFinished }
        } catch (e: Exception) {
            false
        }
    }
}
