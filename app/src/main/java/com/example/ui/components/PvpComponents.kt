package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.FeatherCyan
import com.example.ui.theme.FeatherPurple
import com.example.ui.theme.PvpGreen
import com.example.ui.theme.PvpOrange
import com.example.ui.theme.PvpRed
import com.example.ui.theme.PvpYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CrosshairStyle(val title: String) {
    FEATHER_PLUS("Feather Plus"),
    DOT("Center Dot"),
    CLASSIC_CROSS("Classic Cross"),
    DYNAMIC_CIRCLE("Dynamic Circle"),
    ARROW_CHEVRON("Arrow Reticle"),
    PRECISION_SCOPE("Precision Scope")
}

@Composable
fun CrosshairCanvas(
    style: CrosshairStyle,
    color: Color = FeatherCyan,
    reticleSize: Float = 16f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size((reticleSize * 2.5f).dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val s = reticleSize.toDp().toPx()

        when (style) {
            CrosshairStyle.DOT -> {
                drawCircle(color = color, radius = s * 0.25f, center = center)
                drawCircle(color = Color.Black.copy(alpha = 0.5f), radius = s * 0.35f, center = center, style = Stroke(width = 1.5f))
            }
            CrosshairStyle.CLASSIC_CROSS -> {
                val gap = s * 0.3f
                val length = s * 0.9f
                val stroke = 3f
                // Top
                drawLine(color, Offset(center.x, center.y - gap), Offset(center.x, center.y - gap - length), strokeWidth = stroke)
                // Bottom
                drawLine(color, Offset(center.x, center.y + gap), Offset(center.x, center.y + gap + length), strokeWidth = stroke)
                // Left
                drawLine(color, Offset(center.x - gap, center.y), Offset(center.x - gap - length, center.y), strokeWidth = stroke)
                // Right
                drawLine(color, Offset(center.x + gap, center.y), Offset(center.x + gap + length, center.y), strokeWidth = stroke)
            }
            CrosshairStyle.FEATHER_PLUS -> {
                val gap = s * 0.2f
                val length = s * 0.8f
                // Center dot
                drawCircle(color = color, radius = 2.5f, center = center)
                // Wings
                drawLine(color, Offset(center.x, center.y - gap), Offset(center.x, center.y - gap - length), strokeWidth = 2.5f)
                drawLine(color, Offset(center.x, center.y + gap), Offset(center.x, center.y + gap + length), strokeWidth = 2.5f)
                drawLine(color, Offset(center.x - gap, center.y), Offset(center.x - gap - length, center.y), strokeWidth = 2.5f)
                drawLine(color, Offset(center.x + gap, center.y), Offset(center.x + gap + length, center.y), strokeWidth = 2.5f)
                // Corner accents
                val cGap = s * 0.6f
                drawCircle(color.copy(alpha = 0.5f), radius = 1.5f, center = Offset(center.x - cGap, center.y - cGap))
                drawCircle(color.copy(alpha = 0.5f), radius = 1.5f, center = Offset(center.x + cGap, center.y - cGap))
                drawCircle(color.copy(alpha = 0.5f), radius = 1.5f, center = Offset(center.x - cGap, center.y + cGap))
                drawCircle(color.copy(alpha = 0.5f), radius = 1.5f, center = Offset(center.x + cGap, center.y + cGap))
            }
            CrosshairStyle.DYNAMIC_CIRCLE -> {
                drawCircle(color = color, radius = s * 0.8f, center = center, style = Stroke(width = 2.5f))
                drawCircle(color = color, radius = 2.5f, center = center)
            }
            CrosshairStyle.ARROW_CHEVRON -> {
                val path = Path().apply {
                    moveTo(center.x - s * 0.6f, center.y + s * 0.4f)
                    lineTo(center.x, center.y - s * 0.5f)
                    lineTo(center.x + s * 0.6f, center.y + s * 0.4f)
                }
                drawPath(path, color = color, style = Stroke(width = 3f))
                drawCircle(color = color, radius = 2f, center = center)
            }
            CrosshairStyle.PRECISION_SCOPE -> {
                drawCircle(color = color.copy(alpha = 0.3f), radius = s, center = center, style = Stroke(width = 1.5f))
                drawLine(color, Offset(center.x - s * 1.2f, center.y), Offset(center.x + s * 1.2f, center.y), strokeWidth = 1.5f)
                drawLine(color, Offset(center.x, center.y - s * 1.2f), Offset(center.x, center.y + s * 1.2f), strokeWidth = 1.5f)
                drawCircle(color = color, radius = 3f, center = center)
            }
        }
    }
}

