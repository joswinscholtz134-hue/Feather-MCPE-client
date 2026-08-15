package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val displayName: String, val primaryColor: Color, val accentColor: Color) {
    FEATHER_PURPLE("Obsidian Purple", FeatherPurple, FeatherCyan),
    CYBER_BLUE("Cyber Blue", FeatherBlue, FeatherCyan),
    EMERALD_GREEN("Emerald Glow", Color(0xFF10B981), Color(0xFF34D399)),
    BLOOD_RED("Blood Rose", Color(0xFFF43F5E), Color(0xFFFB7185)),
    MIDNIGHT_AMOLED("Pitch Midnight", Color(0xFF38BDF8), Color(0xFFF8FAFC))
}

fun getDarkColorScheme(themeMode: AppThemeMode) = darkColorScheme(
    primary = themeMode.primaryColor,
    onPrimary = Color.White,
    primaryContainer = themeMode.primaryColor.copy(alpha = 0.2f),
    onPrimaryContainer = themeMode.primaryColor,
    secondary = themeMode.accentColor,
    onSecondary = Color.Black,
    secondaryContainer = themeMode.accentColor.copy(alpha = 0.2f),
    onSecondaryContainer = themeMode.accentColor,
    tertiary = FeatherCyan,
    onTertiary = Color.Black,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderGlow
)

@Composable
fun FeatherClientTheme(
    themeMode: AppThemeMode = AppThemeMode.FEATHER_PURPLE,
    content: @Composable () -> Unit
) {
    val colorScheme = getDarkColorScheme(themeMode)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
