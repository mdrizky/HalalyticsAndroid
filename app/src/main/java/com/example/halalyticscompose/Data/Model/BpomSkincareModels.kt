package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

// ==================== BPOM Models ====================

data class BpomSearchResponse(
    val success: Boolean,
    val source: String? = null,
    val total: Int? = null,
    val data: List<BpomProduct>? = null,
    @SerializedName("ai_analysis") val aiAnalysis: Map<String, Any?>? = null,
    @SerializedName("session_info") val sessionInfo: SessionInfo? = null,
    val message: String? = null
)

data class BpomCheckResponse(
    val success: Boolean,
    val source: String? = null,
    val data: BpomProduct? = null,
    @SerializedName("ai_analysis") val aiAnalysis: Map<String, Any?>? = null,
    @SerializedName("session_info") val sessionInfo: SessionInfo? = null,
    val message: String? = null,
    @SerializedName("kategori_terdeteksi") val kategoriTerdeteksi: String? = null
)

data class BpomAnalyzeResponse(
    val success: Boolean,
    val data: BpomProduct? = null,
    val analysis: Map<String, Any?>? = null,
    @SerializedName("session_info") val sessionInfo: SessionInfo? = null,
    val message: String? = null
)

data class BpomProduct(
    val id: Int? = null,
    @SerializedName("nomor_reg") val nomorReg: String? = null,
    val kategori: String? = null,
    @SerializedName("nama_produk") val namaProduk: String? = null,
    val merk: String? = null,
    val pendaftar: String? = null,
    @SerializedName("alamat_produsen") val alamatProdusen: String? = null,
    val kemasan: String? = null,
    @SerializedName("bentuk_sediaan") val bentukSediaan: String? = null,
    @SerializedName("tanggal_terbit") val tanggalTerbit: String? = null,
    @SerializedName("masa_berlaku") val masaBerlaku: String? = null,
    @SerializedName("ingredients_text") val ingredientsText: String? = null,
    @SerializedName("analisis_halal") val analisisHalal: String? = null,
    @SerializedName("analisis_kandungan") val analisisKandungan: String? = null,
    @SerializedName("status_keamanan") val statusKeamanan: String? = null,
    @SerializedName("skor_keamanan") val skorKeamanan: Int? = null,
    @SerializedName("status_halal") val statusHalal: String? = null,
    @SerializedName("sumber_data") val sumberData: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    val barcode: String? = null,
    @SerializedName("verification_status") val verificationStatus: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class SessionInfo(
    val sumber: String? = null,
    val referensi: String? = null,
    val disclaimer: String? = null,
    val status: String? = null
)

// ==================== Skincare Models ====================

data class SkincareAnalysisResponse(
    val success: Boolean,
    @SerializedName("ingredients_text") val ingredientsText: String? = null,
    val analysis: SkincareAnalysis? = null,
    @SerializedName("ingredients_detected") val ingredientsDetected: List<IngredientIndicator>? = null,
    @SerializedName("score_safety") val scoreSafety: Int? = null,
    @SerializedName("status_safety") val statusSafety: String? = null,
    @SerializedName("status_halal") val statusHalal: String? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("disclaimer") val disclaimer: String? = null,
    @SerializedName("session_info") val sessionInfo: SessionInfo? = null,
    val message: String? = null
)

data class SkincareAnalysis(
    @SerializedName("bahan_terdeteksi") val bahanTerdeteksi: List<IngredientInfo>? = null,
    @SerializedName("bahan_berbahaya") val bahanBerbahaya: List<String>? = null,
    @SerializedName("bahan_syubhat") val bahanSyubhat: List<String>? = null,
    @SerializedName("skor_keamanan") val skorKeamanan: Int? = null,
    @SerializedName("status_keamanan") val statusKeamanan: String? = null,
    @SerializedName("status_halal") val statusHalal: String? = null,
    @SerializedName("cocok_untuk") val cocokUntuk: List<String>? = null,
    @SerializedName("tidak_cocok_untuk") val tidakCocokUntuk: List<String>? = null,
    val ringkasan: String? = null,
    val disclaimer: String? = null
)

data class IngredientInfo(
    val nama: String? = null,
    @SerializedName("nama_umum") val namaUmum: String? = null,
    val fungsi: String? = null,
    @SerializedName("tingkat_bahaya") val tingkatBahaya: Int? = null,
    @SerializedName("status_halal") val statusHalal: String? = null,
    @SerializedName("catatan_halal") val catatanHalal: String? = null,
    val peringatan: String? = null
)

data class IngredientIndicator(
    @SerializedName("name") val name: String? = null,
    @SerializedName("safety_level") val safetyLevel: String? = null,
    @SerializedName("halal_status") val halalStatus: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("color_code") val colorCode: String? = null
)

data class SafetyCheckResponse(
    val success: Boolean,
    @SerializedName("status_keamanan") val statusKeamanan: String? = null,
    @SerializedName("bahan_berbahaya_terdeteksi") val bahanBerbahayaTerdeteksi: List<DangerousIngredient>? = null,
    @SerializedName("jumlah_bahaya") val jumlahBahaya: Int? = null,
    val pesan: String? = null
)

data class DangerousIngredient(
    val bahan: String? = null,
    val peringatan: String? = null
)

data class HalalCheckSkincareResponse(
    val success: Boolean,
    @SerializedName("status_halal") val statusHalal: String? = null,
    @SerializedName("bahan_kritis") val bahanKritis: List<CriticalIngredient>? = null,
    @SerializedName("jumlah_kritis") val jumlahKritis: Int? = null,
    val pesan: String? = null,
    val disclaimer: String? = null
)

data class CriticalIngredient(
    val bahan: String? = null,
    val status: String? = null,
    val alasan: String? = null
)
