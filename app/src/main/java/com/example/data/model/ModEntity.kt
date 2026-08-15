package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ModCategory {
    PERFORMANCE,
    PVP,
    UTILITY
}

@Entity(tableName = "mods")
data class ModEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: ModCategory,
    val isEnabled: Boolean,
    val iconName: String,
    val badgeText: String = "",
    val customValue: Float = 1.0f,
    val customColor: Long = 0xFF9D4EDD
)
