package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.database.ProductRepository
import com.example.halalyticscompose.data.database.ProductHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    
    private val _recentProducts = MutableStateFlow<List<ProductHistoryEntity>>(emptyList())
    val recentProducts: StateFlow<List<ProductHistoryEntity>> = _recentProducts.asStateFlow()
    
    private val _statistics = MutableStateFlow<Map<String, Int>>(emptyMap())
    val statistics: StateFlow<Map<String, Int>> = _statistics.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadRecentProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getRecentProducts(5).collect { products ->
                    _recentProducts.value = products
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                repository.getStatistics().collect { stats ->
                    _statistics.value = stats
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun addToHistory(product: ProductHistoryEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            loadRecentProducts() // Refresh recent products
            loadStatistics() // Refresh statistics
        }
    }
    
    fun toggleFavorite(barcode: String) {
        viewModelScope.launch {
            repository.toggleFavorite(barcode)
            loadRecentProducts() // Refresh recent products
        }
    }
}
