package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class FamilyProfile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("relationship")
    val relationship: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("allergies")
    val allergies: String?,
    @SerializedName("medical_history")
    val medicalHistory: String?,
    @SerializedName("image_path")
    val imagePath: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class FamilyListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<FamilyProfile>
)

data class FamilyDetailResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: FamilyProfile
)
