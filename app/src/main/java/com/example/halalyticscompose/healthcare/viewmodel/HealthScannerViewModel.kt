package com.example.halalyticscompose.healthcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.healthcare.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class HealthScannerViewModel : ViewModel() {
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
            
            // Simulation of Gemini AI analysis
            // val analysis = geminiClient.analyze(ingredients, _healthProfile.value)
            
            kotlinx.coroutines.delay(3000)
            
            _analysisResult.value = HealthAnalysis(
                productId = UUID.randomUUID().toString(),
                productName = productName,
                overallSafety = if (ingredients.any { it.contains("Sugar", ignoreCase = true) }) SafetyLevel.CAUTION else SafetyLevel.SAFE,
                warnings = listOf(
                    HealthWarning(
                        title = "High Sugar Content",
                        message = "This product contains high levels of sugar, which may affect your diabetes management.",
                        severity = WarningSeverity.MEDIUM
                    )
                ),
                recommendations = listOf(
                    AlternativeProduct(
                        id = "alt1",
                        name = "Stevia-sweetened Dark Chocolate",
                        brand = "HealthChoice",
                        reason = "Uses natural sweeteners with low glycemic index."
                    )
                )
            )
            
            _isProcessing.value = false
        }
    }
}
