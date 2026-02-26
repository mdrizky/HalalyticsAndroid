package com.example.halalyticscompose.Data.Model

import com.google.gson.annotations.SerializedName

data class User(
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
    
    @SerializedName("blood_type")
    val bloodType: String?,
    
    @SerializedName("allergy")
    val allergy: String?,
    
    @SerializedName("medical_history")
    val medicalHistory: String?,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("active")
    val active: Boolean,
    
    @SerializedName("image")
    val image: String?,
    
    @SerializedName("goal")
    val goal: String?,
    
    @SerializedName("diet_preference")
    val dietPreference: String?,
    
    @SerializedName("activity_level")
    val activityLevel: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("language")
    val language: String?,
    
    @SerializedName("age")
    val age: Int?,
    
    @SerializedName("height")
    val height: Double?,
    
    @SerializedName("weight")
    val weight: Double?,
    
    @SerializedName("bmi")
    val bmi: Double?,
    
    @SerializedName("notif_enabled")
    val notifEnabled: Boolean?,
    
    @SerializedName("dark_mode")
    val darkMode: Boolean?,
    
    @SerializedName("bio")
    val bio: String?,
    
    @SerializedName("gender")
    val gender: String? = null,
    
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)
