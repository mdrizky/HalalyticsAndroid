package com.example.halalyticscompose.utils

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashReporter {
    private const val PREF_NAME = "halalytics_crash_reporter"
    private const val KEY_LAST_CRASH = "last_crash"
    private const val TAG = "CrashReporter"

    fun install(context: Context) {
        val appContext = context.applicationContext
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val message = buildString {
                    append("Time: ").append(timestamp).append('\n')
                    append("Thread: ").append(thread.name).append('\n')
                    append("Type: ").append(throwable::class.java.name).append('\n')
                    append("Message: ").append(throwable.message ?: "-").append('\n')
                    append(Log.getStackTraceString(throwable))
                }
                appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_LAST_CRASH, message)
                    .apply()
                Log.e(TAG, "Uncaught exception saved", throwable)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist uncaught exception: ${e.message}")
            } finally {
                previousHandler?.uncaughtException(thread, throwable)
                    ?: kotlin.run {
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(10)
                    }
            }
        }
    }

    fun getLastCrash(context: Context): String? =
        context.applicationContext
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_CRASH, null)

    fun clearLastCrash(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_LAST_CRASH)
            .apply()
    }
}
