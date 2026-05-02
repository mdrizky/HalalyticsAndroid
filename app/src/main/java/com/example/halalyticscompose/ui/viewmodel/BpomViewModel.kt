package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.*
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BpomViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<BpomProduct>>(emptyList())
    val searchResults: StateFlow<List<BpomProduct>> = _searchResults

    private val _selectedProduct = MutableStateFlow<BpomProduct?>(null)
    val selectedProduct: StateFlow<BpomProduct?> = _selectedProduct

    private val _sessionInfo = MutableStateFlow<SessionInfo?>(null)
    val sessionInfo: StateFlow<SessionInfo?> = _sessionInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _searchSource = MutableStateFlow<String?>(null)
    val searchSource: StateFlow<String?> = _searchSource

    private fun getToken(): String {
        return sessionManager.getAuthToken() ?: ""
    }

    private fun formatApiError(prefix: String, throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                val body = throwable.response()?.errorBody()?.string()?.take(220)
                val lowerBody = body?.lowercase().orEmpty()
                if (throwable.code() == 422 && (
                        lowerBody.contains("include_a") ||
                            lowerBody.contains("include_ai") ||
                            lowerBody.contains("must be true or false")
                        )
                ) {
                    return "Permintaan verifikasi BPOM tidak valid. Coba ulang pencarian tanpa karakter khusus."
                }
                if (!body.isNullOrBlank()) {
                    "$prefix (${throwable.code()}): $body"
                } else {
                    "$prefix (${throwable.code()}): Layanan sedang bermasalah. Coba lagi."
                }
            }
            is IOException -> "$prefix: Koneksi internet/API tidak tersedia. Coba lagi."
            else -> "$prefix: ${throwable.message ?: "Terjadi kesalahan tak dikenal"}"
        }
    }

    fun searchBpom(query: String, familyId: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.searchBpom(
                    bearer = "Bearer ${getToken()}",
                    query = query,
                    familyId = familyId,
                    includeAi = false
                )
                if (response.success) {
                    _searchResults.value = response.data ?: emptyList()
                    _sessionInfo.value = response.sessionInfo
                    _searchSource.value = response.source
                    if (_searchResults.value.isEmpty()) {
                        _errorMessage.value = "Data BPOM resmi tidak ditemukan. Coba nomor registrasi BPOM (contoh: NA..., BPOM RI MD...)."
                    }
                } else {
                    _errorMessage.value = response.message ?: "Pencarian gagal"
                }
            } catch (e: Exception) {
                _errorMessage.value = formatApiError("Gagal mencari data BPOM", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkRegistration(code: String, familyId: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.checkBpomRegistration(
                    bearer = "Bearer ${getToken()}",
                    code = code,
                    familyId = familyId,
                    includeAi = false
                )
                if (response.success && response.data != null) {
                    _selectedProduct.value = response.data
                    _sessionInfo.value = response.sessionInfo
                    _searchSource.value = response.source
                } else {
                    _errorMessage.value = response.message ?: "Nomor registrasi tidak ditemukan"
                }
            } catch (e: Exception) {
                _errorMessage.value = formatApiError("Gagal verifikasi BPOM", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun analyzeProduct(productName: String, ingredientsText: String? = null, category: String? = null, familyId: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.analyzeBpomProduct(
                    bearer = "Bearer ${getToken()}",
                    productName = productName,
                    ingredientsText = ingredientsText,
                    category = category,
                    barcode = null,
                    familyId = familyId
                )
                if (response.success && response.data != null) {
                    _selectedProduct.value = response.data
                    _sessionInfo.value = response.sessionInfo
                } else {
                    _errorMessage.value = response.message ?: "Gagal menganalisis produk"
                }
            } catch (e: Exception) {
                _errorMessage.value = formatApiError("Gagal analisis BPOM", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSelection() {
        _selectedProduct.value = null
    }
}
