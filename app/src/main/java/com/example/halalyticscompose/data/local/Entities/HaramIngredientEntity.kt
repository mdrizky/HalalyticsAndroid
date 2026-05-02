package com.example.halalyticscompose.data.local.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "haram_ingredients")
data class HaramIngredientEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val aliases: List<String> = emptyList(),
    val category: String,
    val severity: Int,
    val description: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
