package com.example.halalyticscompose.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.DailyNutritionLog
import com.example.halalyticscompose.Data.Model.NutritionDashboardData
import com.example.halalyticscompose.Data.Model.NutritionHistoryItem
import com.example.halalyticscompose.repository.NutritionRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NutritionUiState(
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val dailySummary: NutritionDashboardData? = null,
    val history: List<NutritionHistoryItem> = emptyList(),
    val lastAnalyzedLog: DailyNutritionLog? = null,
    val error: String? = null,
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val repository: NutritionRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    init {
        loadToday()
    }

    fun loadToday() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Sesi login tidak ditemukan.") }
                return@launch
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.getDailyLog(token, today)
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dailySummary = summary,
                            error = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() ?: return@launch
            repository.getHistory(token)
                .onSuccess { history ->
                    _uiState.update { it.copy(history = history, error = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun logMeal(imageUri: Uri, mealType: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, error = null) }
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.update { it.copy(isAnalyzing = false, error = "Sesi login tidak ditemukan.") }
                return@launch
            }

            val imageFile = imageUri.toTempFile(context)
            repository.logMeal(token, imageFile, mealType)
                .onSuccess { log ->
                    _uiState.update {
                        it.copy(
                            isAnalyzing = false,
                            lastAnalyzedLog = log,
                            error = null,
                        )
                    }
                    loadToday()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isAnalyzing = false, error = error.message) }
                }

            imageFile.delete()
        }
    }

    private fun Uri.toTempFile(context: Context): File {
        val file = File.createTempFile("meal_", ".jpg", context.cacheDir)
        context.contentResolver.openInputStream(this)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file
    }
}
