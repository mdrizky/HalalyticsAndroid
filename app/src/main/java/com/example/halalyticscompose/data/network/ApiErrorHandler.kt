package com.example.halalyticscompose.data.network

import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException

object ApiErrorHandler {

    fun <T> fromResponse(
        code: Int,
        rawBody: String? = null,
        headers: Headers? = null
    ): NetworkResult.Error<T> {
        val retryAfter = headers?.get("Retry-After")?.toIntOrNull()
            ?: parseRetryAfterFromBody(rawBody)

        return NetworkResult.Error(
            message = messageForCode(code, rawBody, retryAfter),
            code = code,
            fieldErrors = parseFieldErrors(rawBody),
            retryAfterSeconds = retryAfter
        )
    }

    fun <T> fromThrowable(throwable: Throwable): NetworkResult.Error<T> {
        return when (throwable) {
            is HttpException -> fromResponse(throwable.code(), throwable.response()?.errorBody()?.string(), throwable.response()?.headers())
            is SocketTimeoutException -> NetworkResult.Error("Koneksi timeout, periksa internet", null)
            is IOException -> NetworkResult.Error("Tidak ada koneksi internet. Menampilkan data offline jika tersedia.", null)
            else -> NetworkResult.Error(throwable.message ?: "Terjadi kesalahan jaringan", null)
        }
    }

    private fun messageForCode(code: Int, rawBody: String?, retryAfter: Int?): String {
        val apiMessage = parsePrimaryMessage(rawBody)

        return when (code) {
            401 -> apiMessage ?: "Email atau password salah"
            403 -> apiMessage ?: "Akses tidak diizinkan"
            404 -> apiMessage ?: "Data yang dicari tidak ditemukan"
            422 -> apiMessage ?: "Data yang dikirim tidak valid"
            429 -> "Terlalu banyak permintaan, tunggu ${retryAfter ?: 30} detik"
            500 -> apiMessage ?: "Server sedang bermasalah, coba lagi"
            else -> apiMessage ?: "Terjadi kesalahan jaringan ($code)"
        }
    }

    private fun parsePrimaryMessage(rawBody: String?): String? {
        if (rawBody.isNullOrBlank()) return null

        return runCatching {
            val json = JSONObject(rawBody)
            json.optString("message")
                .takeIf { it.isNotBlank() }
                ?: json.optString("status").takeIf { it.isNotBlank() }
        }.getOrNull()
    }

    private fun parseRetryAfterFromBody(rawBody: String?): Int? {
        if (rawBody.isNullOrBlank()) return null

        return runCatching {
            JSONObject(rawBody).optInt("retry_after").takeIf { it > 0 }
        }.getOrNull()
    }

    private fun parseFieldErrors(rawBody: String?): Map<String, List<String>> {
        if (rawBody.isNullOrBlank()) return emptyMap()

        return runCatching {
            val errors = JSONObject(rawBody).optJSONObject("errors") ?: return emptyMap()
            errors.keys().asSequence().associateWith { key ->
                when (val value = errors.opt(key)) {
                    is JSONArray -> List(value.length()) { index -> value.optString(index) }.filter { it.isNotBlank() }
                    else -> listOf(value?.toString().orEmpty()).filter { it.isNotBlank() }
                }
            }
        }.getOrDefault(emptyMap())
    }
}
