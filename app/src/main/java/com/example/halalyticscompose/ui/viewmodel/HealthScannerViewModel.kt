package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.ApiResponse
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthProfile(
    val age: Int = 0,
    val height: Float = 0f,
    val weight: Float = 0f,
    val bloodType: String = "",
    val allergies: String = "",
    val medicalHistory: String = "",
    val goal: String = ""
)

data class HealthAnalysis(
    val bmi: Float = 0f,
    val status: String = "",
    val recommendations: List<String> = emptyList(),
    val risks: List<String> = emptyList()
)

@HiltViewModel
class HealthScannerViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    private fun extractStringList(value: Any?): List<String> {
        return when (value) {
            is List<*> -> value.mapNotNull { it?.toString()?.takeIf(String::isNotBlank) }
            is String -> value.split(",").map { it.trim() }.filter { it.isNotBlank() }
            else -> emptyList()
        }
    }

    private val _healthProfile = MutableStateFlow(HealthProfile())
    val healthProfile: StateFlow<HealthProfile> = _healthProfile.asStateFlow()

    private val _healthAnalysis = MutableStateFlow<HealthAnalysis?>(null)
    val healthAnalysis: StateFlow<HealthAnalysis?> = _healthAnalysis.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateHealthProfile(profile: HealthProfile) {
        _healthProfile.value = profile
    }

    fun analyzeHealth() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val profile = _healthProfile.value
                val token = sessionManager.getBearerToken() ?: ""
                
                val response = apiService.analyzeHealth(
                    token,
                    mapOf(
                        "age" to profile.age,
                        "height" to profile.height,
                        "weight" to profile.weight,
                        "blood_type" to profile.bloodType,
                        "allergies" to profile.allergies,
                        "medical_history" to profile.medicalHistory,
                        "goal" to profile.goal
                    )
                )

                if (response.success && response.data != null) {
                    val data = response.data!!
                    _healthAnalysis.value = HealthAnalysis(
                        bmi = (data["bmi"] as? Number)?.toFloat() ?: 0f,
                        status = data["status"] as? String ?: "",
                        recommendations = extractStringList(data["recommendations"]),
                        risks = extractStringList(data["risks"])
                    )
                } else {
                    _errorMessage.value = "Gagal menganalisis: ${response.message ?: "Data tidak ditemukan"}"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to analyze health: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
