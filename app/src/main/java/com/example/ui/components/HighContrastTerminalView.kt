package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliLogEntry
import com.example.data.models.CliLogType
import com.example.data.models.TerminalThemeMode

data class TerminalColorPalette(
    val background: Color,
    val windowHeader: Color,
    val border: Color,
    val promptText: Color,
    val primaryText: Color,
    val greenText: Color,
    val yellowText: Color,
    val cyanText: Color,
    val redText: Color,
    val purpleText: Color,
    val mutedText: Color,
    val cursorColor: Color
)

fun getTerminalPalette(mode: TerminalThemeMode): TerminalColorPalette {
    return when (mode) {
        TerminalThemeMode.OBSIDIAN_MATRIX -> TerminalColorPalette(
            background = Color(0xFF040608),
            windowHeader = Color(0xFF0D1218),
            border = Color(0xFF1E2D22),
            promptText = Color(0xFF00FF66),
            primaryText = Color(0xFFE2FBE8),
            greenText = Color(0xFF00FF66),
            yellowText = Color(0xFFFFEE00),
            cyanText = Color(0xFF00F0FF),
            redText = Color(0xFFFF334B),
            purpleText = Color(0xFFE056FD),
            mutedText = Color(0xFF6B8A74),
            cursorColor = Color(0xFF00FF66)
        )
        TerminalThemeMode.CYBER_AMBER -> TerminalColorPalette(
            background = Color(0xFF0A0702),
            windowHeader = Color(0xFF191206),
            border = Color(0xFF38290D),
            promptText = Color(0xFFFFB300),
            primaryText = Color(0xFFFFEDB3),
            greenText = Color(0xFF69F0AE),
            yellowText = Color(0xFFFFC107),
            cyanText = Color(0xFFFFD54F),
            redText = Color(0xFFFF5252),
            purpleText = Color(0xFFFF80AB),
            mutedText = Color(0xFF9E8457),
            cursorColor = Color(0xFFFFB300)
        )
        TerminalThemeMode.HYPER_MONOLITH -> TerminalColorPalette(
            background = Color(0xFF000000),
            windowHeader = Color(0xFF111111),
            border = Color(0xFF333333),
            promptText = Color(0xFFFFFFFF),
            primaryText = Color(0xFFF8FAFC),
            greenText = Color(0xFF4ADE80),
            yellowText = Color(0xFFFDE047),
            cyanText = Color(0xFF38BDF8),
            redText = Color(0xFFF87171),
            purpleText = Color(0xFFC084FC),
            mutedText = Color(0xFF888888),
            cursorColor = Color(0xFFFFFFFF)
        )
        TerminalThemeMode.SYNTH_CYAN -> TerminalColorPalette(
            background = Color(0xFF060913),
            windowHeader = Color(0xFF0F172A),
            border = Color(0xFF1E293B),
            promptText = Color(0xFF00E5FF),
            primaryText = Color(0xFFE0F2FE),
            greenText = Color(0xFF34D399),
            yellowText = Color(0xFFFBBF24),
            cyanText = Color(0xFF00E5FF),
            redText = Color(0xFFFB7185),
            purpleText = Color(0xFFA855F7),
            mutedText = Color(0xFF64748B),
            cursorColor = Color(0xFF00E5FF)
        )
    }
}

