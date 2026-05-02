package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.data.model.*
import com.example.halalyticscompose.data.network.ApiConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Food Scan & Recognition feature
 */
@HiltViewModel
class FoodScanViewModel @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    // UI States
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Search Results
    private val _searchResults = MutableStateFlow<List<StreetFood>>(emptyList())
    val searchResults: StateFlow<List<StreetFood>> = _searchResults.asStateFlow()
    
    // Popular Foods
    private val _popularFoods = MutableStateFlow<List<StreetFood>>(emptyList())
    val popularFoods: StateFlow<List<StreetFood>> = _popularFoods.asStateFlow()
    
    // Selected Food Analysis
    private val _selectedFood = MutableStateFlow<StreetFood?>(null)
    val selectedFood: StateFlow<StreetFood?> = _selectedFood.asStateFlow()
    
    private val _foodAnalysis = MutableStateFlow<FoodAnalysis?>(null)
    val foodAnalysis: StateFlow<FoodAnalysis?> = _foodAnalysis.asStateFlow()
    
    // Portion selector
    private val _selectedPortion = MutableStateFlow(1.0)
    val selectedPortion: StateFlow<Double> = _selectedPortion.asStateFlow()
    
    
    /**
     * Search for street foods by name
     */
    fun searchFood(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val token = sessionManager.getBearerToken() ?: ""
                // Using new API signature: searchFood(token, query) -> ApiResponse<List<StreetFood>>
                val response = apiService.searchFood(token, query)
                
                if (response.success) {
                    _searchResults.value = response.data ?: emptyList()
                } else {
                    _errorMessage.value = response.message
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get popular foods for initial display
     */
    fun loadPopularFoods() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val token = sessionManager.getBearerToken() ?: ""
                val response = apiService.getPopularFoods(token)
                
                if (response.success) {
                    _popularFoods.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                println("Failed to load popular foods: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Select a food for analysis
     */
    fun selectFood(food: StreetFood) {
        _selectedFood.value = food
        _selectedPortion.value = 1.0
        // Load full analysis
        analyzeFood(food.id, null)
    }
    
    /**
     * Update selected portion size
     */
    fun updatePortion(portion: Double) {
        _selectedPortion.value = portion
        // Re-analyze with new portion
        _selectedFood.value?.let { food ->
            analyzeFood(food.id, null, portion)
        }
    }
    
    /**
     * Analyze nutrition for selected food
     */
    fun analyzeFood(foodId: Int, variantId: Int?, portion: Double = 1.0) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val request = FoodAnalysisRequest(
                    foodId = foodId,
                    variantId = variantId,
                    portion = portion
                )
                
                val token = sessionManager.getBearerToken() ?: ""
                val response = apiService.analyzeFood(
                    token = token,
                    request = request
                )
                
                if (response.success) {
                    _foodAnalysis.value = response.data
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Analysis failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear search results
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
    }
    
    /**
     * Clear selected food and analysis
     */
    fun clearSelection() {
        _selectedFood.value = null
        _foodAnalysis.value = null
        _selectedPortion.value = 1.0
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Get health score color based on score value
     */
    fun getHealthScoreColor(score: Int): Long {
        return when {
            score >= 80 -> 0xFF4CAF50 // Green - Excellent
            score >= 60 -> 0xFF8BC34A // Light Green - Good
            score >= 40 -> 0xFFFFEB3B // Yellow - Fair
            score >= 20 -> 0xFFFF9800 // Orange - Poor
            else -> 0xFFF44336 // Red - Very Poor
        }
    }
    
    /**
     * Get halal status color
     */
    fun getHalalStatusColor(status: String): Long {
        return when (status) {
            "halal_umum" -> 0xFF4CAF50 // Green
            "syubhat" -> 0xFFFF9800 // Orange
            "haram" -> 0xFFF44336 // Red
            "tergantung_bahan" -> 0xFFFFEB3B // Yellow
            else -> 0xFF9E9E9E // Grey
        }
    }
    /**
     * Analyze image from URI (OCR / Recognition)
     */
    /**
     * Recognize food from image file
     */
    fun recognizeImage(imageFile: java.io.File, token: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Prepare Multipart Body
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = okhttp3.MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
                
                val token = sessionManager.getBearerToken() ?: ""
                // Call API
                val response = apiService.recognizeFoodImage(token, body)
                
                if (response.success && response.data != null) {
                    val matches = response.data.matches
                    if (matches.isNotEmpty()) {
                        // Map FoodMatch to StreetFood (Simplified for now)
                        val foods = matches.map { match ->
                            StreetFood(
                                id = match.id,
                                name = match.name,
                                nameEn = null,
                                category = match.category,
                                imageUrl = match.imageUrl ?: "",
                                description = "Recognized via AI with ${match.confidence * 100}% confidence",
                                caloriesTypical = 0.0,
                                caloriesRange = null,
                                protein = 0.0,
                                carbs = 0.0,
                                fat = 0.0,
                                healthScore = 0,
                                healthCategory = "Unknown",
                                halalStatus = "unknown",
                                isPopular = false
                            )
                        }
                        _searchResults.value = foods
                    } else {
                        _errorMessage.value = "No food recognized"
                    }
                    onComplete(true)
                } else {
                    _errorMessage.value = response.message ?: "Recognition failed"
                    onComplete(false)
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Image analysis failed: ${e.message}"
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
