package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AddonType {
    RESOURCE_PACK,
    BEHAVIOR_PACK,
    WORLD_TEMPLATE
}

@Entity(tableName = "addons")
data class AddonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val type: AddonType,
    val version: String = "1.0.0",
    val isEnabled: Boolean = true,
    val isCurated: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val iconEmoji: String = "📦"
)
