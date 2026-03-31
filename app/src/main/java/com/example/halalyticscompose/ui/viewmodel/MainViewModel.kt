package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.halalyticscompose.Data.Model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.util.Log
import com.example.halalyticscompose.Data.Network.ApiConfig
import com.example.halalyticscompose.Data.Network.ApiErrorHandler
import com.example.halalyticscompose.Data.API.ApiService
import retrofit2.Response

import com.example.halalyticscompose.Data.Local.Dao.ConsumptionDao
import com.example.halalyticscompose.Data.Local.Entities.Consumption
import java.text.SimpleDateFormat
import java.util.*
import com.example.halalyticscompose.ai.GeminiAnalyzer
import com.example.halalyticscompose.ai.AiAnalysisResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.halalyticscompose.utils.SessionManager
import com.example.halalyticscompose.utils.PreferenceManager
import com.example.halalyticscompose.utils.ImageUtils

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val preferenceManager: PreferenceManager,
    private val consumptionDao: ConsumptionDao
) : ViewModel() {

    // Removed manual setters as we now use Hilt Injection

    private val geminiAnalyzer = GeminiAnalyzer()
    private val _geminiResult = MutableStateFlow<AiAnalysisResult?>(null)
    val geminiResult: StateFlow<AiAnalysisResult?> = _geminiResult.asStateFlow()
    
    // Login State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _userData = MutableStateFlow<com.example.halalyticscompose.Data.Model.User?>(null)
    val userData: StateFlow<com.example.halalyticscompose.Data.Model.User?> = _userData.asStateFlow()
    
    // Public method to update current user
    fun updateCurrentUser(name: String) {
        _currentUser.value = name
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Stats
    private val _totalScans = MutableStateFlow(0)
    val totalScans: StateFlow<Int> = _totalScans.asStateFlow()
    
    private val _halalProducts = MutableStateFlow(0)
    val halalProducts: StateFlow<Int> = _halalProducts.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    // Scan History
    private val _scanHistory = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val scanHistory: StateFlow<List<ScanHistoryItem>> = _scanHistory.asStateFlow()

    // Banners
    private val _banners = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.Banner>>(emptyList())
    val banners: StateFlow<List<com.example.halalyticscompose.Data.Model.Banner>> = _banners.asStateFlow()
    private val _bannersLastUpdated = MutableStateFlow<Long?>(null)
    val bannersLastUpdated: StateFlow<Long?> = _bannersLastUpdated.asStateFlow()

    // Offline Mode State
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    // Theme State
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _appLanguage = MutableStateFlow("id")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    // Health Stats
    private val _bmi = MutableStateFlow("0.0")
    val bmi: StateFlow<String> = _bmi.asStateFlow()
    
    private val _activityLevel = MutableStateFlow("Sedang")
    val activityLevel: StateFlow<String> = _activityLevel.asStateFlow()

    // Premium States
    private val _weeklyStats = MutableStateFlow<List<WeeklyStatItem>>(emptyList())
    val weeklyStats: StateFlow<List<WeeklyStatItem>> = _weeklyStats.asStateFlow()

    private val _dailyIntake = MutableStateFlow<DailyIntakeResponse?>(null)
    val dailyIntake: StateFlow<DailyIntakeResponse?> = _dailyIntake.asStateFlow()

    private val _userAllergies = MutableStateFlow<String>("")
    val userAllergies: StateFlow<String> = _userAllergies.asStateFlow()

    private val _userMedicalHistory = MutableStateFlow<String>("")
    val userMedicalHistory: StateFlow<String> = _userMedicalHistory.asStateFlow()

    private val _recommendations = MutableStateFlow<List<ProductInfo>>(emptyList())
    val recommendations: StateFlow<List<ProductInfo>> = _recommendations.asStateFlow()

    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

    private val _pendingContributionCount = MutableStateFlow(0)
    val pendingContributionCount: StateFlow<Int> = _pendingContributionCount.asStateFlow()

    private val _approvedContributionCount = MutableStateFlow(0)
    val approvedContributionCount: StateFlow<Int> = _approvedContributionCount.asStateFlow()

    private val _lastRealtimeSyncAt = MutableStateFlow<Long?>(null)
    val lastRealtimeSyncAt: StateFlow<Long?> = _lastRealtimeSyncAt.asStateFlow()

    // --- AI Insight & Health Score ---
    private val _aiDailyInsight = MutableStateFlow<String?>(null)
    val aiDailyInsight: StateFlow<String?> = _aiDailyInsight.asStateFlow()

    private val _healthScoreData = MutableStateFlow<HealthScoreData?>(null)
    val healthScoreData: StateFlow<HealthScoreData?> = _healthScoreData.asStateFlow()

    private val _isNotifEnabled = MutableStateFlow(true)
    val isNotifEnabled: StateFlow<Boolean> = _isNotifEnabled.asStateFlow()

    private val _privacyModeEnabled = MutableStateFlow(true)
    val privacyModeEnabled: StateFlow<Boolean> = _privacyModeEnabled.asStateFlow()

    private val _biometricLockEnabled = MutableStateFlow(false)
    val biometricLockEnabled: StateFlow<Boolean> = _biometricLockEnabled.asStateFlow()

    private val _autoLogoutEnabled = MutableStateFlow(false)
    val autoLogoutEnabled: StateFlow<Boolean> = _autoLogoutEnabled.asStateFlow()

    private val _autoLogoutMinutes = MutableStateFlow(5)
    val autoLogoutMinutes: StateFlow<Int> = _autoLogoutMinutes.asStateFlow()

    private val _userWatchlist = MutableStateFlow<List<String>>(emptyList())
    val userWatchlist: StateFlow<List<String>> = _userWatchlist.asStateFlow()

    private var realtimeHistoryJob: kotlinx.coroutines.Job? = null
    private var realtimeStatusJob: kotlinx.coroutines.Job? = null

    val dailyHealthScore: StateFlow<Int> = _scanHistory.map { history ->
        var score = 50 // Base score
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        
        history.filter { it.createdAt != null && it.createdAt.startsWith(todayStr) }.forEach { item ->
            when (item.halalStatus?.lowercase()) {
                "halal" -> score += 10
                "haram" -> score -= 15
                "syubhat" -> score -= 5
            }
        }
        score.coerceIn(0, 100)
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), 50)

    // AI MEAL SCANNER STATE
    private val _mealAnalysisState = MutableStateFlow(com.example.halalyticscompose.Data.Model.AiAnalysisState())
    val mealAnalysisState: StateFlow<com.example.halalyticscompose.Data.Model.AiAnalysisState> = _mealAnalysisState.asStateFlow()

    // FAMILY BOX STATE
    private val _familyProfiles = MutableStateFlow<List<FamilyProfile>>(emptyList())
    val familyProfiles: StateFlow<List<FamilyProfile>> = _familyProfiles.asStateFlow()

    private val _selectedFamilyProfile = MutableStateFlow<FamilyProfile?>(null)
    val selectedFamilyProfile: StateFlow<FamilyProfile?> = _selectedFamilyProfile.asStateFlow()

    // Reactive Health Context
    val currentHealthAllergies: StateFlow<String> = kotlinx.coroutines.flow.combine(
        _userAllergies,
        _selectedFamilyProfile
    ) { userAll, family ->
        family?.allergies ?: userAll
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "")

    val currentHealthMedicalHistory: StateFlow<String> = kotlinx.coroutines.flow.combine(
        _userMedicalHistory,
        _selectedFamilyProfile
    ) { userMed, family ->
        family?.medicalHistory ?: userMed
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "")

    val currentHealthName: StateFlow<String> = kotlinx.coroutines.flow.combine(
        _currentUser,
        _selectedFamilyProfile
    ) { userName, family ->
        family?.name ?: (userName ?: "Saya")
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "Saya")

    private val _remoteAiResult = MutableStateFlow<AiAnalysisResponse?>(null)
    val remoteAiResult: StateFlow<AiAnalysisResponse?> = _remoteAiResult.asStateFlow()

    fun analyzeMealImage(imageFile: java.io.File) {
        viewModelScope.launch {
            _mealAnalysisState.value = com.example.halalyticscompose.Data.Model.AiAnalysisState(isLoading = true)
            try {
                // 1. Ensure upload is not too heavy (prevent long freeze/crash on large images)
                val optimizedFile = try {
                    ImageUtils.reduceFileImage(imageFile)
                } catch (_: Exception) {
                    imageFile
                }
                val bytes = optimizedFile.readBytes()
                val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

                // 2. Call API
                val token = _accessToken.value
                if (token == null) {
                    _mealAnalysisState.value = com.example.halalyticscompose.Data.Model.AiAnalysisState(error = "User not logged in")
                    return@launch
                }

                val currentFamilyId = _selectedFamilyProfile.value?.id
                val request = com.example.halalyticscompose.Data.Model.MealAnalysisRequest(
                    image = base64,
                    familyId = currentFamilyId
                )
                val response = withTimeout(25000) {
                    apiService.analyzeMeal("Bearer $token", request)
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data as? com.example.halalyticscompose.Data.Model.MealData
                    if (data != null) {
                        _mealAnalysisState.value = com.example.halalyticscompose.Data.Model.AiAnalysisState(data = data)
                    } else {
                        _mealAnalysisState.value = com.example.halalyticscompose.Data.Model.AiAnalysisState(error = "Empty data received")
                    }
                } else {
                    val errorMsg = response.body()?.message ?: "Analysis failed: ${response.code()}"
                    _mealAnalysisState.value = com.example.halalyticscompose.Data.Model.AiAnalysisState(error = errorMsg)
                }

            } catch (e: Exception) {
                val message = if (e is kotlinx.coroutines.TimeoutCancellationException) {
                    "Analisis meal timeout. Coba ulang dengan foto lebih jelas atau koneksi lebih stabil."
                } else {
                    "Error: ${e.message}"
                }
                _mealAnalysisState.value = com.example.halalyticscompose.Data.Model.AiAnalysisState(error = message)
                e.printStackTrace()
            }
        }
    }

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        viewModelScope.launch {
            preferenceManager.setDarkMode(newValue)
            sessionManager.saveDarkMode(newValue)
        }
    }

    fun setNotifEnabled(isEnabled: Boolean) {
        _isNotifEnabled.value = isEnabled
        viewModelScope.launch {
            preferenceManager.setNotifEnabled(isEnabled)
            sessionManager.saveNotifEnabled(isEnabled)
        }
    }

    fun setWatchlist(watchlist: String) {
        viewModelScope.launch {
            preferenceManager.setWatchlist(watchlist)
        }
    }

    fun setPrivacyModeEnabled(isEnabled: Boolean) {
        _privacyModeEnabled.value = isEnabled
        viewModelScope.launch {
            preferenceManager.setPrivacyModeEnabled(isEnabled)
            sessionManager.savePrivacyModeEnabled(isEnabled)
        }
    }

    fun setBiometricLockEnabled(isEnabled: Boolean) {
        _biometricLockEnabled.value = isEnabled
        viewModelScope.launch {
            preferenceManager.setBiometricLockEnabled(isEnabled)
            sessionManager.saveBiometricLockEnabled(isEnabled)
        }
    }

    fun setAutoLogoutEnabled(isEnabled: Boolean) {
        _autoLogoutEnabled.value = isEnabled
        viewModelScope.launch {
            preferenceManager.setAutoLogoutEnabled(isEnabled)
            sessionManager.saveAutoLogoutEnabled(isEnabled)
        }
    }

    fun setAutoLogoutMinutes(minutes: Int) {
        _autoLogoutMinutes.value = minutes
        viewModelScope.launch {
            preferenceManager.setAutoLogoutMinutes(minutes)
            sessionManager.saveAutoLogoutMinutes(minutes)
        }
    }

    fun setAppLanguage(languageCode: String) {
        val normalized = normalizeLanguage(languageCode)
        _appLanguage.value = normalized
        viewModelScope.launch {
            preferenceManager.setLanguage(normalized)
            sessionManager.saveLanguage(normalized)
            try {
                _accessToken.value?.let { token ->
                    apiService.updateProfile(
                        bearer = "Bearer $token",
                        language = normalized
                    )
                }
            } catch (e: Exception) {
                Log.w("MainViewModel", "Failed syncing language: ${e.message}")
            }
        }
    }

    init {
        // Initial state
        _isLoading.value = false
        
        // Initialize from injected dependencies
        updateDailyIntakeFromLocal()
        
        viewModelScope.launch {
            preferenceManager.isDarkMode.collect { 
                _isDarkMode.value = it 
            }
        }
        viewModelScope.launch {
            preferenceManager.appLanguage.collect {
                _appLanguage.value = normalizeLanguage(it)
            }
        }
        viewModelScope.launch {
            preferenceManager.userAllergies.collect { 
                _userAllergies.value = it 
            }
        }
        viewModelScope.launch {
            preferenceManager.isNotifEnabled.collect { 
                _isNotifEnabled.value = it 
            }
        }
        viewModelScope.launch {
            preferenceManager.userWatchlist.collect { 
                _userWatchlist.value = if (it.isNotBlank()) it.split(",").map { item -> item.trim() } else emptyList()
            }
        }
        viewModelScope.launch {
            preferenceManager.privacyModeEnabled.collect {
                _privacyModeEnabled.value = it
            }
        }
        viewModelScope.launch {
            preferenceManager.biometricLockEnabled.collect {
                _biometricLockEnabled.value = it
            }
        }
        viewModelScope.launch {
            preferenceManager.autoLogoutEnabled.collect {
                _autoLogoutEnabled.value = it
            }
        }
        viewModelScope.launch {
            preferenceManager.autoLogoutMinutes.collect {
                _autoLogoutMinutes.value = it
            }
        }
        
        if (sessionManager.isLoggedIn()) {
            _isLoggedIn.value = true
            _accessToken.value = sessionManager.getAuthToken()
            _currentUser.value = sessionManager.getFullName() ?: sessionManager.getUsername()
            _isAdmin.value = sessionManager.getRole()?.equals("admin", ignoreCase = true) == true
            _userData.value = com.example.halalyticscompose.Data.Model.User(
                idUser = sessionManager.getUserId(),
                username = sessionManager.getUsername() ?: "",
                fullName = sessionManager.getFullName(),
                email = sessionManager.getEmail() ?: "",
                phone = sessionManager.getPhone(),
                bloodType = sessionManager.getBloodType(),
                allergy = sessionManager.getAllergy(),
                medicalHistory = sessionManager.getMedicalHistory(),
                role = sessionManager.getRole() ?: "user",
                active = true,
                image = sessionManager.getImageUrl(),
                goal = sessionManager.getGoal(),
                dietPreference = sessionManager.getDietPreference(),
                activityLevel = sessionManager.getActivityLevel(),
                address = null,
                language = normalizeLanguage(sessionManager.getLanguage()),
                age = sessionManager.getAge(),
                height = sessionManager.getHeight()?.toDouble(),
                weight = sessionManager.getWeight()?.toDouble(),
                bmi = sessionManager.getBmi()?.toDouble(),
                notifEnabled = sessionManager.isNotifEnabled(),
                darkMode = sessionManager.isDarkMode(),
                bio = null
            )
            _totalScans.value = sessionManager.getTotalScans()
            _halalProducts.value = sessionManager.getHalalCount()
            _isDarkMode.value = sessionManager.isDarkMode()
            _appLanguage.value = normalizeLanguage(sessionManager.getLanguage())
            _privacyModeEnabled.value = sessionManager.isPrivacyModeEnabled()
            _biometricLockEnabled.value = sessionManager.isBiometricLockEnabled()
            _autoLogoutEnabled.value = sessionManager.isAutoLogoutEnabled()
            _autoLogoutMinutes.value = sessionManager.getAutoLogoutMinutes()
            _bmi.value = "%.1f".format(sessionManager.getBmi() ?: 0f)
            _activityLevel.value = sessionManager.getActivityLevel() ?: "Sedang"
            
            // Register FCM token
            registerFcmToken()

            // Refresh data from API
            sessionManager.getAuthToken()?.let { token ->
                refreshData()
            }
        }
    }
    
    fun getApiService() = apiService

    private fun normalizeLanguage(languageCode: String?): String {
        return when (languageCode?.lowercase()) {
            "id", "en", "ms", "ar" -> languageCode.lowercase()
            else -> "id"
        }
    }

    
    fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val loginResponse = apiService.login(username, password)
                
                if (loginResponse.isSuccess) {
                    val token = loginResponse.token // Alias for access_token
                    val user = loginResponse.user // Alias for content
                    
                    if (token != null && user != null) {
                        _accessToken.value = token
                        _isLoggedIn.value = true
                        _currentUser.value = user.full_name ?: user.username
                        _isAdmin.value = user.role.equals("admin", ignoreCase = true)
                        _userData.value = com.example.halalyticscompose.Data.Model.User(
                            idUser = user.id_user,
                            username = user.username,
                            fullName = user.full_name,
                            email = user.email,
                            phone = user.phone,
                            bloodType = user.blood_type,
                            allergy = user.allergy,
                            medicalHistory = user.medical_history,
                            role = user.role,
                            active = user.active,
                            image = user.image,
                            goal = user.goal,
                            dietPreference = user.diet_preference,
                            activityLevel = user.activity_level,
                            address = user.address,
                            language = user.language,
                            age = user.age,
                            height = user.height?.toDouble(),
                            weight = user.weight?.toDouble(),
                            bmi = user.bmi?.toDouble(),
                            notifEnabled = user.notif_enabled ?: true,
                            darkMode = user.dark_mode ?: false,
                            bio = user.bio
                        )
                        
                        // Save to session with complete data
                        sessionManager?.let { manager ->
                            manager.saveCompleteSession(
                                token = token,
                                userId = user.id_user,
                                username = user.username,
                                fullName = user.full_name,
                                email = user.email,
                                role = user.role,
                                phone = user.phone,
                                bloodType = user.blood_type,
                                allergy = user.allergy,
                                medicalHistory = user.medical_history,
                                imageUrl = user.image
                            )
                            manager.saveHealthProfile(
                                age = user.age,
                                height = user.height,
                                weight = user.weight,
                                bmi = user.bmi,
                                activityLevel = user.activity_level,
                                dietPreference = user.diet_preference,
                                goal = user.goal
                            )
                            manager.saveLanguage(user.language ?: manager.getLanguage())
                        }

                        // Fetch real stats
                        fetchUserStats(token)
                        fetchDailyIntake(token)
                        fetchScanHistory(token)
                        registerFcmToken()
                        fetchAiDailyInsight()
                        fetchHealthScore()
                        
                        onSuccess()
                    } else {
                        onError("Data user tidak lengkap")
                    }
                } else {
                    // Handle error response from backend
                    val errorMessage = if (loginResponse.responseCode == 401 || loginResponse.errorMessage?.contains("Invalid", ignoreCase = true) == true) {
                        "Username /password salah"
                    } else {
                        loginResponse.errorMessage ?: "Login gagal"
                    }
                    _errorMessage.value = errorMessage
                    onError(errorMessage)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                val errorMessage = ApiErrorHandler.fromThrowable<LoginModel>(e).message
                _errorMessage.value = errorMessage
                onError(errorMessage)
                e.printStackTrace()
            }
        }
    }
    fun refreshData() {
        val token = _accessToken.value ?: sessionManager?.getAuthToken()
        token?.let { t ->
            _accessToken.value = t
            sessionManager?.let { manager ->
                _currentUser.value = manager.getFullName() ?: manager.getUsername()
                _isLoggedIn.value = manager.isLoggedIn()
            }
            fetchUserStats(t)
            fetchScanHistory(t)
            fetchWeeklyStats(t)
            fetchUnreadCount(t)
            fetchDailyIntake(t)
            startRealtimeStatusSync(t)
            fetchAiDailyInsight()
            fetchHealthScore()
        }
        fetchBanners()
    }

    // ==========================================
    // FETCH AI DAILY INSIGHT & HEALTH SCORE
    // ==========================================
    fun fetchAiDailyInsight() {
        val token = _accessToken.value ?: sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            try {
                val response = apiService.getAiDailyInsight("Bearer $token")
                if (response.isSuccessful && response.body()?.status == "success") {
                    _aiDailyInsight.value = response.body()?.insight
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch AI Daily Insight: ${e.message}")
            }
        }
    }

    fun fetchHealthScore() {
        val token = _accessToken.value ?: sessionManager.getAuthToken() ?: return
        viewModelScope.launch {
            try {
                val response = apiService.getHealthScore("Bearer $token")
                if (response.isSuccessful && response.body()?.status == "success") {
                    _healthScoreData.value = response.body()?.data
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch Health Score: ${e.message}")
            }
        }
    }

    private fun fetchUnreadCount(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getUnreadCount("Bearer $token")
                if (response.success) {
                    _unreadNotificationCount.value = response.count
                }
            } catch (e: Exception) {}
        }
    }

    // Search Results State
    private val _searchResults = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.LocalProduct>>(emptyList())
    val searchResults: StateFlow<List<com.example.halalyticscompose.Data.Model.LocalProduct>> = _searchResults.asStateFlow()

    private val _searchCategories = MutableStateFlow<List<com.example.halalyticscompose.Data.Model.Kategori>>(emptyList())
    val searchCategories: StateFlow<List<com.example.halalyticscompose.Data.Model.Kategori>> = _searchCategories.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun performSearch(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            _searchCategories.value = emptyList()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300) // Debounce
            try {
                val response = apiService.searchProducts(query)
                if (response.responseCode == 200) {
                    _searchResults.value = response.content?.data ?: emptyList()
                    // Extract unique categories from results if API doesn't provide them separately
                    _searchCategories.value = response.content?.data?.mapNotNull { it.kategori }?.distinctBy { it.idKategori } ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Search failed: ${e.message}")
            }
        }
    }

    fun fetchBanners() {
        viewModelScope.launch {
            try {
                val response = apiService.getBanners()
                if (response.isSuccessful && response.body()?.success == true) {
                    val normalized = (response.body()?.data ?: emptyList()).map { banner ->
                        val fullUrl = ApiConfig.getFullImageUrl(banner.image)
                        if (fullUrl != null) banner.copy(image = fullUrl) else banner
                    }

                    if (normalized.isEmpty()) {
                        setDummyBanners()
                    } else {
                        _banners.value = normalized
                        _bannersLastUpdated.value = System.currentTimeMillis()
                    }
                } else {
                    Log.e("MainViewModel", "Failed to fetch banners: ${response.code()}")
                    setDummyBanners()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch banners: ${e.message}")
                setDummyBanners()
            }
        }
    }

    fun exportReport(month: String? = null, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _accessToken.value?.let { token ->
                    val response = apiService.exportMonthlyReport("Bearer $token", month)
                    if (response.isSuccessful && response.body()?.success == true) {
                        onResult(response.body()?.reportUrl)
                    } else {
                        onResult(null)
                    }
                } ?: onResult(null)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Export report failed: ${e.message}")
                onResult(null)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun setDummyBanners() {
        if (_banners.value.isEmpty()) {
            _banners.value = listOf(
                com.example.halalyticscompose.Data.Model.Banner(
                    id = 1,
                    title = "Scan Produk Halal",
                    description = "Pastikan semua produk yang Anda konsumsi bersertifikat halal.",
                    image = "https://picsum.photos/seed/halal1/800/400",
                    position = 1
                ),
                com.example.halalyticscompose.Data.Model.Banner(
                    id = 2,
                    title = "AI Health Scanner",
                    description = "Analisis bahan produk secara instan dengan teknologi AI.",
                    image = "https://picsum.photos/seed/health2/800/400",
                    position = 2
                )
            )
            _bannersLastUpdated.value = System.currentTimeMillis()
        }
    }

    private fun fetchUserStats(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserStats("Bearer $token")
                if (response.responseCode == 200) {
                    _totalScans.value = response.content.totalScans
                    _halalProducts.value = response.content.halalScans
                    _currentStreak.value = response.content.streak?.current ?: 0
                }
            } catch (e: Exception) {
                println("Failed to fetch stats: ${e.message}")
            }
        }
    }

    private fun fetchScanHistory(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRealtimeScanHistory("Bearer $token")
                if (response.success) {
                    _scanHistory.value = response.data?.data ?: emptyList()
                }
                
                // Also start realtime sync for HomeScreen and Global state
                val userId = sessionManager?.getUserId() ?: 0
                if (userId > 0) {
                    startRealtimeHistorySync(userId, token)
                }
            } catch (e: Exception) {
                println("Failed to fetch history: ${e.message}")
            }
        }
    }

    private fun startRealtimeHistorySync(userId: Int, token: String) {
        realtimeHistoryJob?.cancel()
        val listener = com.example.halalyticscompose.services.FirebaseRealtimeListener(userId)
        realtimeHistoryJob = viewModelScope.launch {
            listener.listenToScanHistory().collect { update ->
                // Check if item already exists in the current list
                val currentList = _scanHistory.value.toMutableList()
                if (currentList.none { it.id == update.id }) {
                    val newItem = ScanHistoryItem(
                        id = update.id,
                        productName = update.product_name,
                        productImage = null, // Simplified for realtime update
                        barcode = null, 
                        halalStatus = update.halal_status,
                        source = "realtime",
                        scanMethod = "unknown",
                        createdAt = java.time.Instant.now().toString()
                    )
                    
                    currentList.add(0, newItem)
                    _scanHistory.value = currentList.take(20) // Keep reasonable limits for global state
                    
                    // Also update stats optimistically or refresh
                    fetchUserStats(token)
                }
            }
        }
    }

    private fun startRealtimeStatusSync(token: String) {
        realtimeStatusJob?.cancel()
        realtimeStatusJob = viewModelScope.launch {
            while (true) {
                try {
                    fetchUnreadCount(token)
                    val contributionResponse = apiService.getMyContributions("Bearer $token")
                    if (contributionResponse.success) {
                        val data = contributionResponse.data
                        _pendingContributionCount.value = data.count { it.status.equals("pending", ignoreCase = true) }
                        _approvedContributionCount.value = data.count { it.status.equals("approved", ignoreCase = true) }
                    }
                    _lastRealtimeSyncAt.value = System.currentTimeMillis()
                } catch (e: Exception) {
                    Log.w("MainViewModel", "Realtime status sync failed: ${e.message}")
                }
                delay(30000)
            }
        }
    }

    private fun parseTimestamp(dateString: String?): Long {
        if (dateString == null) return System.currentTimeMillis()
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    
    fun register(
        fullName: String,
        username: String,
        email: String,
        password: String,
        phone: String,
        bloodType: String,
        allergy: String,
        medicalHistory: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val response = apiService.register(
                    username = username,
                    email = email,
                    password = password,
                    passwordConfirmation = password,
                    fullName = fullName,
                    phone = phone,
                    bloodType = bloodType,
                    allergy = allergy,
                    medicalHistory = medicalHistory
                )
                
                if (response.status == "success" || response.responseCode == 200) {
                    val token = response.token
                    val user = response.user
                    
                    if (token != null && user != null) {
                        _accessToken.value = token
                        _isLoggedIn.value = true
                        _currentUser.value = user.full_name ?: user.username
                        _isAdmin.value = user.role.equals("admin", ignoreCase = true)
                        
                        // Save to session
                        sessionManager?.let { manager ->
                            manager.saveAuthToken(token)
                            manager.saveUserData(
                                userId = user.id_user,
                                username = user.username,
                                fullName = user.full_name,
                                email = user.email,
                                phone = user.phone,
                                bloodType = user.blood_type,
                                allergy = user.allergy,
                                medicalHistory = user.medical_history,
                                role = response.role ?: "user",
                                imageUrl = user.image
                            )
                        }

                        fetchUserStats(token)
                        fetchScanHistory(token)
                        
                        // Fetch AI Insight & Health Score untuk Home Screen
                        fetchAiDailyInsight()
                        fetchHealthScore()
                        onSuccess()
                    } else {
                        onError("Registrasi berhasil, tetapi data tidak lengkap")
                    }
                } else {
                    val errorMsg = response.status ?: "Pendaftaran gagal"
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                val errorMessage = ApiErrorHandler.fromThrowable<LoginModel>(e).message
                _errorMessage.value = errorMessage
                onError(errorMessage)
                e.printStackTrace()
            }
        }
    }
    
    fun toggleTheme() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        viewModelScope.launch {
            preferenceManager?.setDarkMode(newValue)
            sessionManager?.saveDarkMode(newValue)
        }
    }

    fun logout(navController: androidx.navigation.NavController? = null) {
        viewModelScope.launch {
            try {
                val token = _accessToken.value
                if (token != null) {
                    apiService.logout("Bearer $token")
                }
            } catch (e: Exception) {
                println("Logout API failed: ${e.message}")
            } finally {
                // Clear state
                _accessToken.value = null
                _isLoggedIn.value = false
                _currentUser.value = null
                _isAdmin.value = false
                _userData.value = null
                _totalScans.value = 0
                _halalProducts.value = 0
                _scanHistory.value = emptyList()
                _unreadNotificationCount.value = 0
                _pendingContributionCount.value = 0
                _approvedContributionCount.value = 0
                _lastRealtimeSyncAt.value = null

                realtimeHistoryJob?.cancel()
                realtimeStatusJob?.cancel()
                
                // Clear session
                sessionManager?.logout()
                
                // Navigate if controller provided
                navController?.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
    
    fun addScanToHistory(
        productId: Int?,
        productName: String,
        barcode: String?,
        category: String?,
        statusHalal: String,
        statusKesehatan: String = "sehat",
        imageUrl: String? = null
    ) {
        val context = sessionManager.getContext() ?: return
        val database = com.example.halalyticscompose.Data.Local.HalalyticsDatabase.getDatabase(context)

        viewModelScope.launch {
            // Optimistic Update: StateFlow
            val optimisticItem = ScanHistoryItem(
                id = (System.currentTimeMillis() / 1000).toInt(), // Temporary ID
                productName = productName,
                productImage = imageUrl,
                barcode = barcode,
                halalStatus = statusHalal,
                source = if (productId != null) "local" else "open_food_facts",
                scanMethod = if (!barcode.isNullOrBlank()) "barcode" else "text_search",
                createdAt = java.time.Instant.now().toString()
            )
            val currentList = _scanHistory.value.toMutableList()
            currentList.add(0, optimisticItem)
            _scanHistory.value = currentList

            // Optimistic Update: Room DB (Critical for HistoryScreen)
            if (barcode != null) {
                 try {
                     val historyEntity = com.example.halalyticscompose.data.database.ProductHistoryEntity(
                        barcode = barcode,
                        name = productName,
                        status = statusHalal,
                        timestamp = System.currentTimeMillis(),
                        isFavorite = false,
                        isSynced = false
                    )
                    database.productHistoryDao().insertProduct(historyEntity)
                    Log.i("MainViewModel", "Local DB: history saved for $productName")
                 } catch (e: Exception) {
                     Log.e("MainViewModel", "Failed to save history locally", e)
                 }
            }

            try {
                _accessToken.value?.let { token ->
                    val request = com.example.halalyticscompose.Data.Model.RecordScanRequest(
                        scannable_id = productId ?: 0,
                        scannable_type = if (productId != null) "product" else "manual",
                        product_name = productName,
                        product_image = imageUrl,
                        barcode = barcode,
                        halal_status = statusHalal,
                        scan_method = if (!barcode.isNullOrBlank()) "barcode" else "text_search",
                        source = if (productId != null) "local" else "open_food_facts",
                        latitude = null,
                        longitude = null,
                        confidence_score = null,
                        nutrition_snapshot = null
                    )
                    
                    val response = apiService.recordScan("Bearer $token", request)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.healthWarning != null) {
                            _errorMessage.value = "⚠️ ${body.healthWarning}" // Reusing error message for toast/snackbar
                        }
                        // Mark as synced if successful
                        if (barcode != null) {
                            database.productHistoryDao().markAsSynced(barcode)
                        }
                    } else {
                        val err = response.errorBody()?.string()?.take(220)
                        _errorMessage.value = if (!err.isNullOrBlank()) {
                            "Gagal simpan riwayat scan (${response.code()}): $err"
                        } else {
                            "Gagal simpan riwayat scan (${response.code()})"
                        }
                        Log.e("MainViewModel", "recordScan failed code=${response.code()} body=$err")
                    }
                    
                    // Refresh history to get real ID and server data
                    fetchScanHistory(token)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to store scan", e)
                _errorMessage.value = "Gagal menyimpan riwayat scan: ${e.message}"
            }
        }
    }

    fun toggleFavorite(
        productId: Int,
        isFavorite: Boolean,
        productName: String,
        barcode: String,
        halalStatus: String,
        favoriteId: Int? = null // Pass if available for deletion
    ){
        val context = sessionManager.getContext() ?: return
        val database = com.example.halalyticscompose.Data.Local.HalalyticsDatabase.getDatabase(context)

        viewModelScope.launch {
            // OPTIMISTIC UPDATE: Update Local DB First for Instant UI Feedback
            try {
                if (!isFavorite) {
                    // Adding to favorites (isFavorite was false, so we are toggling to true)
                    val historyEntity = com.example.halalyticscompose.data.database.ProductHistoryEntity(
                        barcode = barcode,
                        name = productName,
                        status = halalStatus,
                        timestamp = System.currentTimeMillis(),
                        isFavorite = true, // Force true
                        isSynced = false
                    )
                    database.productHistoryDao().insertProduct(historyEntity)
                    database.productHistoryDao().updateFavoriteStatus(barcode, true)
                    Log.i("MainViewModel", "Local DB: added favorite $productName")
                } else {
                    // Removing from favorites
                    database.productHistoryDao().updateFavoriteStatus(barcode, false)
                    Log.i("MainViewModel", "Local DB: removed favorite $productName")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Local favorite update failed", e)
            }

             _accessToken.value?.let { token ->
                 try {
                     if (isFavorite && favoriteId != null) {
                         // Remove favorite by its ID
                         apiService.deleteFavorite("Bearer $token", favoriteId)
                         Log.i("MainViewModel", "API: favorite removed $productName")
                         // Sync success
                         database.productHistoryDao().markAsSynced(barcode)
                     } else if (!isFavorite) {
                         // Add to favorites
                         val request = com.example.halalyticscompose.Data.Model.AddFavoriteRequest(
                             favoritable_id = productId,
                             favoritable_type = "product",
                             product_name = productName,
                             product_image = null,
                             halal_status = halalStatus,
                             category = null,
                             user_notes = null
                         )
                         apiService.addFavorite("Bearer $token", request)
                         Log.i("MainViewModel", "API: favorite added $productName")
                         // Sync success
                         database.productHistoryDao().markAsSynced(barcode)
                     }
                 } catch (e: Exception) {
                     _errorMessage.value = "Gagal mengupdate favorit (Server): ${e.message}"
                     Log.e("MainViewModel", "Server favorite update failed", e)
                 }
             }
        }
    }

    
    fun addFavoriteIngredient(ingredientName: String) {
        viewModelScope.launch {
            // Simulate adding to favorites
            // In real app, this would save to local database or backend
        }
    }
    
    fun updateDietaryPreferences(glutenFree: Boolean, nutAllergy: Boolean, strictHalal: Boolean) {
        sessionManager?.saveDietaryPreferences(glutenFree, nutAllergy, strictHalal)
        
        // Sync with server
        viewModelScope.launch {
            try {
                _accessToken.value?.let { token ->
                    val dietList = mutableListOf<String>()
                    if (glutenFree) dietList.add("gluten_free")
                    if (nutAllergy) dietList.add("nut_allergy")
                    if (strictHalal) dietList.add("strict_halal")
                    
                    val dietString = dietList.joinToString(",")
                    
                    apiService.updateProfile(
                        bearer = "Bearer $token",
                        dietPreference = dietString
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to sync dietary preferences", e)
            }
        }
    }

    fun updateProfile(
        fullName: String? = null,
        email: String? = null,
        phone: String? = null,
        bloodType: String? = null,
        allergy: String? = null,
        medicalHistory: String? = null,
        height: Double? = null,
        weight: Double? = null,
        age: Int? = null,
        dietPreference: String? = null,
        language: String? = null,
        goal: String? = null,
        activityLevel: String? = null,
        gender: String? = null,
        bio: String? = null,
        image: java.io.File? = null,
        onComplete: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _accessToken.value?.let { token ->
                    val response = if (image != null) {
                        // Multipart update
                        val imageBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                        val imagePart = okhttp3.MultipartBody.Part.createFormData("image", image.name, imageBody)
                        
                            apiService.updateProfileMultipart(
                                bearer = "Bearer $token",
                                image = imagePart,
                                fullName = fullName?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                email = email?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                phone = phone?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                bloodType = bloodType?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                allergy = allergy?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                medicalHistory = medicalHistory?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                age = age?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                height = height?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                weight = weight?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                dietPreference = dietPreference?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                language = language?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                goal = goal?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                activityLevel = activityLevel?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                gender = gender?.toRequestBody("text/plain".toMediaTypeOrNull()),
                                bio = bio?.toRequestBody("text/plain".toMediaTypeOrNull())
                            )
                        } else {
                            // Normal update
                            apiService.updateProfile(
                                bearer = "Bearer $token",
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                bloodType = bloodType,
                                allergy = allergy,
                                medicalHistory = medicalHistory,
                                age = age,
                                height = height,
                                weight = weight,
                                dietPreference = dietPreference,
                                language = language,
                                goal = goal,
                                activityLevel = activityLevel,
                                gender = gender,
                                bio = bio
                            )
                        }
                    
                    if (response.isSuccess) {
                        val loginContent = response.user
                        if (loginContent != null) {
                            // Map LoginContent to User model
                            val user = com.example.halalyticscompose.Data.Model.User(
                                idUser = loginContent.id_user,
                                username = loginContent.username,
                                fullName = loginContent.full_name ?: fullName,
                                email = loginContent.email,
                                phone = loginContent.phone ?: phone,
                                bloodType = loginContent.blood_type ?: bloodType,
                                allergy = loginContent.allergy ?: allergy,
                                medicalHistory = loginContent.medical_history ?: medicalHistory,
                                role = loginContent.role,
                                active = loginContent.active,
                                image = loginContent.image,
                                goal = loginContent.goal,
                                dietPreference = loginContent.diet_preference ?: dietPreference,
                                activityLevel = loginContent.activity_level,
                                address = loginContent.address,
                                language = loginContent.language,
                                age = loginContent.age ?: age,
                                height = (loginContent.height?.toDouble() ?: height),
                                weight = (loginContent.weight?.toDouble() ?: weight),
                                bmi = loginContent.bmi?.toDouble(),
                                notifEnabled = loginContent.notif_enabled ?: true,
                                darkMode = loginContent.dark_mode ?: false,
                                bio = loginContent.bio ?: bio,
                                gender = loginContent.gender ?: gender
                            )

                            sessionManager?.let { manager ->
                                manager.saveUserData(
                                    userId = user.idUser,
                                    username = user.username,
                                    fullName = user.fullName,
                                    email = user.email,
                                    phone = user.phone,
                                    bloodType = user.bloodType,
                                    allergy = user.allergy,
                                    medicalHistory = user.medicalHistory,
                                    role = user.role,
                                    imageUrl = user.image
                                )
                                // Persist health data to session
                                manager.saveHealthProfile(
                                    age = user.age,
                                    height = user.height?.toFloat(),
                                    weight = user.weight?.toFloat(),
                                    bmi = user.bmi?.toFloat(),
                                    activityLevel = user.activityLevel,
                                    dietPreference = user.dietPreference,
                                    goal = user.goal
                                )
                                loginContent.language?.let { manager.saveLanguage(it) }
                            }
                            _currentUser.value = user.fullName ?: user.username
                            _isAdmin.value = user.role.equals("admin", ignoreCase = true)
                            _userData.value = user // Update state flow
                            onSuccess()
                            onComplete()
                        } else {
                            onError("Update berhasil, tetapi data tidak lengkap")
                        }
                    } else {
                        onError(response.status ?: "Gagal memperbarui profil")
                    }
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError(ApiErrorHandler.fromThrowable<LoginModel>(e).message)
            }
        }
    }


    fun isGlutenFree(): Boolean = sessionManager?.isGlutenFree() ?: false
    fun hasNutAllergy(): Boolean = sessionManager?.hasNutAllergy() ?: false
    fun isStrictHalal(): Boolean = sessionManager?.isStrictHalal() ?: true

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val response = apiService.forgotPassword(email)
                
                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message ?: "Gagal mengirim email reset password.")
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Gagal mengirim email: ${e.message}")
            }
        }
    }

    private fun registerFcmToken() {
        val token = _accessToken.value ?: return
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                viewModelScope.launch {
                    try {
                        apiService.registerFcmToken(
                            bearer = "Bearer $token",
                            fcmToken = fcmToken,
                            deviceType = "android",
                            deviceId = "android_device" // Use real ID if possible
                        )
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "FCM registration failed", e)
                    }
                }
            }
        }
    }



    @Suppress("DEPRECATION")
    private fun createPartFromString(descriptionString: String): okhttp3.RequestBody {
        return descriptionString.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // --- UMKM Scan features removed ---

    // ROOM CACHE HELPERS
    fun cacheScanResult(
        productName: String,
        barcode: String?,
        halalStatus: String,
        healthScore: Int?,
        ingredients: List<String>?,
        notes: String? = null,
        imageUrl: String? = null,
        sugar: Double? = null,
        sodium: Double? = null,
        calories: Int? = null
    ) {
        val database = com.example.halalyticscompose.Data.Local.HalalyticsDatabase.getDatabase(
            sessionManager?.getContext() ?: return
        )
        viewModelScope.launch {
            database.cachedScanResultDao().insert(
                com.example.halalyticscompose.Data.Local.Entities.CachedScanResult(
                    productName = productName,
                    barcode = barcode,
                    halalStatus = halalStatus,
                    healthScore = healthScore,
                    calories = calories,
                    ingredients = ingredients,
                    halalNotes = notes,
                    imageUrl = imageUrl
                )
            )

            // Record consumption for health tracking (Dewa Feature)
            if (sugar != null || sodium != null) {
                recordConsumption(productName, sugar ?: 0.0, sodium ?: 0.0, halalStatus.lowercase() == "halal")
            }
        }
    }

    private fun recordConsumption(name: String, sugar: Double, sodium: Double, isHalal: Boolean) {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val userId = sessionManager?.getUserId() ?: 0
            consumptionDao?.insertConsumption(
                Consumption(
                    date = date,
                    productName = name,
                    totalSugar = sugar,
                    totalSodium = sodium,
                    isHalal = isHalal,
                    userId = userId
                )
            )
            updateDailyIntakeFromLocal()
        }
    }

    fun updateDailyIntakeFromLocal() {
        viewModelScope.launch {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val userId = sessionManager?.getUserId() ?: 0

                val totalSugar = consumptionDao?.getTotalSugarByDate(date, userId) ?: 0.0
                val totalSodium = consumptionDao?.getTotalSodiumByDate(date, userId) ?: 0.0

                // Stats based on local consumption (Consolidated Health Tracker)
                val sugarLimit = 50.0 // g (Default WHO recommendation)
                val sodiumLimit = 2300.0 // mg (Standard recommendation)
                val calorieLimit = 2000.0

                _dailyIntake.value = DailyIntakeResponse(
                    success = true,
                    message = "Computed from local database",
                    dailyIntake = com.example.halalyticscompose.Data.Model.DailyIntakeData(
                        totalWaterMl = 0,
                        totalCaffeineMg = 0,
                        totalSugarG = totalSugar.toInt(),
                        totalCalories = (totalSugar * 4).toInt(),
                        totalSodiumMg = totalSodium.toInt()
                    ),
                    targets = com.example.halalyticscompose.Data.Model.IntakeTargets(
                        waterTargetMl = 2000,
                        caffeineLimitMg = 400,
                        calorieLimit = calorieLimit.toInt(),
                        sugarLimitG = sugarLimit.toFloat().toInt(),
                        sodiumLimitMg = sodiumLimit.toInt()
                    ),
                    progress = com.example.halalyticscompose.Data.Model.IntakeProgress(
                        waterPercentage = 0f,
                        caffeinePercentage = 0f
                    )
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed reading local intake DB", e)
                _dailyIntake.value = DailyIntakeResponse(
                    success = false,
                    message = "Local intake unavailable",
                    dailyIntake = com.example.halalyticscompose.Data.Model.DailyIntakeData(
                        totalWaterMl = 0,
                        totalCaffeineMg = 0,
                        totalSugarG = 0,
                        totalCalories = 0,
                        totalSodiumMg = 0
                    ),
                    targets = com.example.halalyticscompose.Data.Model.IntakeTargets(
                        waterTargetMl = 2000,
                        caffeineLimitMg = 400,
                        calorieLimit = 2000,
                        sugarLimitG = 50,
                        sodiumLimitMg = 2300
                    ),
                    progress = com.example.halalyticscompose.Data.Model.IntakeProgress(
                        waterPercentage = 0f,
                        caffeinePercentage = 0f
                    )
                )
            }
        }
    }


    fun getCachedResult(barcode: String, onResult: (com.example.halalyticscompose.Data.Local.Entities.CachedScanResult?) -> Unit) {
        val database = com.example.halalyticscompose.Data.Local.HalalyticsDatabase.getDatabase(
            sessionManager?.getContext() ?: return
        )
        viewModelScope.launch {
            val cached = database.cachedScanResultDao().getByBarcode(barcode)
            onResult(cached)
        }
    }

    // ==================== PREMIUM FEATURES LOGIC ====================

    fun fetchWeeklyStats(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeeklyStats("Bearer $token")
                if (response.response_code == 200) {
                    _weeklyStats.value = response.content
                }
            } catch (e: Exception) {
                // Silently handle or log
            }
        }
    }

    // --- Nearby UMKM features removed ---

    fun updateAllergies(allergies: String) {
        _userAllergies.value = allergies
        viewModelScope.launch {
            try {
                preferenceManager?.setAllergies(allergies)
                val token = _accessToken.value
                if (token != null) {
                    apiService.updateProfile("Bearer $token", allergy = allergies) // Update server
                }
            } catch (e: Exception) {}
        }
    }

    fun requestVerification(barcode: String, productName: String?, notes: String?, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: ""
                val response = apiService.requestVerification("Bearer $token", barcode, productName, notes)
                onComplete(response.responseCode == 200)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onComplete(false)
            }
        }
    }

    fun fetchRecommendations(category: String) {
        viewModelScope.launch {
            try {
                val token = _accessToken.value ?: ""
                val response = apiService.getRecommendations("Bearer $token", category)
                if (response.response_code == 200) {
                    _recommendations.value = response.content
                }
            } catch (e: Exception) {}
        }
    }

    fun fetchDailyIntake(token: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getDailyIntake("Bearer $token")
                if (response.isSuccessful) {
                    response.body()?.let { _dailyIntake.value = it }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to fetch daily intake: ${e.message}")
            }
        }
    }

    fun submitContribution(
        productName: String,
        barcode: String?,
        complaint: String?,
        imageFile: java.io.File?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: ""
                
                val namePart = productName.toRequestBody("text/plain".toMediaTypeOrNull())
                val barcodePart = barcode?.toRequestBody("text/plain".toMediaTypeOrNull())
                val complaintPart = complaint?.toRequestBody("text/plain".toMediaTypeOrNull())
                
                val imagePart = imageFile?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    okhttp3.MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                val response = apiService.submitContribution(
                    bearer = "Bearer $token",
                    image = imagePart,
                    productName = namePart,
                    barcode = barcodePart,
                    ingredients = complaintPart
                )
                
                if (response.success) {
                    onSuccess(response.message)
                } else {
                    onError(response.message)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to submit contribution")
            }
        }
    }

    fun verifyCertificate(
        qrData: String,
        onSuccess: (CertificateInfo) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: ""
                
                val response = apiService.verifyCertificate(
                    bearer = "Bearer $token",
                    request = mapOf("qr_data" to qrData)
                )
                
                if (response.success && response.data != null) {
                    onSuccess(response.data)
                } else {
                    onError(response.message)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to verify certificate")
            }
        }
    }

    /**
     * Sync Firebase User with MySQL
     */
    fun syncWithMySQL(firebaseUser: com.google.firebase.auth.FirebaseUser, fcmToken: String?) {
        viewModelScope.launch {
            try {
                apiService.syncUser(
                    firebaseUid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    fcmToken = fcmToken
                )
                Log.d("MainViewModel", "User sync with MySQL successful")
            } catch (e: Exception) {
                Log.e("MainViewModel", "User sync failed", e)
                // We don't necessarily block the user if sync fails, 
                // but we should record it for debugging.
                com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /**
     * Trigger Gemini AI Analysis for ingredients
     */
    fun analyzeWithGemini(ingredients: String) {
        viewModelScope.launch {
            geminiAnalyzer.analyzeIngredients(ingredients).collect { result ->
                _geminiResult.value = result
            }
        }
    }

    /**
     * Remote AI Analysis using Backend (More context-aware)
     */
    fun analyzeRemoteIngredients(ingredients: String, productId: Int? = null, productName: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: return@launch
                
                val request = AiAnalysisRequest(
                    ingredientsText = ingredients,
                    familyId = _selectedFamilyProfile.value?.id
                )
                
                val response = apiService.analyzeIngredients("Bearer $token", request)
                _remoteAiResult.value = response
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("MainViewModel", "Remote AI Analysis failed", e)
            }
        }
    }

    // FAMILY BOX METHODS
    fun fetchFamilyProfiles() {
        viewModelScope.launch {
            try {
                val token = _accessToken.value ?: return@launch
                val response = apiService.getFamilyProfiles("Bearer $token")
                if (response.success) {
                    _familyProfiles.value = response.data
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch family profiles", e)
            }
        }
    }

    fun addFamilyProfile(
        name: String,
        relationship: String?,
        age: Int?,
        gender: String?,
        allergies: String?,
        medicalHistory: String?,
        image: java.io.File? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: return@launch
                
                val response = if (image != null) {
                    val imageBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart = okhttp3.MultipartBody.Part.createFormData("image", image.name, imageBody)
                    
                    apiService.addFamilyProfileMultipart(
                        bearer = "Bearer $token",
                        image = imagePart,
                        name = name.toRequestBody("text/plain".toMediaTypeOrNull()),
                        relationship = relationship?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        age = age?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        gender = gender?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        allergies = allergies?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        medicalHistory = medicalHistory?.toRequestBody("text/plain".toMediaTypeOrNull())
                    )
                } else {
                    apiService.addFamilyProfile(
                        bearer = "Bearer $token",
                        name = name,
                        relationship = relationship,
                        age = age,
                        gender = gender,
                        allergies = allergies,
                        medicalHistory = medicalHistory
                    )
                }
                
                if (response.success) {
                    fetchFamilyProfiles()
                    onSuccess()
                } else {
                    onError(response.message)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to add family profile")
            }
        }
    }

    fun updateFamilyProfile(
        id: Int,
        name: String?,
        relationship: String?,
        age: Int?,
        gender: String?,
        allergies: String?,
        medicalHistory: String?,
        image: java.io.File? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: return@launch
                
                val response = if (image != null) {
                    val imageBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart = okhttp3.MultipartBody.Part.createFormData("image", image.name, imageBody)
                    
                    apiService.updateFamilyProfileMultipart(
                        bearer = "Bearer $token",
                        id = id,
                        method = "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
                        image = imagePart,
                        name = name?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        relationship = relationship?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        age = age?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        gender = gender?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        allergies = allergies?.toRequestBody("text/plain".toMediaTypeOrNull()),
                        medicalHistory = medicalHistory?.toRequestBody("text/plain".toMediaTypeOrNull())
                    )
                } else {
                    apiService.updateFamilyProfile(
                        bearer = "Bearer $token",
                        id = id,
                        name = name,
                        relationship = relationship,
                        age = age,
                        gender = gender,
                        allergies = allergies,
                        medicalHistory = medicalHistory
                    )
                }
                
                if (response.success) {
                    fetchFamilyProfiles()
                    if (_selectedFamilyProfile.value?.id == id) {
                        _selectedFamilyProfile.value = response.data
                    }
                    onSuccess()
                } else {
                    onError(response.message)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to update family profile")
            }
        }
    }

    fun deleteFamilyProfile(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val token = _accessToken.value ?: return@launch
                val response = apiService.deleteFamilyProfile("Bearer $token", id)
                if (response.success) {
                    fetchFamilyProfiles()
                    if (_selectedFamilyProfile.value?.id == id) {
                        _selectedFamilyProfile.value = null
                    }
                    onSuccess()
                } else {
                    onError(response.message ?: "Failed to delete profile")
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Failed to delete family profile")
            }
        }
    }

    fun selectFamilyProfile(profile: FamilyProfile?) {
        _selectedFamilyProfile.value = profile
    }

    override fun onCleared() {
        super.onCleared()
        realtimeHistoryJob?.cancel()
        realtimeStatusJob?.cancel()
    }

}
