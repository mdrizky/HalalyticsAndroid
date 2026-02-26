package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.NotificationApiService
import com.example.halalyticscompose.data.api.NotificationItem
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminNotificationViewModel @Inject constructor(
    private val notificationApiService: NotificationApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private var pollingJob: kotlinx.coroutines.Job? = null

    init {
        startPolling()
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                loadNotifications()
                kotlinx.coroutines.delay(30000) // Poll every 30 seconds
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
    
    fun loadNotifications() {
        val token = sessionManager.getBearerToken()
        if (token != null) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                
                try {
                    val response = notificationApiService.getNotifications(token)
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!
                        if (apiResponse.success) {
                            _notifications.value = apiResponse.data ?: emptyList()
                        } else {
                            _errorMessage.value = apiResponse.message ?: "Gagal memuat notifikasi"
                        }
                    } else {
                        _errorMessage.value = "Error: ${response.code()} ${response.message()}"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun markAsRead(notificationId: Int) {
        val token = sessionManager.getBearerToken()
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = notificationApiService.markNotificationRead(token, notificationId)
                    if (response.isSuccessful) {
                        // Update local state
                        _notifications.value = _notifications.value.map { notification ->
                            if (notification.id == notificationId) {
                                notification.copy(read = true)
                            } else {
                                notification
                            }
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Gagal menandai notifikasi sebagai dibaca: ${e.message}"
                }
            }
        }
    }
    
    fun markAllAsRead() {
        val token = sessionManager.getBearerToken()
        if (token != null) {
            viewModelScope.launch {
                try {
                    // Mark all unread notifications as read
                    val unreadNotifications = _notifications.value.filter { !it.read }
                    unreadNotifications.forEach { notification ->
                        notificationApiService.markNotificationRead(token, notification.id)
                    }
                    
                    // Update local state
                    _notifications.value = _notifications.value.map { it.copy(read = true) }
                } catch (e: Exception) {
                    _errorMessage.value = "Gagal menandai semua notifikasi sebagai dibaca: ${e.message}"
                }
            }
        }
    }
    
    fun deleteNotification(notificationId: Int) {
        val token = sessionManager.getBearerToken()
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = notificationApiService.deleteNotification(token, notificationId)
                    if (response.isSuccessful) {
                        // Remove from local state
                        _notifications.value = _notifications.value.filter { it.id != notificationId }
                    } else {
                        _errorMessage.value = "Gagal menghapus notifikasi"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Gagal menghapus notifikasi: ${e.message}"
                }
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
