package com.example.halalyticscompose.Data.Model

data class LoginModel(
    val access_token: String,
    val content: LoginContent,
    val message: String,
    val response_code: Int,
    val token_type: String
) {

    data class LoginContent(
        val id_user: Int,
        val username: String,
        val full_name: Any?,
        val email: String,
        val phone: Any?,
        val blood_type: Any?,
        val allergy: Any?,
        val medical_history: Any?,
        val role: String,
        val active: Boolean,
        val created_at: String,
        val updated_at: String,
        val last_login: String?,
        val image: String?,


        // 🔥 tambahan field baru (sinkron sama kolom baru di tabel users)
        val goal: String?,               // Tujuan utama user
        val diet_preference: String?,    // Preferensi diet
        val activity_level: String?,     // Level keaktifan (aktif, moderate, sedentary)
        val address: String?,            // Lokasi user
        val language: String?,           // Bahasa aplikasi
        val age: Int?,                   // Umur user
        val height: Float?,              // Tinggi badan (cm)
        val weight: Float?,              // Berat badan (kg)
        val bmi: Float?,                 // Nilai BMI
        val total_scan: Int?,            // Jumlah scan yang pernah dilakukan
        val halal_count: Int?,           // Total hasil scan halal
        val syubhat_count: Int?,         // Total hasil scan syubhat
        val streak: Int?,                // Streak penggunaan (hari berturut)
        val notif_enabled: Boolean?,     // Status notifikasi aktif / tidak
        val dark_mode: Boolean?          // Mode gelap aktif / tidak

    )
}
