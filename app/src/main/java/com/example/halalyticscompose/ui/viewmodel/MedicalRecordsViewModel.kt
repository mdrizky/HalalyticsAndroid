package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.MedicalRecordData
import com.example.halalyticscompose.data.model.MedicalRecordRequest
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MedicalRecordsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    private fun parseApiError(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return runCatching { JSONObject(raw).optString("message").takeIf { it.isNotBlank() } }.getOrNull()
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _records = MutableStateFlow<List<MedicalRecordData>>(emptyList())
    val records: StateFlow<List<MedicalRecordData>> = _records.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _error.value = null
                val userIdPattern = sessionManager.getUserId()
                if (userIdPattern <= 0) {
                    _error.value = "Sesi berakhir, silakan login kembali"
                    return@launch
                }
                val token = sessionManager.getBearerToken()
                if (token.isNullOrBlank()) {
                    _error.value = "Sesi login tidak valid"
                    return@launch
                }
                
                val response = apiService.getMedicalRecords(token, userIdPattern)
                if (response.isSuccessful && response.body() != null) {
                    _records.value = response.body()!!.data
                } else {
                    _error.value = parseApiError(response.errorBody()?.string())
                        ?: "Gagal memuat rekam medis (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Koneksi bermasalah: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addRecord(request: MedicalRecordRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _error.value = null
                val token = sessionManager.getBearerToken()
                if (token.isNullOrBlank()) {
                    _error.value = "Sesi login tidak valid"
                    return@launch
                }
                val response = apiService.addMedicalRecord(token, request)
                if (response.isSuccessful && response.body() != null) {
                    loadRecords() // Refresh
                } else {
                    _error.value = parseApiError(response.errorBody()?.string())
                        ?: "Gagal menyimpan rekam medis (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Koneksi bermasalah: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
