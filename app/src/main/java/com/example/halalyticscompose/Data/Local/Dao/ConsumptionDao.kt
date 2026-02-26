package com.example.halalyticscompose.Data.Local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.halalyticscompose.Data.Local.Entities.Consumption
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionDao {
    @Insert
    suspend fun insertConsumption(consumption: Consumption)

    @Update
    suspend fun updateConsumption(consumption: Consumption)

    @Query("SELECT * FROM consumption_history WHERE date = :date AND userId = :userId")
    suspend fun getConsumptionByDate(date: String, userId: Int): List<Consumption>

    @Query("SELECT * FROM consumption_history WHERE userId = :userId ORDER BY date DESC")
    fun getAllConsumption(userId: Int): Flow<List<Consumption>>

    @Query("SELECT SUM(totalSugar) FROM consumption_history WHERE date = :date AND userId = :userId")
    suspend fun getTotalSugarByDate(date: String, userId: Int): Double?

    @Query("SELECT SUM(totalSodium) FROM consumption_history WHERE date = :date AND userId = :userId")
    suspend fun getTotalSodiumByDate(date: String, userId: Int): Double?
}
