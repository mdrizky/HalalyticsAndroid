package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("content") val content: ReportData?,
    @SerializedName("is_suspicious") val isSuspicious: Boolean? = false
)

data class ReportData(
    @SerializedName("id_report") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("laporan") val laporan: String?,
    @SerializedName("evidence_image") val evidenceImage: String?,
    @SerializedName("status") val status: String,
    @SerializedName("admin_notes") val adminNotes: String?
)
