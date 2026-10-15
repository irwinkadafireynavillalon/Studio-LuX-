package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.LuxPriorityKernelService
import com.example.data.models.BuildStatus
import com.example.data.models.GeneratedAppProject
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.components.AutoDevOsModuleCard
import com.example.ui.components.UniversalFileAndCliHubCard
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxDarkSurfaceElevated
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxGoldSilverGlassBrush
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

@Composable
fun StudioDashboardScreen(
    repository: LuxRepository,
    projects: List<GeneratedAppProject>,
    activeProject: GeneratedAppProject,
    soundService: SoundHapticService,
    onNavigateToBuilder: () -> Unit,
    onNavigateToCli: () -> Unit,
    onNavigateToShareApk: () -> Unit,
    onNavigateToAndroidStudio: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onOpenPermissionsPanel: (() -> Unit)? = null,
    onNavigateToOrchestrator: (() -> Unit)? = null,
    onOpenInterfaceSettings: (() -> Unit)? = null,
    onOpenGoogleSyncDialog: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userEmail by repository.userEmail.collectAsState()
    val isOverlayActive by LuxPriorityKernelService.isInAppFloatingHudEnabled.collectAsState()
    val allocatedMemoryMb by LuxPriorityKernelService.allocatedMemoryMb.collectAsState()
    val luxVersion by repository.luxVersionName.collectAsState()
    val luxVersionCode by repository.luxVersionCode.collectAsState()
    val hostOptScore by repository.hostOptimizationScore.collectAsState()
    val isAutoPilot by repository.isAutoPilotEnabled.collectAsState()
    var showEmailDialog by remember { mutableStateOf(false) }
    var tempEmailInput by remember { mutableStateOf(userEmail) }

    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = {
                Text(
                    text = "Correo del Desarrollador",
                    color = LuxGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Configura tu correo electrónico para asociar las aplicaciones compiladas y autorizar la persistencia en el dispositivo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempEmailInput,
                        onValueChange = { tempEmailInput = it },
                        placeholder = { Text("ejemplo@gmail.com", color = LuxOnDarkMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxGoldPrimary,
                            unfocusedBorderColor = LuxDarkBorder,
                            focusedTextColor = LuxOnDark,
                            unfocusedTextColor = LuxOnDark
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("input_developer_email")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.setUserEmail(tempEmailInput)
                        showEmailDialog = false
                        Toast.makeText(context, "Correo guardado: $tempEmailInput", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Guardar", color = LuxDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEmailDialog = false },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Cancelar", color = LuxOnDarkMuted, fontSize = 11.sp)
                }
            },
            containerColor = LuxDarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Hero Meta-DevOS Card con bordes y transparencias Oro y Plata
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.2.dp,
                        LuxGoldSilverBorderBrush,
                        RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .background(LuxGoldSilverGlassBrush)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(LuxGoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = LuxGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Meta-DevOS Architecture",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = LuxGoldLight
                                )
                                Text(
                                    text = "Creador Autónomo de APKs · ${allocatedMemoryMb} MB Engine",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = LuxPlatinumSecondary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (onOpenInterfaceSettings != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x47FFD54F))
                                        .border(0.7.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(6.dp))
                                        .clickable {
                                            soundService.playClick()
                                            onOpenInterfaceSettings()
                                        }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                        .testTag("btn_open_interface_settings_dashboard")
                                ) {
                                    Text(
                                        text = "ORO / PLATA UI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 8.5.sp
                                        ),
                                        color = LuxGoldLight
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0x661E2433), RoundedCornerShape(6.dp))
                                    .border(0.6.dp, LuxPlatinumSecondary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PRIORIDAD MÁX",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = LuxAccentCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Genera proyectos completos en formato APK con Clean Architecture, Jetpack Compose, SQLite (Room), superposición sobre otras apps y máxima prioridad de memoria.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = LuxOnDarkMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fila 1: Orquestador IA + Compilar APK (Compactos 36.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                soundService.playClick()
                                if (onNavigateToOrchestrator != null) {
                                    onNavigateToOrchestrator()
                                } else {
                                    onNavigateToBuilder()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(36.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF4285F4), Color(0xFF9B51E0))
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                                .testTag("btn_open_ai_orchestrator_hero")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "AI Studio Chat",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                soundService.playClick()
                                onNavigateToBuilder()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_create_app_hero")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = LuxDarkBackground,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "Compilar APK",
                                color = LuxDarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Fila 2: Acciones rápidas compactas (32.dp) en tira limpia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                soundService.playSuccess()
                                repository.shareCurrentLuxAppApk(context)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_share_this_app_apk_dashboard")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Compartir", color = LuxGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                soundService.playSuccess()
                                val ok = repository.installCurrentLuxApk(context)
                                if (!ok) repository.shareCurrentLuxAppApk(context)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_install_this_app_apk_dashboard")
                        ) {
                            Icon(Icons.Default.InstallMobile, contentDescription = null, tint = LuxGoldLight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Instalar", color = LuxGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                soundService.playClick()
                                onNavigateToCli()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_open_cli_hero")
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Terminal", color = LuxAccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Botón compacto para tarea Gradle assembleRelease
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.triggerBuildPipeline(activeProject, soundService)
                                Toast.makeText(
                                    context,
                                    "Iniciando tarea Gradle :app:assembleRelease para '${activeProject.name}'...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF152218),
                            contentColor = LuxAccentGreen
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, LuxAccentGreen.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .testTag("btn_assemble_release_gradle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "Ejecutar assembleRelease",
                            tint = LuxAccentGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gradle :app:assembleRelease (${activeProject.name})",
                            color = LuxAccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 1.4 Hub Universal de Archivos (*/*), Antigravity CLI v2.6 y Sincronización Google (Sin API Key)
        item {
            UniversalFileAndCliHubCard(
                repository = repository,
                soundService = soundService,
                onOpenGoogleSync = onOpenGoogleSyncDialog,
                initiallyExpanded = true
            )
        }

        // 1.5 Módulo 'Auto-DevOS' (Análisis de Logs de Error + Parches en Caliente sin Recompilar)
        item {
            AutoDevOsModuleCard(
                repository = repository,
                soundService = soundService,
                initiallyExpanded = true
            )
        }

        // 1.5. MOTOR DE AUTO-CORREGIR, AUTO-MEJORAR Y AUTO-ACTUALIZAR VERSIÓN (LUX + APPS CREADAS)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, LuxAccentGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("card_autonomous_evolution_engine"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(LuxAccentGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = LuxAccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "MOTOR AUTÓNOMO: CORREGIR · MEJORAR · VERSIÓN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.5.sp
                                    ),
                                    color = LuxGoldLight
                                )
                                Text(
                                    text = "LUX v$luxVersion (build $luxVersionCode) · Opt: $hostOptScore% · App activa: v${activeProject.version}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    ),
                                    color = LuxAccentCyan
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAutoPilot) LuxAccentGreen.copy(alpha = 0.18f) else Color(0xFF1E2430))
                                .clickable {
                                    soundService.playClick()
                                    repository.toggleAutoPilot()
                                }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("badge_toggle_autopilot")
                        ) {
                            Text(
                                text = if (isAutoPilot) "AUTO 24/7 ON" else "AUTO OFF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                color = if (isAutoPilot) LuxAccentGreen else LuxOnDarkMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila de los 3 Motores + Ciclo Completo 3 en 1 (botones compactos de 30.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    repository.autoCorrectLuxAndGeneratedApps(null, soundService)
                                    Toast.makeText(
                                        context,
                                        "Auto-corrección aplicada a LUX y a las ${projects.size} apps creadas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(7.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxAccentGreen.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .testTag("btn_dashboard_autocorrect")
                        ) {
                            Text("Auto-Corregir", color = LuxAccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    repository.autoImproveLuxAndGeneratedApps(null, soundService)
                                    Toast.makeText(
                                        context,
                                        "Auto-mejora inyectada en LUX y en todas las apps creadas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(7.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxAccentCyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .testTag("btn_dashboard_autoimprove")
                        ) {
                            Text("Auto-Mejorar", color = LuxAccentCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    val newVer = repository.autoUpdateAppAndProjectsVersion("MINOR", null, soundService)
                                    Toast.makeText(
                                        context,
                                        "Versión auto-actualizada: LUX v$newVer y apps hijas actualizadas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(7.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxGoldPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .testTag("btn_dashboard_autoupdate_version")
                        ) {
                            Text("Subir Versión", color = LuxGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.runFullAutonomousEvolutionCycle(soundService)
                                    Toast.makeText(
                                        context,
                                        "Ciclo 3 en 1 completado: Corregido + Mejorado + LUX v${repository.luxVersionName.value}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(7.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(0.95f)
                                .height(30.dp)
                                .testTag("btn_dashboard_full_evolution")
                        ) {
                            Text("3 en 1", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                        }
                    }
                }
            }
        }

        // 2. Tarjeta de Prioridad, Superposición ("Sobre Otras Apps"), Permisos y Almacenamiento
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E2738)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = LuxAccentCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "DESARROLLADOR · PRIORIDAD & PERMISOS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = LuxGoldPrimary
                                )
                                Text(
                                    text = userEmail.ifBlank { "Sin cuenta vinculada (Toca Cambiar)" },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    ),
                                    color = LuxOnDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                tempEmailInput = userEmail
                                showEmailDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("btn_change_email")
                        ) {
                            Text("Cambiar", fontSize = 10.sp, color = LuxGoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Controles rápidos: Sobre otras aplicaciones, Prioridad 500MB, Guardar APK y Permisos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                soundService.playSuccess()
                                val path = repository.saveApkToDevice(context, activeProject)
                                Toast.makeText(
                                    context,
                                    "APK guardado en dispositivo:\n$path",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxAccentGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_save_apk_to_device_main")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SaveAlt,
                                contentDescription = null,
                                tint = LuxDarkBackground,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Guardar APK",
                                color = LuxDarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                maxLines = 1
                            )
                        }

                        if (onOpenPermissionsPanel != null) {
                            OutlinedButton(
                                onClick = {
                                    soundService.playClick()
                                    onOpenPermissionsPanel()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(LuxGoldPrimary, LuxAccentCyan))
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("btn_manage_permissions_main")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = LuxGoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Permisos y Prioridad",
                                    color = LuxGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                soundService.playClick()
                                val enabled = LuxPriorityKernelService.toggleSystemOverlay(context)
                                Toast.makeText(
                                    context,
                                    if (enabled) "Superposición sobre otras apps activada" else "Superposición desactivada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_dashboard_overlay_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = if (isOverlayActive) LuxAccentGreen else LuxAccentCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOverlayActive) "Sobre Apps: ON" else "Sobre Otras Apps",
                                color = if (isOverlayActive) LuxAccentGreen else LuxAccentCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                soundService.playSuccess()
                                repository.shareApk(context, activeProject, LuxRepository.ApkShareTarget.ALL)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .testTag("btn_share_active_project_apk")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Compartir (${activeProject.name.take(10)})",
                                color = LuxGoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3. Métricas Operativas en Cuadrícula Compacta
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricMiniCard(
                    title = "Motor Local 500MB",
                    value = "11 ms",
                    subtitle = "NPU + LargeHeap",
                    icon = Icons.Default.Memory,
                    iconTint = LuxAccentCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Prioridad Sistema",
                    value = "MÁXIMA",
                    subtitle = "Sobre otras apps",
                    icon = Icons.Default.Bolt,
                    iconTint = LuxAccentGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricMiniCard(
                    title = "Distribución APK",
                    value = "Lista",
                    subtitle = "LUX Meta-DevOS",
                    icon = Icons.Default.Share,
                    iconTint = LuxGoldPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToShareApk
                )
            }
        }

        // 4. Ecosistema de Estudios Oficiales (Compacto)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ECOSISTEMA DE ESTUDIOS CONECTADOS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 0.8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = LuxGoldLight
                            )
                        }
                        Text(
                            text = "SINCRONIZADO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = LuxAccentGreen
                        )
                    }

                    // 1. Android Studio Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxDarkSurfaceElevated, RoundedCornerShape(10.dp))
                            .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "Android Studio",
                            tint = Color(0xFF3DDC84),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Android Studio Suite",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = LuxOnDark
                            )
                            Text(
                                text = "Gradle Daemon, SDK 36, AGP & KSP",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = LuxOnDarkMuted
                            )
                        }
                        Button(
                            onClick = {
                                soundService.playClick()
                                onNavigateToAndroidStudio?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3DDC84)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp).testTag("btn_open_android_studio_suite")
                        ) {
                            Text("Abrir", fontSize = 10.sp, color = Color(0xFF0D1B12), fontWeight = FontWeight.Bold)
                        }
                    }

                    // 2. Google AI Studio Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxDarkSurfaceElevated, RoundedCornerShape(10.dp))
                            .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Google AI Studio",
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Google AI Studio",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = LuxOnDark
                            )
                            Text(
                                text = "Gemini 3.5 Flash, FunctionGemma & JSON Schema",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = LuxOnDarkMuted
                            )
                        }
                        Button(
                            onClick = {
                                soundService.playClick()
                                onNavigateToOrchestrator?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Ir a IA", fontSize = 10.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 3. Google Cloud Console Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LuxDarkSurfaceElevated, RoundedCornerShape(10.dp))
                            .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Google Cloud Console",
                            tint = LuxAccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Google Cloud Console",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = LuxOnDark
                            )
                            Text(
                                text = "Cloud Run, Artifact Registry & Antigravity CLI",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = LuxOnDarkMuted
                            )
                        }
                        Button(
                            onClick = {
                                soundService.playClick()
                                Toast.makeText(context, "GCP Console sincronizada en tiempo real", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Consola", fontSize = 10.sp, color = LuxAccentCyan)
                        }
                    }
                }
            }
        }

        // 5. Explorador y Navegación de Archivos del Proyecto
        item {
            com.example.ui.components.ProjectFileNavigationUi(
                project = activeProject,
                onSaveFileContent = { fileName, newContent ->
                    coroutineScope.launch {
                        repository.updateProjectFileInRoom(activeProject.id, fileName, newContent)
                        soundService.playSuccess()
                    }
                },
                onAddNewFile = { newFile ->
                    coroutineScope.launch {
                        repository.addNewProjectFile(activeProject.id, newFile)
                        soundService.playSuccess()
                    }
                }
            )
        }

        // 6. Lista de Aplicaciones Creadas por LUX
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "APLICACIONES CREADAS POR LUX",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = LuxGoldPrimary
                )
                Text(
                    text = "${projects.size} Proyectos",
                    style = MaterialTheme.typography.labelSmall,
                    color = LuxOnDarkMuted
                )
            }
        }

        items(projects) { project ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (project.id == activeProject.id) 1.dp else 0.5.dp,
                        color = if (project.id == activeProject.id) LuxGoldPrimary else LuxDarkBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .testTag("project_card_${project.name}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = project.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = LuxOnDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF16222F), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "v${project.version}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp
                                        ),
                                        color = LuxAccentCyan
                                    )
                                }
                            }
                            Text(
                                text = "${project.packageName} · Fixes: ${project.autoCorrectedCount} · Evo: #${project.autoImprovedCount}",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.5.sp),
                                color = LuxPlatinumSecondary.copy(alpha = 0.8f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (project.status == BuildStatus.READY) LuxAccentGreen.copy(alpha = 0.15f) else LuxGoldContainer,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (project.status == BuildStatus.READY) "APK READY" else project.status.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = if (project.status == BuildStatus.READY) LuxAccentGreen else LuxGoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = LuxOnDarkMuted,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chips compactos de archivos fuente
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        project.generatedFiles.take(6).forEach { file ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF141924), RoundedCornerShape(5.dp))
                                    .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = file.fileName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    ),
                                    color = LuxAccentCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botones de acción compactos (30.dp) que incluyen Auto-Evolución por proyecto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Button(
                            onClick = {
                                soundService.playClick()
                                repository.selectProject(project)
                                coroutineScope.launch {
                                    repository.cacheProjectFilesToRoom(project)
                                    repository.triggerBuildPipeline(project, soundService)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .testTag("btn_build_${project.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = LuxDarkBackground,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "Compilar",
                                color = LuxDarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                soundService.playSuccess()
                                val path = repository.saveApkToDevice(context, project)
                                Toast.makeText(
                                    context,
                                    "APK guardado:\n$path",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxAccentGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(0.95f)
                                .height(30.dp)
                                .testTag("btn_save_device_${project.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SaveAlt,
                                contentDescription = null,
                                tint = LuxDarkBackground,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "Guardar",
                                color = LuxDarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                maxLines = 1
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    repository.autoCorrectLuxAndGeneratedApps(project, soundService)
                                    repository.autoImproveLuxAndGeneratedApps(project, null)
                                    repository.autoUpdateAppAndProjectsVersion("PATCH", project, null)
                                    Toast.makeText(
                                        context,
                                        "'${project.name}' auto-corregida, mejorada y actualizada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, LuxAccentCyan),
                            modifier = Modifier
                                .weight(1.05f)
                                .height(30.dp)
                                .testTag("btn_evolve_${project.name}")
                        ) {
                            Text("Auto-Mejorar", color = LuxAccentCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                soundService.playClick()
                                repository.selectProject(project)
                                coroutineScope.launch {
                                    repository.cacheProjectFilesToRoom(project)
                                }
                                onNavigateToBuilder()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .height(30.dp)
                                .testTag("btn_inspect_${project.name}")
                        ) {
                            Text("Código", color = LuxGoldPrimary, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier.border(0.5.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
                if (onClick != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = LuxOnDarkMuted,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp
                ),
                color = LuxOnDark,
                maxLines = 1
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 9.5.sp),
                color = LuxGoldLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                color = LuxOnDarkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
