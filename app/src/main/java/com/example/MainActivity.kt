package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.FeatherScreen
import com.example.ui.FeatherViewModel
import com.example.ui.components.FeatherNavigationBar
import com.example.ui.components.FeatherTopBar
import com.example.ui.screens.AddonsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ModManagerScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.PvpScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.FeatherClientTheme
import com.example.ui.theme.ObsidianDark

class MainActivity : ComponentActivity() {
    private val viewModel: FeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val allMods by viewModel.allMods.collectAsState()
            val enabledMods by viewModel.enabledMods.collectAsState()
            val allServers by viewModel.allServers.collectAsState()
            val allAddons by viewModel.allAddons.collectAsState()

            FeatherClientTheme(themeMode = uiState.themeMode) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianDark),
                    containerColor = ObsidianDark,
                    topBar = {
                        FeatherTopBar(
                            currentScreen = uiState.currentScreen,
                            currentFps = uiState.currentFps,
                            currentPing = uiState.currentPing,
                            statusMessage = uiState.statusMessage
                        )
                    },
                    bottomBar = {
                        FeatherNavigationBar(
                            currentScreen = uiState.currentScreen,
                            onSelectScreen = { viewModel.setScreen(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(ObsidianDark)
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentScreen,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "ScreenTransition"
                        ) { screen ->
                            when (screen) {
                                FeatherScreen.HOME -> {
                                    HomeScreen(
                                        uiState = uiState,
                                        enabledMods = enabledMods,
                                        servers = allServers,
                                        onNavigate = { viewModel.setScreen(it) },
                                        onSetProfile = { viewModel.setPerformanceProfile(it) },
                                        onSetPlayerName = { viewModel.setPlayerName(it) }
                                    )
                                }
                                FeatherScreen.MODS -> {
                                    ModManagerScreen(
                                        mods = allMods,
                                        searchQuery = uiState.modSearchQuery,
                                        selectedCategory = uiState.selectedModCategory,
                                        onSearchChanged = { viewModel.setModSearchQuery(it) },
                                        onCategorySelected = { viewModel.setModCategoryFilter(it) },
                                        onToggleMod = { viewModel.toggleMod(it) }
                                    )
                                }
                                FeatherScreen.PERFORMANCE -> {
                                    PerformanceScreen(
                                        uiState = uiState,
                                        onSetProfile = { viewModel.setPerformanceProfile(it) },
                                        onSetRenderDistance = { viewModel.setRenderDistance(it) },
                                        onSetParticleQuality = { viewModel.setParticleQuality(it) },
                                        onToggleSmoothLighting = { viewModel.toggleSmoothLighting(it) },
                                        onToggleFancyGraphics = { viewModel.toggleFancyGraphics(it) },
                                        onToggleEntityShadows = { viewModel.toggleEntityShadows(it) },
                                        onToggleFastMath = { viewModel.toggleFastMath(it) },
                                        onOptimizeRam = { viewModel.optimizeRam() }
                                    )
                                }
                                FeatherScreen.PVP_HUD -> {
                                    PvpScreen(
                                        uiState = uiState,
                                        onSetCrosshairStyle = { viewModel.setCrosshairStyle(it) },
                                        onSetCrosshairColor = { viewModel.setCrosshairColor(it) }
                                    )
                                }
                                FeatherScreen.SERVERS -> {
                                    ServersScreen(
                                        servers = allServers,
                                        searchQuery = uiState.serverSearchQuery,
                                        onSearchChanged = { viewModel.setServerSearchQuery(it) },
                                        onAddServer = { name, addr, port, ver, motd ->
                                            viewModel.addServer(name, addr, port, ver, motd)
                                        },
                                        onUpdateServer = { viewModel.updateServer(it) },
                                        onDeleteServer = { viewModel.deleteServer(it) },
                                        onToggleFavorite = { viewModel.toggleFavoriteServer(it) },
                                        onRefreshPings = { viewModel.pingAllServers() }
                                    )
                                }
                                FeatherScreen.ADDONS -> {
                                    AddonsScreen(
                                        addons = allAddons,
                                        onImportAddon = { uri, name, size ->
                                            viewModel.importAddonUri(uri, name, size)
                                        },
                                        onToggleAddon = { viewModel.toggleAddon(it) },
                                        onDeleteAddon = { viewModel.deleteAddon(it) }
                                    )
                                }
                                FeatherScreen.SETTINGS -> {
                                    SettingsScreen(
                                        uiState = uiState,
                                        onSetTheme = { viewModel.setTheme(it) },
                                        onSetHudScale = { viewModel.setHudScale(it) },
                                        onToggleHaptics = { viewModel.toggleHaptics(it) },
                                        onToggleNotifications = { viewModel.toggleNotifications(it) },
                                        onResetDefaults = { viewModel.resetDefaults() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
