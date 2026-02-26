package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.Product
import com.example.halalyticscompose.data.api.HalalAlternativeResponse
import com.example.halalyticscompose.Data.Repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private var repository: ProductRepository = ProductRepository()

    fun initRepository(dao: com.example.halalyticscompose.Data.Local.Dao.CachedScanResultDao) {
        repository = ProductRepository(cachedDao = dao)
    }

    private val _productState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val productState: StateFlow<ProductUiState> = _productState.asStateFlow()

    private var authToken: String? = null

    fun setToken(token: String) {
        authToken = token
    }

    fun loadProduct(barcode: String) {
        println("🔍 Loading product for barcode: $barcode")
        viewModelScope.launch {
            _productState.value = ProductUiState.Loading
            
            println("📡 Calling repository.getProductWithHalalInfo for barcode: $barcode with token: ${authToken != null}")
            repository.getProductWithHalalInfo(barcode, authToken)
                .onSuccess { product ->
                    println("✅ Product loaded successfully: ${product.name} (barcode: ${product.barcode})")
                    println("📊 Product source: ${product.halalInfo?.source}")
                    _productState.value = ProductUiState.Success(product)
                }
                .onFailure { error ->
                    println("❌ Failed to load product: ${error.message}")
                    _productState.value = ProductUiState.Error(
                        error.message ?: "Unknown error"
                    )
                }
        }
    }

    fun recheckHalalStatus(product: Product) {
        println("🔄 Rechecking halal status for: ${product.name}")
        viewModelScope.launch {
            repository.checkHalalStatus(
                product.barcode,
                product.name,
                product.brand
            ).onSuccess { halalInfo ->
                println("✅ Halal status updated: ${halalInfo.halalStatus}")
                val updatedProduct = product.copy(halalInfo = halalInfo)
                _productState.value = ProductUiState.Success(updatedProduct)
            }
        }
    }
    
    // Tambahkan fungsi untuk refresh data
    fun refreshProduct(barcode: String) {
        println("🔄 Refreshing product data for barcode: $barcode")
        loadProduct(barcode)
    }

    private val _alternativesState = MutableStateFlow<AlternativesUiState>(AlternativesUiState.Initial)
    val alternativesState: StateFlow<AlternativesUiState> = _alternativesState.asStateFlow()

    fun loadAlternatives(barcode: String) {
        println("🤖 Loading AI alternatives for barcode: $barcode")
        viewModelScope.launch {
            _alternativesState.value = AlternativesUiState.Loading
            repository.getProductAlternatives(barcode, authToken)
                .onSuccess { alternatives ->
                    println("✅ AI alternatives loaded successfully")
                    _alternativesState.value = AlternativesUiState.Success(alternatives)
                }
                .onFailure { error ->
                    println("❌ Failed to load alternatives: ${error.message}")
                    _alternativesState.value = AlternativesUiState.Error(
                        error.message ?: "Failed to get alternatives"
                    )
                }
        }
    }
}

sealed class ProductUiState {
    object Loading : ProductUiState()
    data class Success(val product: Product) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

sealed class AlternativesUiState {
    object Initial : AlternativesUiState()
    object Loading : AlternativesUiState()
    data class Success(val data: HalalAlternativeResponse) : AlternativesUiState()
    data class Error(val message: String) : AlternativesUiState()
}
