package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Radar
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AutoDevOsErrorLogItem
import com.example.data.models.AutoDevOsPatchProposal
import com.example.data.models.AutoDevOsPatchStatus
import com.example.data.models.HotSwapPatchDeployment
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxGoldSilverGlassBrush
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import com.example.ui.theme.LuxSilverLight
import com.example.ui.theme.LuxTerminalBg
import com.example.ui.theme.LuxTerminalGreen
import com.example.ui.theme.LuxTerminalRed
import kotlinx.coroutines.launch

/**
 * Módulo 'Auto-DevOS':
 * Analiza los logs de error de las aplicaciones generadas (APK) y de la propia aplicación LUX,
 * proponiendo correcciones automáticas y permitiendo el despliegue de parches de actualización
 * en caliente (Hot-Swap DEX Delta) sin necesidad de recompilar completamente.
 */
@Composable
fun AutoDevOsModuleCard(
    repository: LuxRepository,
    soundService: SoundHapticService? = null,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val analyzedLogs by repository.analyzedErrorLogs.collectAsState()
    val proposals by repository.autoDevOsProposals.collectAsState()
    val deployedHotPatches by repository.deployedHotPatches.collectAsState()
    val isActive by repository.isAutoDevOsActive.collectAsState()
    val stageText by repository.autoDevOsStageText.collectAsState()

    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    var selectedSectionIndex by remember { mutableIntStateOf(0) } // 0: Propuestas Hot-Patch, 1: Logs Analizados, 2: Parches Desplegados

    val pendingProposalsCount = proposals.count { it.status == AutoDevOsPatchStatus.PROPOSED }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.2.dp,
                brush = LuxGoldSilverBorderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("card_autodevos_module"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x8C10141E) // Cristal translúcido Oro/Plata para dejar ver la imagen de fondo
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxGoldSilverGlassBrush)
                .padding(12.dp)
        ) {
            // Cabecera del Módulo Auto-DevOS con bordes y acentos Oro y Plata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0x55FFD54F), Color(0x55E2E8F0))
                                )
                            )
                            .border(1.dp, LuxGoldSilverBorderBrush, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isActive) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = LuxGoldPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MÓDULO AUTO-DEVOS · HOT-PATCH",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.5.sp,
                                    letterSpacing = 0.6.sp
                                ),
                                color = LuxGoldLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0x40E2E8F0))
                                    .border(0.5.dp, LuxPlatinumSecondary, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "0-RECOMPILE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = LuxSilverLight
                                )
                            }
                        }
                        Text(
                            text = "Analizador de Logs (APKs + LUX) y Parches DEX Delta en Caliente",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = LuxPlatinumSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Contador y botón de expandir/colapsar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x38FFD54F))
                            .border(0.6.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$pendingProposalsCount Propuestas · ${deployedHotPatches.size} Live",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = LuxGoldLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33E2E8F0))
                            .border(0.5.dp, LuxPlatinumSecondary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .clickable { isExpanded = !isExpanded },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expandir o contraer Auto-DevOS",
                            tint = LuxSilverLight,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de Estado en Vivo del Motor Auto-DevOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x73080B11))
                    .border(0.6.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = LuxGoldPrimary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stageText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    ),
                    color = LuxSilverLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones Principales: Analizar Logs (APKs + LUX) y Desplegar Parches Sin Recompilar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val found = repository.analyzeLogsWithAutoDevOs(soundService)
                            Toast.makeText(
                                context,
                                "Auto-DevOS: Logs de LUX y APKs analizados (${found.size} propuestas activas)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !isActive,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.9.dp, LuxPlatinumSecondary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x33E2E8F0)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .testTag("btn_autodevos_analyze_logs")
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = LuxSilverLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Analizar Logs (APKs + LUX)",
                        color = LuxSilverLight,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (pendingProposalsCount == 0) {
                                repository.analyzeLogsWithAutoDevOs(null)
                            }
                            val deployed = repository.deployAllAutoDevOsHotPatches(soundService)
                            Toast.makeText(
                                context,
                                "Auto-DevOS: ${deployed.size} parches inyectados en caliente SIN recompilar APK",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !isActive,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .testTag("btn_autodevos_deploy_all_patches")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = LuxDarkBackground,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Parchear Sin Recompilar",
                        color = LuxDarkBackground,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // Sub-pestañas compactas Oro y Plata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tabs = listOf(
                            "Propuestas (${proposals.size})",
                            "Logs de Error (${analyzedLogs.size})",
                            "Hot-Swaps (${deployedHotPatches.size})"
                        )
                        tabs.forEachIndexed { idx, label ->
                            val isSel = selectedSectionIndex == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(
                                        if (isSel) Color(0x47FFD54F) else Color(0x26E2E8F0)
                                    )
                                    .border(
                                        width = if (isSel) 0.9.dp else 0.5.dp,
                                        brush = LuxGoldSilverBorderBrush,
                                        shape = RoundedCornerShape(7.dp)
                                    )
                                    .clickable { selectedSectionIndex = idx }
                                    .padding(vertical = 4.dp)
                                    .testTag("tab_autodevos_section_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) LuxGoldLight else LuxPlatinumSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when (selectedSectionIndex) {
                        0 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                proposals.take(4).forEach { proposal ->
                                    AutoDevOsProposalItem(
                                        proposal = proposal,
                                        isBusy = isActive,
                                        onDeploySingle = {
                                            coroutineScope.launch {
                                                val dep = repository.deployAutoDevOsHotPatch(proposal.id, soundService)
                                                if (dep != null) {
                                                    Toast.makeText(
                                                        context,
                                                        "Parche ${dep.id} inyectado en ${dep.injectionLatencyMs}ms sin recompilar",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                analyzedLogs.take(4).forEach { logItem ->
                                    AutoDevOsLogItemRow(logItem = logItem)
                                }
                            }
                        }
                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                deployedHotPatches.take(4).forEach { dep ->
                                    AutoDevOsDeploymentRow(deployment = dep)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AutoDevOsProposalItem(
    proposal: AutoDevOsPatchProposal,
    isBusy: Boolean,
    onDeploySingle: () -> Unit
) {
    val isDeployed = proposal.status == AutoDevOsPatchStatus.DEPLOYED_NO_RECOMPILE
    val isInjecting = proposal.status == AutoDevOsPatchStatus.INJECTING_HOT_PATCH

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x800B0E16))
            .border(
                width = 0.7.dp,
                brush = LuxGoldSilverBorderBrush,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (proposal.isHostLuxApp) Color(0x47FFD54F) else Color(0x38E2E8F0)
                        )
                        .border(
                            0.5.dp,
                            if (proposal.isHostLuxApp) LuxGoldPrimary else LuxPlatinumSecondary,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (proposal.isHostLuxApp) "PROPIA APP LUX" else "APK GENERADA",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (proposal.isHostLuxApp) LuxGoldLight else LuxSilverLight
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${proposal.targetAppName} · ${proposal.targetFile}:${proposal.lineNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    color = LuxOnDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isDeployed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = LuxAccentGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "HOT-SWAPPED",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = LuxAccentGreen
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(LuxGoldPrimary, LuxPlatinumSecondary)
                            )
                        )
                        .clickable(enabled = !isBusy && !isInjecting) { onDeploySingle() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("btn_autodevos_deploy_${proposal.id}")
                ) {
                    Text(
                        text = if (isInjecting) "Inyectando..." else "Aplicar (${proposal.estimatedHotPatchMs}ms)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LuxDarkBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Línea de Log de Error Capturada
        Text(
            text = proposal.errorLogLine,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp
            ),
            color = LuxAccentRed,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Diagnóstico de Causa Raíz
        Text(
            text = "Diagnóstico: ${proposal.rootCauseAnalysis}",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
            color = LuxOnDarkMuted,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Vista previa del Diff (Hot-Swap DEX Delta)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(LuxTerminalBg)
                .border(0.5.dp, LuxPlatinumSecondary.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                .padding(6.dp)
        ) {
            Text(
                text = proposal.proposedCodeBefore,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                color = LuxTerminalRed,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = proposal.proposedCodeAfter,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                color = LuxTerminalGreen,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AutoDevOsLogItemRow(logItem: AutoDevOsErrorLogItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x730B0E15))
            .border(0.5.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(8.dp))
            .padding(7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = null,
                    tint = if (logItem.severity == "CRITICAL") LuxAccentRed else LuxGoldPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = logItem.sourceAppName,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxGoldLight
                )
            }
            Text(
                text = "${logItem.componentFile}:${logItem.lineNumber}",
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                color = LuxPlatinumSecondary
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = logItem.rawLogLine,
            fontSize = 8.5.sp,
            fontFamily = FontFamily.Monospace,
            color = LuxAccentRed,
            maxLines = 2
        )
        Text(
            text = logItem.rootCauseSummary,
            fontSize = 8.5.sp,
            color = LuxOnDarkMuted
        )
    }
}

@Composable
private fun AutoDevOsDeploymentRow(deployment: HotSwapPatchDeployment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x730B0E15))
            .border(0.5.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(8.dp))
            .padding(7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = LuxAccentGreen,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${deployment.targetAppName} · ${deployment.targetFile}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxGoldLight
                )
            }
            Text(
                text = deployment.patchDescription,
                fontSize = 8.5.sp,
                color = LuxSilverLight
            )
            Text(
                text = "DEX Delta: ${deployment.dexDeltaSizeKb} KB · Inyectado en ${deployment.injectionLatencyMs}ms · SHA: ${deployment.sha256DeltaHash.take(10)}",
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                color = LuxAccentCyan
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0x3334D399))
                .border(0.5.dp, LuxAccentGreen, RoundedCornerShape(5.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "SIN RECOMPILAR",
                fontSize = 7.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LuxAccentGreen
            )
        }
    }
}
