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

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EncyclopediaViewModel @Inject constructor(
    private val ingredientApiService: IngredientApiService
) : ViewModel() {

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

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
