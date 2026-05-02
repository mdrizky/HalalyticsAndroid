package com.example.halalyticscompose.feature.expansion.network

import com.example.halalyticscompose.data.model.ApiResponse
import com.example.halalyticscompose.feature.expansion.model.CommunityComment
import com.example.halalyticscompose.feature.expansion.model.CommunityLeaderboardEntry
import com.example.halalyticscompose.feature.expansion.model.CommunityPost
import com.example.halalyticscompose.feature.expansion.model.CommunityPostDetail
import com.example.halalyticscompose.feature.expansion.model.HalocodeConsultation
import com.example.halalyticscompose.feature.expansion.model.HalocodeExpert
import com.example.halalyticscompose.feature.expansion.model.HalocodeMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ExpansionApiService {
    @GET("experts")
    suspend fun getExperts(
        @Header("Authorization") bearer: String,
    ): Response<ApiResponse<List<HalocodeExpert>>>

    @GET("experts/{id}")
    suspend fun getExpertDetail(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Response<ApiResponse<HalocodeExpert>>

    @POST("consultations")
    suspend fun createConsultation(
        @Header("Authorization") bearer: String,
        @Body body: Map<String, Int>,
    ): Response<ApiResponse<HalocodeConsultation>>

    @GET("consultations/history")
    suspend fun getConsultationHistory(
        @Header("Authorization") bearer: String,
    ): Response<ApiResponse<List<HalocodeConsultation>>>

    @GET("messages/{consultationId}")
    suspend fun getMessages(
        @Header("Authorization") bearer: String,
        @Path("consultationId") consultationId: Int,
    ): Response<ApiResponse<List<HalocodeMessage>>>

    @POST("messages/{consultationId}")
    suspend fun sendMessage(
        @Header("Authorization") bearer: String,
        @Path("consultationId") consultationId: Int,
        @Body body: Map<String, String>,
    ): Response<ApiResponse<HalocodeMessage>>

    @POST("consultations/{id}/end")
    suspend fun endConsultation(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Response<ApiResponse<HalocodeConsultation>>

    @POST("expert/toggle-online")
    suspend fun toggleOnline(
        @Header("Authorization") bearer: String,
    ): Response<ApiResponse<Map<String, @JvmSuppressWildcards Any>>>

    @GET("expert/queue")
    suspend fun getExpertQueue(
        @Header("Authorization") bearer: String,
    ): Response<ApiResponse<List<HalocodeConsultation>>>

    @GET("community/posts")
    suspend fun getPosts(
        @Header("Authorization") bearer: String,
        @Query("category") category: String? = null,
        @Query("sort") sort: String = "latest",
        @Query("page") page: Int = 1,
    ): Response<ApiResponse<List<CommunityPost>>>

    @Multipart
    @POST("community/posts")
    suspend fun createPost(
        @Header("Authorization") bearer: String,
        @Part("content") content: RequestBody,
        @Part("category") category: RequestBody,
        @Part("title") title: RequestBody? = null,
        @Part image: MultipartBody.Part? = null,
    ): Response<ApiResponse<CommunityPost>>

    @GET("community/posts/{id}")
    suspend fun getPostDetail(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Response<ApiResponse<CommunityPostDetail>>

    @POST("community/posts/{id}/like")
    suspend fun likePost(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
    ): Response<ApiResponse<Unit>>

    @POST("community/posts/{id}/comment")
    suspend fun addComment(
        @Header("Authorization") bearer: String,
        @Path("id") id: Int,
        @Body body: Map<String, String?>,
    ): Response<ApiResponse<CommunityComment>>

    @GET("community/leaderboard")
    suspend fun getLeaderboard(
        @Header("Authorization") bearer: String,
    ): Response<ApiResponse<List<CommunityLeaderboardEntry>>>
}
