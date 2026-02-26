package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.*
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _comparisonQueue = MutableStateFlow<List<String>>(emptyList())
    val comparisonQueue: StateFlow<List<String>> = _comparisonQueue

    private val _comparisonResult = MutableStateFlow<ComparisonData?>(null)
    val comparisonResult: StateFlow<ComparisonData?> = _comparisonResult

    private val _standardizedProducts = MutableStateFlow<List<StandardizedProduct>>(emptyList())
    val standardizedProducts: StateFlow<List<StandardizedProduct>> = _standardizedProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun addToCompare(barcode: String) {
        if (!_comparisonQueue.value.contains(barcode)) {
            if (_comparisonQueue.value.size < 5) {
                _comparisonQueue.value = _comparisonQueue.value + barcode
            } else {
                _errorMessage.value = "Maksimal 5 produk yang bisa dibandingkan."
            }
        }
    }

    fun removeFromCompare(barcode: String) {
        _comparisonQueue.value = _comparisonQueue.value - barcode
    }

    fun clearQueue() {
        _comparisonQueue.value = emptyList()
        _comparisonResult.value = null
        _standardizedProducts.value = emptyList()
    }

    fun startComparison(familyId: Int? = null) {
        if (_comparisonQueue.value.size < 2) {
            _errorMessage.value = "Pilih minimal 2 produk untuk dibandingkan."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _comparisonResult.value = null
            
            try {
                val token = sessionManager.getAuthToken() ?: ""
                val request = ComparisonRequest(
                    barcodes = _comparisonQueue.value,
                    familyId = familyId
                )
                
                val response = apiService.compareProducts("Bearer $token", request)
                if (response.success && response.data != null) {
                    _comparisonResult.value = response.data
                    _standardizedProducts.value = response.products ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Gagal menganalisis perbandingan."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
