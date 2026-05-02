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
class IngredientDetailViewModel @Inject constructor(
    private val apiService: IngredientApiService
) : ViewModel() {

    private val _ingredient = MutableStateFlow<Ingredient?>(null)
    val ingredient: StateFlow<Ingredient?> = _ingredient.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchIngredientDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getIngredientDetail(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _ingredient.value = response.body()?.data
                } else {
                    _error.value = "Failed to load ingredient details"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("IngredientDetailVM", "Fetch error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
