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
     * Search products
     * GET /api/external/search?query=coca&page_size=20&page=1
     */
    @GET("external/search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Get product detail by barcode
     * GET /api/external/product/{barcode}
     */
    @GET("external/product/{barcode}")
    suspend fun getProductDetail(
        @Path("barcode") barcode: String
    ): Response<BaseResponse<ProductItem>>
    
    /**
     * Search halal products
     * GET /api/external/halal?query=chicken
     */
    @GET("external/halal")
    suspend fun searchHalalProducts(
        @Query("query") query: String = "",
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Search vegetarian products
     * GET /api/external/vegetarian?query=tofu
     */
    @GET("external/vegetarian")
    suspend fun searchVegetarianProducts(
        @Query("query") query: String = "",
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Search vegan products
     * GET /api/external/vegan?query=almond
     */
    @GET("external/vegan")
    suspend fun searchVeganProducts(
        @Query("query") query: String = "",
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Get products by brand
     * GET /api/external/brand/nestle
     */
    @GET("external/brand/{brand}")
    suspend fun getProductsByBrand(
        @Path("brand") brand: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
    
    /**
     * Get products by category
     * GET /api/external/category/beverages
     */
    @GET("external/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("page") page: Int = 1
    ): Response<BaseResponse<ExternalSearchResponse>>
}
