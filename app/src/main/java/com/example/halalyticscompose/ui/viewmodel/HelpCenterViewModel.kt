package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HelpCenterViewModel : ViewModel() {
    private val _categories = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val categories: StateFlow<List<Map<String, Any>>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun loadCategories(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ApiConfig.apiService.getHelpCategories("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    _categories.value = response.body()?.content as? List<Map<String, Any>> ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitRequest(token: String, type: String, message: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val requestData = mapOf("type" to type, "message" to message)
                val response = ApiConfig.apiService.submitHelpRequest("Bearer $token", requestData)
                if (response.isSuccessful && response.body()?.success == true) {
                    _successMessage.value = response.body()?.message ?: "Request submitted"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _successMessage.value = null
    }
}
