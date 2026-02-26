package com.example.halalyticscompose.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.halalyticscompose.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Log the message
        Log.d(TAG, "From: ${remoteMessage.from}")

        var handledFromData = false

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val title = remoteMessage.data["title"] ?: "Halalytics Update"
            val body = remoteMessage.data["body"] ?: "Ada informasi baru untuk Anda!"
            val type = remoteMessage.data["type"] ?: "general"
            sendNotification(title, body, type, remoteMessage.data)
            handledFromData = true
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            if (!handledFromData) {
                sendNotification(it.title ?: "Halalytics", it.body ?: "", "general", emptyMap())
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Note: The actual sync to MySQL is handled in MainActivity or Login flow 
        // to ensure we have the Firebase UID associated with this token.
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        type: String,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        data.forEach { (key, value) -> intent.putExtra(key, value) }
        intent.putExtra("notification_type", type)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val (channelId, channelName) = when (type.lowercase()) {
            "news" -> "halal_news" to "Halalytics News"
            "poster" -> "halal_poster" to "Halalytics Poster"
            "product" -> "halal_product" to "Update Produk"
            else -> "halal_alerts" to "Halalytics Alerts"
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with app icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
