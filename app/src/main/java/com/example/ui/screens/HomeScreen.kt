package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModEntity
import com.example.data.model.PerformanceProfile
import com.example.data.model.ServerEntity
import com.example.ui.FeatherScreen
import com.example.ui.FeatherUiState
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingButton
import com.example.ui.components.HudOverlaySandbox
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatPill
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.FeatherCyan
import com.example.ui.theme.FeatherGlowBrush
import com.example.ui.theme.FeatherPurple
import com.example.ui.theme.FeatherPurpleBright
import com.example.ui.theme.LaunchButtonBrush
import com.example.ui.theme.PvpGreen
import com.example.ui.theme.PvpRed
import com.example.ui.theme.PvpYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.GameLauncherHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: FeatherUiState,
    enabledMods: List<ModEntity>,
    servers: List<ServerEntity>,
    onNavigate: (FeatherScreen) -> Unit,
    onSetProfile: (PerformanceProfile) -> Unit,
    onSetPlayerName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPlayerNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(uiState.playerName) }

    val isMcInstalled = remember { GameLauncherHelper.isMinecraftInstalled(context) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player / Brand Banner
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            glowEffect = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(FeatherPurpleBright, FeatherCyan)
                                )
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🪶", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.playerName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            IconButton(
                                onClick = {
                                    editedName = uiState.playerName
                                    showPlayerNameDialog = true
                                },
                                modifier = Modifier.size(24.dp).padding(start = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Gamertag",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(PvpGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Feather Bedrock Ready • v2.4",
                                fontSize = 11.sp,
                                color = FeatherCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Profile Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { onNavigate(FeatherScreen.PERFORMANCE) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = uiState.selectedProfile.title.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Hero PLAY MCPE Launch Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LAUNCH MINECRAFT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = FeatherCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Bedrock Edition Client",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2235))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isMcInstalled) "🟢 INSTALLED" else "🔴 NOT INSTALLED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMcInstalled) PvpGreen else PvpRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Launch Action Button
                GlowingButton(
                    text = "PLAY MINECRAFT BEDROCK",
                    icon = Icons.Default.PlayArrow,
                    onClick = {
                        GameLauncherHelper.launchMinecraft(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "play_minecraft_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (!isMcInstalled) {
                    Text(
                        text = "Please install Minecraft from the official store.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PvpYellow
                    )
                } else {
                    Text(
                        text = "All ${enabledMods.size} active modules & ${uiState.selectedProfile.title} profile applied",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Performance Telemetry Realtime Hub
        Column {
            SectionHeader(
                title = "Live Performance Metrics",
                subtitle = "Real-time client telemetry & device stats"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill(
                    label = "FPS",
                    value = "${uiState.currentFps} FPS",
                    icon = "⚡",
                    accentColor = PvpGreen,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Ping",
                    value = "${uiState.currentPing}ms",
                    icon = "📶",
                    accentColor = FeatherCyan,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "RAM Used",
                    value = "${uiState.memoryStats.usedMb}MB",
                    icon = "🧠",
                    accentColor = FeatherPurpleBright,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Performance Profile Selector
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Performance Profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = { onNavigate(FeatherScreen.PERFORMANCE) }) {
                        Text(text = "Customize", fontSize = 12.sp, color = FeatherCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PerformanceProfile.values().forEach { profile ->
                        val isSelected = uiState.selectedProfile == profile
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                    else Color(0xFF161826)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else Color(0x334B5563)
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSetProfile(profile) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (profile) {
                                        PerformanceProfile.ULTRA_FPS -> "⚡"
                                        PerformanceProfile.BALANCED -> "⚖️"
                                        PerformanceProfile.HIGH_FIDELITY -> "💎"
                                        PerformanceProfile.BATTERY_SAVER -> "🔋"
                                        PerformanceProfile.CUSTOM -> "🛠️"
                                    },
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = profile.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Modules Overview
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Active Modules",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PvpGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${enabledMods.size} ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PvpGreen
                            )
                        }
                    }

                    TextButton(onClick = { onNavigate(FeatherScreen.MODS) }) {
                        Text(text = "View All", fontSize = 12.sp, color = FeatherCyan)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    enabledMods.take(8).forEach { mod ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141724))
                                .border(1.dp, Color(0x334B5563), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = mod.iconName, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mod.name,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    if (enabledMods.size > 8) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FeatherPurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+${enabledMods.size - 8} more",
                                fontSize = 11.sp,
                                color = FeatherPurpleBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live In-Game HUD Sandbox Preview
        Column {
            SectionHeader(
                title = "Live HUD Preview",
                subtitle = "Previewing active overlays on Bedrock canvas"
            )

            HudOverlaySandbox(
                fps = uiState.currentFps,
                ping = uiState.currentPing,
                crosshairStyle = uiState.crosshairStyle,
                crosshairColor = Color(uiState.crosshairColor)
            )
        }

        // Quick Server Connect Shortcut
        if (servers.isNotEmpty()) {
            val favServer = servers.firstOrNull { it.isFavorite } ?: servers.first()
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐ Favorite Server", fontSize = 11.sp, color = PvpYellow, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = favServer.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${favServer.address}:${favServer.port}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    GlowingButton(
                        text = "JOIN",
                        onClick = {
                            GameLauncherHelper.connectToServer(
                                context,
                                favServer.name,
                                favServer.address,
                                favServer.port
                            )
                        },
                        icon = Icons.Default.PlayArrow,
                        testTag = "join_fav_server_button"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Gamertag Editing Dialog
    if (showPlayerNameDialog) {
        AlertDialog(
            onDismissRequest = { showPlayerNameDialog = false },
            title = { Text("Set Player Gamertag", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your Bedrock player name:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSetPlayerName(editedName)
                    showPlayerNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPlayerNameDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF181B29)
        )
    }
}
