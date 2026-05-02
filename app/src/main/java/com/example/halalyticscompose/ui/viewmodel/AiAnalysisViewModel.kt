package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.AiAnalysisRequest
import com.example.halalyticscompose.data.model.AiAnalysisContent
import com.example.halalyticscompose.utils.SessionManager
import com.example.halalyticscompose.ai.GeminiAnalyzer
import com.example.halalyticscompose.ai.AiAnalysisResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

data class AiAnalysisUiState(
    val status: String = "Idle", // Idle, Loading, Success, Error
    val analysisResult: AiAnalysisContent? = null,
    val localAiResult: AiAnalysisResult? = null,
    val healthAlerts: List<String> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AiAnalysisViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val geminiAnalyzer = GeminiAnalyzer()

    private val _uiState = MutableStateFlow(AiAnalysisUiState())
    val uiState: StateFlow<AiAnalysisUiState> = _uiState.asStateFlow()

    fun analyzeIngredients(ingredientsText: String, familyId: Int? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = "Loading")
            
            try {
                val token = sessionManager.getAuthToken() ?: ""
                
                // Construct user profile context for AI
                val userProfile = mutableMapOf<String, Any>()
                sessionManager.getMedicalHistory()?.let { userProfile["medical_history"] = it }
                sessionManager.getAllergy()?.let { userProfile["allergy"] = it }
                userProfile["is_gluten_free"] = sessionManager.isGlutenFree()
                userProfile["has_nut_allergy"] = sessionManager.hasNutAllergy()
                
                val request = AiAnalysisRequest(
                    ingredientsText = ingredientsText,
                    familyId = familyId,
                    userProfile = userProfile
                )
                
                val response = apiService.analyzeIngredients("Bearer $token", request)
                
                if (response.success && response.content != null) {
                    val content = response.content
                    val healthAlerts = processHealthAlerts(content)
                    
                    _uiState.value = _uiState.value.copy(
                        status = "Success",
                        analysisResult = content,
                        healthAlerts = healthAlerts
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        status = "Error",
                        errorMessage = response.message ?: "Gagal menganalisis komposisi. Silakan coba lagi."
                    )
                }
            } catch (e: Exception) {
                Log.e("AiAnalysisVM", "Error analyzing ingredients", e)
                _uiState.value = _uiState.value.copy(
                    status = "Error",
                    errorMessage = "Terjadi kesalahan koneksi. Pastikan internet Anda stabil."
                )
            }
        }
    }

    fun analyzeIngredientsLocal(ingredients: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = "Loading")
            geminiAnalyzer.analyzeIngredients(ingredients).collect { result ->
                _uiState.value = _uiState.value.copy(
                    status = when (result) {
                        is AiAnalysisResult.Error -> "Error"
                        is AiAnalysisResult.Success -> "Success"
                        is AiAnalysisResult.Loading -> "Loading"
                    },
                    localAiResult = result,
                    errorMessage = (result as? AiAnalysisResult.Error)?.message
                )
            }
        }
    }

    private fun processHealthAlerts(content: AiAnalysisContent): List<String> {
        val alerts = mutableListOf<String>()
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
