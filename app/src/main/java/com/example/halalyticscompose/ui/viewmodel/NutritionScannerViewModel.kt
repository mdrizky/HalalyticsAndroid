package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.NutritionScanData
import com.example.halalyticscompose.Data.Model.NutritionScanRequest
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionScannerViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _result = MutableStateFlow<NutritionScanData?>(null)
    val result: StateFlow<NutritionScanData?> = _result.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun scanNutrition(imageBase64: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userIdPattern = sessionManager.getUserId()
                if (userIdPattern <= 0) {
                    _error.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }
                
                val userId = userIdPattern

                val request = NutritionScanRequest(
                    userId = userId,
                    imageBase64 = imageBase64
                )
                
                val response = apiService.scanNutrition(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        _result.value = body.data
                    } else {
                        _error.value = "Gagal memproses komposisi produk."
                    }
                } else {
                    _error.value = "Error from server: \${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: \${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearResult() {
        _result.value = null
        _error.value = null
    }
}
