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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceProfile
import com.example.ui.FeatherUiState
import com.example.ui.components.FeatherSwitch
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowingButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatPill
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.FeatherCyan
import com.example.ui.theme.FeatherPurple
import com.example.ui.theme.FeatherPurpleBright
import com.example.ui.theme.PvpGreen
import com.example.ui.theme.PvpOrange
import com.example.ui.theme.PvpRed
import com.example.ui.theme.PvpYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerformanceScreen(
    uiState: FeatherUiState,
    onSetProfile: (PerformanceProfile) -> Unit,
    onSetRenderDistance: (Int) -> Unit,
    onSetParticleQuality: (String) -> Unit,
    onToggleSmoothLighting: (Boolean) -> Unit,
    onToggleFancyGraphics: (Boolean) -> Unit,
    onToggleEntityShadows: (Boolean) -> Unit,
    onToggleFastMath: (Boolean) -> Unit,
    onOptimizeRam: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val chunkPresets = listOf(4, 6, 8, 12, 16, 20, 24, 32)
    val memoryEstimateMb = remember(uiState.renderDistance) {
        (uiState.renderDistance * uiState.renderDistance * 4.2).toInt() + 450
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Performance & Optimization",
            subtitle = "Fine-tune Bedrock rendering pipeline and framerate"
        )

        // Memory & Live Engine Gauge Card
        GlassCard(modifier = Modifier.fillMaxWidth(), glowEffect = true) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DEVICE MEMORY STATUS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FeatherCyan,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${uiState.memoryStats.usedMb} MB / ${uiState.memoryStats.totalMb} MB",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (uiState.isOptimizingMemory) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = FeatherCyan,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Button(
                            onClick = onOptimizeRam,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("purge_ram_button")
                        ) {
                            Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Purge Cache", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { uiState.memoryStats.percentageUsed / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        uiState.memoryStats.percentageUsed > 80 -> PvpRed
                        uiState.memoryStats.percentageUsed > 60 -> PvpYellow
                        else -> PvpGreen
                    },
                    trackColor = Color(0xFF222638)
                )

                if (uiState.freedMemoryMb > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✨ Last optimization freed ${uiState.freedMemoryMb} MB unneeded buffer cache!",
                        fontSize = 11.sp,
                        color = PvpGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Performance Profile Cards
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "FPS Profile Presets",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Switch between competitive low-latency and cinematic modes",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                PerformanceProfile.values().forEach { profile ->
                    val isSelected = uiState.selectedProfile == profile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else Color(0xFF141624)
                            )
                            .border(
                                BorderStroke(
                                    1.2.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color(0x334B5563)
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSetProfile(profile) }
                            .padding(12.dp)
                            .testTag("profile_${profile.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) FeatherCyan else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF222638))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${profile.targetFps} FPS",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FeatherPurpleBright
                                        )
                                    }
                                }
                                Text(
                                    text = profile.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(FeatherCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Selected",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Render Distance Presets & Memory Footprint
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Render Distance",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Est. Terrain VRAM: ~${memoryEstimateMb} MB",
                            fontSize = 11.sp,
                            color = FeatherCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FeatherPurple.copy(alpha = 0.2f))
                            .border(1.dp, FeatherPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${uiState.renderDistance} CHUNKS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FeatherPurpleBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chunkPresets.forEach { chunks ->
                        val isSel = uiState.renderDistance == chunks
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) FeatherCyan.copy(alpha = 0.25f) else Color(0xFF161928))
                                .border(1.dp, if (isSel) FeatherCyan else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { onSetRenderDistance(chunks) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$chunks",
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) FeatherCyan else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Particle & Graphics Fine-Tuning
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Graphics & Entity Pipeline",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Particle setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Particle Effects", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Controls hit, potion, and explosion particles", fontSize = 10.sp, color = TextSecondary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Minimal", "Decreased", "All").forEach { p ->
                            val isSel = uiState.particleQuality == p
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) FeatherPurple.copy(alpha = 0.3f) else Color(0xFF141724))
                                    .border(1.dp, if (isSel) FeatherPurple else BorderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { onSetParticleQuality(p) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = p,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) FeatherPurpleBright else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Smooth lighting toggle
                SettingToggleRow(
                    title = "Smooth Lighting",
                    subtitle = "Interpolates block light levels (turn off for +15% FPS)",
                    checked = uiState.smoothLighting,
                    onCheckedChange = onToggleSmoothLighting
                )

                // Fancy Graphics toggle
                SettingToggleRow(
                    title = "Fancy Graphics & Leaves",
                    subtitle = "Transparent foliage and atmospheric cloud rendering",
                    checked = uiState.fancyGraphics,
                    onCheckedChange = onToggleFancyGraphics
                )

                // Entity Shadows
                SettingToggleRow(
                    title = "Entity Shadows",
                    subtitle = "Renders dynamic shadow disks beneath entities",
                    checked = uiState.entityShadows,
                    onCheckedChange = onToggleEntityShadows
                )

                // Fast Math
                SettingToggleRow(
                    title = "Fast Math Trigonometry",
                    subtitle = "Replaces sin/cos with lookup tables for lower CPU heat",
                    checked = uiState.fastMath,
                    onCheckedChange = onToggleFastMath
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
        }
        FeatherSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
