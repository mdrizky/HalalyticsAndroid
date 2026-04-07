package com.example.halalyticscompose.Data.Local.Dao

import androidx.room.*
import com.example.halalyticscompose.Data.Local.Entities.HaramIngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HaramIngredientDao {
    @Query("SELECT * FROM haram_ingredients WHERE isActive = 1")
    fun getAllActive(): Flow<List<HaramIngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<HaramIngredientEntity>)

    @Query("SELECT * FROM haram_ingredients WHERE name LIKE :query OR aliases LIKE '%' || :query || '%' LIMIT 10")
    suspend fun searchIngredients(query: String): List<HaramIngredientEntity>

    @Query("SELECT MAX(updatedAt) FROM haram_ingredients")
    suspend fun getLastUpdated(): Long?

    @Query("DELETE FROM haram_ingredients")
    suspend fun clearAll()
}
