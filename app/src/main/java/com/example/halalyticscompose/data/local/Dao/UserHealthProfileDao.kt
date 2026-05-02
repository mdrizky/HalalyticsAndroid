package com.example.halalyticscompose.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.halalyticscompose.data.local.Entities.UserHealthProfileEntity

@Dao
interface UserHealthProfileDao {
    @Query("SELECT * FROM user_health_profile WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: Int): UserHealthProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserHealthProfileEntity)

    @Query("DELETE FROM user_health_profile")
    suspend fun clearAll()
}
