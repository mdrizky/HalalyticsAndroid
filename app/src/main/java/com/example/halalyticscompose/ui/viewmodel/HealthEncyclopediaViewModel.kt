package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.HealthEncyclopedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthEncyclopediaViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _items = MutableStateFlow<List<HealthEncyclopedia>>(emptyList())
    val items: StateFlow<List<HealthEncyclopedia>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedItem = MutableStateFlow<com.example.halalyticscompose.Data.Model.HealthEncyclopedia?>(null)
    val selectedItem: StateFlow<com.example.halalyticscompose.Data.Model.HealthEncyclopedia?> = _selectedItem

    fun fetchEncyclopedia(type: String? = null, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getHealthEncyclopedia(type, search)
                if (response.isSuccessful) {
                    _items.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = "Failed to load encyclopedia data."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getHealthEncyclopediaById(id)
                if (response.isSuccessful) {
                    _selectedItem.value = response.body()?.data
                } else {
                    _error.value = "Item not found."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
