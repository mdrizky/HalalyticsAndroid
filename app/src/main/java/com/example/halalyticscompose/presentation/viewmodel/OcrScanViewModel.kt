package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Local.Dao.UserHealthProfileDao
import com.example.halalyticscompose.Data.Local.Entities.UserHealthProfileEntity
import com.example.halalyticscompose.Data.Model.OcrScanResultRequest
import com.example.halalyticscompose.data.ocr.DetectedIngredient
import com.example.halalyticscompose.data.ocr.IngredientMatcher
import com.example.halalyticscompose.repository.OcrRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OcrScanUiState(
    val isScanning: Boolean = true,
    val isSyncing: Boolean = false,
    val rawText: String = "",
    val detectedIngredients: List<DetectedIngredient> = emptyList(),
    val maxSeverity: Int = 0,
    val showAlert: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class OcrScanViewModel @Inject constructor(
    private val repository: OcrRepository,
    private val matcher: IngredientMatcher,
    private val userHealthProfileDao: UserHealthProfileDao,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrScanUiState())
    val uiState: StateFlow<OcrScanUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null
    private var lastSubmittedFingerprint: String? = null
    private var lastSubmittedAt: Long = 0L

    init {
        viewModelScope.launch {
            seedProfileIfNeeded()
            syncIngredients()
        }
    }

    fun syncIngredients() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.update { it.copy(isSyncing = false, error = "Sesi login tidak ditemukan.") }
                return@launch
            }

            repository.syncIngredients(token)
                .onFailure { error ->
                    _uiState.update { it.copy(isSyncing = false, error = error.message ?: "Gagal sinkron bahan.") }
                }
                .onSuccess {
                    _uiState.update { it.copy(isSyncing = false) }
                }
        }
    }

    fun processText(rawText: String) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(500)

            val cleanedText = rawText.trim()
            if (cleanedText.isBlank()) {
                return@launch
            }

            val userId = sessionManager.getUserId()
            val profile = if (userId > 0) userHealthProfileDao.getProfile(userId) else null
            val detected = matcher.match(cleanedText, profile)
            val maxSeverity = detected.maxOfOrNull { it.matchedIngredient.severity } ?: 0

            _uiState.value = OcrScanUiState(
                isScanning = true,
                isSyncing = _uiState.value.isSyncing,
                rawText = cleanedText,
                detectedIngredients = detected,
                maxSeverity = maxSeverity,
                showAlert = maxSeverity >= 2,
                error = _uiState.value.error,
            )

            saveResultIfNeeded(cleanedText, detected, maxSeverity)
        }
    }

    fun dismissAlert() {
        _uiState.update { it.copy(showAlert = false) }
    }

    private suspend fun seedProfileIfNeeded() {
        val userId = sessionManager.getUserId()
        if (userId <= 0 || userHealthProfileDao.getProfile(userId) != null) {
            return
        }

        val allergies = sessionManager.getAllergy()
            ?.split(",", ";")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()

        val dietConditions = buildList {
            sessionManager.getDietPreference()?.takeIf { it.isNotBlank() }?.let(::add)
            sessionManager.getMedicalHistory()?.takeIf { it.isNotBlank() }?.let(::add)
        }

        userHealthProfileDao.upsert(
            UserHealthProfileEntity(
                userId = userId,
                allergies = allergies,
                dietConditions = dietConditions,
                avoidIngredients = emptyList(),
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    private fun saveResultIfNeeded(
        rawText: String,
        detected: List<DetectedIngredient>,
        maxSeverity: Int,
    ) {
        val token = sessionManager.getAuthToken() ?: return
        val fingerprint = rawText.lowercase().replace(Regex("\\s+"), " ").take(240)
        val now = System.currentTimeMillis()

        if (fingerprint.length < 24) {
            return
        }
        if (fingerprint == lastSubmittedFingerprint && now - lastSubmittedAt < 15_000) {
            return
        }

        lastSubmittedFingerprint = fingerprint
        lastSubmittedAt = now

        viewModelScope.launch {
            repository.saveResultToServer(
                token = token,
                request = OcrScanResultRequest(
                    productName = null,
                    rawText = rawText,
                    detectedHaram = detected.map { it.matchedIngredient.name },
                    severity = maxSeverity.takeIf { it > 0 },
                ),
            )
        }
    }
}
