package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class CertificateInfo(
    @SerializedName("certificate_number") val certificateNumber: String,
    @SerializedName("product_name") val productName: String?,
    @SerializedName("manufacturer") val manufacturer: String?,
    @SerializedName("expiry_date") val expiryDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("issuer") val issuer: String
)

data class CertificateVerificationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CertificateInfo?
)

data class CertificateHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<CertificateInfo>
)
