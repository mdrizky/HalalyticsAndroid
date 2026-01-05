package com.example.halalyticscompose.Data.Network

import com.example.halalyticscompose.Data.Model.BaseResponse
import com.example.halalyticscompose.Data.Model.ExternalSearchResponse
import com.example.halalyticscompose.Data.Model.ProductItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExternalApiService {
    
    /**
     * Search all products
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Search halal products
     */
    @GET("products/search/halal")
    suspend fun searchHalalProducts(
        @Query("q") query: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Search vegetarian products
     */
    @GET("products/search/vegetarian")
    suspend fun searchVegetarianProducts(
        @Query("q") query: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Search vegan products
     */
    @GET("products/search/vegan")
    suspend fun searchVeganProducts(
        @Query("q") query: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Get product detail by barcode
     */
    @GET("products/{barcode}")
    suspend fun getProductDetail(
        @Path("barcode") barcode: String
    ): Response<BaseResponse<ProductItem>>
    
    /**
     * Get all products (no search)
     */
    @GET("products")
    suspend fun getAllProducts(
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
}
