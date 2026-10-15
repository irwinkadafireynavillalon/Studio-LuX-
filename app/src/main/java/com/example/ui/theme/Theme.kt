package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LuxGoldSilverColorScheme = darkColorScheme(
    primary = LuxGoldPrimary,
    onPrimary = Color(0xFF1A1405),
    primaryContainer = LuxGoldContainer,
    onPrimaryContainer = LuxOnGoldContainer,
    secondary = LuxPlatinumSecondary,
    onSecondary = Color(0xFF141820),
    secondaryContainer = LuxSecondaryContainer,
    onSecondaryContainer = LuxOnSecondaryContainer,
    tertiary = LuxGoldLight,
    onTertiary = Color(0xFF1A1405),
    background = Color.Transparent,
    onBackground = LuxPlatinumSecondary,
    surface = LuxDarkSurface,
    onSurface = LuxPlatinumSecondary,
    surfaceVariant = LuxDarkSurfaceVariant,
    onSurfaceVariant = LuxOnDarkMuted,
    outline = LuxGoldPrimary,
    outlineVariant = LuxPlatinumSecondary,
    error = LuxAccentRed,
    onError = Color(0xFF1A1405)
)

@Composable
fun LUXTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = LuxGoldSilverColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color(0x40120E04).toArgb()
                window.navigationBarColor = Color(0x40120E04).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
