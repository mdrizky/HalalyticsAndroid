package com.example.halalyticscompose.repository

import com.example.halalyticscompose.data.api.ApiService
import com.example.halalyticscompose.data.model.DailyMissionData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun getDailyMission(token: String): Result<DailyMissionData> {
        return try {
            val response = api.getDailyMission("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: DailyMissionData(emptyList(), 0, 0, 0))
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal memuat misi harian"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeMission(token: String, missionId: String): Result<Unit> {
        return try {
            val response = api.completeMission("Bearer $token", mapOf("mission_id" to missionId))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menyelesaikan misi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
