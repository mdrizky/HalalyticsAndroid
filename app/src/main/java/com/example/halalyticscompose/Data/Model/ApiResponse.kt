package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Global API Response wrapper.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("errors")
    val errors: List<String>? = null,
    
    @SerializedName("meta")
    val meta: ResponseMeta? = null
)

data class ResponseMeta(
    @SerializedName("current_page")
    val currentPage: Int? = null,
    
    @SerializedName("total_pages")
    val totalPages: Int? = null,
    
    @SerializedName("total_items")
    val totalItems: Int? = null
)
