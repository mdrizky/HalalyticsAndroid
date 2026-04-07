package com.example.halalyticscompose.feature.expansion.socket

import com.example.halalyticscompose.BuildConfig
import com.example.halalyticscompose.feature.expansion.model.HalocodeMessage
import com.example.halalyticscompose.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val sessionManager: SessionManager,
) {
    private var webSocket: WebSocket? = null
    private var currentConsultationId: Int? = null

    private val _messages = MutableStateFlow<List<HalocodeMessage>>(emptyList())
    val messages: StateFlow<List<HalocodeMessage>> = _messages.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    sealed class ConnectionState {
        data object Connected : ConnectionState()
        data object Disconnected : ConnectionState()
        data object Connecting : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    fun connect(consultationId: Int) {
        val wsBase = BuildConfig.REVERB_BASE_URL.trimEnd('/')
        val appKey = BuildConfig.REVERB_APP_KEY

        if (wsBase.isBlank() || appKey.isBlank()) {
            _connectionState.value = ConnectionState.Error("Konfigurasi Reverb belum lengkap")
            return
        }

        disconnect()
        currentConsultationId = consultationId
        _connectionState.value = ConnectionState.Connecting

        val wsUrl = "$wsBase/app/$appKey?protocol=7&client=android&version=1.0&flash=false"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = okHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    _connectionState.value = ConnectionState.Connecting
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    handleIncomingMessage(webSocket, text, consultationId)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                    _connectionState.value = ConnectionState.Error(t.message ?: "Koneksi WebSocket gagal")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    _connectionState.value = ConnectionState.Disconnected
                }
            },
        )
    }

    fun disconnect() {
        webSocket?.close(1000, "closed")
        webSocket = null
        currentConsultationId = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    private fun handleIncomingMessage(webSocket: WebSocket, text: String, consultationId: Int) {
        try {
            val json = JSONObject(text)
            val event = json.optString("event")

            when (event) {
                "pusher:connection_established" -> {
                    val data = JSONObject(json.getString("data"))
                    val socketId = data.getString("socket_id")
                    authenticateAndSubscribe(webSocket, socketId, consultationId)
                }

                "App\\Events\\NewMessageSent",
                "new.message" -> {
                    val payload = when (val rawData = json.opt("data")) {
                        is JSONObject -> rawData
                        is String -> JSONObject(rawData)
                        else -> JSONObject()
                    }

                    val message = HalocodeMessage(
                        id = payload.optInt("id"),
                        consultationId = payload.optInt("consultation_id", consultationId),
                        senderId = payload.optInt("sender_id"),
                        senderName = payload.optString("sender_name").ifBlank { null },
                        message = payload.optString("message"),
                        attachmentPath = payload.optString("attachment_path").ifBlank { null },
                        isRead = payload.optBoolean("is_read", false),
                        createdAt = payload.optString("created_at"),
                    )

                    _messages.value = (_messages.value + message).distinctBy { it.id }
                    _connectionState.value = ConnectionState.Connected
                }

                "pusher_internal:subscription_succeeded",
                "pusher:subscription_succeeded" -> {
                    _connectionState.value = ConnectionState.Connected
                }
            }
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(e.message ?: "Gagal memproses pesan WebSocket")
        }
    }

    private fun authenticateAndSubscribe(webSocket: WebSocket, socketId: String, consultationId: Int) {
        val token = sessionManager.getAuthToken().orEmpty()
        val apiBaseUrl = BuildConfig.API_BASE_URL.substringBefore("/api").trimEnd('/')

        if (token.isBlank() || apiBaseUrl.isBlank()) {
            _connectionState.value = ConnectionState.Error("Sesi login tidak tersedia")
            return
        }

        val channelName = "private-consultation.$consultationId"
        val request = Request.Builder()
            .url("$apiBaseUrl/broadcasting/auth")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .post(
                FormBody.Builder()
                    .add("socket_id", socketId)
                    .add("channel_name", channelName)
                    .build(),
            )
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                _connectionState.value = ConnectionState.Error("Autentikasi channel gagal")
                return
            }

            val body = response.body?.string().orEmpty()
            val auth = JSONObject(body).optString("auth")

            if (auth.isBlank()) {
                _connectionState.value = ConnectionState.Error("Token subscribe kosong")
                return
            }

            val subscribeMessage = JSONObject().apply {
                put("event", "pusher:subscribe")
                put("data", JSONObject().apply {
                    put("auth", auth)
                    put("channel", channelName)
                })
            }

            webSocket.send(subscribeMessage.toString())
        }
    }
}
