package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.Network.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MentalHealthViewModel : ViewModel() {
    private val _topics = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val topics: StateFlow<List<Map<String, Any>>> = _topics.asStateFlow()

    private val _articles = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val articles: StateFlow<List<Map<String, Any>>> = _articles.asStateFlow()

    private val _experts = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val experts: StateFlow<List<Map<String, Any>>> = _experts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadData(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val topicsResp = ApiConfig.apiService.getMentalHealthTopics("Bearer $token")
                if (topicsResp.isSuccessful && topicsResp.body()?.success == true) {
                    _topics.value = topicsResp.body()?.content as? List<Map<String, Any>> ?: emptyList()
                }

                val articlesResp = ApiConfig.apiService.getMentalHealthArticles("Bearer $token")
                if (articlesResp.isSuccessful && articlesResp.body()?.success == true) {
                    _articles.value = articlesResp.body()?.content as? List<Map<String, Any>> ?: emptyList()
                }

                val expertsResp = ApiConfig.apiService.getMentalHealthExperts("Bearer $token")
                if (expertsResp.isSuccessful && expertsResp.body()?.success == true) {
                    _experts.value = expertsResp.body()?.content as? List<Map<String, Any>> ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
