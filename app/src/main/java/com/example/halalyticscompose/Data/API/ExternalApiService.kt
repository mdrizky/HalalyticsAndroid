package com.example.halalyticscompose.Data.API

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Models for OpenBeautyFacts
data class OpenBeautyFactsResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("products") val products: List<BeautyProduct>
)

data class BeautyProduct(
    @SerializedName("id") val id: String,
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("ingredients_text") val ingredientsText: String?,
    @SerializedName("ingredients_text_en") val ingredientsTextEn: String? = null,
    @SerializedName("ingredients_text_with_allergens") val ingredientsTextWithAllergens: String? = null,
    @SerializedName("ingredients_text_with_allergens_en") val ingredientsTextWithAllergensEn: String? = null,
    @SerializedName("quantity") val quantity: String? = null,
    @SerializedName("packaging") val packaging: String? = null,
    @SerializedName("categories") val categories: String? = null,
    @SerializedName("countries") val countries: String? = null,
    @SerializedName("image_url") val imageUrl: String?
)

val BeautyProduct.bestIngredientsText: String?
    get() = ingredientsText
        ?: ingredientsTextEn
        ?: ingredientsTextWithAllergens
        ?: ingredientsTextWithAllergensEn

interface ExternalApiService {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    suspend fun searchBeautyProducts(
        @Query("search_terms") query: String,
        @Query("page") page: Int = 1
    ): Response<OpenBeautyFactsResponse>
}
