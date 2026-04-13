package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.Recipe
import com.example.halalyticscompose.repository.RecipeRepository
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val activeCategory: String? = null,
    val error: String? = null,
)

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    fun loadRecipes(category: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, activeCategory = category, error = null) }
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Sesi login tidak ditemukan.") }
                return@launch
            }

            repository.getRecipes(token, category)
                .onSuccess { recipes ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipes = recipes,
                            activeCategory = category,
                            error = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, error = throwable.message ?: "Gagal memuat resep.")
                    }
                }
        }
    }
}
