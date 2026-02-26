package com.example.halalyticscompose.Data.Local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consumption_history")
data class Consumption(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val productName: String,
    val totalSugar: Double,
    val totalSodium: Double,
    val isHalal: Boolean = true,
    val userId: Int
)
