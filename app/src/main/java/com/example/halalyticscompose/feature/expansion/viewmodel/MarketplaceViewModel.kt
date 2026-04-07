package com.example.halalyticscompose.feature.expansion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.feature.expansion.model.HealthFacility
import com.example.halalyticscompose.feature.expansion.model.MarketplaceMerchant
import com.example.halalyticscompose.feature.expansion.model.MarketplaceProduct
import com.example.halalyticscompose.feature.expansion.network.ExpansionApiService
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val apiService: ExpansionApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _merchants = MutableStateFlow<List<MarketplaceMerchant>>(emptyList())
    val merchants: StateFlow<List<MarketplaceMerchant>> = _merchants.asStateFlow()

    private val _healthFacilities = MutableStateFlow<List<HealthFacility>>(emptyList())
    val healthFacilities: StateFlow<List<HealthFacility>> = _healthFacilities.asStateFlow()

    private val _products = MutableStateFlow<List<MarketplaceProduct>>(emptyList())
    val products: StateFlow<List<MarketplaceProduct>> = _products.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setTab(index: Int) {
        _activeTab.value = index
    }

    fun loadNearbyMerchants(lat: Double, lng: Double, type: String? = null) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getNearbyMerchants(bearer, lat, lng, type = type)
                if (response.isSuccessful) {
                    _merchants.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Gagal memuat merchant"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadHealthFacilities(lat: Double, lng: Double) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getNearbyHealthFacilities(bearer, lat, lng)
                if (response.isSuccessful) {
                    _healthFacilities.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Gagal memuat fasilitas kesehatan"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProducts(search: String? = null, category: String? = null) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getProducts(bearer, category = category, search = search)
                if (response.isSuccessful) {
                    _products.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Gagal memuat produk"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
