package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

data class Contribution(
    @SerializedName("id") val id: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("barcode") val barcode: String?,
    @SerializedName("ingredients") val ingredients: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("status") val status: String,
    @SerializedName("admin_note") val adminNote: String?,
    @SerializedName("created_at") val createdAt: String
)

data class ContributionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Contribution?
)

data class MyContributionsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Contribution>
)
