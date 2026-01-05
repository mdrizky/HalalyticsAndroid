    package com.example.halalyticscompose.Data.Model

data class ScanModel(

        val content: List<ScanContent>,
        val message: String,
        val response_code: Int
) {
    data class ScanContent(
        val barcode: String?,
        val id_scan: Int,
        val kategori: String?,
        val nama_produk: String,
        val product_id: Int?,
        val status_halal: String,
        val status_kesehatan: String,
        val tanggal_expired: String?,
        val tanggal_scan: String,
        val user_id: Int
    )
}