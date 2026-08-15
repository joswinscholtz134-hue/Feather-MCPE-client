package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String,
    val port: Int = 19132,
    val motd: String = "Bedrock Network",
    val pingMs: Int = -1,
    val isOnline: Boolean = true,
    val isFavorite: Boolean = false,
    val version: String = "1.21.x",
    val isDefault: Boolean = false
)
