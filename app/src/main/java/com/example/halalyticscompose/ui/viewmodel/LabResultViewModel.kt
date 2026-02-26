package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.LabResultData
import com.example.halalyticscompose.Data.Model.LabResultRequest
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabResultViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _result = MutableStateFlow<LabResultData?>(null)
    val result: StateFlow<LabResultData?> = _result.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun uploadLabResult(imageBase64: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Determine user ID. If not logged in, error.
                val userIdPattern = sessionManager.getUserId()
                if (userIdPattern <= 0) {
                    _error.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }
                
                val userId = userIdPattern

                val request = LabResultRequest(
                    userId = userId,
                    imageBase64 = imageBase64,
                    testDate = null,
                    labType = "Pemeriksaan Lab"
                )
                
                val response = apiService.uploadLabResult(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        _result.value = body.data
                    } else {
                        _error.value = "Failed to analyze lab result."
                    }
                } else {
                    _error.value = "Error from server: \${response.message()}"
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
