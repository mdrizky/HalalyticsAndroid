package com.example.halalyticscompose.Data.API

import com.example.halalyticscompose.Data.Model.LoginModel
import com.example.halalyticscompose.Data.Model.ScanModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // 🔹 LOGIN
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginModel

    // 🔹 REGISTER
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("full_name") fullName: String,
        @Field("phone") phone: String,
        @Field("blood_type") bloodType: String,
        @Field("allergy") allergy: String,
        @Field("medical_history") medicalHistory: String
    ): LoginModel

    // 🔹 GET PROFILE
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") bearer: String
    ): LoginModel

    // 🔹 UPDATE PROFILE
    @FormUrlEncoded
    @POST("profile/update")
    suspend fun updateProfile(
        @Header("Authorization") bearer: String,
        @Field("username") username: String,
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("blood_type") bloodType: String,
        @Field("allergy") allergy: String,
        @Field("medical_history") medicalHistory: String
    ): LoginModel

    // 🔹 UPDATE PROFILE PICTURE
    @Multipart
    @POST("profile/picture")
    suspend fun updateProfilePicture(
        @Header("Authorization") bearer: String,
        @Part image: MultipartBody.Part
    ): LoginModel

    // 🔹 GET USER STATS
    @GET("stats")
    suspend fun getUserStats(
        @Header("Authorization") bearer: String
    ): LoginModel

    // 🔹 SCAN BY BARCODE
    @GET("scans/{barcode}")
    suspend fun scanByBarcode(
        @retrofit2.http.Path("barcode") barcode: String
    ): ScanModel

    // 🔹 STORE SCAN
    @FormUrlEncoded
    @POST("scans")
    suspend fun storeScan(
        @Header("Authorization") bearer: String,
        @Field("product_id") productId: Int,
        @Field("nama_produk") namaProduk: String,
        @Field("barcode") barcode: String?,
        @Field("kategori") kategori: String?,
        @Field("status_halal") statusHalal: String,
        @Field("status_kesehatan") statusKesehatan: String,
        @Field("tanggal_expired") tanggalExpired: String?
    ): ScanModel

    // 🔹 GET MY SCANS
    @GET("scans")
    suspend fun getMyScans(
        @Header("Authorization") bearer: String
    ): ScanModel
}
