package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicalInfoViewModel : ViewModel() {
    private val _profileData = MutableStateFlow<Map<String, Any>?>(null)
    val profileData: StateFlow<Map<String, Any>?> = _profileData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProfile(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ApiConfig.apiService.getMedicalProfile("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.content as? Map<String, Any>
                    _profileData.value = data
                } else {
                    _error.value = "Failed to load medical profile"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(token: String, data: Map<String, Any>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = ApiConfig.apiService.updateMedicalProfile("Bearer $token", data)
                if (response.isSuccessful && response.body()?.success == true) {
                    loadProfile(token)
                    onSuccess()
                } else {
                    _error.value = "Failed to update profile"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
