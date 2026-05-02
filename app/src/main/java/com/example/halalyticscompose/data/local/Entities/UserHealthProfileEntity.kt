package com.example.halalyticscompose.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_health_profile")
data class UserHealthProfileEntity(
    @PrimaryKey val userId: Int,
    val allergies: List<String> = emptyList(),
    val dietConditions: List<String> = emptyList(),
    val avoidIngredients: List<String> = emptyList(),
    val updatedAt: Long,
)
