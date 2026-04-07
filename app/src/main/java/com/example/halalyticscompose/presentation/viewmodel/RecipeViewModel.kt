package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.Recipe
import com.example.halalyticscompose.Data.Model.RecipeSubstitutionResponse
import com.example.halalyticscompose.repository.RecipeRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val substitution: RecipeSubstitutionResponse? = null,
    val error: String? = null,
    val isSubstituting: Boolean = false
)

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            repository.getRecipeDetail(token, id)
                .onSuccess { recipe ->
                    _uiState.update { it.copy(isLoading = false, recipe = recipe, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun applyHalalSwitch(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubstituting = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            repository.getHalalSubstitution(token, id)
                .onSuccess { sub ->
                    _uiState.update { it.copy(isSubstituting = false, substitution = sub, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSubstituting = false, error = e.message) }
                }
        }
    }
}
