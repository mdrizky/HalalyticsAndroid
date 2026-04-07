package com.example.halalyticscompose.Data.Local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.halalyticscompose.Data.Local.Converters

@Entity(tableName = "haram_ingredients")
data class HaramIngredientEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val aliases: String?, // Stored as comma-separated or via Converter
    val category: String,
    val severity: Int,
    val description: String?,
    val isActive: Boolean,
    val updatedAt: Long
)
