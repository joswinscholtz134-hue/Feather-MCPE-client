package com.example.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FeatherUiState
import com.example.ui.components.ComboDummyCard
import com.example.ui.components.CpsTesterCard
import com.example.ui.components.CrosshairCanvas
import com.example.ui.components.CrosshairStyle
import com.example.ui.components.GlassCard
import com.example.ui.components.HudOverlaySandbox
import com.example.ui.components.SectionHeader
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
fun PvpScreen(
    uiState: FeatherUiState,
    onSetCrosshairStyle: (CrosshairStyle) -> Unit,
    onSetCrosshairColor: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var crosshairScale by remember { mutableFloatStateOf(14f) }

    val colorOptions = listOf(
        Pair("Cyan", 0xFF00F5D4),
        Pair("Purple", 0xFF9D4EDD),
        Pair("Red", 0xFFFF3366),
        Pair("Green", 0xFF10B981),
        Pair("Yellow", 0xFFFBBF24),
        Pair("White", 0xFFFFFFFF),
        Pair("Orange", 0xFFF97316)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "PvP Suite & HUD Overlays",
            subtitle = "Live combat modules, click trainer, and custom reticles"
        )

        // Live In-Game HUD Preview Sandbox
        HudOverlaySandbox(
            fps = uiState.currentFps,
            ping = uiState.currentPing,
            crosshairStyle = uiState.crosshairStyle,
            crosshairColor = Color(uiState.crosshairColor)
        )

        // Realtime CPS Practice Tester Card
        CpsTesterCard()

        // Combo Counter Target Dummy Card
        ComboDummyCard()

        // Custom Crosshair Designer
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Custom Crosshair Reticle",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Customize reticle geometry, color and center dot",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF131522))
                            .border(1.dp, Color(uiState.crosshairColor), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CrosshairCanvas(
                            style = uiState.crosshairStyle,
                            color = Color(uiState.crosshairColor),
                            reticleSize = 14f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "RETICLE SHAPE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FeatherCyan,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CrosshairStyle.values().forEach { style ->
                        val isSel = uiState.crosshairStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) FeatherPurple.copy(alpha = 0.25f) else Color(0xFF141724))
                                .border(1.dp, if (isSel) FeatherPurple else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { onSetCrosshairStyle(style) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("crosshair_style_${style.name.lowercase()}")
                        ) {
                            Text(
                                text = style.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) FeatherPurpleBright else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ACCENT COLOR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FeatherCyan,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEach { (name, hex) ->
                        val isSel = uiState.crosshairColor == hex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    2.dp,
                                    if (isSel) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { onSetCrosshairColor(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSel) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (hex == 0xFFFFFFFF) Color.Black else Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }

        // HUD Elements Detailed Guide Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Included PvP HUD Elements",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                HudFeatureRow(
                    emoji = "🛡️",
                    title = "Armor & Durability HUD",
                    desc = "Real-time remaining durability percentage for helmet, chestplate, leggings, boots and held weapon."
                )
                HudFeatureRow(
                    emoji = "🧪",
                    title = "Potion Effects HUD",
                    desc = "Clean countdown timers and buff icons for Speed, Strength, Fire Res, Invisibility, and Regeneration."
                )
                HudFeatureRow(
                    emoji = "📍",
                    title = "Coordinates & Biome HUD",
                    desc = "Real-time XYZ coordinates, player facing direction, and Nether portal coordinate ratio (8:1)."
                )
                HudFeatureRow(
                    emoji = "📶",
                    title = "Ping & Network Latency HUD",
                    desc = "Dynamic millisecond response time to connected Bedrock server with latency signal bars."
                )
                HudFeatureRow(
                    emoji = "⌨️",
                    title = "Keystrokes / Touch Visualizer",
                    desc = "On-screen touch inputs and directional taps glow dynamically as you move and strike."
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun HudFeatureRow(
    emoji: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 16.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = desc, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
