package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

/**
 * Auth related request/response models
 */
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

typealias LoginResponse = LoginModel

data class ProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: User?,
    @SerializedName("message") val message: String? = null
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    @SerializedName("name") val fullName: String,
    @SerializedName("phone_number") val phone: String? = null,
    @SerializedName("blood_type") val bloodType: String? = null,
    @SerializedName("allergies") val allergy: String? = null,
    @SerializedName("medical_history") val medicalHistory: String? = null
)
