package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Model.Product
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.utils.SessionManager
import com.example.halalyticscompose.Data.Model.SymptomsAnalysisResponse
import com.example.halalyticscompose.Data.Model.MedicineSearchResponse
import com.example.halalyticscompose.Data.Model.MedicationReminderResponse
import com.example.halalyticscompose.Data.Model.UserRemindersResponse
import com.example.halalyticscompose.Data.Model.NextDoseResponse
import com.example.halalyticscompose.Data.Model.GenericResponse
import com.example.halalyticscompose.Data.Model.SafeScheduleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log
import org.json.JSONObject
import retrofit2.HttpException

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    private fun parseApiErrorMessage(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return runCatching { JSONObject(raw).optString("message").takeIf { it.isNotBlank() } }.getOrNull()
    }

    private val _medicines = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.MedicineData>>(emptyList())
    val medicines: StateFlow<List<com.example.halalyticscompose.Data.Model.MedicineData>> = _medicines.asStateFlow()

    private val _symptomsAnalysis = MutableStateFlow<com.example.halalyticscompose.Data.Model.SymptomsAnalysis?>(null)
    val symptomsAnalysis: StateFlow<com.example.halalyticscompose.Data.Model.SymptomsAnalysis?> = _symptomsAnalysis.asStateFlow()

    private val _recommendedMedicines = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.MedicineData>>(emptyList())
    val recommendedMedicines: StateFlow<List<com.example.halalyticscompose.Data.Model.MedicineData>> = _recommendedMedicines.asStateFlow()

    private val _reminders = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.MedicationReminderItem>>(emptyList())
    val reminders: StateFlow<List<com.example.halalyticscompose.Data.Model.MedicationReminderItem>> = _reminders.asStateFlow()

    private val _nextDoses = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.NextDose>>(emptyList())
    val nextDoses: StateFlow<List<com.example.halalyticscompose.Data.Model.NextDose>> = _nextDoses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<com.example.halalyticscompose.Data.Model.MedicineData?>(null)
    val selectedMedicine: StateFlow<com.example.halalyticscompose.Data.Model.MedicineData?> = _selectedMedicine.asStateFlow()

    private val _safeSchedule = MutableStateFlow<SafeScheduleData?>(null)
    val safeSchedule: StateFlow<SafeScheduleData?> = _safeSchedule.asStateFlow()

    private val _personalRiskScore = MutableStateFlow<com.example.halalyticscompose.Data.Model.PersonalRiskScoreResponse?>(null)
    val personalRiskScore: StateFlow<com.example.halalyticscompose.Data.Model.PersonalRiskScoreResponse?> = _personalRiskScore.asStateFlow()

    private val _drugFoodConflict = MutableStateFlow<com.example.halalyticscompose.Data.Model.DrugFoodConflictData?>(null)
    val drugFoodConflict: StateFlow<com.example.halalyticscompose.Data.Model.DrugFoodConflictData?> = _drugFoodConflict.asStateFlow()

    private val _isRiskLoading = MutableStateFlow(false)
    val isRiskLoading: StateFlow<Boolean> = _isRiskLoading.asStateFlow()

    private val _isConflictLoading = MutableStateFlow(false)
    val isConflictLoading: StateFlow<Boolean> = _isConflictLoading.asStateFlow()

    // AI Symptom Analysis
    fun analyzeSymptoms(symptoms: String, familyId: Int? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val userId = sessionManager.getUserId()
                
                if (userId == 0) {
                    _errorMessage.value = "Sesi berakhir, silakan login kembali"
                    return@launch
                }

                val response = apiService.analyzeSymptoms(token, symptoms, userId.toString(), familyId)

                if (response.body()?.success == true) {
                    Log.d("MedicineVM", "Symptom analysis success: ${response.body()?.symptoms_analysis?.condition}")
                    _symptomsAnalysis.value = response.body()?.symptoms_analysis
                    _recommendedMedicines.value = response.body()?.recommended_medicines ?: emptyList()
                } else {
                    val msg = response.body()?.message ?: "Failed to analyze symptoms"
                    Log.e("MedicineVM", "Symptom analysis failure: $msg")
                    _errorMessage.value = msg
                }
            } catch (e: Exception) {
                Log.e("MedicineVM", "Symptom analysis error: ${e.message}", e)
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Search Medicine (Hybrid Local + International)
    fun searchMedicine(query: String, searchType: String = "name") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val response = apiService.searchMedicine(token, query, searchType)

                if (response.isSuccessful && response.body()?.success == true) {
                    val count = response.body()?.data?.size ?: 0
                    Log.d("MedicineVM", "Search success: Found $count medicines for '$query'")
                    _medicines.value = response.body()?.data ?: emptyList()
                } else {
                    val apiMsg = parseApiErrorMessage(response.errorBody()?.string())
                    val msg = apiMsg ?: response.body()?.message ?: "Failed to search medicine (${response.code()})"
                    Log.e("MedicineVM", "Search failure: $msg")
                    _errorMessage.value = msg
                }
            } catch (e: Exception) {
                Log.e("MedicineVM", "Search error: ${e.message}", e)
                _errorMessage.value = when (e) {
                    is HttpException -> "Layanan obat tidak tersedia (${e.code()})"
                    else -> "Error: ${e.message}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Create Medicine Reminder
    fun createReminder(
        medicineId: Int,
        symptoms: String? = null,
        frequencyPerDay: Int,
        startDate: String,
        endDate: String? = null,
        notes: String? = null,
        familyId: Int? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val userId = sessionManager.getUserId()
                if (userId == 0) {
                    _errorMessage.value = "Sesi berakhir, silakan login kembali"
                    return@launch
                }

                val response = apiService.createMedicineReminder(
                    bearer = token,
                    userId = userId.toString(),
                    medicineId = medicineId,
                    symptoms = symptoms,
                    frequencyPerDay = frequencyPerDay,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    familyId = familyId
                )

                if (response.body()?.success == true) {
                    _errorMessage.value = "Reminder created successfully"
                    loadUserReminders() // Refresh reminders
                } else {
                    _errorMessage.value = response.body()?.message ?: "Failed to create reminder"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load User Reminders
    fun loadUserReminders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val userId = sessionManager.getUserId()
                if (userId == 0) return@launch

                val response = apiService.getUserMedicineReminders(token, userId.toString())

                if (response.body()?.success == true) {
                    _reminders.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = response.body()?.message ?: "Failed to load reminders"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mark Medicine as Taken
    fun markAsTaken(reminderId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val userId = sessionManager.getUserId()
                if (userId == 0) return@launch

                val response = apiService.markMedicineAsTaken(token, reminderId, userId.toString())

                if (response.body()?.success == true) {
                    _errorMessage.value = "Medicine marked as taken"
                    loadUserReminders() // Refresh reminders
                } else {
                    _errorMessage.value = response.body()?.message ?: "Failed to mark as taken"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get Next Doses
    fun getNextDoses() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val userId = sessionManager.getUserId()
                if (userId == 0) return@launch

                val response = apiService.getNextDose(token, userId.toString())

                if (response.body()?.success == true) {
                    _nextDoses.value = (response.body()?.next_doses as? List<com.example.halalyticscompose.Data.Model.NextDose>) ?: emptyList()
                } else {
                    _errorMessage.value = response.body()?.message ?: "Failed to get next doses"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSymptomsAnalysis() {
        _symptomsAnalysis.value = null
        _recommendedMedicines.value = emptyList()
    }
    
    // Delete Reminder
    fun deleteReminder(reminderId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val response = apiService.deleteAdvancedReminder(token, reminderId)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    _reminders.value = _reminders.value.filter { it.id != reminderId }
                    Log.d("MedicineVM", "Reminder $reminderId deleted successfully")
                } else {
                    _errorMessage.value = response.body()?.message ?: "Gagal menghapus pengingat"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus pengingat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicineDetail(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val response = apiService.getMedicineDetail(token, id)
                if (response.isSuccessful && response.body()?.success == true) {
                    _selectedMedicine.value = response.body()?.data?.firstOrNull()
                    Log.i("MedicineVM", "Selected medicine: ${_selectedMedicine.value?.name}")
                } else {
                    val apiMsg = parseApiErrorMessage(response.errorBody()?.string())
                    _errorMessage.value = apiMsg ?: response.body()?.message ?: "Gagal memuat detail obat (${response.code()})"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateSafeSchedule(
        medicineId: Int? = null,
        medicineName: String? = null,
        frequencyPerDay: Int = 3,
        mealRelation: String = "after_meal"
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val body = mutableMapOf<String, Any>(
                    "frequency_per_day" to frequencyPerDay,
                    "meal_relation" to mealRelation
                )
                if (medicineId != null && medicineId > 0) {
                    body["medicine_id"] = medicineId
                } else if (!medicineName.isNullOrBlank()) {
                    body["medicine_name"] = medicineName
                }

                val response = apiService.generateSafeSchedule(token, body)
                if (response.isSuccessful && response.body()?.success == true) {
                    _safeSchedule.value = response.body()?.data
                } else {
                    val apiMsg = parseApiErrorMessage(response.errorBody()?.string())
                    _errorMessage.value = apiMsg ?: response.body()?.message ?: "Gagal membuat jadwal aman (${response.code()})"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Terjadi kesalahan koneksi"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSafeSchedule() {
        _safeSchedule.value = null
    }

    fun fetchPersonalRiskScore(date: String? = null) {
        viewModelScope.launch {
            try {
                _isRiskLoading.value = true
                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val response = apiService.getPersonalRiskScore(token, date)
                if (response.success) {
                    _personalRiskScore.value = response
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Gagal memuat risk score"
            } finally {
                _isRiskLoading.value = false
            }
        }
    }

    fun checkDrugFoodConflict(
        medicineName: String? = null,
        medicineId: Int? = null,
        lookbackMinutes: Int = 180
    ) {
        viewModelScope.launch {
            try {
                _isConflictLoading.value = true
                val token = sessionManager.getBearerToken() ?: sessionManager.getAuthToken()?.let { "Bearer $it" } ?: ""
                val response = apiService.checkDrugFoodConflict(
                    bearer = token,
                    medicineName = medicineName,
                    medicineId = medicineId,
                    lookbackMinutes = lookbackMinutes
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _drugFoodConflict.value = response.body()?.data
                } else {
                    val apiMsg = parseApiErrorMessage(response.errorBody()?.string())
                    _errorMessage.value = apiMsg ?: response.body()?.message ?: "Gagal cek konflik obat-makanan (${response.code()})"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Gagal cek konflik obat-makanan"
            } finally {
                _isConflictLoading.value = false
            }
        }
    }
}

// ✅ Manual Factory for non-Hilt usage
class MedicineViewModelFactory(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MedicineViewModel(apiService, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
