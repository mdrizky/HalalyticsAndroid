package com.example.halalyticscompose.utils

/**
 * Sealed class wrapper for network API results.
 * Used consistently across all ViewModels to handle loading, success, error, and empty states.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
    class Empty<T> : NetworkResult<T>()

    val isLoading get() = this is Loading
    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isEmpty get() = this is Empty

    fun getOrNull(): T? = (this as? Success)?.data
    fun errorMessageOrNull(): String? = (this as? Error)?.message
}

/**
 * Maps HTTP error codes to user-friendly messages (Bahasa Indonesia).
 */
object ApiErrorHandler {
    fun getErrorMessage(code: Int?, fallbackMessage: String? = null): String {
        return when (code) {
            401 -> "Sesi Anda telah berakhir. Silakan login kembali."
            403 -> "Akses tidak diizinkan."
            404 -> "Data tidak ditemukan."
            422 -> fallbackMessage ?: "Data yang dikirim tidak valid."
            429 -> "Terlalu banyak permintaan. Tunggu beberapa saat."
            500 -> "Terjadi kesalahan pada server. Coba lagi nanti."
            502, 503 -> "Server sedang dalam perbaikan. Coba lagi nanti."
            null -> fallbackMessage ?: "Koneksi timeout. Periksa internet Anda."
            else -> fallbackMessage ?: "Terjadi kesalahan (kode: $code)."
        }
    }

    fun isAuthError(code: Int?) = code == 401
    fun isRateLimited(code: Int?) = code == 429
    fun isServerError(code: Int?) = code != null && code >= 500
}
