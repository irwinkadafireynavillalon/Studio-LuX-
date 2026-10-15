package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// LUX Imperial Gold & Metallic Silver Transparent Glass Palette
val LuxGoldPrimary = Color(0xFFFFD54F)
val LuxGoldLight = Color(0xFFFFECB3)
val LuxGoldDark = Color(0xFFC89D28)
val LuxGoldContainer = Color(0x66423414)
val LuxOnGoldContainer = Color(0xFFFFF3CC)

// Pure Metallic Silver & Platinum Palette
val LuxPlatinumSecondary = Color(0xFFE2E8F0)
val LuxSilverLight = Color(0xFFF8FAFC)
val LuxPlatinumDark = Color(0xFF94A3B8)
val LuxSecondaryContainer = Color(0x6628303D)
val LuxOnSecondaryContainer = Color(0xFFF1F5F9)

// Transparent Gold & Silver Glassmorphic Tokens
val LuxGoldTransparent = Color(0x2EE5C158)
val LuxSilverTransparent = Color(0x29E2E8F0)
val LuxGoldGlassSurface = Color(0x4728200E)
val LuxSilverGlassSurface = Color(0x471E2430)
val LuxGoldBorderGlass = Color(0xB3FFD54F)
val LuxSilverBorderGlass = Color(0x99E2E8F0)

// Translucent Obsidian Glass Surfaces (allows background image to shine through)
val LuxDarkBackground = Color(0xFF090B10)
val LuxDarkSurface = Color(0x9E10141E)
val LuxDarkSurfaceVariant = Color(0x8C181E2C)
val LuxDarkSurfaceElevated = Color(0xB31D2334)
val LuxDarkBorder = Color(0x80C89D28)

val LuxAccentCyan = Color(0xFF38BDF8)
val LuxAccentGreen = Color(0xFF34D399)
val LuxAccentAmber = Color(0xFFFBBF24)
val LuxAccentOrange = Color(0xFFFF8A00)
val LuxAccentRed = Color(0xFFF87171)

val LuxOnDark = Color(0xFFF8FAFC)
val LuxOnDarkMuted = Color(0xFFCBD5E1)

val LuxTerminalBg = Color(0xA607090E)
val LuxTerminalText = Color(0xFFE2E8F0)
val LuxTerminalGreen = Color(0xFF4ADE80)
val LuxTerminalYellow = Color(0xFFFACC15)
val LuxTerminalCyan = Color(0xFF38BDF8)
val LuxTerminalRed = Color(0xFFF87171)
val LuxTerminalPurple = Color(0xFFC084FC)

// Reusable Dual Gold & Silver Gradient Brushes for Borders and Transparent Surfaces
val LuxGoldSilverBorderBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xD9FFD54F), // Oro Imperial
        Color(0xCCE2E8F0), // Plata Metálica
        Color(0xB3C89D28), // Oro Profundo
        Color(0xD9F8FAFC)  // Plata Brillante
    )
)

val LuxGoldSilverSubtleBorderBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0x99FFD54F),
        Color(0x99E2E8F0),
        Color(0x99FFD54F)
    )
)

val LuxGoldSilverGlassBrush = Brush.linearGradient(
    colors = listOf(
        Color(0x42332811), // Cristal Oro Translúcido
        Color(0x3D1C2331), // Cristal Plata Translúcido
        Color(0x47292110)  // Reflejo Oro/Plata
    )
)
