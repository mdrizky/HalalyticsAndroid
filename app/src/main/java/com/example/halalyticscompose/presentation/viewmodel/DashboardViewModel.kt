package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.DailyMissionData
import com.example.halalyticscompose.repository.DashboardRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isCompletingMission: Boolean = false,
    val missionData: DailyMissionData? = null,
    val userName: String? = null,
    val error: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            userName = sessionManager.getFullName() ?: sessionManager.getUsername(),
        ),
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Sesi login tidak ditemukan.") }
                return@launch
            }

            repository.getDailyMission(token)
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            missionData = data,
                            userName = sessionManager.getFullName() ?: sessionManager.getUsername(),
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.message ?: "Gagal memuat dashboard.")
                    }
                }
        }
    }

    fun completeMission(missionId: String, onCompleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.update { it.copy(error = "Sesi login tidak ditemukan.") }
                return@launch
            }

            _uiState.update { it.copy(isCompletingMission = true, error = null) }
            repository.completeMission(token, missionId)
                .onSuccess {
                    _uiState.update { it.copy(isCompletingMission = false) }
                    loadDashboard()
                    onCompleted?.invoke()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isCompletingMission = false,
                            error = throwable.message ?: "Gagal menyelesaikan misi.",
                        )
                    }
                }
        }
    }
}
