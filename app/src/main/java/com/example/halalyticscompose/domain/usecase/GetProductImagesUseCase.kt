package com.example.halalyticscompose.domain.usecase

import android.net.Uri
import com.example.halalyticscompose.BuildConfig
import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.ProductImage
import com.example.halalyticscompose.data.model.ProductImageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetProductImagesUseCase @Inject constructor(
    private val apiService: ApiService
) {
    suspend operator fun invoke(
        productName: String,
        barcode: String?,
        source: String
    ): ProductImageResult = withContext(Dispatchers.IO) {

        // Skenario A: ambil dari API eksternal dulu
        if (source == "external" && barcode != null) {
            try {
                val response = apiService.getOpenFoodFactsProduct(barcode)
                if (response.isSuccessful && response.body()?.status == 1) {
                    val product = response.body()?.product
                    val externalImages = mutableListOf<ProductImage>()
                    
                    product?.image_front_url?.let {
                        externalImages.add(ProductImage(it, "front", "Front"))
                    }
                    product?.image_ingredients_url?.let {
                        externalImages.add(ProductImage(it, "ingredients", "Ingredients"))
                    }
                    product?.image_nutrition_url?.let {
                        externalImages.add(ProductImage(it, "nutrition", "Nutrition"))
                    }
                    
                    if (externalImages.isNotEmpty()) {
                        return@withContext ProductImageResult(source = "external_api", images = externalImages)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback: cari dari Unsplash
        try {
            val accessKey = BuildConfig.UNSPLASH_API_KEY
            val unsplashResponse = apiService.searchUnsplashPhotos(
                query = buildQuery(productName),
                clientId = "Client-ID $accessKey"
            )
            
            if (unsplashResponse.isSuccessful) {
                val unsplashImages = unsplashResponse.body()?.results?.map { photo ->
                    ProductImage(
                        url = photo.urls.regular,
                        type = "unsplash",
                        label = "Foto ilustrasi",
                        credit = photo.user.name
                    )
                } ?: emptyList()
                
                if (unsplashImages.isNotEmpty()) {
                    return@withContext ProductImageResult(source = "unsplash", images = unsplashImages)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Ultimate fallback: placeholder
        return@withContext ProductImageResult(
            source = "placeholder",
            images = listOf(
                ProductImage(
                    url = "https://placehold.co/400x400/e2e8f0/64748b?text=${Uri.encode(productName)}",
                    type = "placeholder",
                    label = "Foto belum tersedia"
                )
            )
        )
    }

    private fun buildQuery(name: String): String {
        val lower = name.lowercase()
        return when {
            listOf("obat","tablet","kapsul","sirup","vitamin").any { lower.contains(it) } ->
                "$name medicine product"
            listOf("sabun","shampo","lotion","krim","parfum").any { lower.contains(it) } ->
                "$name cosmetic product"
            listOf("susu","jus","minuman","air","teh","kopi").any { lower.contains(it) } ->
                "$name drink beverage"
            listOf("mie","nasi","snack","biskuit","keripik").any { lower.contains(it) } ->
                "$name food snack"
            else -> "$name product packaging"
        }
    }
}
