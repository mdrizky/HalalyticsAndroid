package com.example.halalyticscompose.data.api

import com.example.halalyticscompose.data.model.ProductItem
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class OpenFoodFactsSearchResponse(
    @SerializedName("count") val count: Int? = 0,
    @SerializedName("page") val page: Int? = 1,
    @SerializedName("products") val products: List<ProductItem> = emptyList()
)

data class OpenFoodFactsDetailResponse(
    @SerializedName("status") val status: Int? = 0,
    @SerializedName("product") val product: ProductItem? = null
)

interface OpenFoodFactsApiService {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<OpenFoodFactsSearchResponse>

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductDetail(
        @Path("barcode") barcode: String
    ): Response<OpenFoodFactsDetailResponse>
}
