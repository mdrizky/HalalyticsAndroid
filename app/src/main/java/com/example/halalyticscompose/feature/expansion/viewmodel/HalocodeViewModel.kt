package com.example.halalyticscompose.feature.expansion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halalyticscompose.feature.expansion.model.HalocodeConsultation
import com.example.halalyticscompose.feature.expansion.model.HalocodeExpert
import com.example.halalyticscompose.feature.expansion.model.HalocodeMessage
import com.example.halalyticscompose.feature.expansion.network.ExpansionApiService
import com.example.halalyticscompose.feature.expansion.socket.ChatWebSocketManager
import com.example.halalyticscompose.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HalocodeViewModel @Inject constructor(
    private val apiService: ExpansionApiService,
    private val wsManager: ChatWebSocketManager,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _experts = MutableStateFlow<List<HalocodeExpert>>(emptyList())
    val experts: StateFlow<List<HalocodeExpert>> = _experts.asStateFlow()

    private val _queue = MutableStateFlow<List<HalocodeConsultation>>(emptyList())
    val queue: StateFlow<List<HalocodeConsultation>> = _queue.asStateFlow()

    private val _currentConsultation = MutableStateFlow<HalocodeConsultation?>(null)
    val currentConsultation: StateFlow<HalocodeConsultation?> = _currentConsultation.asStateFlow()

    private val _messages = MutableStateFlow<List<HalocodeMessage>>(emptyList())
    val messages: StateFlow<List<HalocodeMessage>> = _messages.asStateFlow()

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val wsMessages = wsManager.messages
    val wsConnectionState = wsManager.connectionState

    fun loadExperts() {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getExperts(bearer)
                if (response.isSuccessful) {
                    _experts.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.body()?.message ?: "Gagal memuat daftar pakar"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createConsultation(expertId: Int, onCreated: ((HalocodeConsultation) -> Unit)? = null) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.createConsultation(bearer, mapOf("expert_id" to expertId))
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _currentConsultation.value = it
                        onCreated?.invoke(it)
                    }
                } else {
                    _error.value = response.body()?.message ?: "Gagal membuat konsultasi"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun connectToChat(consultationId: Int) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            try {
                val response = apiService.getMessages(bearer, consultationId)
                if (response.isSuccessful) {
                    _messages.value = response.body()?.data ?: emptyList()
                }
                wsManager.connect(consultationId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun sendMessage(consultationId: Int, message: String) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            try {
                val response = apiService.sendMessage(bearer, consultationId, mapOf("message" to message))
                if (response.isSuccessful) {
                    response.body()?.data?.let { sent ->
                        _messages.value = (_messages.value + sent).distinctBy { it.id }
                    }
                } else {
                    _error.value = response.body()?.message ?: "Gagal mengirim pesan"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun endConsultation(consultationId: Int, onEnded: (() -> Unit)? = null) {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            try {
                apiService.endConsultation(bearer, consultationId)
            } catch (_: Exception) {
            } finally {
                wsManager.disconnect()
                onEnded?.invoke()
            }
        }
    }

    fun toggleOnline() {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            try {
                val response = apiService.toggleOnline(bearer)
                if (response.isSuccessful) {
                    val rawStatus = response.body()?.data?.get("is_online")
                    _isOnline.value = rawStatus as? Boolean ?: _isOnline.value.not()
                } else {
                    _error.value = response.body()?.message ?: "Gagal mengubah status"
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadQueue() {
        val bearer = sessionManager.getBearerToken() ?: return

        viewModelScope.launch {
            try {
                val response = apiService.getExpertQueue(bearer)
                if (response.isSuccessful) {
                    _queue.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.disconnect()
    }
}
