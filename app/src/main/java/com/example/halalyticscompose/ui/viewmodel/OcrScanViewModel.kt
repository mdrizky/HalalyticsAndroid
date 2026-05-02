package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.local.Dao.UserHealthProfileDao
import com.example.halalyticscompose.data.ocr.DetectedIngredient
import com.example.halalyticscompose.data.ocr.IngredientMatcher
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OcrScanUiState(
    val isSyncing: Boolean = false,
    val detectedIngredients: List<DetectedIngredient> = emptyList(),
    val maxSeverity: Int = 0,
    val rawText: String = "",
    val showAlert: Boolean = false
)

@HiltViewModel
class OcrScanViewModel @Inject constructor(
    private val ingredientMatcher: IngredientMatcher,
    private val userHealthProfileDao: UserHealthProfileDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrScanUiState())
    val uiState: StateFlow<OcrScanUiState> = _uiState.asStateFlow()

    fun processText(text: String) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            val profile = userHealthProfileDao.getProfile(userId)
            
            val detected = ingredientMatcher.match(text, profile)
            val maxSeverity = detected.maxOfOrNull { it.matchedIngredient.severity } ?: 0
            
            _uiState.value = _uiState.value.copy(
                rawText = text,
                detectedIngredients = detected,
                maxSeverity = maxSeverity,
                showAlert = maxSeverity >= 2
            )
        }
    }

    fun dismissAlert() {
        _uiState.value = _uiState.value.copy(showAlert = false)
    }
}
