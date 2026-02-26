package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.Ingredient
import com.example.halalyticscompose.data.api.IngredientApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class EncyclopediaViewModel : ViewModel() {
    private val apiService = com.example.halalyticscompose.Data.Network.ApiConfig.apiService // We need to ensure IngredientApiService is accessible

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    // For simplicity, we'll try to use the same ApiConfig since it has a baseUrl.
    // However, EncyclopediaController is in the same Laravel backend.
    // We might need to cast or provide specific service.
    private val ingredientApiService: IngredientApiService by lazy {
        com.example.halalyticscompose.Data.Network.ApiConfig.getIngredientApiService()
    }

    init {
        fetchIngredients()
    }

    fun fetchIngredients(query: String? = null, status: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = ingredientApiService.getIngredients(query, status)
                if (response.isSuccessful) {
                    _ingredients.value = response.body()?.data?.data ?: emptyList()
                } else {
                    _error.value = "Failed to fetch ingredients: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("EncyclopediaViewModel", "Fetch error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchIngredients(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300)
            fetchIngredients(query = if (query.isEmpty()) null else query)
        }
    }
}
