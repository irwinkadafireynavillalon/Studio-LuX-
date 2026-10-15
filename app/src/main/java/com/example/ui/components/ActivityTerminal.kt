package com.example.ui.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.CliLogEntry
import com.example.data.models.CliLogType
import com.example.data.models.TerminalThemeMode
import kotlinx.coroutines.flow.StateFlow

/**
 * Componente 'ActivityTerminal' de alto contraste para Compose:
 * - LazyColumn con tipografía monoespaciada e inyección de logs reactivos en tiempo real (StateFlow).
 * - Botón y Banner 'Recovery Mode' activado automáticamente ante la detección de errores en el log stream.
 * - Panel de Control de la Consola expandible con telemetría de CPU/RAM, macros y simulador de fallos.
 * - Locución y síntesis de voz 'Convert text to speech' con modelo gemini-3.8-flash-tts e ícono audio_spark.
 */
@Composable
fun ActivityTerminal(
    logsFlow: StateFlow<List<CliLogEntry>>,
    onExecuteCommand: (String) -> Unit,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier,
    commandHistory: List<String> = emptyList(),
    themeMode: TerminalThemeMode = TerminalThemeMode.OBSIDIAN_MATRIX,
    onThemeChanged: ((TerminalThemeMode) -> Unit)? = null,
    onTriggerRecoveryMode: (() -> Unit)? = null,
    onSimulateErrorForTest: (() -> Unit)? = null,
    onConvertTextToSpeech: ((String) -> Unit)? = null,
    onOpenRechartsDashboard: (() -> Unit)? = null,
    onOpenModelManager: (() -> Unit)? = null,
    terminalTitle: String = "activity_cli@lux-devos: ~",
    suggestedCommands: List<String> = listOf(
        "antigravity run \"diagnóstico\"",
        "patch list",
        "monitor status",
        "fix --last-error",
        "upgrade --models",
        "status",
        "build --apk"
    )
) {
    // Inyección de logs en tiempo real desde el StateFlow
    val logs by logsFlow.collectAsState()

    var currentTheme by remember(themeMode) { mutableStateOf(themeMode) }
    var commandInput by remember { mutableStateOf("") }
    var historyIndex by remember { mutableIntStateOf(-1) }
    var isAutoScrollPaused by remember { mutableStateOf(false) }
    var showControlPanel by remember { mutableStateOf(false) }
    var isCompactFont by remember { mutableStateOf(false) }
    var dismissErrorAlert by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Launcher para reconocimiento de voz (Speech-to-Text)
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                commandInput = spoken
                Toast.makeText(context, "Voz reconocida: \"$spoken\"", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Detección reactiva de errores en el stream de logs
    val recentErrorLogs = remember(logs) {
        logs.filter { it.type == CliLogType.ERROR }
    }
    val hasDetectedError = recentErrorLogs.isNotEmpty() && !dismissErrorAlert

    // Animación de pulso para el botón y banner de Recovery Mode
    val pulseTransition = rememberInfiniteTransition(label = "recoveryPulse")
    val pulseGlow by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recoveryGlow"
    )

    // Cursor parpadeante
    val cursorTransition = rememberInfiniteTransition(label = "terminalCursor")
    val cursorAlpha by cursorTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    // Auto-scroll reactivo en tiempo real al inyectar nuevos logs
    LaunchedEffect(logs.size) {
        if (!isAutoScrollPaused && logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    // Paleta de colores Trasparente Oro y Plata con Letras Plata y Oro y Bordes Metálicos
    val terminalBg = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0x661C1608) // Cristal Trasparente Oro y Plata
        TerminalThemeMode.CYBER_AMBER -> Color(0x6E261C06)     // Cristal Trasparente Oro Imperial
        TerminalThemeMode.HYPER_MONOLITH -> Color(0x66181D26)  // Cristal Trasparente Plata Pura
        TerminalThemeMode.SYNTH_CYAN -> Color(0x661E2430)      // Cristal Trasparente Platino
    }
    val headerBg = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0x8028200C)
        TerminalThemeMode.CYBER_AMBER -> Color(0x85302308)
        TerminalThemeMode.HYPER_MONOLITH -> Color(0x80222834)
        TerminalThemeMode.SYNTH_CYAN -> Color(0x80242B38)
    }
    val controlPanelBg = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0x78221B0A)
        TerminalThemeMode.CYBER_AMBER -> Color(0x782A2008)
        TerminalThemeMode.HYPER_MONOLITH -> Color(0x781E242E)
        TerminalThemeMode.SYNTH_CYAN -> Color(0x78202632)
    }
    val borderColor = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0xFFD4AF37) // Borde Oro Imperial
        TerminalThemeMode.CYBER_AMBER -> Color(0xFFFFD54F)     // Borde Oro 24K
        TerminalThemeMode.HYPER_MONOLITH -> Color(0xFFE8EEF5)  // Borde Plata Brillante
        TerminalThemeMode.SYNTH_CYAN -> Color(0xFFD0DAE8)      // Borde Platino
    }
    val promptGreen = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0xFFFFD54F) // Letras Oro 24K
        TerminalThemeMode.CYBER_AMBER -> Color(0xFFFFEA8A)     // Letras Oro Claro
        TerminalThemeMode.HYPER_MONOLITH -> Color(0xFFF2F6FC)  // Letras Plata Pura
        TerminalThemeMode.SYNTH_CYAN -> Color(0xFFFFD54F)      // Letras Oro 24K
    }
    val promptCyan = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0xFFE8EEF5) // Letras Plata Brillante
        TerminalThemeMode.CYBER_AMBER -> Color(0xFFFFD54F)
        TerminalThemeMode.HYPER_MONOLITH -> Color(0xFFFFD54F)
        TerminalThemeMode.SYNTH_CYAN -> Color(0xFFF0F4FA)
    }
    val promptAmber = Color(0xFFFFECB3)
    val promptRed = Color(0xFFFF8A80)
    val textPrimary = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0xFFF2F6FC) // Letras Plata Brillante
        TerminalThemeMode.CYBER_AMBER -> Color(0xFFFFECB3)     // Letras Oro Claro
        TerminalThemeMode.HYPER_MONOLITH -> Color(0xFFFFFFFF)
        TerminalThemeMode.SYNTH_CYAN -> Color(0xFFEDF2F8)
    }
    val textMuted = when (currentTheme) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> Color(0xFFE5C97B) // Letras Oro Champaña
        TerminalThemeMode.CYBER_AMBER -> Color(0xFFD4AF37)
        TerminalThemeMode.HYPER_MONOLITH -> Color(0xFFC8D4E3)
        TerminalThemeMode.SYNTH_CYAN -> Color(0xFFD6C38B)
    }

    Column(
        modifier = modifier
            .background(terminalBg, RoundedCornerShape(16.dp))
            .border(1.5.dp, com.example.ui.theme.LuxGoldSilverBorderBrush, RoundedCornerShape(16.dp))
            .testTag("activity_terminal")
    ) {
        // Cabecera de la Consola de Alto Contraste
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBg, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .border(1.dp, borderColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Luces de estado UNIX
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(promptRed))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(promptAmber))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(promptGreen))
                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = promptGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = terminalTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = textPrimary
                )
            }

            // Controles de Cabecera: Panel de Control, Pausa de Stream, Copia y Limpieza
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botón: Dashboard Recharts (Métricas de logs, error rate y latencia)
                if (onOpenRechartsDashboard != null) {
                    IconButton(
                        onClick = onOpenRechartsDashboard,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_terminal_recharts_dashboard")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Dashboard Recharts",
                            tint = promptCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Botón: Abrir/Cerrar Panel de Control
                IconButton(
                    onClick = { showControlPanel = !showControlPanel },
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (showControlPanel) promptCyan.copy(alpha = 0.2f) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .testTag("btn_terminal_control_panel")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Panel de Control de la Consola",
                        tint = if (showControlPanel) promptCyan else textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Convert text to speech con ícono audio_spark (gemini-3.8-flash-tts)
                IconButton(
                    onClick = {
                        val textToSpeak = logs.lastOrNull()?.text ?: "Terminal interactiva de LUX activa y en espera de instrucciones."
                        if (onConvertTextToSpeech != null) {
                            onConvertTextToSpeech(textToSpeak)
                        } else {
                            onExecuteCommand("tts \"$textToSpeak\"")
                        }
                        Toast.makeText(context, "Locución TTS (gemini-3.8-flash-tts)", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp).testTag("btn_convert_text_to_speech")
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_audio_spark),
                        contentDescription = "Convert text to speech",
                        tint = promptAmber,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Pausar / Reanudar Auto-Scroll
                IconButton(
                    onClick = { isAutoScrollPaused = !isAutoScrollPaused },
                    modifier = Modifier.size(28.dp).testTag("btn_terminal_pause_stream")
                ) {
                    Icon(
                        imageVector = if (isAutoScrollPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isAutoScrollPaused) "Reanudar auto-scroll" else "Pausar auto-scroll",
                        tint = if (isAutoScrollPaused) promptAmber else textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Copiar Buffer al Portapapeles
                IconButton(
                    onClick = {
                        val bufferText = logs.joinToString("\n") { "[${it.timestamp}] [${it.type}] ${it.text}" }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("ActivityTerminal Logs", bufferText))
                        Toast.makeText(context, "Logs copiados al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp).testTag("btn_terminal_copy_logs")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar logs",
                        tint = textMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Limpiar Buffer de Logs
                IconButton(
                    onClick = onClearLogs,
                    modifier = Modifier.size(28.dp).testTag("btn_terminal_clear")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpiar consola",
                        tint = textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // PANEL DE CONTROL DE LA CONSOLA (Expandible / Colapsable)
        AnimatedVisibility(
            visible = showControlPanel,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(controlPanelBg)
                    .border(width = 1.dp, color = borderColor)
                    .padding(12.dp)
                    .testTag("console_control_panel")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DashboardCustomize,
                            contentDescription = null,
                            tint = promptCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PANEL DE CONTROL DE LA CONSOLA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = promptCyan
                        )
                    }

                    // Toggle de densidad de texto
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isCompactFont) "Compacto" else "Normal",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                            color = textMuted
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { isCompactFont = !isCompactFont },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Alternar tamaño de fuente",
                                tint = promptGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Telemetría en Vivo del Kernel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0F1522), RoundedCornerShape(8.dp))
                            .border(0.5.dp, borderColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("CPU / NPU", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = textMuted)
                            Text("3.2% · Acelerado", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = promptGreen)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0F1522), RoundedCornerShape(8.dp))
                            .border(0.5.dp, borderColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("RAM USADA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = textMuted)
                            Text("3.4 GB / 8.0 GB", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = promptAmber)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0F1522), RoundedCornerShape(8.dp))
                            .border(0.5.dp, borderColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("ANTIGRAVITY", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = textMuted)
                            Text("11.8 ms · 0 Net", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = promptCyan)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Temas de Color para la Consola (CLI)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = promptCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TEMAS DE COLOR DEL TERMINAL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        ),
                        color = promptCyan
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TerminalThemeMode.entries.forEach { themeOption ->
                        val isSelected = currentTheme == themeOption
                        val themeLabel = when (themeOption) {
                            TerminalThemeMode.OBSIDIAN_MATRIX -> "Obsidian Matrix"
                            TerminalThemeMode.CYBER_AMBER -> "Cyber Amber"
                            TerminalThemeMode.HYPER_MONOLITH -> "Monolith B&W"
                            TerminalThemeMode.SYNTH_CYAN -> "Synth Cyan"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                currentTheme = themeOption
                                onThemeChanged?.invoke(themeOption)
                            },
                            label = {
                                Text(themeLabel, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = promptGreen.copy(alpha = 0.2f),
                                selectedLabelColor = promptGreen,
                                containerColor = Color(0xFF0F1522),
                                labelColor = textMuted
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) promptGreen else borderColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Macros de Control Rápido
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = {
                            if (onTriggerRecoveryMode != null) {
                                onTriggerRecoveryMode()
                            } else {
                                onExecuteCommand("fix --last-error")
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Healing, null, tint = promptRed, modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Forzar Auto-Fix", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptRed)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF241416)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptRed.copy(alpha = 0.5f))
                    )

                    AssistChip(
                        onClick = {
                            onExecuteCommand("antigravity status")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Speed, null, tint = promptGreen, modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Estado Antigravity", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptGreen)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF0C1914)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptGreen.copy(alpha = 0.4f))
                    )

                    AssistChip(
                        onClick = {
                            onExecuteCommand("patch list")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Build, null, tint = promptAmber, modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Lista de Parches", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptAmber)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1E170A)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptAmber.copy(alpha = 0.4f))
                    )

                    AssistChip(
                        onClick = {
                            if (onOpenRechartsDashboard != null) {
                                onOpenRechartsDashboard()
                            } else {
                                onExecuteCommand("antigravity telemetry")
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.BarChart, null, tint = promptCyan, modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Dashboard Recharts", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptCyan)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF0E1A24)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptCyan.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("chip_recharts_dashboard")
                    )

                    AssistChip(
                        onClick = {
                            if (onOpenModelManager != null) {
                                onOpenModelManager()
                            } else {
                                onExecuteCommand("antigravity models")
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Memory, null, tint = promptGreen, modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Modelos Gemma (SHA-256)", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptGreen)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF0C1F16)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("chip_gemma_models")
                    )

                    AssistChip(
                        onClick = {
                            onExecuteCommand("monitor status")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Memory, null, tint = promptCyan, modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Telemetría OS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptCyan)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF0E1A24)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptCyan.copy(alpha = 0.4f))
                    )

                    AssistChip(
                        onClick = {
                            val textToSpeak = logs.lastOrNull()?.text ?: "Sistema LUX y Antigravity en estado óptimo."
                            if (onConvertTextToSpeech != null) {
                                onConvertTextToSpeech(textToSpeak)
                            } else {
                                onExecuteCommand("tts \"$textToSpeak\"")
                            }
                            Toast.makeText(context, "Locución TTS iniciada con gemini-3.8-flash-tts", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_audio_spark),
                                contentDescription = "audio_spark",
                                tint = promptAmber,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = {
                            Text("Convert text to speech", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = promptAmber)
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF231B0A)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = promptAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("chip_convert_text_to_speech")
                    )

                    // Botón para provocar error de prueba y verificar el Recovery Mode en vivo
                    AssistChip(
                        onClick = {
                            if (onSimulateErrorForTest != null) {
                                onSimulateErrorForTest()
                            } else {
                                onExecuteCommand("simular-fallo-prueba")
                            }
                            dismissErrorAlert = false
                            Toast.makeText(context, "Fallo simulado inyectado en el log stream", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.BugReport, null, tint = Color(0xFFFF8A80), modifier = Modifier.size(13.dp))
                        },
                        label = {
                            Text("Simular Error (Test)", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFFFF8A80))
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF261214)),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color(0xFFFF8A80).copy(alpha = 0.4f))
                    )
                }
            }
        }

        // BANNER & BOTÓN 'RECOVERY MODE' (Activado al Detectar Errores en el Log Stream)
        AnimatedVisibility(
            visible = hasDetectedError,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2B0A0E), Color(0xFF180A0C))
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = promptRed.copy(alpha = pulseGlow),
                        shape = RoundedCornerShape(0.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("recovery_mode_alert_banner")
            ) {
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
                                .background(promptRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = promptRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "RECOVERY MODE DISPONIBLE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = promptRed
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(promptRed, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "${recentErrorLogs.size} ERROR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.Black
                                    )
                                }
                            }
                            Text(
                                text = "Fallo interceptado en el stream. Ejecuta el Motor de Autocorrección de AntigravityCLI.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = textPrimary.copy(alpha = 0.85f),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón Principal de 'Recovery Mode'
                    Button(
                        onClick = {
                            if (onTriggerRecoveryMode != null) {
                                onTriggerRecoveryMode()
                            } else {
                                onExecuteCommand("fix --last-error")
                            }
                            dismissErrorAlert = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = promptRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_trigger_recovery_mode")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Healing,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-Fix",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Sugerencias Rápidas de Comandos en Línea (Compactas para no estorbar la terminal)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            suggestedCommands.forEach { cmd ->
                androidx.compose.material3.Surface(
                    onClick = {
                        commandInput = cmd
                        onExecuteCommand(cmd)
                        commandInput = ""
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0A0F16),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)
                ) {
                    Text(
                        text = cmd,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.5.sp
                        ),
                        color = promptGreen,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // LazyColumn: Consola de Alto Contraste con Tipografía Monoespaciada
        val fontSize = if (isCompactFont) 10.5.sp else 11.5.sp
        val lineHeight = if (isCompactFont) 13.5.sp else 15.sp

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag("terminal_lazy_column")
        ) {
            itemsIndexed(logs, key = { _, item -> item.id }) { index, log ->
                val (badgeColor, badgePrefix) = when (log.type) {
                    CliLogType.SUCCESS -> promptGreen to "[HOTFIX_OK]"
                    CliLogType.WARNING -> promptAmber to "[ALERTA]"
                    CliLogType.ERROR -> promptRed to "[FALLO]"
                    CliLogType.AGENT -> Color(0xFFD946EF) to "[AGENTE]"
                    CliLogType.COMMAND -> promptCyan to ">"
                    CliLogType.SYSTEM -> promptGreen to "[SISTEMA]"
                    CliLogType.INFO -> textPrimary to "[INFO]"
                }

                // Animación fluida de Fade y Slide al inyectar nuevos logs en tiempo real
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 260)
                    ) + slideInVertically(
                        animationSpec = tween(durationMillis = 260),
                        initialOffsetY = { fullHeight -> (fullHeight * 0.7f).toInt() }
                    ),
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(260),
                        placementSpec = tween(260)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = if (isCompactFont) 1.dp else 2.dp)
                    ) {
                        // Marca de tiempo / índice
                        Text(
                            text = if (log.timestamp.isNotEmpty()) log.timestamp.take(8) else "${index + 1}".padStart(3, '0'),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp
                            ),
                            color = textMuted,
                            modifier = Modifier.width(58.dp)
                        )

                        // Prefijo de severidad
                        Text(
                            text = "$badgePrefix ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = fontSize
                            ),
                            color = badgeColor
                        )

                        // Mensaje con tipografía monoespaciada y resaltado de sintaxis para lenguajes de programación
                        Text(
                            text = CliSyntaxHighlighter.highlightTerminalText(log.text, log.type),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = fontSize,
                                lineHeight = lineHeight
                            )
                        )
                    }
                }
            }
        }

        // Input Interactivo en Línea de Comandos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBg)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "lux$",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = promptGreen,
                modifier = Modifier.padding(start = 2.dp, end = 4.dp)
            )

            // Navegación de Historial: Flecha Arriba (↑)
            IconButton(
                onClick = {
                    val fullHistory = (suggestedCommands + commandHistory).distinct()
                    if (fullHistory.isNotEmpty()) {
                        if (historyIndex < fullHistory.size - 1) {
                            historyIndex++
                            commandInput = fullHistory[fullHistory.size - 1 - historyIndex]
                        }
                    }
                },
                modifier = Modifier.size(28.dp).testTag("btn_terminal_history_up")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Historial comando anterior",
                    tint = promptGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Navegación de Historial: Flecha Abajo (↓)
            IconButton(
                onClick = {
                    val fullHistory = (suggestedCommands + commandHistory).distinct()
                    if (fullHistory.isNotEmpty() && historyIndex > 0) {
                        historyIndex--
                        commandInput = fullHistory[fullHistory.size - 1 - historyIndex]
                    } else if (historyIndex == 0) {
                        historyIndex = -1
                        commandInput = ""
                    }
                },
                modifier = Modifier.size(28.dp).testTag("btn_terminal_history_down")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Historial comando siguiente",
                    tint = promptGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = {
                    Text(
                        "antigravity | fix --last-error | monitor...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = textMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("activity_terminal_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (commandInput.isNotBlank()) {
                            historyIndex = -1
                            onExecuteCommand(commandInput)
                            commandInput = ""
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = promptGreen,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    cursorColor = promptGreen
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp
                )
            )

            // Cursor animado
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 18.dp)
                    .background(promptGreen.copy(alpha = cursorAlpha))
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Dictado por voz (Reconocimiento de voz Speech-to-Text)
            IconButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta un comando o describe código para LUX...")
                    }
                    try {
                        speechLauncher.launch(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, "Reconocimiento de voz no disponible", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(promptCyan.copy(alpha = 0.15f))
                    .testTag("btn_terminal_voice_dictation")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Dictar comando por voz",
                    tint = promptCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        historyIndex = -1
                        onExecuteCommand(commandInput)
                        commandInput = ""
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(promptGreen, CircleShape)
                    .testTag("btn_activity_terminal_send")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar comando",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
