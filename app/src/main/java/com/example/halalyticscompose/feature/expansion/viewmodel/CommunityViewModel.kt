package com.example.halalyticscompose.feature.expansion.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.feature.expansion.model.CommunityComment
import com.example.halalyticscompose.feature.expansion.model.CommunityLeaderboardEntry
import com.example.halalyticscompose.feature.expansion.model.CommunityPost
import com.example.halalyticscompose.feature.expansion.model.CommunityPostDetail
import com.example.halalyticscompose.feature.expansion.network.ExpansionApiService
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val apiService: ExpansionApiService,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _selectedPost = MutableStateFlow<CommunityPostDetail?>(null)
    val selectedPost: StateFlow<CommunityPostDetail?> = _selectedPost.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<CommunityLeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<CommunityLeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _activeCategory = MutableStateFlow<String?>(null)
    val activeCategory: StateFlow<String?> = _activeCategory.asStateFlow()

    private val _showComposer = MutableStateFlow(false)
    val showComposer: StateFlow<Boolean> = _showComposer.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPosts(category: String? = null, sort: String = "latest") {
        val bearer = sessionManager.getBearerToken()
        if (bearer == null) {
            _error.value = "Silakan login terlebih dahulu untuk menggunakan fitur ini"
            return
        }
        _activeCategory.value = category

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPosts(bearer, category = category, sort = sort)
                if (response.isSuccessful) {
                    _posts.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Gagal memuat forum"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPostDetail(postId: Int) {
        val bearer = sessionManager.getBearerToken()
        if (bearer == null) {
            _error.value = "Silakan login terlebih dahulu untuk menggunakan fitur ini"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPostDetail(bearer, postId)
                if (response.isSuccessful) {
                    _selectedPost.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Gagal memuat detail postingan"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun likePost(postId: Int) {
        val bearer = sessionManager.getBearerToken()
        if (bearer == null) {
            _error.value = "Silakan login terlebih dahulu untuk menggunakan fitur ini"
            return
        }

        viewModelScope.launch {
            val current = _posts.value
            _posts.value = current.map { post ->
                if (post.id == postId) {
                    post.copy(
                        isLikedByMe = !post.isLikedByMe,
                        likesCount = if (post.isLikedByMe) (post.likesCount - 1).coerceAtLeast(0) else post.likesCount + 1,
                    )
                } else {
                    post
                }
            }

            runCatching {
                apiService.likePost(bearer, postId)
            }.onFailure {
                _posts.value = current
                _error.value = it.message
            }
        }
    }

    fun addComment(postId: Int, content: String, parentId: Int? = null) {
        val bearer = sessionManager.getBearerToken()
        if (bearer == null) {
            _error.value = "Silakan login terlebih dahulu untuk menggunakan fitur ini"
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.addComment(
                    bearer,
                    postId,
                    mapOf("content" to content, "parent_id" to parentId?.toString()),
                )
                if (response.isSuccessful) {
                    _posts.value = _posts.value.map { post ->
                        if (post.id == postId) post.copy(commentsCount = post.commentsCount + 1) else post
                    }

                    response.body()?.data?.let { comment ->
                        _selectedPost.value = _selectedPost.value?.let { current ->
                            current.copy(comments = mergeComment(current.comments, comment), commentsCount = current.commentsCount + 1)
                        }
                    }
                } else {
                    _error.value = response.body()?.message ?: "Gagal mengirim komentar"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadLeaderboard() {
        val bearer = sessionManager.getBearerToken()
        if (bearer == null) {
            _error.value = "Silakan login terlebih dahulu untuk menggunakan fitur ini"
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.getLeaderboard(bearer)
                if (response.isSuccessful) {
                    _leaderboard.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleComposer() {
        _showComposer.value = !_showComposer.value
    }

    fun submitPost(content: String, category: String, imageUri: Uri?, context: Context, title: String? = null) {
        val bearer = sessionManager.getBearerToken()
        if (bearer == null) {
            _error.value = "Silakan login terlebih dahulu untuk menggunakan fitur ini"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                val titleBody = title?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageUri?.let { uri ->
                    val tempFile = File.createTempFile("community_post_", ".jpg", context.cacheDir)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        tempFile.outputStream().use { output -> input.copyTo(output) }
                    }
                    MultipartBody.Part.createFormData(
                        "image",
                        tempFile.name,
                        tempFile.asRequestBody("image/*".toMediaTypeOrNull()),
                    )
                }

                val response = apiService.createPost(
                    bearer = bearer,
                    content = contentBody,
                    category = categoryBody,
                    title = titleBody,
                    image = imagePart,
                )

                if (response.isSuccessful) {
                    response.body()?.data?.let { newPost ->
                        _posts.value = listOf(newPost) + _posts.value
                    }
                    _showComposer.value = false
                } else {
                    _error.value = response.body()?.message ?: "Gagal membuat postingan"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mergeComment(existing: List<CommunityComment>, newComment: CommunityComment): List<CommunityComment> {
        if (newComment.parentId == null) {
            return existing + newComment
        }

        return existing.map { comment ->
            if (comment.id == newComment.parentId) {
                comment.copy(replies = comment.replies + newComment)
            } else {
                comment
            }
        }
    }
}
