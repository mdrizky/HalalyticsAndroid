package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class AiReportResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("stats")
    val stats: WeeklyStats? = null,
    
    @SerializedName("insight")
    val insight: AiInsight? = null
)

data class WeeklyStats(
    @SerializedName("total_scans")
    val totalScans: Int,
    
    @SerializedName("halal_count")
    val halalCount: Int,
    
    @SerializedName("haram_count")
    val haramCount: Int,
    
    @SerializedName("syubhat_count")
    val syubhatCount: Int,
    
    @SerializedName("healthy_count")
    val healthyCount: Int,
    
    @SerializedName("unhealthy_count")
    val unhealthyCount: Int,
    
    @SerializedName("health_score")
    val healthScore: Int,
    
    @SerializedName("top_categories")
    val topCategories: Map<String, Int> = emptyMap(),
    
    @SerializedName("recent_products")
    val recentProducts: List<String> = emptyList()
)

data class AiInsight(
    @SerializedName("summary")
    val summary: String? = null,
    
    @SerializedName("tips")
    val tips: List<String> = emptyList(),
    
    @SerializedName("highlight")
    val highlight: String? = null,
    
    @SerializedName("error")
    val error: String? = null
)
