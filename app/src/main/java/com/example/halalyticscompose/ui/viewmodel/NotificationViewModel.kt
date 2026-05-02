package com.example.halalyticscompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.*
import com.example.halalyticscompose.services.FirebaseRealtimeListener
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var realtimeJob: Job? = null
    private var pollingJob: Job? = null
    private var realtimeStarted = false
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadNotifications(token: String? = null, userId: Int? = null) {
        val authToken = token ?: sessionManager.getAuthToken() ?: return
        val currentUserId = userId ?: sessionManager.getUserId()
        
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getNotifications("Bearer $authToken")
                if (response.success) {
                    _notifications.value = response.data.data
                    _unreadCount.value = response.unreadCount
                }
                
                if (!realtimeStarted && currentUserId > 0) {
                    startRealtimeListener(currentUserId)
                    startPollingFallback(authToken, currentUserId)
                }
            } catch (e: Exception) {
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
            }
        } catch (e: Exception) {
            Log.e("NotificationViewModel", "poll refresh failed", e)
        }
    }

    private fun startRealtimeListener(userId: Int) {
        val listener = FirebaseRealtimeListener(userId)
        realtimeJob?.cancel()
        realtimeStarted = true
        realtimeJob = viewModelScope.launch {
            listener.listenToNotifications().collect { update ->
                val currentList = _notifications.value
                val exists = currentList.any { it.id == update.id }
                
                if (!exists) {
                    val newNotification = NotificationItem(
                        id = update.id,
                        title = update.title,
                        message = update.message,
                        type = update.type,
                        isRead = update.is_read,
                        createdAt = java.time.Instant.ofEpochSecond(update.created_at).toString(),
                        actionType = update.action_type,
                        actionValue = update.action_value,
                        relatedProduct = null
                    )
                    
                    val newList = mutableListOf<NotificationItem>()
                    newList.add(newNotification)
                    newList.addAll(currentList)
                    
                    _notifications.value = newList
                    _unreadCount.value += 1
                }
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        val token = sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            try {
                val currentList = _notifications.value.map { 
                    if (it.id == notificationId && !it.isRead) {
                        _unreadCount.value = maxOf(0, _unreadCount.value - 1)
                        it.copy(isRead = true)
                    } else {
                        it
                    }
                }
                _notifications.value = currentList
                apiService.markAsRead("Bearer $token", notificationId)
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "markAsRead failed", e)
            }
        }
    }

    fun markAllAsRead() {
        val token = sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            try {
                val currentList = _notifications.value.map { it.copy(isRead = true) }
                _notifications.value = currentList
                _unreadCount.value = 0
                apiService.markAllAsRead("Bearer $token")
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "markAllAsRead failed", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realtimeJob?.cancel()
        pollingJob?.cancel()
    }
}
