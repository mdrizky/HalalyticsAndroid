package com.example.halalyticscompose.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.remote.ScanData
import com.example.halalyticscompose.data.remote.UserData
import com.example.halalyticscompose.data.repository.SyncResult
import com.example.halalyticscompose.data.repository.MobileSyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling mobile synchronization operations
 */
class MobileSyncViewModel(
    private val syncRepository: MobileSyncRepository,
    private val tokenProvider: () -> String,
    private val currentUserId: () -> Int
) : ViewModel() {
    
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    private val _products = MutableStateFlow<List<AdminProduct>>(emptyList())
    val products: StateFlow<List<AdminProduct>> = _products.asStateFlow()
    
    private val _categories = MutableStateFlow<List<AdminCategory>>(emptyList())
    val categories: StateFlow<List<AdminCategory>> = _categories.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Sync scan data to admin system
     */
    fun syncScans(scans: List<ScanData>) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            _errorMessage.value = null
            
            val userId = currentUserId()
            syncRepository.syncScans(userId, scans)
                .onSuccess { result ->
                    _syncState.value = SyncState.Success(result)
                }
                .onFailure { error ->
                    _syncState.value = SyncState.Error(error.message ?: "Unknown error occurred")
                    _errorMessage.value = error.message
                }
        }
    }
    
    /**
     * Sync user data to admin system
     */
    fun syncUsers(users: List<UserData>) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            _errorMessage.value = null
            
            syncRepository.syncUsers(users)
                .onSuccess { result ->
                    _syncState.value = SyncState.Success(result)
                }
                .onFailure { error ->
                    _syncState.value = SyncState.Error(error.message ?: "Unknown error occurred")
                    _errorMessage.value = error.message
                }
        }
    }
    
    /**
     * Load products from admin system
     */
    fun loadProducts(
        search: String? = null,
        categoryId: Int? = null,
        status: String? = null
    ) {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            _errorMessage.value = null
            
            syncRepository.getProducts(search, categoryId, status)
                .onSuccess { products ->
                    _products.value = products
                    _syncState.value = SyncState.Idle
                }
                .onFailure { error ->
                    _syncState.value = SyncState.Error(error.message ?: "Failed to load products")
                    _errorMessage.value = error.message
                }
        }
    }
    
    /**
     * Load categories from admin system
     */
    fun loadCategories() {
        viewModelScope.launch {
            _syncState.value = SyncState.Loading
            _errorMessage.value = null
            
            syncRepository.getCategories()
                .onSuccess { categories ->
                    _categories.value = categories
                    _syncState.value = SyncState.Idle
                }
                .onFailure { error ->
                    _syncState.value = SyncState.Error(error.message ?: "Failed to load categories")
                    _errorMessage.value = error.message
                }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Reset sync state to idle
     */
    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }
}

/**
 * Sync state for UI
 */
sealed class SyncState {
    object Idle : SyncState()
    object Loading : SyncState()
    object Syncing : SyncState()
    data class Success(val result: SyncResult) : SyncState()
    data class Error(val message: String) : SyncState()
}

// Import required data classes
typealias AdminProduct = com.example.halalyticscompose.data.remote.AdminProduct
typealias AdminCategory = com.example.halalyticscompose.data.remote.AdminCategory
