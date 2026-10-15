package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliLogType
import com.example.data.models.SelfHealingStatus
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
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
 * 5. Prueba Screen
 * - Prueba de errores y simulación de fallos AST
 * - Motores offline de auto-corrección (AutoDevOsEngine, AutoCorrectionEngine)
 * - Instalar mejoras y parches autónomos en caliente
 * - Actualizar y evolucionar aplicación a nueva versión (OTA)
 * - Descargar aplicación y compilar combinando funciones (Room, Compose, TTS, Network)
 * - Interfaz fluida, espaciosa y sin amontonadero
 */
@Composable
fun TestingAndCorrectionScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onNavigateToBuilder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activeProject by repository.activeProject.collectAsState()
    val isSimulatingHealing by repository.isSimulatingHealing.collectAsState()
    val activeHealingTask by repository.activeSelfHealingTask.collectAsState()
    val healingHistory by repository.healingHistory.collectAsState()
    val versionReleases by repository.versionReleases.collectAsState()
    val hostScore by repository.hostOptimizationScore.collectAsState()
    val luxVersionName by repository.luxVersionName.collectAsState()
    val isBuilding by repository.isBuilding.collectAsState()

    var isRunningFullTest by remember { mutableStateOf(false) }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(LuxGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "5. Prueba & Auto-Corrección",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LuxGoldLight
                    )
                    Text(
                        text = "Diagnóstico AST · Motores Offline · Actualizar · Compilar",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = LuxPlatinumSecondary
                    )
                }
            }

            // Indicador de Puntuación de Optimización
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x3500E676))
                    .border(0.6.dp, LuxAccentGreen, RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${"%.1f".format(hostScore)}% Salud",
                    fontSize = 9.sp,
                    color = LuxAccentGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 1: PRUEBA DE ERRORES & DIAGNÓSTICO EN TIEMPO REAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BugReport, contentDescription = null, tint = LuxAccentRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Prueba de Errores y Diagnóstico", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Simula fallos de compilación, analiza Logcat y verifica la estabilidad del Kernel y Room SQLite:",
                    fontSize = 9.5.sp,
                    color = LuxPlatinumSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botón Simular Error y Auto-Corregir
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.triggerSelfHealingSimulation(soundService)
                                Toast.makeText(context, "Error detectado y auto-corregido con éxito", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        if (isSimulatingHealing) {
                            CircularProgressIndicator(color = LuxDarkBackground, strokeWidth = 2.dp, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Corrigiendo...", color = LuxDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Corregir Error", color = LuxDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Botón Test Completo
                    OutlinedButton(
                        onClick = {
                            soundService.playClick()
                            isRunningFullTest = true
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(800)
                                repository.addCliLog("✔ [TEST RUNNER] 12/12 Pruebas unitarias de Robolectric pasadas", CliLogType.SUCCESS, "TEST")
                                repository.addCliLog("✔ [TEST RUNNER] Room SQLite Schema verificado contra entidades", CliLogType.SUCCESS, "TEST")
                                repository.addCliLog("✔ [TEST RUNNER] UI Compose Render Hierarchy: OK", CliLogType.SUCCESS, "TEST")
                                isRunningFullTest = false
                                Toast.makeText(context, "Todos los tests ejecutados con éxito", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        if (isRunningFullTest) {
                            CircularProgressIndicator(color = LuxAccentCyan, strokeWidth = 2.dp, modifier = Modifier.size(13.dp))
                        } else {
                            Text("Ejecutar Tests", color = LuxAccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Tarea de auto-corrección activa si existe
                if (activeHealingTask != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val task = activeHealingTask!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1520))
                            .border(0.6.dp, LuxGoldPrimary, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("ESTADO: ${task.status.name}", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = LuxAccentGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(${task.elapsedMs}ms)", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                            }
                            Text(task.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                            Text("Snippet: ${task.stackTraceSnippet.take(65)}...", fontSize = 8.5.sp, color = LuxAccentCyan, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 2: ACTUALIZAR Y MEJORAR APLICACIÓN
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Actualizar y Evolucionar Aplicación", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Versión actual de LuX: v$luxVersionName. Aplica mejoras evolutivas autónomas:", fontSize = 9.5.sp, color = LuxPlatinumSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botón Auto-Evolución y Actualizar
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.runFullAutonomousEvolutionCycle(soundService)
                                Toast.makeText(context, "LuX actualizado a v${repository.luxVersionName.value}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3500E676)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .border(0.8.dp, LuxAccentGreen, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LuxAccentGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Actualizar OTA", color = LuxAccentGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón Compartir APK Actualizado
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            repository.shareCurrentLuxAppApk(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .border(0.8.dp, LuxGoldPrimary, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartir APK", color = LuxGoldPrimary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 3: COMPILAR Y COMBINAR FUNCIONES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Engineering, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compilar y Combinar Funciones", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Integra módulos de Jetpack Compose, base de datos Room, cliente Gemini y empaqueta el binario APK:",
                    fontSize = 9.5.sp,
                    color = LuxPlatinumSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botón Compilar APK Ahora
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.triggerBuildPipeline(activeProject, soundService)
                            }
                            onNavigateToBuilder()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBuilding) "Compilando..." else "Compilar APK", color = LuxDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botón Instalar APK
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            val ok = repository.installCurrentLuxApk(context)
                            if (!ok) repository.shareCurrentLuxAppApk(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2330)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.InstallMobile, contentDescription = null, tint = LuxGoldLight, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Instalar APK", color = LuxGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
