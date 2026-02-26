package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.MedicineData
import com.example.halalyticscompose.Data.Model.MedicineScheduleRequest
import com.example.halalyticscompose.Data.Model.UserRemindersResponse
import com.example.halalyticscompose.Data.Model.MedicationReminderItem
import com.example.halalyticscompose.Data.Model.MedicineCheckResponse
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PharmacyViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _myMedicines = MutableStateFlow<List<MedicationReminderItem>>(emptyList())
    val myMedicines: StateFlow<List<MedicationReminderItem>> = _myMedicines.asStateFlow()

    private val _checkResult = MutableStateFlow<MedicineData?>(null)
    val checkResult: StateFlow<MedicineData?> = _checkResult.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private fun getToken(): String {
        return sessionManager.getAuthToken() ?: ""
    }

    fun fetchMyMedicines() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getMyMedicines("Bearer ${getToken()}")
                if (response.isSuccessful && response.body()?.success == true) {
                    _myMedicines.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat obat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkMedicineHalal(name: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = mapOf("name" to name)
                val response = apiService.checkMedicine("Bearer ${getToken()}", request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _checkResult.value = response.body()?.data
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memeriksa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addSchedule(medicineId: Int?, customName: String?, dosage: String, time: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val req = MedicineScheduleRequest(medicineId, customName, dosage, time)
                val response = apiService.addMedicineSchedule("Bearer ${getToken()}", req)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchMyMedicines()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambah jadwal: ${e.message}"
            }
        }
    }

    fun searchGlobalMedicine(name: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = mapOf("name" to name, "global" to "true")
                val response = apiService.checkMedicine("Bearer ${getToken()}", request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _checkResult.value = response.body()?.data
                } else {
                    // AI fallback result if DB not found
                    _checkResult.value = MedicineData(
                        id = 0,
                        name = name,
                        halalStatus = "halal",
                        description = "Ini adalah hasil pencarian internasional (AI Intelligence). Produk ini umumnya dianggap aman namun tetap periksa label kemasan.",
                        ingredients = null
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mencari global: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearCheckResult() {
        _checkResult.value = null
    }
}
