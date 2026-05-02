package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

data class UserStatsResponse(
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("content")
    val content: UserStatsContent
)

data class UserStatsContent(
    @SerializedName("total_scans")
    val totalScans: Int,
    @SerializedName("halal_scans")
    val halalScans: Int,
    @SerializedName("syubhat_scans")
    val syubhatScans: Int,
    @SerializedName("haram_scans")
    val haramScans: Int,
    @SerializedName("total_reports")
    val totalReports: Int,
    @SerializedName("streak")
    val streak: StreakInfo? = null,
    @SerializedName("user_data")
    val userData: UserData
)

data class StreakInfo(
    @SerializedName("current")
    val current: Int = 0,
    @SerializedName("longest")
    val longest: Int = 0,
    @SerializedName("last_active")
    val lastActive: String? = null
)

data class UserData(
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("image")
    val image: String?
)
