package com.example.halalyticscompose.data.model

data class Banner(
    val id: Int,
    val title: String,
    val description: String?,
    val image: String,
    val position: Int,
    val action_type: String? = null,
    val action_value: String? = null
)

data class BannerResponse(
    val success: Boolean,
    val data: List<Banner>,
    val message: String
)
