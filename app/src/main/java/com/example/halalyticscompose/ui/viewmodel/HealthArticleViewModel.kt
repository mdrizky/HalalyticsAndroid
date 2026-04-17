package com.example.halalyticscompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.BuildConfig
import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.HealthArticleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID
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

    private val articleCache = linkedMapOf<String, HealthArticleItem>()

    fun setSelectedArticle(article: HealthArticleItem) {
        _selectedArticle.value = article
        cacheArticle(article)
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
                    val data = response.data ?: emptyList()
                    _articles.value = data
                    data.forEach { cacheArticle(it) }
                    if (data.isEmpty() && includeExternal) {
                        val external = loadNewsDataArticles(query)
                        if (external.isNotEmpty()) {
                            _articles.value = external
                            external.forEach { cacheArticle(it) }
                            _error.value = null
                        }
                    }
                } else {
                    if (includeExternal) {
                        val external = loadNewsDataArticles(query)
                        if (external.isNotEmpty()) {
                            _articles.value = external
                            external.forEach { cacheArticle(it) }
                            _error.value = null
                        } else {
                            _error.value = response.message ?: "Gagal memuat artikel."
                        }
                    } else {
                        _error.value = response.message ?: "Gagal memuat artikel."
                    }
                }
            } catch (e: Exception) {
                if (includeExternal) {
                    val external = loadNewsDataArticles(query)
                    if (external.isNotEmpty()) {
                        _articles.value = external
                        external.forEach { cacheArticle(it) }
                        _error.value = null
                    } else {
                        _error.value = "Gagal memuat artikel: ${e.message ?: "unknown error"}"
                    }
                } else {
                    _error.value = "Gagal memuat artikel: ${e.message ?: "unknown error"}"
                }
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
                val cached = articleCache[idOrSlug]
                    ?: _articles.value.firstOrNull { it.id == idOrSlug || it.slug == idOrSlug }
                if (cached != null) {
                    _selectedArticle.value = cached
                    _isLoading.value = false
                    return@launch
                }

                val response = apiService.getHealthArticleDetail(idOrSlug)
                if (response.success) {
                    _selectedArticle.value = response.data
                    response.data?.let { cacheArticle(it) }
                } else {
                    // Fallback: reload merged list (local + external) and try match by ID/slug.
                    val listResponse = apiService.getHealthArticles(
                        query = null,
                        limit = 50,
                        includeExternal = true
                    )
                    val matched = listResponse.data
                        ?.firstOrNull { it.id == idOrSlug || it.slug == idOrSlug }
                    if (matched != null) {
                        _selectedArticle.value = matched
                        cacheArticle(matched)
                    } else {
                        _error.value = response.message ?: "Detail artikel tidak ditemukan."
                    }
                }
            } catch (e: Exception) {
                _error.value = "Gagal memuat detail artikel: ${e.message ?: "unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun cacheArticle(article: HealthArticleItem) {
        articleCache[article.id] = article
        article.slug?.takeIf { it.isNotBlank() }?.let { articleCache[it] = article }
        if (articleCache.size > 300) {
            val firstKey = articleCache.keys.firstOrNull()
            if (firstKey != null) articleCache.remove(firstKey)
        }
    }

    private suspend fun loadNewsDataArticles(query: String?): List<HealthArticleItem> {
        val apiKey = BuildConfig.NEWSDATA_API_KEY
        if (apiKey.isBlank()) return emptyList()
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = query?.takeIf { it.isNotBlank() }?.let {
                    "&q=${URLEncoder.encode(it, "UTF-8")}"
                } ?: ""
                val url = "https://newsdata.io/api/1/news?apikey=$apiKey&country=id&language=id&category=health$encodedQuery"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                if (!response.isSuccessful) {
                    emptyList()
                } else {
                    val body = response.body?.string().orEmpty()
                    val root = JSONObject(body)
                    val results = root.optJSONArray("results")
                    if (results == null) {
                        emptyList()
                    } else {
                        buildList {
                            for (i in 0 until results.length()) {
                                val item = results.optJSONObject(i) ?: continue
                                val title = item.optString("title").takeIf { it.isNotBlank() } ?: continue
                                val articleId = item.optString("article_id").ifBlank { UUID.randomUUID().toString() }
                                val desc = item.optString("description").ifBlank { "Baca artikel kesehatan terbaru." }
                                val content = item.optString("content").ifBlank { desc }
                                val category = item.optJSONArray("category")?.optString(0)?.ifBlank { "Kesehatan" } ?: "Kesehatan"
                                val image = item.optString("image_url").takeIf { it.isNotBlank() }
                                val source = item.optString("source_name").ifBlank { "NewsData.io" }
                                val sourceUrl = item.optString("link").takeIf { it.isNotBlank() }
                                add(
                                    HealthArticleItem(
                                        id = articleId,
                                        slug = articleId,
                                        title = title,
                                        excerpt = desc,
                                        content = content,
                                        category = category,
                                        imageUrl = image,
                                        publishedAt = item.optString("pubDate").ifBlank { null },
                                        source = source,
                                        sourceUrl = sourceUrl
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
