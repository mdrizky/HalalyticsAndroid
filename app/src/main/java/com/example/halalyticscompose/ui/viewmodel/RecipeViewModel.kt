package com.example.halalyticscompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.*
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val isSubstituting: Boolean = false,
    val substitution: RecipeSubstitutionResponse? = null,
    val error: String? = null
)

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: Int) {
        val token = sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.getRecipeDetail("Bearer $token", recipeId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = _uiState.value.copy(
                        recipe = response.body()!!.data,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Gagal memuat resep"
                    )
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "loadRecipe error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun applyHalalSwitch(recipeId: Int) {
        val token = sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubstituting = true)
            try {
                val response = apiService.getHalalSubstitution("Bearer $token", recipeId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        substitution = response.body(),
                        isSubstituting = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isSubstituting = false)
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "applyHalalSwitch error", e)
                _uiState.value = _uiState.value.copy(isSubstituting = false)
            }
        }
    }
}
