package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("status")
    val status: String?,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("user", alternate = ["content"])
    val user: LoginContent?,

    val role: String?,

    @SerializedName("token", alternate = ["access_token"])
    val token: String?,

    @SerializedName("response_code")
    val responseCode: Int? = null
) {
    // Helper property to check if login is successful
    val isSuccess: Boolean
        get() = success == true || status == "success" || responseCode == 200

    // Helper property to get error message
    val errorMessage: String?
        get() = when {
            !message.isNullOrBlank() -> message
            status?.contains("Invalid", ignoreCase = true) == true -> status
            status?.contains("error", ignoreCase = true) == true -> status
            responseCode == 401 -> "Invalid credentials"
            else -> status
        }

    data class LoginContent(
        val id_user: Int,
        val username: String,
        val full_name: String?,
        val email: String,
        val phone: String?,
        val blood_type: String?,
        val allergy: String?,
        val medical_history: String?,
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
        val dark_mode: Boolean?,         // Mode gelap aktif / tidak
        val bio: String?,                // Biografi user
        val gender: String?              // Jenis kelamin user

    )
}
