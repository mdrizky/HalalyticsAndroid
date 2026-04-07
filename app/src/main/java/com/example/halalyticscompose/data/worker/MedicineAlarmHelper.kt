package com.example.halalyticscompose.data.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

class MedicineAlarmHelper {

    companion object {
        fun scheduleExactAlarm(
            context: Context,
            reminderId: Int,
            medicineName: String,
            timeInMillis: Long
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w("MedicineAlarmHelper", "Cannot schedule exact alarms. Permission denied.")
                    return
                }
            }

            val intent = Intent(context, MedicineTakenReceiver::class.java).apply {
                action = "MEDICINE_REMINDER_ALARM"
                putExtra("REMINDER_ID", reminderId)
                putExtra("MEDICINE_NAME", medicineName)
            }

            // Must use FLAG_UPDATE_CURRENT and FLAG_IMMUTABLE per modern Android standards
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                // Allows alarm to fire even when the device is idle (Doze mode)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
                Log.d("MedicineAlarmHelper", "Scheduled exact alarm for $medicineName at $timeInMillis")
            } catch (e: SecurityException) {
                Log.e("MedicineAlarmHelper", "Exact alarm permission missing", e)
            }
        }

        fun cancelAlarm(context: Context, reminderId: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MedicineTakenReceiver::class.java).apply {
                action = "MEDICINE_REMINDER_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d("MedicineAlarmHelper", "Cancelled alarm for $reminderId")
        }
    }
}
