package com.example.halalyticscompose.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.ProductItem
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import org.json.JSONObject

/**
 * ViewModel for handling External Product API (OpenFoodFacts via Laravel backend)
 */
class ProductExternalViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "ProductExternalVM"
    }

    private fun buildHttpError(response: Response<*>, fallback: String): String {
        val raw = runCatching { response.errorBody()?.string() }.getOrNull()
        val parsedMessage = runCatching {
            if (raw.isNullOrBlank()) null else JSONObject(raw).optString("message").takeIf { it.isNotBlank() }
        }.getOrNull()
        return if (!parsedMessage.isNullOrBlank()) {
            "$fallback (${response.code()}): $parsedMessage"
        } else {
            "$fallback (${response.code()}): ${response.message().ifBlank { "Unknown error" }}"
        }
    }

    private fun buildThrowableError(prefix: String, e: Exception): String {
        return when (e) {
            is IOException -> "$prefix: API internasional sedang lambat/tidak dapat diakses. Coba lagi beberapa saat."
            else -> "$prefix: Terjadi kesalahan saat memproses pencarian."
        }
    }
    
    // ========================================
    // STATE FLOWS
    // ========================================
    
    // Search Results
    private val _searchResults = MutableStateFlow<List<ProductItem>>(emptyList())
    val searchResults: StateFlow<List<ProductItem>> = _searchResults.asStateFlow()
    
    // Product Detail
    private val _productDetail = MutableStateFlow<ProductItem?>(null)
    val productDetail: StateFlow<ProductItem?> = _productDetail.asStateFlow()
    
    // Total count from search
    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()
    
    // Loading States
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()
    
    // Error States
    private val _searchError = MutableStateFlow("")
    val searchError: StateFlow<String> = _searchError.asStateFlow()
    
    private val _detailError = MutableStateFlow("")
    val detailError: StateFlow<String> = _detailError.asStateFlow()
    
    // Current filter
    private val _currentFilter = MutableStateFlow("All")
    val currentFilter: StateFlow<String> = _currentFilter.asStateFlow()
    
    // ========================================
    // SEARCH FUNCTIONS
    // ========================================
    
    /**
     * Search all products by query
     */
    fun searchProducts(query: String, pageSize: Int = 20, page: Int = 1) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = ""
            _currentFilter.value = "All"
            
            Log.d(TAG, "🔍 Searching products: $query")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .searchProducts(query = query, pageSize = pageSize, page = page)
                
                Log.d(TAG, "📡 Response code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        val products = content.products
                        _searchResults.value = products
                        _totalCount.value = content.count
                        Log.d(TAG, "✅ Found ${products.size} products (total: ${content.count})")
                    } else {
                        _searchError.value = body?.message ?: "No products found"
                        _searchResults.value = emptyList()
                        _totalCount.value = 0
                        Log.d(TAG, "⚠️ ${body?.message}")
                    }
                } else {
                    val errorMsg = buildHttpError(response, "Server error")
                    _searchError.value = errorMsg
                    _searchResults.value = emptyList()
                    _totalCount.value = 0
                    Log.e(TAG, "❌ $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = buildThrowableError("Network error", e)
                _searchError.value = errorMsg
                _searchResults.value = emptyList()
                _totalCount.value = 0
                Log.e(TAG, "❌ $errorMsg", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    /**
     * Search halal products
     */
    fun searchHalalProducts(query: String = "", pageSize: Int = 20, page: Int = 1) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = ""
            _currentFilter.value = "Halal"
            
            Log.d(TAG, "🕌 Searching halal products: $query")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .searchHalalProducts(query = query, pageSize = pageSize, page = page)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        _searchResults.value = content.products
                        _totalCount.value = content.count
                        Log.d(TAG, "✅ Found ${content.products.size} halal products")
                    } else {
                        _searchError.value = body?.message ?: "No halal products found"
                        _searchResults.value = emptyList()
                        _totalCount.value = 0
                    }
                } else {
                    _searchError.value = buildHttpError(response, "Server error")
                    _searchResults.value = emptyList()
                    _totalCount.value = 0
                }
            } catch (e: Exception) {
                _searchError.value = buildThrowableError("Network error", e)
                _searchResults.value = emptyList()
                _totalCount.value = 0
                Log.e(TAG, "Error searching halal products", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    /**
     * Search vegetarian products
     */
    fun searchVegetarianProducts(query: String = "", pageSize: Int = 20, page: Int = 1) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = ""
            _currentFilter.value = "Vegetarian"
            
            Log.d(TAG, "🌱 Searching vegetarian products: $query")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .searchVegetarianProducts(query = query, pageSize = pageSize, page = page)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        _searchResults.value = content.products
                        _totalCount.value = content.count
                        Log.d(TAG, "✅ Found ${content.products.size} vegetarian products")
                    } else {
                        _searchError.value = body?.message ?: "No vegetarian products found"
                        _searchResults.value = emptyList()
                        _totalCount.value = 0
                    }
                } else {
                    _searchError.value = buildHttpError(response, "Server error")
                    _searchResults.value = emptyList()
                    _totalCount.value = 0
                }
            } catch (e: Exception) {
                _searchError.value = buildThrowableError("Network error", e)
                _searchResults.value = emptyList()
                _totalCount.value = 0
                Log.e(TAG, "Error searching vegetarian products", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    /**
     * Search vegan products
     */
    fun searchVeganProducts(query: String = "", pageSize: Int = 20, page: Int = 1) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = ""
            _currentFilter.value = "Vegan"
            
            Log.d(TAG, "🥬 Searching vegan products: $query")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .searchVeganProducts(query = query, pageSize = pageSize, page = page)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        _searchResults.value = content.products
                        _totalCount.value = content.count
                        Log.d(TAG, "✅ Found ${content.products.size} vegan products")
                    } else {
                        _searchError.value = body?.message ?: "No vegan products found"
                        _searchResults.value = emptyList()
                        _totalCount.value = 0
                    }
                } else {
                    _searchError.value = buildHttpError(response, "Server error")
                    _searchResults.value = emptyList()
                    _totalCount.value = 0
                }
            } catch (e: Exception) {
                _searchError.value = buildThrowableError("Network error", e)
                _searchResults.value = emptyList()
                _totalCount.value = 0
                Log.e(TAG, "Error searching vegan products", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    /**
     * Search products by brand
     */
    fun searchByBrand(brand: String, pageSize: Int = 20, page: Int = 1) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = ""
            _currentFilter.value = "Brand: $brand"
            
            Log.d(TAG, "🏷️ Searching by brand: $brand")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .getProductsByBrand(brand = brand, pageSize = pageSize, page = page)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        _searchResults.value = content.products
                        _totalCount.value = content.count
                        Log.d(TAG, "✅ Found ${content.products.size} products from $brand")
                    } else {
                        _searchError.value = body?.message ?: "No products found for this brand"
                        _searchResults.value = emptyList()
                        _totalCount.value = 0
                    }
                } else {
                    _searchError.value = buildHttpError(response, "Server error")
                    _searchResults.value = emptyList()
                    _totalCount.value = 0
                }
            } catch (e: Exception) {
                _searchError.value = buildThrowableError("Network error", e)
                _searchResults.value = emptyList()
                _totalCount.value = 0
                Log.e(TAG, "Error searching by brand", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    /**
     * Search products by category
     */
    fun searchByCategory(category: String, pageSize: Int = 20, page: Int = 1) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = ""
            _currentFilter.value = "Category: $category"
            
            Log.d(TAG, "📁 Searching by category: $category")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .getProductsByCategory(category = category, pageSize = pageSize, page = page)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        _searchResults.value = content.products
                        _totalCount.value = content.count
                        Log.d(TAG, "✅ Found ${content.products.size} products in $category")
                    } else {
                        _searchError.value = body?.message ?: "No products found in this category"
                        _searchResults.value = emptyList()
                        _totalCount.value = 0
                    }
                } else {
                    _searchError.value = buildHttpError(response, "Server error")
                    _searchResults.value = emptyList()
                    _totalCount.value = 0
                }
            } catch (e: Exception) {
                _searchError.value = buildThrowableError("Network error", e)
                _searchResults.value = emptyList()
                _totalCount.value = 0
                Log.e(TAG, "Error searching by category", e)
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    // ========================================
    // PRODUCT DETAIL
    // ========================================
    
    /**
     * Get product detail by barcode
     */
    fun getProductDetail(barcode: String) {
        viewModelScope.launch {
            _isLoadingDetail.value = true
            _detailError.value = ""
            
            Log.d(TAG, "📦 Fetching product detail: $barcode")
            
            try {
                val response = ApiConfig.getExternalApiService()
                    .getProductDetail(barcode)
                
                Log.d(TAG, "📡 Detail response code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    val content = body?.content
                    
                    if (body?.responseCode == 200 && content != null) {
                        _productDetail.value = content
                        Log.d(TAG, "✅ Product found: ${content.getDisplayName()}")
                    } else {
                        _detailError.value = body?.message ?: "Product not found"
                        _productDetail.value = null
                        Log.d(TAG, "⚠️ ${body?.message}")
                    }
                } else {
                    val errorMsg = buildHttpError(response, "Server error")
                    _detailError.value = errorMsg
                    _productDetail.value = null
                    Log.e(TAG, "❌ $errorMsg")
                }
            } catch (e: Exception) {
                val errorMsg = buildThrowableError("Network error", e)
                _detailError.value = errorMsg
                _productDetail.value = null
                Log.e(TAG, "❌ $errorMsg", e)
            } finally {
                _isLoadingDetail.value = false
            }
        }
    }
    
    // ========================================
    // UTILITY FUNCTIONS
    // ========================================
    
    /**
     * Clear search results
     */
    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _searchError.value = ""
        _totalCount.value = 0
        Log.d(TAG, "🧹 Search results cleared")
    }
    
    /**
     * Clear product detail
     */
    fun clearProductDetail() {
        _productDetail.value = null
        _detailError.value = ""
        Log.d(TAG, "🧹 Product detail cleared")
    }
    
    /**
     * Set filter and perform search
     */
    fun applyFilter(filter: String, query: String = "") {
        when (filter) {
            "All" -> searchProducts(query)
            "Halal" -> searchHalalProducts(query)
            "Vegetarian" -> searchVegetarianProducts(query)
            "Vegan" -> searchVeganProducts(query)
            else -> searchProducts(query)
        }
    }
}
