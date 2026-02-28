package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.HealthArticleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthArticleViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _articles = MutableStateFlow<List<HealthArticleItem>>(emptyList())
    val articles: StateFlow<List<HealthArticleItem>> = _articles.asStateFlow()

    private val _selectedArticle = MutableStateFlow<HealthArticleItem?>(null)
    val selectedArticle: StateFlow<HealthArticleItem?> = _selectedArticle.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setSelectedArticle(article: HealthArticleItem) {
        _selectedArticle.value = article
    }

    fun loadArticles(query: String? = null, includeExternal: Boolean = true) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getHealthArticles(
                    query = query?.takeIf { it.isNotBlank() },
                    includeExternal = includeExternal
                )
                if (response.success) {
                    _articles.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message ?: "Gagal memuat artikel."
                }
            } catch (e: Exception) {
                _error.value = "Gagal memuat artikel: ${e.message ?: "unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadArticleDetail(idOrSlug: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getHealthArticleDetail(idOrSlug)
                if (response.success) {
                    _selectedArticle.value = response.data
                } else {
                    _error.value = response.message ?: "Detail artikel tidak ditemukan."
                }
            } catch (e: Exception) {
                _error.value = "Gagal memuat detail artikel: ${e.message ?: "unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
