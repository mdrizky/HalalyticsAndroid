package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.database.ProductHistoryEntity
import com.example.halalyticscompose.data.database.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val apiService: com.example.halalyticscompose.Data.API.ApiService,
    private val sessionManager: com.example.halalyticscompose.utils.SessionManager
) : ViewModel() {
    
    init {
        syncHistoryFromApi()
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val allProducts = repository.getAllProducts()
    
    fun getRecentProducts(limit: Int = 5) = repository.getRecentProducts(limit)
    
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
    
    fun addToHistory(product: ProductHistoryEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }
    
    fun cleanOldProducts() {
        viewModelScope.launch {
            repository.cleanOldProducts()
        }
    }

    private fun syncHistoryFromApi() {
        viewModelScope.launch {
            val token = sessionManager.getBearerToken() ?: return@launch
            // Don't set global isLoading to true to avoid blocking UI if local data exists
            // Just sync in background
            try {
                val response = apiService.getRealtimeScanHistory(token)
                if (response.success) {
                    val serverItems = response.data?.data ?: emptyList()
                    println("🔄 Syncing history... Found ${serverItems.size} items")
                    serverItems.forEach { historyItem ->
                        historyItem.barcode?.let { barcode ->
                            val entity = com.example.halalyticscompose.data.database.ProductHistoryEntity(
                                barcode = barcode,
                                name = historyItem.productName ?: "Unknown Product",
                                status = historyItem.halalStatus ?: "unknown",
                                image = historyItem.productImage,
                                timestamp = System.currentTimeMillis(), // Or parse createdAt
                                isFavorite = false, // We don't know favorite status here easily unless we join checks, potentially overwrite if already exists
                                // However, insertProduct uses OnConflictStrategy.REPLACE. 
                                // This is risky if we overwrite isFavorite=true with false.
                                // We should check if it exists or use ignore/update only specific fields if possible?
                                // Repository.insertProduct uses REPLACE.
                                // Better check if product exists first.
                                isSynced = true
                            )
                            
                            // Safe Insert: Preserve isFavorite status if exists
                            val existing = repository.getProductByBarcode(barcode)
                            val finalEntity = if (existing != null) {
                                entity.copy(isFavorite = existing.isFavorite) 
                            } else {
                                entity
                            }
                            
                            repository.insertProduct(finalEntity)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
