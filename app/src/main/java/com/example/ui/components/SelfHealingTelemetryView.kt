package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.models.EvolutionCategory
import com.example.data.models.SelfHealingStatus
import com.example.data.models.SelfHealingTask
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxDarkSurfaceElevated
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import com.example.ui.theme.LuxTerminalBg
import com.example.ui.theme.LuxTerminalGreen
import com.example.ui.theme.LuxTerminalRed
import kotlinx.coroutines.launch

@Composable
fun SelfHealingTelemetryView(
    activeTask: SelfHealingTask?,
    healingHistory: List<SelfHealingTask>,
    isSimulating: Boolean,
    onTriggerSimulation: () -> Unit,
    modifier: Modifier = Modifier,
    repository: LuxRepository? = null,
    soundService: SoundHapticService? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val luxVersion by (repository?.luxVersionName?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("2.5.0") })
    val luxVersionCode by (repository?.luxVersionCode?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(25) })
    val isAutoPilot by (repository?.isAutoPilotEnabled?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) })
    val hostOptScore by (repository?.hostOptimizationScore?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(99.4) })
    val evolutionHistory by (repository?.evolutionHistory?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) })
    val versionReleases by (repository?.versionReleases?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) })
    val projects by (repository?.projects?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) })

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 0. MÓDULO AUTO-DEVOS: ANÁLISIS DE LOGS (APKs + LUX) Y PARCHES EN CALIENTE SIN RECOMPILAR
        if (repository != null) {
            item {
                AutoDevOsModuleCard(
                    repository = repository,
                    soundService = soundService,
                    initiallyExpanded = true
                )
            }
        }

        // 1. MOTOR TRIPLE AUTÓNOMO: AUTO-CORREGIR · AUTO-MEJORAR · AUTO-ACTUALIZAR VERSIÓN
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LuxGoldPrimary.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B2B22)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = null,
                                    tint = LuxAccentGreen,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "MOTOR DE AUTO-CORRECCIÓN, AUTO-MEJORA Y AUTO-UPDATE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    ),
                                    color = LuxGoldLight
                                )
                                Text(
                                    text = "LUX v$luxVersion (build $luxVersionCode) · Opt: $hostOptScore% · ${projects.size} Apps Hijas",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.5.sp
                                    ),
                                    color = LuxAccentCyan
                                )
                            }
                        }

                        // Switch de Piloto Automático 24/7
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAutoPilot) "AUTO 24/7" else "MANUAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                color = if (isAutoPilot) LuxAccentGreen.copy(alpha = pulseAlpha) else LuxOnDarkMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = isAutoPilot,
                                onCheckedChange = {
                                    soundService?.playClick()
                                    repository?.toggleAutoPilot()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = LuxAccentGreen
                                ),
                                modifier = Modifier.testTag("switch_autopilot_daemon")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fila de 3 Motores Individuales (Auto-Corregir, Auto-Mejorar, Auto-Actualizar Versión)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (repository != null) {
                                    coroutineScope.launch {
                                        repository.autoCorrectLuxAndGeneratedApps(null, soundService)
                                        Toast.makeText(
                                            context,
                                            "Errores auto-corregidos en LUX y en las ${projects.size} apps creadas",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    onTriggerSimulation()
                                }
                            },
                            enabled = !isSimulating,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuxAccentGreen.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_autocorrect_all_apps")
                        ) {
                            Icon(Icons.Default.Healing, null, tint = LuxAccentGreen, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Corregir", color = LuxAccentGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                if (repository != null) {
                                    coroutineScope.launch {
                                        repository.autoImproveLuxAndGeneratedApps(null, soundService)
                                        Toast.makeText(
                                            context,
                                            "Auto-mejora aplicada a LUX y a todas las apps creadas",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    onTriggerSimulation()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuxAccentCyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_autoimprove_all_apps")
                        ) {
                            Icon(Icons.Default.TrendingUp, null, tint = LuxAccentCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Mejorar", color = LuxAccentCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                if (repository != null) {
                                    coroutineScope.launch {
                                        val nextVer = repository.autoUpdateAppAndProjectsVersion("MINOR", null, soundService)
                                        Toast.makeText(
                                            context,
                                            "Versión auto-actualizada: LUX v$nextVer y apps creadas actualizadas",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    onTriggerSimulation()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuxGoldPrimary.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_autoupdate_version")
                        ) {
                            Icon(Icons.Default.SystemUpdateAlt, null, tint = LuxGoldLight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Subir Versión", color = LuxGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Botón Maestro: Ciclo Triple + Botón de Simulación Clásico (mantiene testTag btn_trigger_self_heal_sim)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onTriggerSimulation,
                            enabled = !isSimulating,
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_trigger_self_heal_sim")
                        ) {
                            if (isSimulating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reparando...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            } else {
                                Icon(Icons.Default.FlashOn, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Simular y Auto-Corregir",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        if (repository != null) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.runFullAutonomousEvolutionCycle(soundService)
                                        Toast.makeText(
                                            context,
                                            "Ciclo Triple Completado: Auto-Corregido + Mejorado + Versión v${repository.luxVersionName.value}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LuxAccentGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("btn_full_autonomous_cycle")
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Evolución Total (3 en 1)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. ESTADO DE LA TAREA ACTIVA (5-Stage Pipeline + Hotfix Diff)
        activeTask?.let { task ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (task.status == SelfHealingStatus.RESTORED) LuxAccentGreen.copy(alpha = 0.5f) else LuxAccentRed.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TAREA ACTIVA · ${task.targetScope.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                ),
                                color = LuxGoldPrimary
                            )

                            Text(
                                text = "${task.elapsedMs} ms",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = LuxAccentCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
                            color = LuxOnDark
                        )
                        Text(
                            text = "${task.exceptionType} en ${task.affectedFile}:${task.lineNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.5.sp),
                            color = if (task.status == SelfHealingStatus.RESTORED) LuxAccentGreen else LuxAccentRed
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val stages = listOf(
                            SelfHealingStatus.INCIDENT_DETECTED to "Detección",
                            SelfHealingStatus.DIAGNOSING_AST to "Diagnóstico",
                            SelfHealingStatus.SYNTHESIZING_HOTFIX to "Síntesis",
                            SelfHealingStatus.BYTECODE_VERIFIED to "Validación",
                            SelfHealingStatus.RESTORED to "Restaurado"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            stages.forEachIndexed { index, (status, name) ->
                                val isPassed = task.status.ordinal >= status.ordinal
                                val isCurrent = task.status == status

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isCurrent -> LuxGoldPrimary
                                                    isPassed -> LuxAccentGreen
                                                    else -> Color(0xFF1E2430)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPassed && !isCurrent) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                color = if (isCurrent) Color.Black else LuxOnDarkMuted
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.5.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isCurrent) LuxGoldPrimary else if (isPassed) LuxAccentGreen else LuxOnDarkMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = task.stageDescription,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = LuxPlatinumSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LuxTerminalBg, RoundedCornerShape(8.dp))
                                .border(1.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "@@ ${task.affectedFile}:${task.lineNumber} @@",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = LuxAccentCyan
                                )
                                task.patchDiffBefore.lines().forEach { line ->
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        ),
                                        color = LuxTerminalRed
                                    )
                                }
                                task.patchDiffAfter.lines().forEach { line ->
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        ),
                                        color = LuxTerminalGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. ESTADO DE LAS APLICACIONES CREADAS (Auto-Corregidas, Auto-Mejoradas y Versiones)
        if (projects.isNotEmpty()) {
            item {
                Text(
                    text = "APLICACIONES CREADAS POR LUX (AUTO-EVOLUCIÓN Y VERSIÓN)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = LuxGoldPrimary
                )
            }

            items(projects, key = { "proj_evo_${it.id}" }) { proj ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = proj.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LuxOnDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(LuxAccentGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "v${proj.version} (build ${proj.versionCode})",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = LuxAccentGreen
                                    )
                                }
                            }
                            Text(
                                text = "Fixes: ${proj.autoCorrectedCount} · Mejoras: #${proj.autoImprovedCount} · ${proj.lastEvolutionSummary}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = LuxOnDarkMuted,
                                maxLines = 1
                            )
                        }

                        if (repository != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.autoCorrectLuxAndGeneratedApps(proj, soundService)
                                            repository.autoImproveLuxAndGeneratedApps(proj, null)
                                            repository.autoUpdateAppAndProjectsVersion("PATCH", proj, null)
                                            Toast.makeText(
                                                context,
                                                "${proj.name} auto-corregida, mejorada y actualizada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 7.dp, vertical = 2.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, LuxGoldPrimary),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Evolucionar", color = LuxGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. REGISTRO DE AUTO-ACTUALIZACIONES DE VERSIÓN Y EVOLUCIÓN
        if (evolutionHistory.isNotEmpty()) {
            item {
                Text(
                    text = "BITÁCORA DE AUTO-MEJORAS Y ACTUALIZACIONES DE VERSIÓN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = LuxAccentCyan
                )
            }

            items(evolutionHistory.take(6), key = { "evo_${it.id}" }) { record ->
                val badgeColor = when (record.category) {
                    EvolutionCategory.AUTO_CORRECT_HOST, EvolutionCategory.AUTO_CORRECT_CHILD_APP -> LuxAccentGreen
                    EvolutionCategory.AUTO_IMPROVE_HOST, EvolutionCategory.AUTO_IMPROVE_CHILD_APP -> LuxAccentCyan
                    EvolutionCategory.AUTO_UPDATE_VERSION_HOST, EvolutionCategory.AUTO_UPDATE_VERSION_CHILD_APP -> LuxGoldPrimary
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = record.title,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = badgeColor,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${record.versionBefore} → ${record.versionAfter}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = LuxGoldLight
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = record.details,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = LuxOnDarkMuted
                        )
                    }
                }
            }
        }

        // 5. HISTORIAL DE INCIDENCIAS RESUELTAS
        item {
            Text(
                text = "HISTORIAL DE INCIDENCIAS RESUELTAS (HOTFIXES)",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                ),
                color = LuxGoldPrimary
            )
        }

        items(healingHistory, key = { "heal_${it.id}_${it.timestamp}" }) { historyItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(LuxAccentGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LuxAccentGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = historyItem.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = LuxOnDark
                            )
                            Text(
                                text = "${historyItem.affectedFile} · ${historyItem.timestamp}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = LuxOnDarkMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF141F2B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${historyItem.elapsedMs} ms",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = LuxAccentCyan
                        )
                    }
                }
            }
        }
    }
}
