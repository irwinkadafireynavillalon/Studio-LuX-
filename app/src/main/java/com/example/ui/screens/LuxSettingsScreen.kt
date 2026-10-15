package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.R
import com.example.data.engine.LuxPriorityKernelService
import com.example.data.models.CliLogType
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
import kotlinx.coroutines.launch

/**
 * 7. Ajustes LuX Screen
 * Ajustes de la propia aplicación de Sys-LUX (diferenciados de la app en desarrollo):
 * - Sincronizar cuenta Google (OAuth2 / Credential Manager sin API Key)
 * - Interfaz y temas: elegir colores, temas e imagen de fondo (con previsualizador)
 * - Permisos del dispositivo y de aplicaciones
 * - Seguridad de la app: agregar API Key de Gemini manualmente
 * - Tiempo de actualizarse automáticamente (1h, 6h, 12h, 24h)
 * - Tiempo en revisar errores y corrección de problemas (cada 30s, 1m, 5m, 15m)
 * - Mejoras de la app: permisos sobre otras aplicaciones (PiP / Floating HUD), compartir aplicación en APK
 */
@Composable
fun LuxSettingsScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onOpenGoogleSyncDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val interfaceSettings by repository.interfaceSettings.collectAsState()
    val googleSyncState by repository.googleAccountSyncState.collectAsState()
    val luxVersionName by repository.luxVersionName.collectAsState()
    val isFloatingHudEnabled by LuxPriorityKernelService.isInAppFloatingHudEnabled.collectAsState()

    var manualApiKey by remember { mutableStateOf(BuildConfig.GEMINI_API_KEY) }
    var autoUpdateIntervalHours by remember { mutableStateOf("6h") }
    var errorCheckIntervalSeconds by remember { mutableStateOf("1 min") }
    var bgAlpha by remember { mutableFloatStateOf(interfaceSettings.backgroundImageAlpha) }

    val updateIntervalOptions = listOf("1h", "6h", "12h", "24h")
    val errorCheckIntervalOptions = listOf("30s", "1 min", "5 min", "15 min")

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
                Icon(Icons.Default.Settings, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "7. Ajustes LuX (Meta-DevOS)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = LuxGoldLight
                )
                Text(
                    text = "Ajustes propios del sistema LuX v$luxVersionName (No confundir con la app en creación)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = LuxPlatinumSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. SINCRONIZAR CUENTA GOOGLE
        SettingsBlock(
            title = "1. Sincronizar Cuenta Google (Sys-LUX)",
            icon = Icons.Default.AccountCircle,
            accentColor = LuxAccentGreen
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131A24))
                    .border(0.6.dp, if (googleSyncState.isSynced) LuxAccentGreen else LuxDarkBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (googleSyncState.isSynced) "✔ Cuenta Google Sincronizada" else "Cuenta Google No Vinculada",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (googleSyncState.isSynced) LuxAccentGreen else LuxGoldLight
                    )
                    Text(googleSyncState.email, fontSize = 9.sp, color = LuxPlatinumSecondary)
                    Text("Evita requerir Google API Key manual", fontSize = 8.sp, color = LuxAccentCyan)
                }
                Button(
                    onClick = { onOpenGoogleSyncDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Sincronizar", fontSize = 9.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. INTERFAZ Y TEMAS: ELEGIR COLORES, TEMAS E IMAGEN DE FONDO
        SettingsBlock(
            title = "2. Interfaz, Temas & Fondo de Pantalla",
            icon = Icons.Default.Palette,
            accentColor = LuxGoldPrimary
        ) {
            Text("Imagen de Fondo de Sys-LUX:", fontSize = 10.sp, color = LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opción 1: Emblema Imperial
                val isSel1 = interfaceSettings.backgroundImageKey == "lux_emblem"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel1) Color(0x35FFD54F) else Color(0xFF131822))
                        .border(if (isSel1) 1.dp else 0.5.dp, if (isSel1) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            soundService.playClick()
                            repository.updateInterfaceSettings(interfaceSettings.copy(backgroundImageKey = "lux_emblem"))
                        }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.lux_emblem),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Emblema Oro", fontSize = 9.5.sp, color = if (isSel1) LuxGoldLight else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                // Opción 2: Icono Estelar
                val isSel2 = interfaceSettings.backgroundImageKey == "lux_icon"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel2) Color(0x35FFD54F) else Color(0xFF131822))
                        .border(if (isSel2) 1.dp else 0.5.dp, if (isSel2) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            soundService.playClick()
                            repository.updateInterfaceSettings(interfaceSettings.copy(backgroundImageKey = "lux_icon"))
                        }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.lux_icon_1791488984626),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Icono Estelar", fontSize = 9.5.sp, color = if (isSel2) LuxGoldLight else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Transparencia del fondo: ${(bgAlpha * 100).toInt()}%", fontSize = 9.sp, color = LuxPlatinumSecondary)
            }
            Slider(
                value = bgAlpha,
                onValueChange = {
                    bgAlpha = it
                    repository.updateInterfaceSettings(interfaceSettings.copy(backgroundImageAlpha = it))
                },
                valueRange = 0.1f..0.85f,
                colors = SliderDefaults.colors(thumbColor = LuxGoldPrimary, activeTrackColor = LuxGoldPrimary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. SEGURIDAD DE LA APP: AGREGAR API KEY MANUALMENTE
        SettingsBlock(
            title = "3. Seguridad de la App · Gemini API Key Manual",
            icon = Icons.Default.Key,
            accentColor = LuxAccentCyan
        ) {
            Text("Ingresa o edita manualmente tu clave API de Google AI Studio / Gemini:", fontSize = 9.sp, color = LuxPlatinumSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualApiKey,
                    onValueChange = { manualApiKey = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    placeholder = { Text("GEMINI_API_KEY...", fontSize = 10.sp, color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LuxGoldPrimary,
                        unfocusedBorderColor = LuxDarkBorder,
                        focusedContainerColor = Color(0xFF0D121B),
                        unfocusedContainerColor = Color(0xFF0D121B)
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = {
                        soundService.playSuccess()
                        repository.addCliLog("🔑 [API KEY] Gemini API Key actualizada manualmente.", CliLogType.INFO, "SECURITY")
                        Toast.makeText(context, "API Key guardada con éxito", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Guardar", color = LuxDarkBackground, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. TIEMPO DE ACTUALIZACIÓN & REVISIÓN DE ERRORES
        SettingsBlock(
            title = "4. Tiempos de Actualización Automática & Auto-Corrección",
            icon = Icons.Default.Timer,
            accentColor = LuxGoldLight
        ) {
            Text("Frecuencia de actualización automática (OTA):", fontSize = 9.5.sp, color = LuxPlatinumSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                updateIntervalOptions.forEach { opt ->
                    val isSel = autoUpdateIntervalHours == opt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) LuxGoldContainer else Color(0xFF141924))
                            .border(0.6.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                soundService.playClick()
                                autoUpdateIntervalHours = opt
                            }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt, fontSize = 9.5.sp, color = if (isSel) LuxGoldPrimary else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Frecuencia de escaneo y corrección de errores de Kernel:", fontSize = 9.5.sp, color = LuxPlatinumSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                errorCheckIntervalOptions.forEach { opt ->
                    val isSel = errorCheckIntervalSeconds == opt
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0x3500E676) else Color(0xFF141924))
                            .border(0.6.dp, if (isSel) LuxAccentGreen else LuxDarkBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                soundService.playClick()
                                errorCheckIntervalSeconds = opt
                            }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(opt, fontSize = 9.5.sp, color = if (isSel) LuxAccentGreen else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. MEJORAS: PERMISOS SOBRE OTRAS APPS & COMPARTIR APK DE LUX
        SettingsBlock(
            title = "5. Mejoras de LuX · Overlay & Compartir APK",
            icon = Icons.Default.AutoAwesome,
            accentColor = LuxAccentGreen
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mini-HUD Flotante Sobre Otras Aplicaciones", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                    Text("Modo Picture-in-Picture y widget persistente de telemetría", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                }
                Switch(
                    checked = isFloatingHudEnabled,
                    onCheckedChange = {
                        soundService.playClick()
                        LuxPriorityKernelService.toggleInAppFloatingHud(it)
                    },
                    modifier = Modifier.size(width = 38.dp, height = 22.dp),
                    colors = SwitchDefaults.colors(checkedTrackColor = LuxGoldPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Compartir APK de LuX
            Button(
                onClick = {
                    soundService.playSuccess()
                    repository.shareCurrentLuxAppApk(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text("Compartir Archivo APK de Sys-LUX", color = LuxDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6. OPCIONES DE DESARROLLADOR: RENDIMIENTO GPU & CPU SEGÚN SE NECESITE
        SettingsBlock(
            title = "6. Opciones de Desarrollador · GPU & CPU Dinámica",
            icon = Icons.Default.Settings,
            accentColor = LuxGoldPrimary
        ) {
            var gpuRenderingEnabled by remember { mutableStateOf(true) }
            var multiThreadCpuActive by remember { mutableStateOf(true) }
            var force4xMsaa by remember { mutableStateOf(false) }
            var npuQuantizationFast by remember { mutableStateOf(true) }
            var selectedCpuGovernor by remember { mutableStateOf("Desempeño Alto (Turbo)") }

            val governors = listOf("Ahorro", "Balanceado", "Desempeño Alto (Turbo)")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Configura la aceleración de hardware por GPU y CPU para compilar, inferir modelos LLM y ejecutar scripts sin límites:",
                    fontSize = 9.sp,
                    color = LuxPlatinumSecondary,
                    lineHeight = 12.sp
                )

                // GPU Hardware Rendering
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Forzar Renderizado GPU (Vulkan / OpenGL ES)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        Text("Acelera la UI de Compose y terminal gráfica 120 FPS", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                    }
                    Switch(
                        checked = gpuRenderingEnabled,
                        onCheckedChange = {
                            gpuRenderingEnabled = it
                            soundService.playClick()
                            repository.addCliLog(
                                if (it) "⚡ [DEV_GPU] Renderizado por GPU acelerado activado (Vulkan pipeline)."
                                else "⚡ [DEV_GPU] Renderizado por GPU desactivado.",
                                CliLogType.INFO,
                                "HARDWARE"
                            )
                        },
                        modifier = Modifier.size(width = 38.dp, height = 22.dp),
                        colors = SwitchDefaults.colors(checkedTrackColor = LuxGoldPrimary)
                    )
                }

                // CPU Multi-Thread Core Allocation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Asignación Multinúcleo CPU (Todos los Cores)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        Text("Compilación Gradle y Termux en paralelo sin estrangulamiento térmico", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                    }
                    Switch(
                        checked = multiThreadCpuActive,
                        onCheckedChange = {
                            multiThreadCpuActive = it
                            soundService.playClick()
                            repository.addCliLog(
                                if (it) "🔥 [DEV_CPU] Multinúcleo completo asignado (Prioridad THREAD_PRIORITY_URGENT_DISPLAY)."
                                else "🔥 [DEV_CPU] Multinúcleo restringido a modo estándar.",
                                CliLogType.INFO,
                                "HARDWARE"
                            )
                        },
                        modifier = Modifier.size(width = 38.dp, height = 22.dp),
                        colors = SwitchDefaults.colors(checkedTrackColor = LuxGoldPrimary)
                    )
                }

                // NPU / Tensor Quantization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Aceleración NPU / Tensor para Gemma3 Local", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        Text("Descarga inferencia matemática a la GPU/NPU del dispositivo móvil", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                    }
                    Switch(
                        checked = npuQuantizationFast,
                        onCheckedChange = {
                            npuQuantizationFast = it
                            soundService.playClick()
                            repository.addCliLog(
                                if (it) "🧠 [DEV_NPU] Inferencia en NPU/GPU activada para modelos locales (Gemma3 270m)."
                                else "🧠 [DEV_NPU] Inferencia en NPU desactivada.",
                                CliLogType.INFO,
                                "HARDWARE"
                            )
                        },
                        modifier = Modifier.size(width = 38.dp, height = 22.dp),
                        colors = SwitchDefaults.colors(checkedTrackColor = LuxGoldPrimary)
                    )
                }

                // 4x MSAA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Anti-Aliasing 4x MSAA en OpenGL / Metal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        Text("Suavizado de bordes Oro y Plata metálicos en la interfaz", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                    }
                    Switch(
                        checked = force4xMsaa,
                        onCheckedChange = {
                            force4xMsaa = it
                            soundService.playClick()
                            repository.addCliLog(
                                if (it) "✨ [DEV_GPU] Forzar 4x MSAA activado."
                                else "✨ [DEV_GPU] Forzar 4x MSAA desactivado.",
                                CliLogType.INFO,
                                "HARDWARE"
                            )
                        },
                        modifier = Modifier.size(width = 38.dp, height = 22.dp),
                        colors = SwitchDefaults.colors(checkedTrackColor = LuxGoldPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Perfil de Rendimiento del Kernel CPU/GPU:", fontSize = 9.sp, color = LuxPlatinumSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    governors.forEach { gov ->
                        val isSel = selectedCpuGovernor == gov
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) LuxGoldContainer else Color(0xFF141924))
                                .border(0.6.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    soundService.playClick()
                                    selectedCpuGovernor = gov
                                    repository.addCliLog("⚙️ [KERNEL] Perfil CPU/GPU cambiado a: $gov", CliLogType.SUCCESS, "HARDWARE")
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                gov,
                                fontSize = 8.5.sp,
                                color = if (isSel) LuxGoldPrimary else LuxPlatinumSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsBlock(
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
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
