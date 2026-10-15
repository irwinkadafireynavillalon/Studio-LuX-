package com.example.ui.theme

import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Deep Gold (#D4AF37) and Bright Silver (#C0C0C0) Metallic Palette
val DeepGold = Color(0xFFD4AF37)
val BrightSilver = Color(0xFFC0C0C0)
val MetallicGoldLight = Color(0xFFFFE57F)
val MetallicSilverDark = Color(0xFF9E9E9E)
val MetallicBackground = Color(0xFF0F1117)
val MetallicSurface = Color(0xFF1A1D24)
val MetallicSurfaceVariant = Color(0xFF262B35)

val MetallicColorScheme = darkColorScheme(
    primary = DeepGold,
    onPrimary = Color(0xFF1A1400),
    secondary = BrightSilver,
    onSecondary = Color(0xFF141414),
    tertiary = MetallicGoldLight,
    background = MetallicBackground,
    surface = MetallicSurface,
    onSurface = BrightSilver,
    surfaceVariant = MetallicSurfaceVariant,
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = DeepGold,
    outlineVariant = BrightSilver
)

val MetallicBorderBrush = Brush.linearGradient(
    colors = listOf(
        DeepGold,
        BrightSilver,
        MetallicGoldLight,
        BrightSilver,
        DeepGold
    )
)

/**
 * Applies a metallic gradient border effect with deep gold and bright silver to UI containers.
 */
fun Modifier.borderGradient(
    width: Dp = 1.5.dp,
    shape: Shape,
    brush: Brush = MetallicBorderBrush
): Modifier = this.border(width = width, brush = brush, shape = shape)

@Composable
fun MetallicTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MetallicColorScheme,
        typography = Typography,
        content = content
    )
}
