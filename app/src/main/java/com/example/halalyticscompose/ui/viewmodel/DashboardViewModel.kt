package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.model.DailyMissionData
import com.example.halalyticscompose.repository.DashboardRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val missionData: DailyMissionData? = null,
    val userName: String? = null,
    val isCompletingMission: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        val token = sessionManager.getAuthToken() ?: return
        val userName = sessionManager.getFullName()
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, userName = userName, error = null)
            val result = dashboardRepository.getDailyMission(token)
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(isLoading = false, missionData = data)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = exception.localizedMessage)
            }
        }
    }

    fun completeMission(missionId: String) {
        val token = sessionManager.getAuthToken() ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompletingMission = true)
            val result = dashboardRepository.completeMission(token, missionId)
            result.onSuccess {
                loadDashboard() // Refresh data
                _uiState.value = _uiState.value.copy(isCompletingMission = false)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(isCompletingMission = false, error = exception.localizedMessage)
            }
        }
    }
}
