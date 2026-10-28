package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BlackWhiteColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = GamingDarkBackground,
    primaryContainer = ElectricCyanContainer,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = GamingDarkBackground,
    secondaryContainer = GamingSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonGreenActive,
    onTertiary = GamingDarkBackground,
    background = GamingDarkBackground,
    onBackground = TextPrimary,
    surface = GamingSurface,
    onSurface = TextPrimary,
    surfaceVariant = GamingSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GamingCardBorder,
    error = NeonRedAlert,
    onError = GamingDarkBackground
)

@Composable
fun FrameForgeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlackWhiteColorScheme,
        content = content
    )
}
