package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AiOrchestratorService
import com.example.data.models.CliLogEntry
import com.example.data.models.GeneratedAppProject
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.components.ActivityCliComponent
import com.example.ui.components.ActivityTerminal
import com.example.ui.components.CliRechartsDashboard
import com.example.ui.components.GemmaModelManagerComponent
import com.example.ui.components.SelfHealingTelemetryView
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxOnDarkMuted
import kotlinx.coroutines.launch

@Composable
fun ActivityCliScreen(
    repository: LuxRepository,
    logs: List<CliLogEntry>,
    activeProject: GeneratedAppProject,
    soundService: SoundHapticService,
    orchestratorService: AiOrchestratorService,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isAudioMuted by remember { mutableStateOf(false) }
    var selectedConsoleTab by remember { mutableIntStateOf(0) }

    val currentTheme by repository.terminalTheme.collectAsState()
    val commandHistory by repository.commandHistory.collectAsState()
    val activeHealingTask by repository.activeSelfHealingTask.collectAsState()
    val healingHistory by repository.healingHistory.collectAsState()
    val isSimulatingHealing by repository.isSimulatingHealing.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Barra superior compacta todo-en-uno con Cristal Oro y Plata: Título + Macros rápidas en 1 sola línea sin botones gigantes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(10.dp))
                .border(0.8.dp, LuxGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(LuxGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = LuxGoldPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CLI",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = LuxGoldLight
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Tira horizontal compacta de Macros (Auto-Fix, Sync, Build, Share)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auto-Fix Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF221815))
                        .border(0.5.dp, LuxAccentRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.triggerSelfHealingSimulation(svc)
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("btn_quick_fix")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Healing,
                            contentDescription = null,
                            tint = LuxAccentRed,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Auto-Fix", color = LuxAccentRed, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }

                // Sync Modelos Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF131D24))
                        .border(0.5.dp, LuxAccentCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.executeCliCommand("upgrade --models", svc)
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("btn_quick_upgrade")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = LuxAccentCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Sync Modelos", color = LuxAccentCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }

                // Compilar APK Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LuxGoldContainer)
                        .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.executeCliCommand("build --apk", svc)
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("btn_quick_build_apk")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Compilar APK", color = LuxGoldPrimary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }

                // Compartir Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF231E12))
                        .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.executeCliCommand("share --apk", svc)
                            }
                        }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("btn_quick_share_apk")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = LuxGoldLight,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Compartir", color = LuxGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }

            IconButton(
                onClick = { isAudioMuted = !isAudioMuted },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isAudioMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Audio feedback",
                    tint = if (isAudioMuted) LuxOnDarkMuted else LuxGoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Pestañas Desplazables Compactas (ScrollableTabRow para que nunca se corten ni se hagan grandes)
        ScrollableTabRow(
            selectedTabIndex = selectedConsoleTab,
            edgePadding = 4.dp,
            containerColor = Color(0xFF10131B),
            contentColor = LuxGoldPrimary,
            indicator = { tabPositions ->
                if (selectedConsoleTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedConsoleTab]),
                        color = LuxGoldPrimary,
                        height = 2.dp
                    )
                }
            },
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedConsoleTab == 0,
                onClick = {
                    soundService.playClick()
                    selectedConsoleTab = 0
                },
                modifier = Modifier.height(34.dp).testTag("tab_activity_cli_mode")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (selectedConsoleTab == 0) LuxAccentGreen else LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Activity CLI",
                        fontWeight = if (selectedConsoleTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.5.sp,
                        maxLines = 1
                    )
                }
            }

            Tab(
                selected = selectedConsoleTab == 1,
                onClick = {
                    soundService.playClick()
                    selectedConsoleTab = 1
                },
                modifier = Modifier.height(34.dp).testTag("tab_terminal_logs_mode")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (selectedConsoleTab == 1) LuxGoldPrimary else LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Terminal Logs",
                        fontWeight = if (selectedConsoleTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.5.sp,
                        maxLines = 1
                    )
                }
            }

            Tab(
                selected = selectedConsoleTab == 2,
                onClick = {
                    soundService.playClick()
                    selectedConsoleTab = 2
                },
                modifier = Modifier.height(34.dp).testTag("tab_recharts_mode")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (selectedConsoleTab == 2) LuxAccentCyan else LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Recharts",
                        fontWeight = if (selectedConsoleTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.5.sp,
                        maxLines = 1
                    )
                }
            }

            Tab(
                selected = selectedConsoleTab == 3,
                onClick = {
                    soundService.playClick()
                    selectedConsoleTab = 3
                },
                modifier = Modifier.height(34.dp).testTag("tab_gemma_models_mode")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (selectedConsoleTab == 3) LuxAccentGreen else LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Modelos (500MB)",
                        fontWeight = if (selectedConsoleTab == 3) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.5.sp,
                        maxLines = 1
                    )
                }
            }

            Tab(
                selected = selectedConsoleTab == 4,
                onClick = {
                    soundService.playClick()
                    selectedConsoleTab = 4
                },
                modifier = Modifier.height(34.dp).testTag("tab_telemetry_mode")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (selectedConsoleTab == 4) LuxAccentGreen else LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Self-Healing",
                        fontWeight = if (selectedConsoleTab == 4) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.5.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Área Principal de la Terminal (Ocupa todo el espacio disponible sin estorbos)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedConsoleTab) {
                0 -> {
                    ActivityCliComponent(
                        orchestratorService = orchestratorService,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                1 -> {
                    ActivityTerminal(
                        logsFlow = repository.cliLogs,
                        commandHistory = commandHistory,
                        themeMode = currentTheme,
                        onThemeChanged = { newTheme ->
                            repository.setTerminalTheme(newTheme)
                        },
                        onExecuteCommand = { cmd ->
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.executeCliCommand(cmd, svc)
                            }
                        },
                        onTriggerRecoveryMode = {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.triggerSelfHealingSimulation(svc)
                            }
                        },
                        onSimulateErrorForTest = {
                            repository.addCliLog(
                                "FATAL CRASH: ConcurrentModificationException in WebSocket client buffer at SyncManager.kt:89",
                                com.example.data.models.CliLogType.ERROR,
                                "SIMULATED_TEST"
                            )
                            soundService.playError()
                        },
                        onClearLogs = {
                            soundService.playClick()
                            repository.clearCliLogs()
                        },
                        onConvertTextToSpeech = { text ->
                            repository.speakWithTts(text)
                        },
                        onOpenRechartsDashboard = {
                            soundService.playClick()
                            selectedConsoleTab = 2
                        },
                        onOpenModelManager = {
                            soundService.playClick()
                            selectedConsoleTab = 3
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                2 -> {
                    val telemetryMetrics = remember(logs.size, activeHealingTask) {
                        repository.getTelemetryMetrics()
                    }
                    CliRechartsDashboard(
                        metrics = telemetryMetrics,
                        onTriggerRecoveryMode = {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.triggerSelfHealingSimulation(svc)
                            }
                        },
                        onExecuteCommand = { cmd ->
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.executeCliCommand(cmd, svc)
                            }
                        },
                        onCloseDashboard = {
                            selectedConsoleTab = 0
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                3 -> {
                    GemmaModelManagerComponent(
                        modelManager = repository.gemmaModelManager,
                        onExecuteCliCommand = { cmd ->
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.executeCliCommand(cmd, svc)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    SelfHealingTelemetryView(
                        activeTask = activeHealingTask,
                        healingHistory = healingHistory,
                        isSimulating = isSimulatingHealing,
                        onTriggerSimulation = {
                            val svc = if (!isAudioMuted) soundService else null
                            coroutineScope.launch {
                                repository.triggerSelfHealingSimulation(svc)
                            }
                        },
                        repository = repository,
                        soundService = if (!isAudioMuted) soundService else null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
