package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

data class ScanModel(
    @SerializedName("response_code")
    val response_code: Int = 0,
    
    @SerializedName("success")
    val success: Boolean = false,
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("content")
    val content: ScanContent? = null
) {
    data class ScanContent(
        @SerializedName("id_scan")
        val id_scan: Int = 0,
        
        @SerializedName("user_id")
        val user_id: Int = 0,
        
        @SerializedName("product_id")
        val product_id: Int? = null,
        
        @SerializedName("nama_produk")
        val nama_produk: String = "",
        
        @SerializedName("barcode")
        val barcode: String? = null,
        
        @SerializedName("kategori")
        val kategori: String? = null,
        
        @SerializedName("status_halal")
        val status_halal: String = "",
        
        @SerializedName("status_kesehatan")
        val status_kesehatan: String = "",
        
        @SerializedName("tanggal_expired")
        val tanggal_expired: String? = null,
        
        @SerializedName("tanggal_scan")
        val tanggal_scan: String? = null,
        
        @SerializedName("created_at")
        val created_at: String? = null
    )
}

// ⚠️ NEW: Response from recordScan
data class ScanResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("scan_id") val scanId: Int?,
    @SerializedName("health_warning") val healthWarning: String?
)