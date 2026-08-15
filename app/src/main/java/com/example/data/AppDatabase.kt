package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AddonDao
import com.example.data.dao.ModDao
import com.example.data.dao.ServerDao
import com.example.data.dao.SettingDao
import com.example.data.model.AddonEntity
import com.example.data.model.AddonType
import com.example.data.model.ModCategory
import com.example.data.model.ModEntity
import com.example.data.model.ServerEntity
import com.example.data.model.SettingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromModCategory(category: ModCategory): String = category.name

    @TypeConverter
    fun toModCategory(value: String): ModCategory = runCatching { ModCategory.valueOf(value) }.getOrDefault(ModCategory.UTILITY)

    @TypeConverter
    fun fromAddonType(type: AddonType): String = type.name

    @TypeConverter
    fun toAddonType(value: String): AddonType = runCatching { AddonType.valueOf(value) }.getOrDefault(AddonType.RESOURCE_PACK)
}

@Database(
    entities = [
        ModEntity::class,
        ServerEntity::class,
        AddonEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modDao(): ModDao
    abstract fun serverDao(): ServerDao
    abstract fun addonDao(): AddonDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "feather_mcpe_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.modDao().getModCount() == 0) {
                            populateInitialData(database)
                        }
                    }
                }
            }

            suspend fun populateInitialData(database: AppDatabase) {
                val defaultMods = listOf(
                    // Performance
                    ModEntity("fps_boost", "FPS Boost & Stabilizer", "Optimizes chunk render ticks and limits background garbage collection spikes", ModCategory.PERFORMANCE, true, "⚡", "120 FPS Target"),
                    ModEntity("fullbright", "Fullbright / Night Vision", "Enhances gamma curve for crystal clear cave and night visibility without torches", ModCategory.PERFORMANCE, true, "💡", "Gamma 1000%"),
                    ModEntity("fast_math", "Fast Math Algorithm", "Replaces heavy trigonometry operations with lookup tables for lower CPU overhead", ModCategory.PERFORMANCE, true, "📐", "Fast LUT"),
                    ModEntity("entity_culling", "Entity Occlusion Culling", "Skips rendering hidden mobs, chests, and item frames behind solid blocks", ModCategory.PERFORMANCE, true, "👻", "Occlusion"),
                    ModEntity("particle_limiter", "Particle Optimizer", "Throttles explosive and potion particle density during intense PvP combat", ModCategory.PERFORMANCE, false, "✨", "50% Limit"),
                    ModEntity("chunk_loader", "Async Chunk Preloader", "Multithreaded world terrain streaming ahead of player movement", ModCategory.PERFORMANCE, true, "🗺️", "Multithread"),
                    ModEntity("mem_cleaner", "RAM Compactor", "Proactively clears unreferenced textures and memory buffers", ModCategory.PERFORMANCE, true, "🧹", "Auto Clean"),
                    ModEntity("texture_opt", "Texture Optimizer", "Lowers VRAM footprint with optimized mipmapping for Bedrock blocks", ModCategory.PERFORMANCE, false, "📦", "16x Optimized"),

                    // PvP
                    ModEntity("cps_counter", "CPS Counter", "Real-time Left & Right clicks-per-second indicator with high-score tracking", ModCategory.PVP, true, "🖱️", "Live Clicks"),
                    ModEntity("combo_counter", "Combo Counter", "Tracks consecutive unbroken melee hits landed on opponents with damage audio", ModCategory.PVP, true, "🥊", "Dynamic Combo"),
                    ModEntity("armor_hud", "Armor & Durability HUD", "Shows equipped helmet, chestplate, leggings, boots & exact durability %", ModCategory.PVP, true, "🛡️", "Durability Alert"),
                    ModEntity("potion_hud", "Potion Effects HUD", "Displays active buff and debuff status icons with live countdown timers", ModCategory.PVP, true, "🧪", "Timers Active"),
                    ModEntity("custom_crosshair", "Custom Crosshair", "Customizable reticle with color tint, center dot, dynamic expansion and scope", ModCategory.PVP, true, "🎯", "Feather Crosshair"),
                    ModEntity("coords_hud", "Coordinates XYZ & Biome", "Clean lightweight player coordinates, facing direction, and Nether sync", ModCategory.PVP, true, "📍", "XYZ + Nether"),
                    ModEntity("ping_hud", "Ping Latency HUD", "Live millisecond network delay indicator with server connection bars", ModCategory.PVP, true, "📶", "Realtime Ping"),
                    ModEntity("keystrokes_hud", "Keystrokes / Tap Visualizer", "On-screen touch movement visualizer with color glow on press", ModCategory.PVP, true, "⌨️", "Touch Glow"),
                    ModEntity("compass_hud", "Top Compass HUD", "Horizontal compass bar showing cardinal directions (N, E, S, W)", ModCategory.PVP, false, "🧭", "360 Bar"),

                    // Utility
                    ModEntity("auto_sprint", "Auto-Sprint Indicator", "Displays sprint lock state and toggle shortcut for effortless movement", ModCategory.UTILITY, true, "🏃", "Sprint Lock"),
                    ModEntity("scoreboard_cleaner", "Scoreboard Cleaner", "Removes ugly red numbers and trims server sidebar headers for cleaner view", ModCategory.UTILITY, true, "📊", "Clean Sidebar"),
                    ModEntity("time_hud", "In-Game Time & Clock", "Displays real-world 24h clock and Minecraft day/night progression cycle", ModCategory.UTILITY, true, "⏰", "24H / MC Time"),
                    ModEntity("item_physics", "3D Item Physics", "Renders dropped items realistically flat on ground blocks", ModCategory.UTILITY, false, "💎", "3D Drops"),
                    ModEntity("freelook", "360° FreeLook Indicator", "Perspective viewer to survey surroundings without altering movement vector", ModCategory.UTILITY, false, "👀", "360 View"),
                    ModEntity("chat_cleaner", "Chat Filter & Cleaner", "Hides repetitive server spam, spam bots, and advertising messages", ModCategory.UTILITY, true, "💬", "Spam Filter")
                )
                database.modDao().insertMods(defaultMods)

                val defaultServers = listOf(
                    ServerEntity(name = "The Hive", address = "geo.hivebedrock.network", port = 19132, motd = "Treasure Wars, SkyWars, Hide & Seek, Bedrock PvP", pingMs = 28, isOnline = true, isFavorite = true, version = "1.21.x", isDefault = true),
                    ServerEntity(name = "CubeCraft Games", address = "play.cubecraft.net", port = 19132, motd = "EggWars, Skyblock, BlockWars, PvP Arena", pingMs = 35, isOnline = true, isFavorite = true, version = "1.21.x", isDefault = true),
                    ServerEntity(name = "NetherGames Network", address = "play.nethergames.org", port = 19132, motd = "Bedwars, Duels, SkyWars, Bridge, Factions", pingMs = 22, isOnline = true, isFavorite = true, version = "1.21.x", isDefault = true),
                    ServerEntity(name = "Galaxite Bedrock", address = "play.galaxite.net", port = 19132, motd = "Chronos, Rush, Hyper Racers, Alien Games", pingMs = 45, isOnline = true, isFavorite = false, version = "1.21.x", isDefault = true),
                    ServerEntity(name = "Mineville Bedrock", address = "play.inpvp.net", port = 19132, motd = "City Life RP, High School, Prison, Survival", pingMs = 52, isOnline = true, isFavorite = false, version = "1.21.x", isDefault = true),
                    ServerEntity(name = "Lifeboat Network", address = "play.lbsg.net", port = 19132, motd = "Survival Games, Skyblock, Zombie Apocalypse", pingMs = 40, isOnline = true, isFavorite = false, version = "1.21.x", isDefault = true)
                )
                database.serverDao().insertServers(defaultServers)

                val defaultAddons = listOf(
                    AddonEntity(name = "Feather Minimal Dark GUI 16x", description = "Sleek translucent dark inventory, rounded hotbar slots, and neon accents.", fileName = "feather_dark_gui_v2.mcpack", fileSizeBytes = 2516582, type = AddonType.RESOURCE_PACK, version = "2.4.0", isEnabled = true, isCurated = true, iconEmoji = "🎨"),
                    AddonEntity(name = "Dynamic Fullbright Night Vision", description = "Permanent 100% light level in underground caves and deep dark biome.", fileName = "feather_fullbright.mcpack", fileSizeBytes = 838860, type = AddonType.RESOURCE_PACK, version = "1.3.0", isEnabled = true, isCurated = true, iconEmoji = "💡"),
                    AddonEntity(name = "Clean PvP Hits & Low Fire", description = "Low visual burning fire screen overlay and high-visibility crit particles.", fileName = "clean_pvp_particles.mcpack", fileSizeBytes = 1258291, type = AddonType.RESOURCE_PACK, version = "1.1.0", isEnabled = true, isCurated = true, iconEmoji = "⚔️"),
                    AddonEntity(name = "Clear Glass & Clean Redstone", description = "Border-free seamless glass textures and marked repeater/comparator delay ticks.", fileName = "clear_glass_redstone.mcpack", fileSizeBytes = 1572864, type = AddonType.RESOURCE_PACK, version = "1.0.2", isEnabled = true, isCurated = true, iconEmoji = "🔍"),
                    AddonEntity(name = "Feather FPS Boost Overlay", description = "8x optimized particle effects and compressed mipmap block textures for +30% FPS.", fileName = "feather_fps_boost.mcpack", fileSizeBytes = 3250585, type = AddonType.RESOURCE_PACK, version = "2.0.1", isEnabled = true, isCurated = true, iconEmoji = "⚡")
                )
                database.addonDao().insertAddons(defaultAddons)

                val defaultSettings = listOf(
                    SettingEntity("theme", "FEATHER_PURPLE"),
                    SettingEntity("hud_scale", "1.0"),
                    SettingEntity("performance_profile", "BALANCED"),
                    SettingEntity("render_distance", "12"),
                    SettingEntity("target_fps", "60"),
                    SettingEntity("particles", "Decreased"),
                    SettingEntity("smooth_lighting", "true"),
                    SettingEntity("fancy_graphics", "true"),
                    SettingEntity("entity_shadows", "false"),
                    SettingEntity("fast_math", "true"),
                    SettingEntity("haptics_enabled", "true"),
                    SettingEntity("crosshair_style", "FEATHER_PLUS"),
                    SettingEntity("crosshair_color", "0xFF00F5D4"),
                    SettingEntity("crosshair_size", "14"),
                    SettingEntity("player_name", "FeatherPlayer"),
                    SettingEntity("auto_clean_ram", "true"),
                    SettingEntity("notifications_enabled", "true")
                )
                database.settingDao().setSettings(defaultSettings)
            }
        }
    }
}
