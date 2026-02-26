package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.AdminProduct
import com.example.halalyticscompose.Data.Model.DashboardStats
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    // We access ApiService directly or via Repository. For consistency with MainViewModel pattern:
    // private val apiService: ApiService = com.example.halalyticscompose.Data.Network.ApiConfig.apiService
) : ViewModel() {

    // Helper for simple Hilt injection if ApiService isn't provided directly
    // Ideally use DI, but falling back to singleton if that's the pattern used
    private val apiService = com.example.halalyticscompose.Data.Network.ApiConfig.apiService

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats: StateFlow<DashboardStats?> = _dashboardStats.asStateFlow()

    private val _pendingProducts = MutableStateFlow<List<AdminProduct>>(emptyList())
    val pendingProducts: StateFlow<List<AdminProduct>> = _pendingProducts.asStateFlow()

    private val _actionResult = MutableStateFlow<String?>(null)
    val actionResult: StateFlow<String?> = _actionResult.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = sessionManager.getBearerToken()
            if (token != null) {
                try {
                    val response = apiService.getDashboardStats(token)
                    if (response.success) {
                        _dashboardStats.value = response.data
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load stats: ${e.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun loadPendingProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = sessionManager.getBearerToken()
            if (token != null) {
                try {
                    val response = apiService.getPendingProducts(token)
                    if (response.success) {
                        _pendingProducts.value = response.data
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load products: ${e.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun approveProduct(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = sessionManager.getBearerToken()
            if (token != null) {
                try {
                    val response = apiService.approveProduct(token, productId)
                    if (response.success) {
                        _actionResult.value = "Product Approved!"
                        // Remove from local list
                        _pendingProducts.value = _pendingProducts.value.filter { it.idProduct != productId }
                        // Refresh stats
                        loadDashboardData()
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Approval failed: ${e.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun rejectProduct(productId: Int, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = sessionManager.getBearerToken()
            if (token != null) {
                try {
                    val response = apiService.rejectProduct(token, productId, mapOf("reason" to reason))
                    if (response.success) {
                        _actionResult.value = "Product Rejected"
                        _pendingProducts.value = _pendingProducts.value.filter { it.idProduct != productId }
                        loadDashboardData()
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Rejection failed: ${e.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun clearMessage() {
        _errorMessage.value = null
        _actionResult.value = null
    }
}
