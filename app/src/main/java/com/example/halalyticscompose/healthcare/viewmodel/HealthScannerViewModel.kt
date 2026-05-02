package com.example.halalyticscompose.healthcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.healthcare.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import java.util.*

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.halalyticscompose.repository.MedicalRepository
import com.example.halalyticscompose.utils.SessionManager

@HiltViewModel
class HealthScannerViewModel @Inject constructor(
    private val medicalRepository: MedicalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _healthProfile = MutableStateFlow<HealthProfile?>(null)
    val healthProfile = _healthProfile.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _analysisResult = MutableStateFlow<HealthAnalysis?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    fun updateHealthProfile(profile: HealthProfile) {
        viewModelScope.launch {
            _healthProfile.value = profile
            // Save to DataStore/Database here
        }
    }

    fun analyzeProduct(productName: String, ingredients: List<String>) {
        viewModelScope.launch {
            _isProcessing.value = true
            _analysisResult.value = null
            
            try {
                val symptoms = "Saya baru saja menscan produk $productName dengan komposisi: ${ingredients.joinToString(", ")}. Bagaimana dampak kesehatannya bagi saya?"
                
                val analysis = medicalRepository.analyzeSymptomsDirect(
                    symptoms = symptoms,
                    age = sessionManager.getAge(),
                    weight = sessionManager.getWeight(),
                    height = sessionManager.getHeight(),
                    gender = null,
                    allergies = sessionManager.getAllergy(),
                    medicalHistory = sessionManager.getMedicalHistory(),
                    isGlutenFree = sessionManager.isGlutenFree(),
                    hasNutAllergy = sessionManager.hasNutAllergy()
                )
                
                // Map SymptomsAnalysis to HealthAnalysis
                _analysisResult.value = HealthAnalysis(
                    productId = UUID.randomUUID().toString(),
                    productName = productName,
                    overallSafety = if (analysis.severity == "emergency" || analysis.severity == "severe") SafetyLevel.DANGER 
                                    else if (analysis.severity == "moderate") SafetyLevel.CAUTION 
                                    else SafetyLevel.SAFE,
                    warnings = listOf(
                        HealthWarning(
                            title = analysis.condition,
                            message = analysis.recommendation,
                            severity = if (analysis.severity == "emergency") WarningSeverity.HIGH else WarningSeverity.MEDIUM
                        )
                    ),
                    recommendations = analysis.recommended_medicine_details.map { med ->
                        AlternativeProduct(
                            id = UUID.randomUUID().toString(),
                            name = med.name,
                            brand = med.halal_status ?: "N/A",
                            reason = med.function ?: "Recommended by AI"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("HealthScannerVM", "Analysis error", e)
            } finally {
                _isProcessing.value = false
            }
        }
    }
}
