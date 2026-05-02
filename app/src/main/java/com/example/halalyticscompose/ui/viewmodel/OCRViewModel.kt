package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.network.ApiConfig
// import com.example.halalyticscompose.data.api.ProductSubmission - REMOVED
// import com.example.halalyticscompose.data.api.ProductResponse - REMOVED
import com.example.halalyticscompose.data.model.ApiResponse
import com.example.halalyticscompose.data.model.OCRProductData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.halalyticscompose.data.api.ProductSubmission
import com.example.halalyticscompose.data.api.OCRProductResponse

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.api.OCRProductApiService

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val apiService: ApiService,
    private val ocrProductApiService: OCRProductApiService
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submissionResult = MutableStateFlow<OCRProductData?>(null)
    val submissionResult: StateFlow<OCRProductData?> = _submissionResult.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // History and Statistics State
    private val _ocrHistory = MutableStateFlow<List<OCRProductData>>(emptyList())
    val ocrHistory: StateFlow<List<OCRProductData>> = _ocrHistory.asStateFlow()

    private val _ocrStatistics = MutableStateFlow<com.example.halalyticscompose.data.model.OCRStatisticsData?>(null)
    val ocrStatistics: StateFlow<com.example.halalyticscompose.data.model.OCRStatisticsData?> = _ocrStatistics.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    fun clearResults() {
        _submissionResult.value = null
        _error.value = null
    }

    fun submitOCR(
        token: String,
        frontImage: java.io.File,
        backImage: java.io.File,
        ocrText: String?,
        familyMemberId: Int?,
        language: String,
        onSuccess: (OCRProductData) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _error.value = null
            try {
                // Prepare Multipart Body
                val frontPart = okhttp3.MultipartBody.Part.createFormData(
                    "front_image",
                    frontImage.name,
                    frontImage.asRequestBody("image/*".toMediaTypeOrNull())
                )

                val backPart = okhttp3.MultipartBody.Part.createFormData(
                    "back_image",
                    backImage.name,
                    backImage.asRequestBody("image/*".toMediaTypeOrNull())
                )

                val textPart = ocrText?.toRequestBody("text/plain".toMediaTypeOrNull())
                val familyPart = familyMemberId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val langPart = language.toRequestBody("text/plain".toMediaTypeOrNull())

                // Call API
                val response = apiService.submitOCR(
                    bearer = "Bearer $token",
                    frontImage = frontPart,
                    backImage = backPart,
                    ocrText = textPart,
                    familyMemberId = familyPart,
                    language = langPart
                )

                if (response.success) {
                    val result = response.data
                    if (result != null) {
                        _submissionResult.value = result
                        onSuccess(result)
                    } else {
                        val msg = "Gagal mendapatkan data respon"
                        _error.value = msg
                        onError(msg)
                    }
                } else {
                    val msg = response.message ?: "Gagal submit OCR"
                    _error.value = msg
                    onError(msg)
                }
            } catch (e: Exception) {
                Log.e("OCRViewModel", "Submit failed", e)
                val msg = "Terjadi kesalahan: ${e.message}"
                _error.value = msg
                onError(msg)
            } finally {
                _isSubmitting.value = false
            }
        }

    }

    fun loadHistory(token: String, userId: Int) {
        viewModelScope.launch {
            _isLoadingHistory.value = true
            try {
                val response = apiService.getUserOCRHistory("Bearer $token")
                if (response.success) {
                    _ocrHistory.value = response.data ?: emptyList()
                } else {
                    Log.e("OCRViewModel", "Failed to load history: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("OCRViewModel", "Error loading history", e)
            } finally {
                _isLoadingHistory.value = false
            }
        }
    }

    fun loadStatistics(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getOCRStatistics("Bearer $token")
                if (response.success) {
                    _ocrStatistics.value = response.data
                }
            } catch (e: Exception) {
                Log.e("OCRViewModel", "Error loading statistics", e)
            }
        }
    }

    fun submitProduct(
        token: String,
        barcode: String,
        name: String,
        brand: String,
        ingredients: String,
        userId: String,
        onSuccess: (OCRProductResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val submission = ProductSubmission(
                    barcode = barcode,
                    name = name,
                    brand = brand,
                    ingredients = ingredients,
                    user_id = userId
                )
                
                val response = ocrProductApiService.submitProduct("Bearer $token", submission)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        onSuccess(data)
                    } else {
                        onError("Data produk kosong")
                    }
                } else {
                    onError(response.body()?.message ?: "Gagal submit produk")
                }
            } catch (e: Exception) {
                Log.e("OCRViewModel", "Submit Product Error", e)
                onError(e.message ?: "Terjadi kesalahan jaringan")
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
