package com.example.halalyticscompose.data.local.Dao

import androidx.room.*
import com.example.halalyticscompose.data.local.Entities.CachedScanResult
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedScanResultDao {
    
    @Query("SELECT * FROM cached_scan_results ORDER BY scannedAt DESC")
    fun getAllCached(): Flow<List<CachedScanResult>>
    
    @Query("SELECT * FROM cached_scan_results WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): CachedScanResult?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: CachedScanResult): Long
    
    @Update
    suspend fun update(result: CachedScanResult)
    
    @Query("DELETE FROM cached_scan_results WHERE scannedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM cached_scan_results")
    suspend fun getCount(): Int
}
