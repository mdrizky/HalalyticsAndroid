package com.example.halalyticscompose.repository

import com.example.halalyticscompose.Data.API.ApiService
import com.example.halalyticscompose.Data.Model.ArPOI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getNearbyPOIs(token: String, lat: Double, lng: Double): Result<List<ArPOI>> {
        return try {
            val response = api.getNearbyForAr("Bearer $token", lat, lng)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Fetch POIs failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
