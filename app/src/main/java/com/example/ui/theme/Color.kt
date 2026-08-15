package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Obsidian & Dark Gaming Backgrounds
val ObsidianDark = Color(0xFF0A0B10)
val SurfaceDark = Color(0xFF12141F)
val SurfaceDarkCard = Color(0xFF181B29)
val SurfaceDarkGlass = Color(0xCC181B29)
val SurfaceElevated = Color(0xFF222638)
val BorderSubtle = Color(0x334F5B7D)
val BorderGlow = Color(0x669D4EDD)

// Feather Signature Accent (Electric Violet & Cyan)
val FeatherPurple = Color(0xFF9D4EDD)
val FeatherPurpleBright = Color(0xFFC77DFF)
val FeatherPurpleDeep = Color(0xFF7B2CBF)
val FeatherCyan = Color(0xFF00F5D4)
val FeatherBlue = Color(0xFF00BBF9)

// PvP & Status Accents
val PvpRed = Color(0xFFFF3366)
val PvpGreen = Color(0xFF10B981)
val PvpYellow = Color(0xFFFBBF24)
val PvpOrange = Color(0xFFF97316)

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val FeatherGlowBrush = Brush.horizontalGradient(
    colors = listOf(FeatherPurpleBright, FeatherCyan)
)

val FeatherHeroBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF2A144E), Color(0xFF0D1B2A), ObsidianDark)
)

val GlassCardBrush = Brush.verticalGradient(
    colors = listOf(Color(0x33383E58), Color(0x1A1A2234))
)

val LaunchButtonBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9), Color(0xFF06B6D4))
)
