package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.LeaderboardMember
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _leaderboard = MutableStateFlow<List<LeaderboardMember>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardMember>> = _leaderboard.asStateFlow()

    private val _myRank = MutableStateFlow<LeaderboardMember?>(null)
    val myRank: StateFlow<LeaderboardMember?> = _myRank.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadLeaderboard(period: String = "monthly") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = apiService.getLeaderboard(period = period)
                if (response.success) {
                    _leaderboard.value = response.content ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load leaderboard"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMyRank(period: String = "monthly") {
        val token = sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            try {
                val response = apiService.getMyRank("Bearer $token", period)
                if (response.success) {
                    _myRank.value = response.content
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
