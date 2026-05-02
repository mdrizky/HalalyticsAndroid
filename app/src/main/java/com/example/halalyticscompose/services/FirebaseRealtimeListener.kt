package com.example.halalyticscompose.services

import com.example.halalyticscompose.data.model.AdminStats
import com.example.halalyticscompose.data.model.NotificationUpdate
import com.example.halalyticscompose.data.model.ScanHistoryUpdate
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseRealtimeListener(private val userId: Int) {

    private val database = FirebaseDatabase.getInstance()

    /**
     * Listen to notifications realtime
     */
    fun listenToNotifications(): Flow<NotificationUpdate> = callbackFlow {
        val userRef = database.getReference("notifications/$userId")
        val broadcastRef = database.getReference("notifications/broadcast")

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val notification = snapshot.getValue(NotificationUpdate::class.java)
                notification?.let { trySend(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val notification = snapshot.getValue(NotificationUpdate::class.java)
                notification?.let { trySend(it) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userRef.addChildEventListener(listener)
        broadcastRef.addChildEventListener(listener)

        awaitClose {
            userRef.removeEventListener(listener)
            broadcastRef.removeEventListener(listener)
        }
    }

    /**
     * Listen to scan history updates
     */
    fun listenToScanHistory(): Flow<ScanHistoryUpdate> = callbackFlow {
        val ref = database.getReference("scan_histories/$userId")
        
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val scan = snapshot.getValue(ScanHistoryUpdate::class.java)
                scan?.let { trySend(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addChildEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    /**
     * Listen to admin stats (for admin panel usage, or demo)
     */
    fun listenToAdminStats(): Flow<AdminStats> = callbackFlow {
        val ref = database.getReference("admin/stats")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stats = snapshot.getValue(AdminStats::class.java)
                stats?.let { trySend(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}
