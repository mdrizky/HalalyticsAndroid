package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.ScanHistoryDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScanHistoryDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _detail = MutableStateFlow<ScanHistoryDetail?>(null)
    val detail: StateFlow<ScanHistoryDetail?> = _detail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadDetail(token: String, id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = apiService.getScanHistoryDetail("Bearer $token", id)
                if (response.success) {
                    _detail.value = response.data
                } else {
                    _error.value = response.message ?: "Detail riwayat tidak ditemukan."
                }
            } catch (_: Exception) {
                _error.value = "Gagal memuat detail riwayat. Coba lagi."
            } finally {
                _loading.value = false
            }
        }
    }
}
