package com.example.halalyticscompose.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Models for OpenBeautyFacts
data class OpenBeautyFactsResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("products") val products: List<BeautyProduct>
)

data class OpenBeautyFactsDetailResponse(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("product") val product: BeautyProduct? = null
)

data class BeautyProduct(
    @SerializedName(value = "id", alternate = ["_id", "code", "barcode"]) val id: String? = null,
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

val BeautyProduct.bestId: String?
    get() = id?.trim()?.takeIf { it.isNotBlank() }

val BeautyProduct.bestIngredientsText: String?
    get() = ingredientsText
        ?: ingredientsTextEn
        ?: ingredientsTextWithAllergens
        ?: ingredientsTextWithAllergensEn

interface OpenBeautyFactsApiService {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    suspend fun searchBeautyProducts(
        @Query("search_terms") query: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 100
    ): Response<OpenBeautyFactsResponse>

    @GET("api/v2/product/{productId}.json")
    suspend fun getBeautyProductDetail(
        @Path("productId") productId: String
    ): Response<OpenBeautyFactsDetailResponse>
}
