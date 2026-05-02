package com.example.halalyticscompose.data.model

data class ProductImage(
    val url: String,
    val type: String,        // "external_api", "unsplash", "placeholder"
    val label: String,
    val credit: String? = null
)

data class ProductImageResult(
    val source: String,      // sumber foto: "external_api", "unsplash", "placeholder"
    val images: List<ProductImage>
)

data class UnsplashSearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashUrls,
    val user: UnsplashUser
)

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String
)
