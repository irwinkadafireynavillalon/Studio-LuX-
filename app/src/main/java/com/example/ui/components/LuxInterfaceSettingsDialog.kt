package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.LuxInterfaceSettings
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxGoldSilverGlassBrush
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import com.example.ui.theme.LuxSilverLight

@Composable
fun LuxInterfaceSettingsDialog(
    currentSettings: LuxInterfaceSettings,
    onUpdateSettings: (LuxInterfaceSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var bgKey by remember { mutableStateOf(currentSettings.backgroundImageKey) }
    var bgAlpha by remember { mutableFloatStateOf(currentSettings.backgroundImageAlpha) }
    var glassAlpha by remember { mutableFloatStateOf(currentSettings.surfaceGlassAlpha) }
    var borderGlow by remember { mutableFloatStateOf(currentSettings.goldSilverBorderGlow) }
    var preset by remember { mutableStateOf(currentSettings.presetStyle) }
    var showVignette by remember { mutableStateOf(currentSettings.showBackgroundVignette) }
    var floatingSize by remember { mutableFloatStateOf(currentSettings.floatingButtonSizeDp) }
    var floatingAlpha by remember { mutableFloatStateOf(currentSettings.floatingButtonAlpha) }

    fun pushLiveUpdate(
        newBgKey: String = bgKey,
        newBgAlpha: Float = bgAlpha,
        newGlassAlpha: Float = glassAlpha,
        newBorderGlow: Float = borderGlow,
        newPreset: String = preset,
        newVignette: Boolean = showVignette,
        newFloatingSize: Float = floatingSize,
        newFloatingAlpha: Float = floatingAlpha
    ) {
        onUpdateSettings(
            LuxInterfaceSettings(
                backgroundImageKey = newBgKey,
                backgroundImageAlpha = newBgAlpha,
                surfaceGlassAlpha = newGlassAlpha,
                goldSilverBorderGlow = newBorderGlow,
                presetStyle = newPreset,
                showBackgroundVignette = newVignette,
                compactUiMode = true,
                floatingButtonSizeDp = newFloatingSize,
                floatingButtonAlpha = newFloatingAlpha
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xEB0E121B),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .border(1.2.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(18.dp))
            .testTag("dialog_interface_settings"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(LuxGoldSilverGlassBrush)
                        .border(1.dp, LuxGoldSilverBorderBrush, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = LuxGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AJUSTES DE INTERFAZ · ORO Y PLATA",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        ),
                        color = LuxGoldLight
                    )
                    Text(
                        text = "Imagen de Fondo, Transparencias y Bordes Metálicos",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = LuxPlatinumSecondary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // 1. Selector de Imagen de Fondo
                Text(
                    text = "IMAGEN DE FONDO ACTIVA:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = LuxGoldPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val options = listOf(
                        Triple("lux_emblem", "Emblema Oro/Plata", R.drawable.lux_emblem),
                        Triple("lux_icon", "Icono LUX Imperial", R.drawable.lux_icon_1791488984626)
                    )
                    options.forEach { (key, label, drawableRes) ->
                        val isSelected = bgKey == key
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) Color(0x47E5C158) else Color(0x331E2430)
                                )
                                .border(
                                    width = if (isSelected) 1.2.dp else 0.7.dp,
                                    brush = LuxGoldSilverBorderBrush,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    bgKey = key
                                    pushLiveUpdate(newBgKey = key)
                                }
                                .padding(8.dp)
                                .testTag("btn_bg_option_$key"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = drawableRes),
                                contentDescription = label,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.8.dp, LuxGoldPrimary, RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = if (isSelected) LuxGoldLight else LuxSilverLight,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isSelected) "Activo" else "Usar fondo",
                                    fontSize = 8.5.sp,
                                    color = if (isSelected) LuxGoldPrimary else LuxOnDarkMuted
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = LuxGoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Presets rápidos Oro y Plata Transparentes
                Text(
                    text = "ESTILOS TRANSPARENTES ORO Y PLATA:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = LuxPlatinumSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        "ORO_Y_PLATA_TRANSPARENTE" to "Oro & Plata Cristal",
                        "CRISTAL_PLATINO_PURO" to "Plata Transparente",
                        "ORO_IMPERIAL_VIVO" to "Oro Real Luminoso"
                    )
                    presets.forEach { (presetId, label) ->
                        val isSel = preset == presetId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSel) Color(0x4DE5C158) else Color(0x2EE2E8F0)
                                )
                                .border(
                                    width = if (isSel) 1.dp else 0.5.dp,
                                    brush = LuxGoldSilverBorderBrush,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    preset = presetId
                                    when (presetId) {
                                        "ORO_Y_PLATA_TRANSPARENTE" -> {
                                            bgAlpha = 0.38f
                                            glassAlpha = 0.62f
                                            borderGlow = 0.92f
                                        }
                                        "CRISTAL_PLATINO_PURO" -> {
                                            bgAlpha = 0.45f
                                            glassAlpha = 0.52f
                                            borderGlow = 0.98f
                                        }
                                        else -> {
                                            bgAlpha = 0.50f
                                            glassAlpha = 0.68f
                                            borderGlow = 1.0f
                                        }
                                    }
                                    pushLiveUpdate(
                                        newBgAlpha = bgAlpha,
                                        newGlassAlpha = glassAlpha,
                                        newBorderGlow = borderGlow,
                                        newPreset = presetId
                                    )
                                }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) LuxGoldLight else LuxSilverLight,
                                maxLines = 1
                            )
                        }
                    }
                }

                // 3. Control de Opacidad de la Imagen de Fondo
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Visibilidad de Imagen de Fondo",
                            fontSize = 10.sp,
                            color = LuxOnDark
                        )
                        Text(
                            text = "${(bgAlpha * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = LuxGoldPrimary
                        )
                    }
                    Slider(
                        value = bgAlpha,
                        onValueChange = {
                            bgAlpha = it
                            pushLiveUpdate(newBgAlpha = it)
                        },
                        valueRange = 0.15f..0.75f,
                        modifier = Modifier.height(24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = LuxGoldPrimary,
                            activeTrackColor = LuxGoldPrimary,
                            inactiveTrackColor = LuxPlatinumSecondary.copy(alpha = 0.3f)
                        )
                    )
                }

                // 4. Control de Transparencia de Tarjetas Oro y Plata
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Transparencia Cristal Oro/Plata",
                            fontSize = 10.sp,
                            color = LuxOnDark
                        )
                        Text(
                            text = "${((1f - glassAlpha) * 100).toInt()}% Translúcido",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = LuxPlatinumSecondary
                        )
                    }
                    Slider(
                        value = glassAlpha,
                        onValueChange = {
                            glassAlpha = it
                            pushLiveUpdate(newGlassAlpha = it)
                        },
                        valueRange = 0.35f..0.90f,
                        modifier = Modifier.height(24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = LuxPlatinumSecondary,
                            activeTrackColor = LuxPlatinumSecondary,
                            inactiveTrackColor = LuxGoldPrimary.copy(alpha = 0.3f)
                        )
                    )
                }

                // 5. Intensidad de Bordes Metálicos Oro y Plata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Velo Atmosférico Oro y Plata en Fondo",
                            fontSize = 10.sp,
                            color = LuxOnDark
                        )
                    }
                    Switch(
                        checked = showVignette,
                        onCheckedChange = {
                            showVignette = it
                            pushLiveUpdate(newVignette = it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LuxDarkBackground,
                            checkedTrackColor = LuxGoldPrimary
                        )
                    )
                }

                // 6. Botón Flotante: Tamaño y Transparencia
                Text(
                    text = "BOTÓN FLOTANTE (HUD / PiP):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = LuxGoldPrimary
                )

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tamaño del Botón Flotante",
                            fontSize = 10.sp,
                            color = LuxOnDark
                        )
                        Text(
                            text = "${floatingSize.toInt()} dp",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = LuxGoldPrimary
                        )
                    }
                    Slider(
                        value = floatingSize,
                        onValueChange = {
                            floatingSize = it
                            pushLiveUpdate(newFloatingSize = it)
                        },
                        valueRange = 32f..72f,
                        modifier = Modifier.height(24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = LuxGoldPrimary,
                            activeTrackColor = LuxGoldPrimary,
                            inactiveTrackColor = LuxPlatinumSecondary.copy(alpha = 0.3f)
                        )
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Opacidad / Transparencia del Botón Flotante",
                            fontSize = 10.sp,
                            color = LuxOnDark
                        )
                        Text(
                            text = "${(floatingAlpha * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = LuxPlatinumSecondary
                        )
                    }
                    Slider(
                        value = floatingAlpha,
                        onValueChange = {
                            floatingAlpha = it
                            pushLiveUpdate(newFloatingAlpha = it)
                        },
                        valueRange = 0.20f..1.0f,
                        modifier = Modifier.height(24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = LuxPlatinumSecondary,
                            activeTrackColor = LuxPlatinumSecondary,
                            inactiveTrackColor = LuxGoldPrimary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("btn_save_interface_settings")
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Listo", color = LuxDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    val defaults = LuxInterfaceSettings()
                    bgKey = defaults.backgroundImageKey
                    bgAlpha = defaults.backgroundImageAlpha
                    glassAlpha = defaults.surfaceGlassAlpha
                    borderGlow = defaults.goldSilverBorderGlow
                    preset = defaults.presetStyle
                    showVignette = defaults.showBackgroundVignette
                    onUpdateSettings(defaults)
                },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxPlatinumSecondary.copy(alpha = 0.6f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Restaurar Oro/Plata", color = LuxPlatinumSecondary, fontSize = 10.5.sp)
            }
        }
    )
}
