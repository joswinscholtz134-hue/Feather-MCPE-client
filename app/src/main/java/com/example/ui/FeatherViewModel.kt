package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FeatherRepository
import com.example.data.MemoryStats
import com.example.data.model.AddonEntity
import com.example.data.model.AddonType
import com.example.data.model.ModCategory
import com.example.data.model.ModEntity
import com.example.data.model.PerformanceProfile
import com.example.data.model.ServerEntity
import com.example.ui.components.CrosshairStyle
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

enum class FeatherScreen(val title: String, val iconEmoji: String) {
    HOME("Home", "🏠"),
    MODS("Mods", "⚡"),
    PERFORMANCE("Performance", "🚀"),
    PVP_HUD("PvP HUD", "⚔️"),
    SERVERS("Servers", "🌐"),
    ADDONS("Add-ons", "📦"),
    SETTINGS("Settings", "⚙️")
}

data class FeatherUiState(
    val currentScreen: FeatherScreen = FeatherScreen.HOME,
    val playerName: String = "FeatherPlayer",
    val currentFps: Int = 120,
    val targetFps: Int = 120,
    val currentPing: Int = 24,
    val memoryStats: MemoryStats = MemoryStats(3200, 8192, 4992, 39),
    val selectedProfile: PerformanceProfile = PerformanceProfile.BALANCED,
    val themeMode: AppThemeMode = AppThemeMode.FEATHER_PURPLE,
    val hudScale: Float = 1.0f,
    val renderDistance: Int = 12,
    val particleQuality: String = "Decreased",
    val smoothLighting: Boolean = true,
    val fancyGraphics: Boolean = true,
    val entityShadows: Boolean = false,
    val fastMath: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val crosshairStyle: CrosshairStyle = CrosshairStyle.FEATHER_PLUS,
    val crosshairColor: Long = 0xFF00F5D4,
    val modSearchQuery: String = "",
    val selectedModCategory: ModCategory? = null,
    val serverSearchQuery: String = "",
    val isOptimizingMemory: Boolean = false,
    val freedMemoryMb: Int = 0,
    val statusMessage: String? = null
)

class FeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = FeatherRepository(
        context = application,
        modDao = database.modDao(),
        serverDao = database.serverDao(),
        addonDao = database.addonDao(),
        settingDao = database.settingDao()
    )

    private val _uiState = MutableStateFlow(FeatherUiState())
    val uiState: StateFlow<FeatherUiState> = _uiState.asStateFlow()

    val allMods: StateFlow<List<ModEntity>> = repository.allMods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enabledMods: StateFlow<List<ModEntity>> = repository.enabledMods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServers: StateFlow<List<ServerEntity>> = repository.allServers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAddons: StateFlow<List<AddonEntity>> = repository.allAddons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSettings()
        startTelemetrySimulation()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val themeStr = repository.getSetting("theme", "FEATHER_PURPLE")
            val themeMode = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.FEATHER_PURPLE)
            val profileStr = repository.getSetting("performance_profile", "BALANCED")
            val profile = runCatching { PerformanceProfile.valueOf(profileStr) }.getOrDefault(PerformanceProfile.BALANCED)
            val pName = repository.getSetting("player_name", "FeatherPlayer")
            val hudScale = repository.getSetting("hud_scale", "1.0").toFloatOrNull() ?: 1.0f
            val rDist = repository.getSetting("render_distance", "12").toIntOrNull() ?: 12
            val crossStyleStr = repository.getSetting("crosshair_style", "FEATHER_PLUS")
            val crossStyle = runCatching { CrosshairStyle.valueOf(crossStyleStr) }.getOrDefault(CrosshairStyle.FEATHER_PLUS)
            val crossColor = repository.getSetting("crosshair_color", "0xFF00F5D4").toLongOrNull() ?: 0xFF00F5D4

            _uiState.update {
                it.copy(
                    themeMode = themeMode,
                    selectedProfile = profile,
                    targetFps = profile.targetFps,
                    playerName = pName,
                    hudScale = hudScale,
                    renderDistance = rDist,
                    crosshairStyle = crossStyle,
                    crosshairColor = crossColor,
                    particleQuality = repository.getSetting("particles", profile.particleQuality),
                    smoothLighting = repository.getSetting("smooth_lighting", "true").toBoolean(),
                    fancyGraphics = repository.getSetting("fancy_graphics", "true").toBoolean(),
                    entityShadows = repository.getSetting("entity_shadows", "false").toBoolean(),
                    fastMath = repository.getSetting("fast_math", "true").toBoolean(),
                    hapticsEnabled = repository.getSetting("haptics_enabled", "true").toBoolean(),
                    notificationsEnabled = repository.getSetting("notifications_enabled", "true").toBoolean()
                )
            }
        }
    }

    private fun startTelemetrySimulation() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                val baseFps = _uiState.value.targetFps
                val jitter = Random.nextInt(-3, 4)
                val newFps = (baseFps + jitter).coerceIn(24, 240)

                val basePing = 24
                val pingJitter = Random.nextInt(-4, 6)
                val newPing = (basePing + pingJitter).coerceIn(12, 90)

                val mem = repository.getMemoryInfo()

                _uiState.update {
                    it.copy(
                        currentFps = newFps,
                        currentPing = newPing,
                        memoryStats = mem
                    )
                }
                delay(1200)
            }
        }
    }

    fun setScreen(screen: FeatherScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setPlayerName(name: String) {
        val trimmed = name.trim().ifEmpty { "FeatherPlayer" }
        _uiState.update { it.copy(playerName = trimmed) }
        viewModelScope.launch {
            repository.setSetting("player_name", trimmed)
        }
    }

    fun toggleMod(mod: ModEntity) {
        viewModelScope.launch {
            repository.setModEnabled(mod.id, !mod.isEnabled)
        }
    }

    fun setModCategoryFilter(category: ModCategory?) {
        _uiState.update { it.copy(selectedModCategory = category) }
    }

    fun setModSearchQuery(query: String) {
        _uiState.update { it.copy(modSearchQuery = query) }
    }

    fun setServerSearchQuery(query: String) {
        _uiState.update { it.copy(serverSearchQuery = query) }
    }

    fun setPerformanceProfile(profile: PerformanceProfile) {
        _uiState.update {
            it.copy(
                selectedProfile = profile,
                targetFps = profile.targetFps,
                renderDistance = profile.renderDistanceChunks,
                particleQuality = profile.particleQuality,
                smoothLighting = profile.smoothLighting,
                fancyGraphics = profile.fancyGraphics,
                entityShadows = profile.entityShadows,
                fastMath = profile.fastMath
            )
        }
        viewModelScope.launch {
            repository.applyPerformanceProfile(profile)
        }
    }

    fun setRenderDistance(chunks: Int) {
        _uiState.update { it.copy(renderDistance = chunks) }
        viewModelScope.launch {
            repository.setSetting("render_distance", chunks.toString())
        }
    }

    fun setParticleQuality(quality: String) {
        _uiState.update { it.copy(particleQuality = quality) }
        viewModelScope.launch {
            repository.setSetting("particles", quality)
        }
    }

    fun toggleSmoothLighting(value: Boolean) {
        _uiState.update { it.copy(smoothLighting = value) }
        viewModelScope.launch {
            repository.setSetting("smooth_lighting", value.toString())
        }
    }

    fun toggleFancyGraphics(value: Boolean) {
        _uiState.update { it.copy(fancyGraphics = value) }
        viewModelScope.launch {
            repository.setSetting("fancy_graphics", value.toString())
        }
    }

    fun toggleEntityShadows(value: Boolean) {
        _uiState.update { it.copy(entityShadows = value) }
        viewModelScope.launch {
            repository.setSetting("entity_shadows", value.toString())
        }
    }

    fun toggleFastMath(value: Boolean) {
        _uiState.update { it.copy(fastMath = value) }
        viewModelScope.launch {
            repository.setSetting("fast_math", value.toString())
        }
    }

    fun setTheme(themeMode: AppThemeMode) {
        _uiState.update { it.copy(themeMode = themeMode) }
        viewModelScope.launch {
            repository.setSetting("theme", themeMode.name)
        }
    }

    fun setHudScale(scale: Float) {
        _uiState.update { it.copy(hudScale = scale) }
        viewModelScope.launch {
            repository.setSetting("hud_scale", scale.toString())
        }
    }

    fun setCrosshairStyle(style: CrosshairStyle) {
        _uiState.update { it.copy(crosshairStyle = style) }
        viewModelScope.launch {
            repository.setSetting("crosshair_style", style.name)
        }
    }

    fun setCrosshairColor(colorHex: Long) {
        _uiState.update { it.copy(crosshairColor = colorHex) }
        viewModelScope.launch {
            repository.setSetting("crosshair_color", colorHex.toString())
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        _uiState.update { it.copy(hapticsEnabled = enabled) }
        viewModelScope.launch {
            repository.setSetting("haptics_enabled", enabled.toString())
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        viewModelScope.launch {
            repository.setSetting("notifications_enabled", enabled.toString())
        }
    }

    fun addServer(name: String, address: String, port: Int, version: String, motd: String) {
        viewModelScope.launch {
            val server = ServerEntity(
                name = name.ifBlank { "Bedrock Server" },
                address = address.trim(),
                port = if (port in 1..65535) port else 19132,
                version = version.ifBlank { "1.21.x" },
                motd = motd.ifBlank { "Custom Bedrock Server" },
                pingMs = Random.nextInt(20, 50),
                isOnline = true
            )
            repository.addServer(server)
            showStatus("Server '${server.name}' added successfully!")
        }
    }

    fun updateServer(server: ServerEntity) {
        viewModelScope.launch {
            repository.updateServer(server)
            showStatus("Server '${server.name}' updated.")
        }
    }

    fun deleteServer(server: ServerEntity) {
        viewModelScope.launch {
            repository.deleteServer(server.id)
            showStatus("Server '${server.name}' removed.")
        }
    }

    fun toggleFavoriteServer(server: ServerEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteServer(server.id, !server.isFavorite)
        }
    }

    fun pingAllServers() {
        viewModelScope.launch {
            showStatus("Testing latency across all servers...")
            repository.refreshAllServerPings()
            showStatus("Ping test complete.")
        }
    }

    fun importAddonUri(uri: Uri, fileName: String, fileSize: Long) {
        viewModelScope.launch {
            showStatus("Importing $fileName...")
            val result = repository.importAddonFromUri(uri, fileName, fileSize)
            if (result.isSuccess) {
                showStatus("Successfully imported '${result.getOrNull()?.name}'!")
            } else {
                showStatus("Import failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}")
            }
        }
    }

    fun toggleAddon(addon: AddonEntity) {
        viewModelScope.launch {
            repository.setAddonEnabled(addon.id, !addon.isEnabled)
        }
    }

    fun deleteAddon(addon: AddonEntity) {
        viewModelScope.launch {
            repository.deleteAddon(addon.id)
            showStatus("Deleted ${addon.name}")
        }
    }

    fun optimizeRam() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOptimizingMemory = true) }
            val freed = repository.optimizeAndPurgeMemory()
            delay(600)
            val updatedMem = repository.getMemoryInfo()
            _uiState.update {
                it.copy(
                    isOptimizingMemory = false,
                    freedMemoryMb = freed,
                    memoryStats = updatedMem
                )
            }
            showStatus("RAM Optimized! Cleared $freed MB cache.")
        }
    }

    fun resetDefaults() {
        viewModelScope.launch {
            repository.resetAllSettingsToDefault()
            loadSettings()
            showStatus("All settings restored to Feather defaults.")
        }
    }

    private fun showStatus(msg: String) {
        _uiState.update { it.copy(statusMessage = msg) }
        viewModelScope.launch {
            delay(3500)
            if (_uiState.value.statusMessage == msg) {
                _uiState.update { it.copy(statusMessage = null) }
            }
        }
    }
}
