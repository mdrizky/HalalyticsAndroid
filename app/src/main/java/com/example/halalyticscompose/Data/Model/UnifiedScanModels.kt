package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class UnifiedScanResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("source") val source: String,
    @SerializedName("data") val data: UnifiedProductData?,
    @SerializedName("message") val message: String,
    @SerializedName("needs_verification") val needsVerification: Boolean = false,
    @SerializedName("halal_issues") val halalIssues: List<String> = emptyList(),
    @SerializedName("action") val action: String? = null,
    @SerializedName("instructions") val instructions: List<String> = emptyList()
)

data class UnifiedProductData(
    @SerializedName("id") val id: Int,
    @SerializedName("nama_product") val namaProduct: String,
    @SerializedName("barcode") val barcode: String,
    @SerializedName("image") val image: String?,
    @SerializedName("halal_status") val halalStatus: String,
    @SerializedName("verification_status") val verificationStatus: String,
    @SerializedName("source") val source: String,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("komposisi") val komposisi: List<String>?,
    @SerializedName("info_gizi") val infoGizi: Map<String, Any>?,
    @SerializedName("kategori") val kategori: String?
)
