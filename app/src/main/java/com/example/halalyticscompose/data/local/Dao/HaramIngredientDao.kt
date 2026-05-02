package com.example.halalyticscompose.data.local.Dao

import androidx.room.*
import com.example.halalyticscompose.data.local.Entities.HaramIngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HaramIngredientDao {
    @Query("SELECT * FROM haram_ingredients WHERE is_active = 1 ORDER BY severity DESC, name ASC")
    fun getAllActive(): Flow<List<HaramIngredientEntity>>

    @Query("SELECT * FROM haram_ingredients WHERE is_active = 1 ORDER BY severity DESC, name ASC")
    suspend fun getAllActiveList(): List<HaramIngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<HaramIngredientEntity>)

    @Query("SELECT * FROM haram_ingredients WHERE name LIKE :query OR aliases LIKE '%' || :query || '%' LIMIT 10")
    suspend fun searchIngredients(query: String): List<HaramIngredientEntity>

    @Query("SELECT MAX(updated_at) FROM haram_ingredients")
    suspend fun getLastUpdated(): Long?

    @Query("DELETE FROM haram_ingredients")
    suspend fun clearAll()
}