@Composable
fun CpsTesterCard(
    modifier: Modifier = Modifier
) {
    val clickTimestampsLeft = remember { mutableStateListOf<Long>() }
    val clickTimestampsRight = remember { mutableStateListOf<Long>() }
    var currentCpsLeft by remember { mutableFloatStateOf(0f) }
    var currentCpsRight by remember { mutableFloatStateOf(0f) }
    var peakCps by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Loop to calculate sliding window CPS
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            clickTimestampsLeft.removeAll { now - it > 1000 }
            clickTimestampsRight.removeAll { now - it > 1000 }

            currentCpsLeft = clickTimestampsLeft.size.toFloat()
            currentCpsRight = clickTimestampsRight.size.toFloat()

            val combined = currentCpsLeft + currentCpsRight
            if (combined > peakCps) peakCps = combined

            delay(50)
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth(), glowEffect = currentCpsLeft > 0 || currentCpsRight > 0) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🖱️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CPS Practice & Tester",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Test Bedrock butterfly / jitter / drag clicks",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        clickTimestampsLeft.clear()
                        clickTimestampsRight.clear()
                        currentCpsLeft = 0f
                        currentCpsRight = 0f
                        peakCps = 0f
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset CPS",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CpsScorePill(title = "LEFT CPS", value = "%.1f".format(currentCpsLeft), color = FeatherPurple)
                CpsScorePill(title = "RIGHT CPS", value = "%.1f".format(currentCpsRight), color = FeatherCyan)
                CpsScorePill(title = "PEAK RECORD", value = "%.1f".format(peakCps), color = PvpYellow)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tap Zones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TapButton(
                    label = "LEFT CLICK (LMB)",
                    count = clickTimestampsLeft.size,
                    color = FeatherPurple,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        clickTimestampsLeft.add(System.currentTimeMillis())
                    },
                    testTag = "lmb_tap_zone"
                )

                TapButton(
                    label = "RIGHT CLICK (RMB)",
                    count = clickTimestampsRight.size,
                    color = FeatherCyan,
                    modifier = Modifier.weight(1f),
                    onTap = {
                        clickTimestampsRight.add(System.currentTimeMillis())
                    },
                    testTag = "rmb_tap_zone"
                )
            }
        }
    }
}

@Composable
private fun CpsScorePill(
    title: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}

