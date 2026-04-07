package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PointsViewModel : ViewModel() {
    private val _points = MutableStateFlow<Int>(0)
    val points: StateFlow<Int> = _points.asStateFlow()

    private val _history = MutableStateFlow<List<Any>>(emptyList())
    val history: StateFlow<List<Any>> = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPoints(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = ApiConfig.apiService.getMyPoints("Bearer $token")
                if (response.success) {
                    try {
                        val dataMap = response.content as? Map<String, Any>
                        val pts = (dataMap?.get("points") as? Number)?.toInt() ?: 0
                        _points.value = pts
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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

    fun loadHistory(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ApiConfig.apiService.getPointsHistory("Bearer $token")
                if (response.success) {
                    val dataList = response.content as? List<Any> ?: emptyList()
                    _history.value = dataList
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
