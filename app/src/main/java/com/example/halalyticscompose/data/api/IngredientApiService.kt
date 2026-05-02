package com.example.halalyticscompose.data.api

import retrofit2.Response
import retrofit2.http.*
import com.example.halalyticscompose.data.model.ApiResponse



data class Ingredient(
    val id_ingredient: Int,
    val name: String,
    val e_number: String? = null,
    val halal_status: String,
    val health_risk: String,
    val description: String? = null,
    val sources: String? = null,
    val notes: String? = null,
    val active: Boolean = true
)

data class IngredientPagination(
    val current_page: Int,
    val data: List<Ingredient>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String?,
    val path: String,
    val per_page: Int,
    val prev_page_url: String?,
    val to: Int,
    val total: Int
)

interface IngredientApiService {
    
    @GET("local/encyclopedia")
    suspend fun getIngredients(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("risk") risk: String? = null,
        @Query("page") page: Int = 1
    ): Response<ApiResponse<IngredientPagination>>
    
    @GET("local/encyclopedia/{id}")
    suspend fun getIngredientDetail(
        @Path("id") id: Int
    ): Response<ApiResponse<Ingredient>>
    
    @GET("local/encyclopedia/e-number/{eNumber}")
    suspend fun getIngredientByENumber(
        @Path("eNumber") eNumber: String
    ): Response<ApiResponse<Ingredient>>
}