@Composable
fun HighContrastTerminalView(
    logs: List<CliLogEntry>,
    commandHistory: List<String>,
    currentTheme: TerminalThemeMode,
    onExecuteCommand: (String) -> Unit,
    onClear: () -> Unit,
    onThemeChange: (TerminalThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    suggestedCommands: List<String> = listOf(
        "antigravity --run \"verificar kernel\"",
        "fix --last-error",
        "upgrade --models",
        "evolve --feature \"Sync Engine\"",
        "partner --ask \"Arquitectura Clean\"",
        "build --apk",
        "status",
        "logs --tail"
    )
) {
    var commandInput by remember { mutableStateOf("") }
    var isStreamPaused by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<CliLogType?>(null) }
    var showThemeMenu by remember { mutableStateOf(false) }
    var showHistoryMenu by remember { mutableStateOf(false) }

    val palette = getTerminalPalette(currentTheme)
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Cursor Blink animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorBlink"
    )

    // Filter logs if a category is selected
    val filteredLogs = remember(logs, selectedFilter) {
        if (selectedFilter == null) logs else logs.filter { it.type == selectedFilter }
    }

    // Auto-scroll when not paused
    LaunchedEffect(filteredLogs.size) {
        if (!isStreamPaused && filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(filteredLogs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .background(palette.background, RoundedCornerShape(16.dp))
            .border(1.5.dp, palette.border, RoundedCornerShape(16.dp))
            .testTag("high_contrast_terminal")
    ) {
        // High-Contrast Window Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.windowHeader, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .border(
                    width = 1.dp,
                    color = palette.border,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Window status lights
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFF334B)))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFFEE00)))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF00FF66)))
                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = palette.promptText,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "activity_cli@lux-devos: ~",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = palette.primaryText
                )
            }

            // Window Action Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pause / Resume Stream
                IconButton(
                    onClick = { isStreamPaused = !isStreamPaused },
                    modifier = Modifier.size(28.dp).testTag("btn_toggle_stream_pause")
                ) {
                    Icon(
                        imageVector = if (isStreamPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isStreamPaused) "Reanudar stream" else "Pausar stream",
                        tint = if (isStreamPaused) Color(0xFFFFEE00) else palette.mutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Command History Toggle
                IconButton(
                    onClick = { showHistoryMenu = !showHistoryMenu },
                    modifier = Modifier.size(28.dp).testTag("btn_toggle_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Historial de comandos",
                        tint = palette.mutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Theme Mode Switcher
                IconButton(
                    onClick = { showThemeMenu = !showThemeMenu },
                    modifier = Modifier.size(28.dp).testTag("btn_toggle_theme")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Tema del terminal",
                        tint = palette.promptText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Copy all logs
                IconButton(
                    onClick = {
                        val text = logs.joinToString("\n") { "[${it.timestamp}] [${it.type}] ${it.text}" }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Terminal Logs", text))
                        Toast.makeText(context, "Logs copiados al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp).testTag("btn_copy_logs")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar logs",
                        tint = palette.mutedText,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Clear Buffer
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(28.dp).testTag("btn_clear_buffer")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpiar terminal",
                        tint = palette.mutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Theme Switcher Drawer
        AnimatedVisibility(visible = showThemeMenu) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.windowHeader)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TerminalThemeMode.entries.forEach { mode ->
                    val isSelected = mode == currentTheme
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onThemeChange(mode)
                            showThemeMenu = false
                        },
                        label = {
                            Text(
                                text = mode.name.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                                color = if (isSelected) palette.promptText else palette.mutedText
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF141922),
                            selectedContainerColor = palette.border
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) palette.promptText else palette.border
                        )
                    )
                }
            }
        }

        // Command History Drawer
        AnimatedVisibility(visible = showHistoryMenu) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.windowHeader)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "HISTORIAL RECIENTE:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = palette.promptText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commandHistory.reversed().take(6).forEach { histCmd ->
                        AssistChip(
                            onClick = {
                                commandInput = histCmd
                                showHistoryMenu = false
                            },
                            label = {
                                Text(
                                    histCmd,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    ),
                                    color = palette.primaryText
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF161F28))
                        )
                    }
                }
            }
        }

        // Live Log Filters + Stream Status Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Pause indicator badge
            if (isStreamPaused) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF332A08), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFFFEE00), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "⏸ STREAM PAUSADO",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = Color(0xFFFFEE00)
                    )
                }
            }

            // Category filter chips
            val filterOptions = listOf(
                null to "TODOS (${logs.size})",
                CliLogType.AGENT to "AGENTE",
                CliLogType.ERROR to "ERRORES",
                CliLogType.SUCCESS to "HOTFIX OK",
                CliLogType.COMMAND to "COMANDOS"
            )

            filterOptions.forEach { (type, label) ->
                val isSelected = selectedFilter == type
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = if (isSelected) null else type },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) palette.promptText else palette.mutedText
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF0F1520),
                        selectedContainerColor = palette.border
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) palette.promptText else palette.border
                    )
                )
            }
        }

        // Suggestions Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestedCommands.forEach { cmd ->
                AssistChip(
                    onClick = {
                        commandInput = cmd
                        onExecuteCommand(cmd)
                        commandInput = ""
                    },
                    label = {
                        Text(
                            text = cmd,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = palette.promptText
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF0B1017),
                        labelColor = palette.promptText
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = palette.border
                    )
                )
            }
        }

        // Terminal Output Screen (Scrollable, High-Contrast)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            items(filteredLogs, key = { it.id }) { log ->
                val (color, prefix) = when (log.type) {
                    CliLogType.SUCCESS -> palette.greenText to "[HOTFIX_OK]"
                    CliLogType.WARNING -> palette.yellowText to "[ALERTA]"
                    CliLogType.ERROR -> palette.redText to "[FALLO]"
                    CliLogType.AGENT -> palette.purpleText to "[AUTOCORRECCIÓN]"
                    CliLogType.COMMAND -> palette.cyanText to ">"
                    CliLogType.SYSTEM -> palette.promptText to "[SISTEMA]"
                    CliLogType.INFO -> palette.primaryText to "[INFO]"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    if (log.timestamp.isNotEmpty()) {
                        Text(
                            text = log.timestamp.take(8),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = palette.mutedText,
                            modifier = Modifier.width(58.dp)
                        )
                    }

                    Text(
                        text = "$prefix ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = color
                    )

                    Text(
                        text = log.text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        ),
                        color = when (log.type) {
                            CliLogType.COMMAND -> palette.cyanText
                            CliLogType.AGENT -> palette.purpleText
                            CliLogType.SUCCESS -> palette.greenText
                            CliLogType.ERROR -> palette.redText
                            CliLogType.WARNING -> palette.yellowText
                            else -> palette.primaryText
                        }
                    )
                }
            }
        }

        // Interactive Command Line Input (with blinking cursor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.windowHeader)
                .border(
                    width = 1.dp,
                    color = palette.border,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "lux$",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = palette.promptText,
                modifier = Modifier.padding(start = 4.dp, end = 6.dp)
            )

            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = {
                    Text(
                        "fix --last-error | evolve | status...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = palette.mutedText
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_command_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (commandInput.isNotBlank()) {
                            onExecuteCommand(commandInput)
                            commandInput = ""
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.promptText,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = palette.primaryText,
                    unfocusedTextColor = palette.primaryText,
                    cursorColor = palette.cursorColor
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp
                )
            )

            // Blinking terminal cursor badge indicator
            Box(
                modifier = Modifier
                    .size(width = 8.dp, height = 18.dp)
                    .background(palette.cursorColor.copy(alpha = cursorAlpha))
                    .padding(end = 4.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        onExecuteCommand(commandInput)
                        commandInput = ""
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(palette.promptText, CircleShape)
                    .testTag("btn_execute_terminal_cmd")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Ejecutar comando",
                    tint = palette.background,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
