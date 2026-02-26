package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.AiAnalysisRequest
import com.example.halalyticscompose.Data.Model.AiAnalysisResponse
import com.example.halalyticscompose.Data.Model.AiAnalysisContent
import com.example.halalyticscompose.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

data class AiAnalysisUiState(
    val status: String = "Idle", // Idle, Loading, Success, Error
    val analysisResult: AiAnalysisContent? = null,
    val healthAlerts: List<String> = emptyList(),
    val errorMessage: String? = null
)

class AiAnalysisViewModel : ViewModel() {
    private var sessionManager: SessionManager? = null
    
    private val apiService: ApiService by lazy {
        com.example.halalyticscompose.Data.Network.ApiConfig.apiService
    }

    private val _uiState = MutableStateFlow(AiAnalysisUiState())
    val uiState: StateFlow<AiAnalysisUiState> = _uiState.asStateFlow()

    fun setSessionManager(manager: SessionManager) {
        sessionManager = manager
    }

    fun analyzeIngredients(ingredientsText: String, familyId: Int? = null) {
        viewModelScope.launch {
            _uiState.value = AiAnalysisUiState(status = "Loading")
            
            try {
                val manager = sessionManager ?: throw Exception("SessionManager not initialized")
                val token = manager.getBearerToken() ?: ""
                
                // Construct user profile context for AI
                val userProfile = mutableMapOf<String, Any>()
                manager.getMedicalHistory()?.let { userProfile["medical_history"] = it }
                manager.getAllergy()?.let { userProfile["allergy"] = it }
                userProfile["is_gluten_free"] = manager.isGlutenFree()
                userProfile["has_nut_allergy"] = manager.hasNutAllergy()
                
                val request = AiAnalysisRequest(
                    ingredientsText = ingredientsText,
                    familyId = familyId,
                    userProfile = userProfile
                )
                
                val response = apiService.analyzeIngredients(token, request)
                
                if (response.success && response.content != null) {
                    val content = response.content
                    val healthAlerts = processHealthAlerts(content, manager, familyId)
                    
                    _uiState.value = AiAnalysisUiState(
                        status = "Success",
                        analysisResult = content,
                        healthAlerts = healthAlerts
                    )
                } else {
                    _uiState.value = AiAnalysisUiState(
                        status = "Error",
                        errorMessage = response.message ?: "Failed to analyze ingredients"
                    )
                }
            } catch (e: Exception) {
                Log.e("AiAnalysisVM", "Error analyzing ingredients", e)
                _uiState.value = AiAnalysisUiState(
                    status = "Error",
                    errorMessage = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private fun processHealthAlerts(content: AiAnalysisContent, sessionManager: SessionManager, familyId: Int?): List<String> {
        val alerts = mutableListOf<String>()
        // Note: For now, client-side alerts still use main user session if family member data isn't easily accessible here.
        // However, the backend 'analysis' text already contains family-specific warnings.
        // We can supplement it here if we want.
        
        val medicalHistory = sessionManager.getMedicalHistory()?.lowercase() ?: ""
        val allergies = sessionManager.getAllergy()?.lowercase() ?: ""
        
        // 1. Check for chronic conditions (Diabetes, Hypertension)
        if (medicalHistory.contains("diabetes") && 
            (content.analysis.lowercase().contains("sugar") || content.analysis.lowercase().contains("gula"))) {
            alerts.add("⚠️ Kandungan Gula Tinggi: Tidak direkomendasikan untuk profil diabetes Anda.")
        }
        
        if (medicalHistory.contains("hipertensi") && 
            (content.analysis.lowercase().contains("garam") || content.analysis.lowercase().contains("salt") || content.analysis.lowercase().contains("natrium"))) {
            alerts.add("⚠️ Tinggi Natrium: Harap batasi konsumsi untuk menjaga tekanan darah.")
        }

        // 2. Check for allergies
        if (sessionManager.hasNutAllergy() || allergies.contains("kacang") || allergies.contains("nut")) {
            if (content.redFlags.any { it.lowercase().contains("kacang") || it.lowercase().contains("nut") }) {
                alerts.add("❌ BAHAYA: Mengandung Alergen Kacang!")
            }
        }
        
        if (sessionManager.isGlutenFree() || allergies.contains("gluten")) {
            if (content.redFlags.any { it.lowercase().contains("wheat") || it.lowercase().contains("gandum") || it.lowercase().contains("gluten") }) {
                alerts.add("⚠️ Peringatan: Mengandung Gluten/Gandum.")
            }
        }

        return alerts
    }
}
