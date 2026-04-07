package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    private val _leaderboard = MutableStateFlow<List<Any>>(emptyList())
    val leaderboard: StateFlow<List<Any>> = _leaderboard.asStateFlow()

    private val _myRank = MutableStateFlow<Any?>(null)
    val myRank: StateFlow<Any?> = _myRank.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadLeaderboard(period: String = "monthly") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = ApiConfig.apiService.getLeaderboard(period = period)
                if (response.success) {
                    _leaderboard.value = response.content as? List<Any> ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMyRank(token: String, period: String = "monthly") {
        viewModelScope.launch {
            try {
                val response = ApiConfig.apiService.getMyRank("Bearer $token", period)
                if (response.success) {
                    _myRank.value = response.content
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
