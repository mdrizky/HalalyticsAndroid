package com.example.halalyticscompose.feature.expansion.model

import com.google.gson.annotations.SerializedName

data class HalocodeExpert(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("photo_url")
    val photoUrl: String? = null,
    @SerializedName("specialization")
    val specialization: String,
    @SerializedName("bio")
    val bio: String? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("is_online")
    val isOnline: Boolean = false,
    @SerializedName("price_per_session")
    val pricePerSession: Int = 0,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("total_reviews")
    val totalReviews: Int = 0,
)

data class HalocodeConsultation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("payment_token")
    val paymentToken: String? = null,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("payment_is_mock")
    val paymentIsMock: Boolean = false,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("started_at")
    val startedAt: String? = null,
    @SerializedName("ended_at")
    val endedAt: String? = null,
    @SerializedName("payment_redirect_url")
    val paymentRedirectUrl: String? = null,
    @SerializedName("expert")
    val expert: HalocodeExpert? = null,
)

data class HalocodeMessage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("consultation_id")
    val consultationId: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("sender_name")
    val senderName: String? = null,
    @SerializedName("message")
    val message: String,
    @SerializedName("attachment_path")
    val attachmentPath: String? = null,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
)

data class MarketplaceMerchant(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("website")
    val website: String? = null,
    @SerializedName("affiliate_link")
    val affiliateLink: String? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("distance")
    val distance: Double? = null,
)

data class MarketplaceProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("merchant_id")
    val merchantId: Int,
    @SerializedName("merchant_name")
    val merchantName: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: Int,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("is_halal_certified")
    val isHalalCertified: Boolean = false,
    @SerializedName("halal_cert_number")
    val halalCertNumber: String? = null,
    @SerializedName("stock")
    val stock: Int = 0,
)

data class HealthFacility(
    @SerializedName("place_id")
    val placeId: String? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("types")
    val types: List<String> = emptyList(),
    @SerializedName("rating")
    val rating: Float? = null,
    @SerializedName("is_open")
    val isOpen: Boolean? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
)

data class CommunityBadge(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("icon_url")
    val iconUrl: String? = null,
    @SerializedName("earned_at")
    val earnedAt: String? = null,
)

data class CommunityPost(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_photo")
    val userPhoto: String? = null,
    @SerializedName("user_badge")
    val userBadge: String? = null,
    @SerializedName("user_level")
    val userLevel: String = "Pemula",
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("category")
    val category: String,
    @SerializedName("hashtags")
    val hashtags: List<String> = emptyList(),
    @SerializedName("likes_count")
    val likesCount: Int = 0,
    @SerializedName("comments_count")
    val commentsCount: Int = 0,
    @SerializedName("is_pinned")
    val isPinned: Boolean = false,
    @SerializedName("is_liked_by_me")
    val isLikedByMe: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
)

data class CommunityComment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_photo")
    val userPhoto: String? = null,
    @SerializedName("parent_id")
    val parentId: Int? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("likes_count")
    val likesCount: Int = 0,
    @SerializedName("replies")
    val replies: List<CommunityComment> = emptyList(),
    @SerializedName("created_at")
    val createdAt: String,
)

data class CommunityPostDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_photo")
    val userPhoto: String? = null,
    @SerializedName("user_badge")
    val userBadge: String? = null,
    @SerializedName("user_level")
    val userLevel: String = "Pemula",
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("category")
    val category: String,
    @SerializedName("hashtags")
    val hashtags: List<String> = emptyList(),
    @SerializedName("likes_count")
    val likesCount: Int = 0,
    @SerializedName("comments_count")
    val commentsCount: Int = 0,
    @SerializedName("is_pinned")
    val isPinned: Boolean = false,
    @SerializedName("is_liked_by_me")
    val isLikedByMe: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("comments")
    val comments: List<CommunityComment> = emptyList(),
)

data class CommunityLeaderboardEntry(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_photo")
    val userPhoto: String? = null,
    @SerializedName("total_points")
    val totalPoints: Int,
    @SerializedName("level")
    val level: String,
    @SerializedName("badges")
    val badges: List<CommunityBadge> = emptyList(),
)
