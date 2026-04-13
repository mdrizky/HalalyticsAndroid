package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.ArPOI
import com.example.halalyticscompose.repository.ArRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArUiState(
    val isLoading: Boolean = false,
    val pois: List<ArPOI> = emptyList(),
    val selectedPoi: ArPOI? = null,
    val error: String? = null
)

@HiltViewModel
class ArViewModel @Inject constructor(
    private val repository: ArRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArUiState())
    val uiState: StateFlow<ArUiState> = _uiState.asStateFlow()

    fun loadNearbyPois(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            repository.getNearbyPOIs(token, lat, lng)
                .onSuccess { pois ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pois = pois,
                            selectedPoi = pois.firstOrNull(),
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, pois = emptyList(), selectedPoi = null, error = e.message) }
                }
        }
    }

    fun selectPoi(poi: ArPOI?) {
        _uiState.update { it.copy(selectedPoi = poi) }
    }
}
