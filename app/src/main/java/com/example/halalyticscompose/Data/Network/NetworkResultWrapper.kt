package com.example.halalyticscompose.Data.Network

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val fieldErrors: Map<String, List<String>> = emptyMap(),
        val retryAfterSeconds: Int? = null
    ) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
    class Empty<T> : NetworkResult<T>()
}
