package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GamingColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = GamingDarkBackground,
    primaryContainer = ElectricCyanContainer,
    onPrimaryContainer = ElectricCyan,

    secondary = VividViolet,
    onSecondary = TextPrimary,
    secondaryContainer = VividVioletContainer,
    onSecondaryContainer = VividViolet,

    tertiary = IndigoAccent,
    onTertiary = TextPrimary,

    background = GamingDarkBackground,
    onBackground = TextPrimary,

    surface = GamingSurface,
    onSurface = TextPrimary,
    surfaceVariant = GamingSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = GamingCardBorder,
    outlineVariant = GamingCardBorderGlow,

    error = NeonRedAlert,
    onError = TextPrimary,
    errorContainer = NeonRedContainer,
    onErrorContainer = NeonRedAlert
)

@Composable
fun FrameForgeTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = GamingDarkBackground.toArgb()
                window.navigationBarColor = GamingDarkBackground.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = GamingColorScheme,
        typography = Typography,
        content = content
    )
}
