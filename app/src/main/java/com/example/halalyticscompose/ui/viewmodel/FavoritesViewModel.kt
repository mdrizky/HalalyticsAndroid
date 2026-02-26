package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.database.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val apiService: com.example.halalyticscompose.Data.API.ApiService,
    private val sessionManager: com.example.halalyticscompose.utils.SessionManager
) : ViewModel() {
    
    init {
        syncFavoritesFromApi()
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val favoriteProducts = repository.getFavoriteProducts()
    
    fun deleteProduct(barcode: String) {
        viewModelScope.launch {
            repository.deleteProduct(barcode)
        }
    }
    
    fun toggleFavorite(barcode: String) {
        viewModelScope.launch {
            repository.toggleFavorite(barcode)
        }
    }
    
    fun addToFavorites(barcode: String) {
        viewModelScope.launch {
            repository.addToFavorites(barcode)
        }
    }
    
    fun removeFromFavorites(barcode: String) {
        viewModelScope.launch {
            repository.removeFromFavorites(barcode)
        }
    }

    private fun syncFavoritesFromApi() {
        viewModelScope.launch {
            val token = sessionManager.getBearerToken() ?: return@launch
            _isLoading.value = true
            try {
                val response = apiService.getFavorites(token)
                if (response.success) {
                    println("🔄 Syncing favorites... Found ${response.data.size} items")
                    response.data.forEach { favorite ->
                        favorite.barcode?.let { barcode ->
                            val entity = com.example.halalyticscompose.data.database.ProductHistoryEntity(
                                barcode = barcode,
                                name = favorite.productName,
                                status = favorite.halalStatus,
                                image = favorite.productImage,
                                timestamp = System.currentTimeMillis(), // Use current time or parse createdAt if needed
                                isFavorite = true,
                                isSynced = true
                            )
                            repository.insertProduct(entity)
                            // Explicitly set favorite status to true
                            repository.toggleFavorite(barcode) // This toggles, be careful. 
                            // Repository.addToFavorites sets it to true explicitly
                            repository.addToFavorites(barcode)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
