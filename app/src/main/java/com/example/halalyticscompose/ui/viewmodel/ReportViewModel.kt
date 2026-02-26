package com.example.halalyticscompose.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.ReportResponse
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reportSuccess = MutableStateFlow<Boolean?>(null)
    val reportSuccess: StateFlow<Boolean?> = _reportSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuspiciousResult = MutableStateFlow(false)
    val isSuspiciousResult: StateFlow<Boolean> = _isSuspiciousResult

    fun submitReport(
        productId: Int,
        reason: String,
        details: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _reportSuccess.value = null
            _isSuspiciousResult.value = false

            try {
                val token = sessionManager.getAuthToken() ?: ""
                
                val productIdBody = productId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val reasonBody = reason.toRequestBody("text/plain".toMediaTypeOrNull())
                val detailsBody = details?.toRequestBody("text/plain".toMediaTypeOrNull())
                
                var imagePart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = getFileFromUri(uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("evidence_image", file.name, requestFile)
                    }
                }

                val response = apiService.submitReport(
                    bearer = "Bearer $token",
                    productId = productIdBody,
                    reason = reasonBody,
                    laporan = detailsBody,
                    evidenceImage = imagePart
                )

                _reportSuccess.value = true
                _isSuspiciousResult.value = response.isSuspicious ?: false
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengirim laporan: ${e.message}"
                _reportSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_report_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    fun resetState() {
        _reportSuccess.value = null
        _errorMessage.value = null
        _isSuspiciousResult.value = false
    }
}
