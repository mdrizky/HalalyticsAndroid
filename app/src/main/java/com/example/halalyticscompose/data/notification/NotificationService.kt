package com.example.halalyticscompose.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.halalyticscompose.MainActivity
import com.example.halalyticscompose.R

class NotificationService(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "halalytics_notifications"
        const val CHANNEL_NAME = "Halalytics Notifications"
        const val NEW_PRODUCT_CHANNEL_ID = "new_products"
        const val NEW_PRODUCT_CHANNEL_NAME = "New Products"
        
        // Notification IDs
        const val NOTIFICATION_NEW_PRODUCT = 1001
        const val NOTIFICATION_STATUS_CHANGED = 1002
        const val NOTIFICATION_ADMIN_APPROVAL = 1003
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        // Main notification channel
        val mainChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for Halalytics app"
            enableLights(true)
            enableVibration(true)
        }
        
        // New products notification channel
        val newProductChannel = NotificationChannel(
            NEW_PRODUCT_CHANNEL_ID,
            NEW_PRODUCT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for new products added by admin"
            enableLights(true)
            enableVibration(true)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mainChannel)
        notificationManager.createNotificationChannel(newProductChannel)
    }
    
    fun showNewProductNotification(
        productName: String,
        productBrand: String? = null,
        productId: String
    ) {
        // Create intent for when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "product_detail")
            putExtra("product_id", productId)
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val brandText = if (productBrand != null) " - $productBrand" else ""
        val message = "Produk baru telah ditambahkan: $productName$brandText"
        
        val notification = NotificationCompat.Builder(context, NEW_PRODUCT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You'll need to add this icon
            .setContentTitle("Produk Baru!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_NEW_PRODUCT, notification)
        }
    }
    
    fun showStatusChangedNotification(
        productName: String,
        oldStatus: String,
        newStatus: String,
        productId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "product_detail")
            putExtra("product_id", productId)
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val message = "Status produk $productName berubah dari $oldStatus menjadi $newStatus"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Status Produk Berubah!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_STATUS_CHANGED, notification)
        }
    }
    
    fun showAdminApprovalNotification(
        productName: String,
        isApproved: Boolean,
        productId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "product_detail")
            putExtra("product_id", productId)
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = if (isApproved) "Produk Disetujui!" else "Produk Ditolak"
        val message = "Produk $productName telah ${if (isApproved) "disetujui" else "ditolak"} oleh admin"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ADMIN_APPROVAL, notification)
        }
    }
    
    fun cancelNotification(notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }
}