@Composable
private fun TapButton(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
    testTag: String
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .height(84.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    scale.snapTo(0.92f)
                    scale.animateTo(1f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                }
                onTap()
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count CPS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = "TAP RAPIDLY",
                fontSize = 8.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun ComboDummyCard(modifier: Modifier = Modifier) {
    var comboCount by remember { mutableIntStateOf(0) }
    var highestCombo by remember { mutableIntStateOf(0) }
    var lastHitTime by remember { mutableStateOf(0L) }
    val hitAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    // Combo timeout check (1.2 seconds to keep combo alive)
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            if (comboCount > 0 && now - lastHitTime > 1200) {
                comboCount = 0
            }
            delay(100)
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth(), glowEffect = comboCount > 3) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🥊", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Combo Target Dummy",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Practice sprint-reset timing & W-Tap hits",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PvpRed.copy(alpha = 0.2f))
                        .border(1.dp, PvpRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MAX: $highestCombo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PvpRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Dummy Target
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131522))
                    .border(1.dp, Color(0x33475569), RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val now = System.currentTimeMillis()
                        lastHitTime = now
                        comboCount++
                        if (comboCount > highestCombo) highestCombo = comboCount
                        scope.launch {
                            hitAnim.snapTo(0.85f)
                            hitAnim.animateTo(1f, animationSpec = tween(150, easing = FastOutSlowInEasing))
                        }
                    }
                    .testTag("combo_dummy_target"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(hitAnim.value)
                ) {
                    Text(
                        text = if (comboCount > 0) "🎯" else "🤺",
                        fontSize = 32.sp
                    )
                    Text(
                        text = if (comboCount > 0) "$comboCount COMBO!" else "TAP TO STRIKE",
                        fontSize = if (comboCount > 0) 20.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        color = when {
                            comboCount > 10 -> PvpRed
                            comboCount > 5 -> PvpYellow
                            comboCount > 0 -> FeatherCyan
                            else -> TextSecondary
                        }
                    )
                    if (comboCount > 0) {
                        Text(
                            text = "Keep rhythm under 1.2s to maintain combo",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HudOverlaySandbox(
    modifier: Modifier = Modifier,
    fps: Int = 118,
    ping: Int = 28,
    cps: Float = 14.5f,
    combo: Int = 7,
    crosshairStyle: CrosshairStyle = CrosshairStyle.FEATHER_PLUS,
    crosshairColor: Color = FeatherCyan
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E2235), Color(0xFF0F111A))
                )
            )
            .border(1.5.dp, BorderGlow, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Background Grid / Terrain representation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 32.dp.toPx()
            for (x in 0..(size.width / step).toInt()) {
                drawLine(
                    color = Color(0x0DFFFFFF),
                    start = Offset(x * step, 0f),
                    end = Offset(x * step, size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(size.height / step).toInt()) {
                drawLine(
                    color = Color(0x0DFFFFFF),
                    start = Offset(0f, y * step),
                    end = Offset(size.width, y * step),
                    strokeWidth = 1f
                )
            }
        }

        // Top Left: Coordinates & FPS & Ping HUD
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC000000))
                .border(0.8.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "FPS: $fps | PING: ${ping}ms",
                color = PvpGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "XYZ: 142.5 / 68.0 / -824.3 (Facing North)",
                color = FeatherCyan,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Biome: Plains | Light: 15 (Sky)",
                color = Color.White,
                fontSize = 8.sp
            )
        }

        // Top Right: Active Potion Effects HUD
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC000000))
                .border(0.8.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            PotionStatusRow(emoji = "⚡", name = "Speed II", time = "03:45", color = FeatherCyan)
            PotionStatusRow(emoji = "💪", name = "Strength I", time = "01:12", color = PvpRed)
            PotionStatusRow(emoji = "🔥", name = "Fire Res", time = "06:20", color = PvpOrange)
        }

        // Center: Custom Crosshair Reticle
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            CrosshairCanvas(
                style = crosshairStyle,
                color = crosshairColor,
                reticleSize = 14f
            )
        }

        // Bottom Left: Armor & Durability HUD
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC000000))
                .border(0.8.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArmorSlotItem(icon = "🪖", dura = "88%", color = PvpGreen)
            ArmorSlotItem(icon = "🦺", dura = "94%", color = PvpGreen)
            ArmorSlotItem(icon = "👖", dura = "72%", color = PvpYellow)
            ArmorSlotItem(icon = "👢", dura = "45%", color = PvpOrange)
            ArmorSlotItem(icon = "🗡️", dura = "98%", color = FeatherCyan)
        }

        // Bottom Right: CPS & Combo & Keystrokes
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC000000))
                .border(0.8.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${"%.1f".format(cps)} CPS | $combo COMBO",
                color = FeatherPurple,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            // Keystrokes mini visualizer
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                KeyPill("W", active = true)
                KeyPill("A", active = false)
                KeyPill("S", active = false)
                KeyPill("D", active = true)
                KeyPill("LMB", active = true)
            }
        }
    }
}

@Composable
private fun PotionStatusRow(
    emoji: String,
    name: String,
    time: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, fontSize = 9.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = name, fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = time, fontSize = 8.sp, color = Color.White)
    }
}

@Composable
private fun ArmorSlotItem(
    icon: String,
    dura: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 11.sp)
        Text(text = dura, fontSize = 7.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun KeyPill(label: String, active: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(if (active) FeatherPurple else Color(0x44FFFFFF))
            .padding(horizontal = 3.dp, vertical = 1.dp)
    ) {
        Text(
            text = label,
            fontSize = 7.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
