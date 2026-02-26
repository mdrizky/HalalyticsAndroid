package com.example.halalyticscompose.Data.Local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.halalyticscompose.Data.Local.Converters

@Entity(tableName = "cached_scan_results")
@TypeConverters(Converters::class)
data class CachedScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val productName: String,
    val barcode: String?,
    val halalStatus: String,
    val healthScore: Int?,
    val calories: Int?,
    val ingredients: List<String>?,
    val halalNotes: String?,
    val imageUrl: String?,
    
    // Metadata
    val scannedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
