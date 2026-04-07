package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Local.Entities.HaramIngredientEntity
import com.example.halalyticscompose.Data.Model.OcrScanResultRequest
import com.example.halalyticscompose.repository.OcrRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OcrScanUiState(
    val isLoading: Boolean = false,
    val detectedIngredients: List<HaramIngredientEntity> = emptyList(),
    val rawText: String = "",
    val error: String? = null,
    val isSyncing: Boolean = false
)

@HiltViewModel
class OcrScanViewModel @Inject constructor(
    private val repository: OcrRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrScanUiState())
    val uiState: StateFlow<OcrScanUiState> = _uiState.asStateFlow()

    private val allHaramIngredients = repository.activeIngredients

    init {
        syncIngredients()
    }

    fun syncIngredients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            repository.syncIngredients(token)
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun processText(rawText: String) {
        viewModelScope.launch {
            val detected = mutableListOf<HaramIngredientEntity>()
            val lines = rawText.lines()
            
            // Fetch all active ingredients from local DB
            val ingredients = allHaramIngredients.first()
            
            for (ingredient in ingredients) {
                val patterns = mutableListOf(ingredient.name)
                ingredient.aliases?.split(",")?.forEach { patterns.add(it.trim()) }
                
                for (pattern in patterns) {
                    if (rawText.contains(pattern, ignoreCase = true)) {
                        detected.add(ingredient)
                        break
                    }
                }
            }
            
            _uiState.update { 
                it.copy(
                    detectedIngredients = detected.distinctBy { lang -> lang.id },
                    rawText = rawText
                )
            }
            
            if (detected.isNotEmpty()) {
                saveResult(rawText, detected)
            }
        }
    }

    private fun saveResult(rawText: String, detected: List<HaramIngredientEntity>) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() ?: return@launch
            val maxSeverity = detected.maxOfOrNull { it.severity } ?: 0
            val request = OcrScanResultRequest(
                productName = null, // Can be updated by user later
                rawText = rawText,
                detectedHaram = detected.map { it.name },
                severity = maxSeverity
            )
            repository.saveResultToServer(token, request)
        }
    }
}
