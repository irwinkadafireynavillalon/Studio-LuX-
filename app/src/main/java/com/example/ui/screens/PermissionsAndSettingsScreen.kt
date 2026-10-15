package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliLogType
import com.example.data.models.TerminalThemeMode
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxPlatinumSecondary

/**
 * 3. Permisos y Ajustes Screen
 * - Ajustes de batería (Ignorar optimización, wakelock, alto rendimiento)
 * - Permisos de la aplicación (Notificaciones, overlay, almacenamiento)
 * - Permisos del dispositivo (Cámara, micrófono, ubicación, bluetooth)
 * - Agregar paletas de colores (Oro Imperial, Plata Neón, Cyber Cyan, Matrix Verde, AMOLED)
 * - Agregar temas (Modo Oscuro, Vidrio Translúcido, Alto Contraste)
 * - Vincular aplicaciones (Termux, Termux:API, Navegador, Gestor de archivos)
 * - Vincular funciones (Antigravity CLI, Lifertl, TTS de voz, AutoDevOs)
 * - Botones pequeños y fluidos sin estorbarse.
 */
@Composable
fun PermissionsAndSettingsScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interfaceSettings by repository.interfaceSettings.collectAsState()
    val currentTheme by repository.terminalTheme.collectAsState()

    var batteryOptimizationIgnored by remember { mutableStateOf(true) }
    var highPerformanceWakelock by remember { mutableStateOf(true) }
    var appNotificationsGranted by remember { mutableStateOf(true) }
    var overlayWindowGranted by remember { mutableStateOf(true) }
    var cameraPermission by remember { mutableStateOf(true) }
    var micPermission by remember { mutableStateOf(true) }
    var locationPermission by remember { mutableStateOf(true) }

    var linkedTermux by remember { mutableStateOf(true) }
    var linkedTermuxApi by remember { mutableStateOf(true) }
    var linkedAntigravityCli by remember { mutableStateOf(true) }
    var linkedLifertl by remember { mutableStateOf(true) }
    var linkedAutoDevOs by remember { mutableStateOf(true) }
    var linkedTtsVoice by remember { mutableStateOf(true) }

    val colorPalettes = listOf(
        "Oro Imperial & Plata" to listOf(Color(0xFFFFD54F), Color(0xFFE2E8F0)),
        "Cyberpunk Cyan" to listOf(Color(0xFF00E5FF), Color(0xFF18FFFF)),
        "Matrix Verde Neón" to listOf(Color(0xFF00E676), Color(0xFF69F0AE)),
        "Púrpura Amatista" to listOf(Color(0xFFD500F9), Color(0xFFE040FB)),
        "Obsidiana AMOLED" to listOf(Color(0xFFFFFFFF), Color(0xFF757575))
    )
    var selectedPaletteIndex by remember { mutableStateOf(0) }

    val appThemes = listOf(
        "Cristal Oro y Plata" to "ORO_Y_PLATA_TRANSPARENTE",
        "Oscuro Profundo" to "DEEP_DARK",
        "Alto Contraste Neón" to "HIGH_CONTRAST"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(10.dp))
                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(LuxGoldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "3. Permisos y Ajustes",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = LuxGoldLight
                )
                Text(
                    text = "Batería · Permisos · Paletas · Temas · Vinculaciones",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = LuxPlatinumSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 1: AJUSTES DE BATERÍA
        SettingsCategoryCard(
            title = "Ajustes de Batería & Rendimiento",
            icon = Icons.Default.BatteryChargingFull,
            accentColor = LuxAccentGreen
        ) {
            SmallToggleRow(
                title = "Ignorar Optimización de Batería",
                subtitle = "Permite a LuX operar en segundo plano sin congelarse",
                checked = batteryOptimizationIgnored,
                onCheckedChange = {
                    batteryOptimizationIgnored = it
                    soundService.playClick()
                    repository.addCliLog("🔋 [BATERÍA] Optimización de batería: ${if (it) "IGNORADA" else "ESTÁNDAR"}", CliLogType.INFO, "BATTERY")
                    Toast.makeText(context, "Optimización de batería actualizada", Toast.LENGTH_SHORT).show()
                }
            )
            SmallToggleRow(
                title = "Wakelock de Alto Rendimiento",
                subtitle = "Mantiene los hilos de IA y compilación Gradle a máxima prioridad",
                checked = highPerformanceWakelock,
                onCheckedChange = {
                    highPerformanceWakelock = it
                    soundService.playClick()
                    repository.addCliLog("⚡ [KERNEL] Wakelock NPU: ${if (it) "ACTIVO" else "DESACTIVADO"}", CliLogType.INFO, "KERNEL")
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 2: PERMISOS DE LA APLICACIÓN & DISPOSITIVO
        SettingsCategoryCard(
            title = "Permisos de Aplicación y Dispositivo",
            icon = Icons.Default.Security,
            accentColor = LuxAccentCyan
        ) {
            SmallToggleRow(
                title = "Permisos de Notificaciones",
                subtitle = "Alertas de compilación APK y auto-corrección",
                checked = appNotificationsGranted,
                onCheckedChange = {
                    appNotificationsGranted = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Dibujar Sobre Otras Aplicaciones (Overlay)",
                subtitle = "Ventana flotante mini-HUD y PiP interactivo",
                checked = overlayWindowGranted,
                onCheckedChange = {
                    overlayWindowGranted = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Cámara & Análisis Visual",
                subtitle = "Multimodalidad para capturar pantallas de errores de código",
                checked = cameraPermission,
                onCheckedChange = {
                    cameraPermission = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Micrófono & Comandos de Voz",
                subtitle = "Prompt por voz y transcripción integrada",
                checked = micPermission,
                onCheckedChange = {
                    micPermission = it
                    soundService.playClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 3: PALETAS DE COLORES Y TEMAS
        SettingsCategoryCard(
            title = "Paletas de Colores & Temas Visuales",
            icon = Icons.Default.Palette,
            accentColor = LuxGoldPrimary
        ) {
            Text("Seleccionar Paleta de Color:", fontSize = 10.sp, color = LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                colorPalettes.forEachIndexed { idx, (name, colors) ->
                    val isSel = selectedPaletteIndex == idx
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (isSel) LuxGoldContainer else Color(0xFF141924))
                            .border(0.7.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(7.dp))
                            .clickable {
                                soundService.playClick()
                                selectedPaletteIndex = idx
                                Toast.makeText(context, "Paleta '$name' activada", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(colors))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(name, fontSize = 9.sp, color = if (isSel) LuxGoldPrimary else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Estilos de Tema:", fontSize = 10.sp, color = LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                appThemes.forEach { (name, preset) ->
                    val isSel = interfaceSettings.presetStyle == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (isSel) Color(0x35FFD54F) else Color(0xFF141924))
                            .border(0.6.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(7.dp))
                            .clickable {
                                soundService.playClick()
                                repository.updateInterfaceSettings(interfaceSettings.copy(presetStyle = preset))
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name, fontSize = 9.sp, color = if (isSel) LuxGoldLight else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 4: VINCULAR APLICACIONES & FUNCIONES
        SettingsCategoryCard(
            title = "Vincular Aplicaciones y Funciones",
            icon = Icons.Default.Link,
            accentColor = LuxAccentCyan
        ) {
            SmallToggleRow(
                title = "Vincular Termux & Termux API",
                subtitle = "Acceso a binarios bash, pkg y scripts de emulación",
                checked = linkedTermux,
                onCheckedChange = {
                    linkedTermux = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Vincular Antigravity CLI",
                subtitle = "Puente de comandos autónomos para ejecutar tareas desde el chat",
                checked = linkedAntigravityCli,
                onCheckedChange = {
                    linkedAntigravityCli = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Vincular Lifertl Local Gateway (Puerto 4250)",
                subtitle = "Inferencia local ligera conectada con localhost",
                checked = linkedLifertl,
                onCheckedChange = {
                    linkedLifertl = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Vincular Motor Auto-DevOS & Auto-Corrección",
                subtitle = "Reparación continua de proyectos y parcheo en caliente",
                checked = linkedAutoDevOs,
                onCheckedChange = {
                    linkedAutoDevOs = it
                    soundService.playClick()
                }
            )
            SmallToggleRow(
                title = "Vincular Síntesis de Voz (TTS) y Audio",
                subtitle = "Lectura auditiva de respuestas y logs",
                checked = linkedTtsVoice,
                onCheckedChange = {
                    linkedTtsVoice = it
                    soundService.playClick()
                }
            )
        }
    }
}

@Composable
private fun SettingsCategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SmallToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = LuxGoldLight)
            Text(subtitle, fontSize = 8.5.sp, color = LuxPlatinumSecondary, lineHeight = 11.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(width = 38.dp, height = 22.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = LuxDarkBackground,
                checkedTrackColor = LuxGoldPrimary,
                uncheckedThumbColor = LuxPlatinumSecondary,
                uncheckedTrackColor = Color(0xFF1B2230)
            )
        )
    }
}
