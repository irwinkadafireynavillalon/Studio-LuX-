package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// MetallicGoldSilver Color Scheme for Material3
val MetallicGoldSilverColorScheme = darkColorScheme(
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

/**
 * Extension modifier to apply a gradient border with metallic gold and silver tones
 * over any container shape with customizable width.
 */
fun Modifier.metallicBorder(
    width: Dp = 1.dp,
    shape: Shape,
    brush: Brush = LuxGoldSilverBorderBrush
): Modifier = this.border(width = width, brush = brush, shape = shape)

/**
 * Semi-transparent text style modifiers for transparent/translucent premium typography.
 */
val SemiTransparentGoldTextStyle = androidx.compose.ui.text.TextStyle(
    color = LuxGoldLight.copy(alpha = 0.85f)
)

val SemiTransparentSilverTextStyle = androidx.compose.ui.text.TextStyle(
    color = LuxPlatinumSecondary.copy(alpha = 0.80f)
)

@Composable
fun LUXTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = MetallicGoldSilverColorScheme
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
