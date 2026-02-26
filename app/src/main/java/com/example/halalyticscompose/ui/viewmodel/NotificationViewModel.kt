package com.example.halalyticscompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.*
import com.example.halalyticscompose.Data.Network.ApiConfig
import com.example.halalyticscompose.services.FirebaseRealtimeListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class NotificationViewModel : ViewModel() {

    private val apiService = ApiConfig.apiService
    private var realtimeJob: Job? = null
    private var pollingJob: Job? = null
    private var realtimeStarted = false
    private var currentToken: String? = null
    private var currentUserId: Int? = null
    
    // Notifications State
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Load notifications from API and start realtime listener
     */
    fun loadNotifications(token: String, userId: Int) {
        currentToken = token
        currentUserId = userId
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                // Initial Load via API
                val response = apiService.getNotifications("Bearer $token")
                if (response.success) {
                    _notifications.value = response.data.data
                    _unreadCount.value = response.unreadCount
                } else {
                    _errorMessage.value = "Gagal memuat notifikasi dari server"
                }

                // Start Listening to Firebase Realtime Events
                if (!realtimeStarted) {
                    startRealtimeListener(userId)
                    startPollingFallback(token, userId)
                }
            } catch (e: Exception) {
                _errorMessage.value = formatApiError("Gagal memuat notifikasi", e)
                Log.e("NotificationViewModel", "loadNotifications failed", e)
            } finally {
                _loading.value = false
            }
        }
    }

    private fun startPollingFallback(token: String, userId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(30000)
                refreshFromApi(token, userId)
            }
        }
    }

    private suspend fun refreshFromApi(token: String, userId: Int) {
        try {
            val response = apiService.getNotifications("Bearer $token")
            if (response.success) {
                _notifications.value = response.data.data
                _unreadCount.value = response.unreadCount
            } else {
                _errorMessage.value = "Sinkronisasi notifikasi gagal"
            }
        } catch (e: Exception) {
            _errorMessage.value = formatApiError("Sinkronisasi notifikasi gagal", e)
            Log.e("NotificationViewModel", "poll refresh failed for userId=$userId", e)
        }
    }

    private fun startRealtimeListener(userId: Int) {
        val listener = FirebaseRealtimeListener(userId)
        realtimeJob?.cancel()
        realtimeStarted = true
        realtimeJob = viewModelScope.launch {
            // Listen for new notifications
            listener.listenToNotifications().collect { update ->
                // Check if this notification is already in the list to avoid duplicates
                val currentList = _notifications.value
                val exists = currentList.any { it.id == update.id }
                
                if (!exists) {
                    // Prepend new notification to top
                    val newNotification = NotificationItem(
                        id = update.id,
                        title = update.title,
                        message = update.message,
                        type = update.type, // 'system', 'scan', etc
                        isRead = update.is_read,
                        createdAt = java.time.Instant.ofEpochSecond(update.created_at).toString(), // simplified
                        actionType = null,
                        actionValue = null,
                        relatedProduct = null
                    )
                    
                    val newList = mutableListOf<NotificationItem>()
                    newList.add(newNotification)
                    newList.addAll(currentList)
                    
                    _notifications.value = newList
                    _unreadCount.value += 1
                    _errorMessage.value = null
                }
            }
        }
    }

    private fun formatApiError(prefix: String, e: Exception): String {
        return when (e) {
            is HttpException -> "$prefix (${e.code()}): ${e.response()?.errorBody()?.string()?.take(180) ?: "HTTP error"}"
            is IOException -> "$prefix: koneksi internet/server bermasalah"
            else -> "$prefix: ${e.message ?: "unknown error"}"
        }
    }

    fun markAsRead(token: String, notificationId: Int) {
        viewModelScope.launch {
            try {
                // Optimistic UI Update
                val currentList = _notifications.value.map { 
                    if (it.id == notificationId && !it.isRead) {
                        _unreadCount.value = maxOf(0, _unreadCount.value - 1)
                        it.copy(isRead = true)
                    } else {
                        it
                    }
                }
                _notifications.value = currentList

                // API Call
                apiService.markAsRead("Bearer $token", notificationId)
            } catch (e: Exception) {
                _errorMessage.value = formatApiError("Gagal menandai notifikasi", e)
                Log.e("NotificationViewModel", "markAsRead failed", e)
            }
        }
    }

    fun markAllAsRead(token: String) {
        viewModelScope.launch {
            try {
                // Optimistic UI Update
                val currentList = _notifications.value.map { it.copy(isRead = true) }
                _notifications.value = currentList
                _unreadCount.value = 0

                // API Call
                apiService.markAllAsRead("Bearer $token")
            } catch (e: Exception) {
                _errorMessage.value = formatApiError("Gagal menandai semua notifikasi", e)
                Log.e("NotificationViewModel", "markAllAsRead failed", e)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        realtimeJob?.cancel()
        pollingJob?.cancel()
    }
}
