package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.*
import com.example.halalyticscompose.services.FirebaseRealtimeListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScanHistoryViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _history = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val history: StateFlow<List<ScanHistoryItem>> = _history.asStateFlow()

    private val _stats = MutableStateFlow<ScanStats?>(null)
    val stats: StateFlow<ScanStats?> = _stats.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadHistory(token: String, userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                // API Load Realtime
                val response = apiService.getRealtimeScanHistory("Bearer $token")
                if (response.success) {
                    _history.value = response.data?.data ?: emptyList()
                    _stats.value = response.stats
                } else {
                    _history.value = emptyList()
                    _errorMessage.value = response.message ?: "Riwayat gagal dimuat dari server."
                }

                // Realtime Sync
                startRealtimeListener(userId)
            } catch (e: Exception) {
                Log.e("ScanHistoryViewModel", "Failed to load history", e)
                _history.value = emptyList()
                _errorMessage.value = "Gagal memuat riwayat: ${e.message ?: "Unknown error"}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun startRealtimeListener(userId: Int) {
        val listener = FirebaseRealtimeListener(userId)
        viewModelScope.launch {
            listener.listenToScanHistory().collect { update ->
                // Check if exists
                val currentList = _history.value
                if (currentList.none { it.id == update.id }) {
                    val newItem = ScanHistoryItem(
                        id = update.id,
                        productName = update.product_name,
                        productImage = null, // lightweight update from real-time db
                        barcode = update.barcode, // Ensuring barcode is available for navigation
                        halalStatus = update.halal_status,
                        source = "realtime",
                        scanMethod = "unknown",
                        createdAt = "Just now" // Visual indicator for real-time update
                    )
                    
                    val newList = mutableListOf<ScanHistoryItem>()
                    newList.add(newItem)
                    newList.addAll(currentList)
                    _history.value = newList
                    
                    // Optimistic stats update? Maybe too complex, let's keep it simple
                }
            }
        }
    }

    fun deleteHistory(token: String, historyId: Int) {
        viewModelScope.launch {
            try {
                // Optimistic UI Update
                val currentList = _history.value.filter { it.id != historyId }
                _history.value = currentList
                
                // API Call
                apiService.deleteScanHistory("Bearer $token", historyId)
            } catch (e: Exception) {
                // Rollback if needed
                Log.e("ScanHistoryViewModel", "Failed to delete history", e)
                _errorMessage.value = "Gagal hapus riwayat: ${e.message ?: "Unknown error"}"
            }
        }
    }
}
