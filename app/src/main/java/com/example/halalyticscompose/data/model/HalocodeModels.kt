package com.example.halalyticscompose.data.model

import com.google.gson.annotations.SerializedName

data class Consultation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("payment_token")
    val paymentToken: String? = null,
    @SerializedName("amount")
    val amount: Int = 0,
    @SerializedName("started_at")
    val startedAt: String? = null,
    @SerializedName("expert")
    val expert: Expert? = null,
)

data class Message(
    @SerializedName("id")
    val id: Int,
    @SerializedName("consultation_id")
    val consultationId: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("attachment_path")
    val attachmentPath: String? = null,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
)
