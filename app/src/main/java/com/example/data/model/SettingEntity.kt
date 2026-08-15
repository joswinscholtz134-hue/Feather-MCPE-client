package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

enum class PerformanceProfile(
    val title: String,
    val description: String,
    val targetFps: Int,
    val renderDistanceChunks: Int,
    val particleQuality: String,
    val smoothLighting: Boolean,
    val fancyGraphics: Boolean,
    val entityShadows: Boolean,
    val fastMath: Boolean
) {
    ULTRA_FPS(
        title = "Ultra FPS",
        description = "Maximum frame-rates, stripped visuals, competitive focus",
        targetFps = 120,
        renderDistanceChunks = 6,
        particleQuality = "Minimal",
        smoothLighting = false,
        fancyGraphics = false,
        entityShadows = false,
        fastMath = true
    ),
    BALANCED(
        title = "Balanced",
        description = "Optimal 60 FPS balance between fidelity and fluidity",
        targetFps = 60,
        renderDistanceChunks = 12,
        particleQuality = "Decreased",
        smoothLighting = true,
        fancyGraphics = true,
        entityShadows = false,
        fastMath = true
    ),
    HIGH_FIDELITY(
        title = "High Quality",
        description = "Rich shaders, beautiful leaves and maximum visual depth",
        targetFps = 60,
        renderDistanceChunks = 18,
        particleQuality = "All",
        smoothLighting = true,
        fancyGraphics = true,
        entityShadows = true,
        fastMath = false
    ),
    BATTERY_SAVER(
        title = "Battery Saver",
        description = "30 FPS power efficiency mode for extended mobile gameplay",
        targetFps = 30,
        renderDistanceChunks = 8,
        particleQuality = "Minimal",
        smoothLighting = false,
        fancyGraphics = false,
        entityShadows = false,
        fastMath = true
    ),
    CUSTOM(
        title = "Custom",
        description = "User-tailored optimization and visual parameters",
        targetFps = 90,
        renderDistanceChunks = 10,
        particleQuality = "Decreased",
        smoothLighting = true,
        fancyGraphics = false,
        entityShadows = false,
        fastMath = true
    )
}
