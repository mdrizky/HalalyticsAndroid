package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Models for Admin Dashboard and Product Management
 */
data class DashboardStatsResponse(
    val success: Boolean,
    val data: DashboardStats
)

data class DashboardStats(
    @SerializedName("total_products") val totalProducts: Int,
    @SerializedName("pending_approval") val pendingApproval: Int,
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("total_scans_today") val totalScansToday: Int,
    // Legacy/Extra fields
    @SerializedName("total_ocr_products") val totalOcrProducts: Int,
    @SerializedName("pending_review") val pendingReview: Int
)

data class PendingProductsResponse(
    val success: Boolean,
    val data: List<AdminProduct>
)

data class AdminProduct(
    val id: Int? = null,
    @SerializedName("id_product") val idProduct: Int,
    val barcode: String,
    @SerializedName("nama_product") val name: String,
    @SerializedName("approval_status") val approvalStatus: String,
    @SerializedName("caffeine_mg") val caffeineMg: Int,
    @SerializedName("sugar_g") val sugarG: Int,
    @SerializedName("halal_certificate") val halalCertificate: String? = null,
    @SerializedName("created_at") val createdAt: String
)

data class ApprovalResponse(
    val success: Boolean,
    val message: String
)
