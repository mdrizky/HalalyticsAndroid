package com.example.halalyticscompose.Data.Model

data class Banner(
    val id: Int,
    val title: String,
    val description: String?,
    val image: String,
    val position: Int
)

data class BannerResponse(
    val success: Boolean,
    val data: List<Banner>,
    val message: String
)
