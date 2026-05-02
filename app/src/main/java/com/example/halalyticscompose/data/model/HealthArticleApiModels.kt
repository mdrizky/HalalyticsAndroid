package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

data class HealthArticleApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<HealthArticleItem>? = null
)

data class HealthArticleDetailApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: HealthArticleItem? = null
)

data class HealthArticleItem(
    @SerializedName("id") val id: String,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("excerpt") val excerpt: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("ai_summary") val aiSummary: String? = null,
    @SerializedName("published_at") val publishedAt: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("source_url") val sourceUrl: String? = null
)

