package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AiOrchestratorService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentOrange
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxTerminalBg
import com.example.ui.theme.LuxTerminalText
import kotlinx.coroutines.launch

data class TerminalLine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val type: TerminalLineType = TerminalLineType.OUTPUT,
    val timestamp: String = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
)

enum class TerminalLineType {
    COMMAND_ECHO,
    AI_ORCHESTRATOR_FEEDBACK,
    SYSTEM_BANNER,
    OUTPUT,
    ERROR,
    SUCCESS
}

@Composable
fun ActivityCliComponent(
    orchestratorService: AiOrchestratorService,
    modifier: Modifier = Modifier,
    initialWelcomeText: String = """
    ╔══════════════════════════════════════════════════════════════════╗
    ║              LUX DEVOS · ACTIVITY CLI (500MB ENGINE)             ║
    ╚══════════════════════════════════════════════════════════════════╝
    [KERNEL] Prioridad: URGENT_DISPLAY | Memoria: 500 MB LargeHeap | NPU: Activo
    [ORCHESTRATOR] Enlazado a Google AI Studio & FunctionGemma Local.
    Escribe 'help' o ingresa cualquier comando o instrucción en lenguaje natural.
    """.trimIndent()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputCommand by remember { mutableStateOf("") }
    var isExecuting by remember { mutableStateOf(false) }
    var historyIndex by remember { mutableIntStateOf(-1) }

    val terminalLines = remember {
        mutableStateListOf(
            TerminalLine(text = initialWelcomeText, type = TerminalLineType.SYSTEM_BANNER)
        )
    }

    val commandHistory = remember { mutableStateListOf<String>() }

    val quickCommands = listOf(
        "help",
        "ls",
        "cd",
        "mkdir",
        "status",
        "tools",
        "auth",
        "sysinfo",
        "build --apk",
        "tree",
        "diagnose",
        "fix",
        "models",
        "clear"
    )

    LaunchedEffect(terminalLines.size) {
        if (terminalLines.isNotEmpty()) {
            listState.animateScrollToItem(terminalLines.size - 1)
        }
    }

    fun submitCommand(cmdToRun: String) {
        val trimmed = cmdToRun.trim()
        if (trimmed.isEmpty()) return

        if (!commandHistory.contains(trimmed)) {
            commandHistory.add(trimmed)
        }
        historyIndex = -1

        if (trimmed.lowercase() == "clear" || trimmed.lowercase() == "cls") {
            terminalLines.clear()
            terminalLines.add(
                TerminalLine(
                    text = "[TERMINAL BUFFER CLEAR] Consola reiniciada a ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}.",
                    type = TerminalLineType.SYSTEM_BANNER
                )
            )
            inputCommand = ""
            return
        }

        terminalLines.add(
            TerminalLine(
                text = "lux-devos:~$ $trimmed",
                type = TerminalLineType.COMMAND_ECHO
            )
        )
        inputCommand = ""

        coroutineScope.launch {
            isExecuting = true
            try {
                val feedback = orchestratorService.executeCommandForCli(trimmed)
                if (feedback.isNotBlank()) {
                    val lineType = when {
                        feedback.contains("Error", ignoreCase = true) -> TerminalLineType.ERROR
                        feedback.contains("[APK READY]") || feedback.contains("éxito", ignoreCase = true) -> TerminalLineType.SUCCESS
                        else -> TerminalLineType.AI_ORCHESTRATOR_FEEDBACK
                    }
                    terminalLines.add(
                        TerminalLine(
                            text = feedback,
                            type = lineType
                        )
                    )
                }
            } catch (e: Exception) {
                terminalLines.add(
                    TerminalLine(
                        text = "[ERROR] Fallo al ejecutar '$trimmed': ${e.message}",
                        type = TerminalLineType.ERROR
                    )
                )
            } finally {
                isExecuting = false
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, LuxDarkBorder, RoundedCornerShape(12.dp))
            .testTag("activity_cli_component"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LuxTerminalBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera compacta UNIX + Tira de comandos rápidos integrada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C1019), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .border(width = 0.5.dp, color = LuxDarkBorder, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(LuxAccentRed))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(LuxAccentOrange))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(LuxAccentGreen))
                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = LuxAccentGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "lux@devos:~",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = LuxTerminalText
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Comandos rápidos en micro-píldoras de 20dp (no estorban la consola)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    quickCommands.forEach { cmd ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF111824))
                                .border(0.5.dp, LuxAccentGreen.copy(alpha = 0.35f), RoundedCornerShape(5.dp))
                                .clickable {
                                    inputCommand = cmd
                                    submitCommand(cmd)
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("quick_cmd_$cmd")
                        ) {
                            Text(
                                text = cmd,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp
                                ),
                                color = LuxAccentGreen,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isExecuting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp,
                            color = LuxAccentGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = {
                            val buffer = terminalLines.joinToString("\n") { "[${it.timestamp}] ${it.text}" }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Terminal Buffer", buffer))
                            Toast.makeText(context, "Buffer copiado", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(22.dp).testTag("btn_copy_terminal_buffer")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar buffer",
                            tint = LuxOnDarkMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    IconButton(
                        onClick = { submitCommand("clear") },
                        modifier = Modifier.size(22.dp).testTag("btn_clear_terminal_screen")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Limpiar terminal",
                            tint = LuxOnDarkMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // ÁREA DE TEXTO DESPLAZABLE (Maximizada)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF030508))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("terminal_scrollable_text_area")
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(terminalLines, key = { _, item -> item.id }) { _, line ->
                        val (textColor, prefix) = when (line.type) {
                            TerminalLineType.COMMAND_ECHO -> LuxAccentCyan to ""
                            TerminalLineType.AI_ORCHESTRATOR_FEEDBACK -> LuxGoldLight to ""
                            TerminalLineType.SYSTEM_BANNER -> LuxAccentGreen to ""
                            TerminalLineType.SUCCESS -> LuxAccentGreen to "✔ "
                            TerminalLineType.ERROR -> LuxAccentRed to "✖ "
                            TerminalLineType.OUTPUT -> LuxTerminalText to ""
                        }

                        Column(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text(
                                text = "$prefix${line.text}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                ),
                                color = textColor
                            )
                        }
                    }
                }
            }

            // CAMPO DE TEXTO INTERACTIVO COMPACTO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0E17))
                    .border(width = 0.5.dp, color = LuxDarkBorder, shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "lux:~$",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    ),
                    color = LuxAccentGreen,
                    modifier = Modifier.padding(start = 2.dp, end = 4.dp)
                )

                IconButton(
                    onClick = {
                        if (commandHistory.isNotEmpty() && historyIndex < commandHistory.size - 1) {
                            historyIndex++
                            inputCommand = commandHistory[commandHistory.size - 1 - historyIndex]
                        }
                    },
                    modifier = Modifier.size(22.dp).testTag("btn_cli_history_prev")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Comando previo",
                        tint = LuxAccentGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (commandHistory.isNotEmpty() && historyIndex > 0) {
                            historyIndex--
                            inputCommand = commandHistory[commandHistory.size - 1 - historyIndex]
                        } else if (historyIndex == 0) {
                            historyIndex = -1
                            inputCommand = ""
                        }
                    },
                    modifier = Modifier.size(22.dp).testTag("btn_cli_history_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Comando siguiente",
                        tint = LuxAccentGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (inputCommand.isNotEmpty()) {
                        val suggestions = quickCommands.filter { it.startsWith(inputCommand.lowercase()) }
                        if (suggestions.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                suggestions.forEach { suggestion ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(Color(0xFF1E2633))
                                            .border(0.5.dp, LuxAccentCyan.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                                            .clickable { inputCommand = suggestion }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            ),
                                            color = LuxAccentCyan
                                        )
                                    }
                                }
                            }
                        }
                    }

                    TextField(
                        value = inputCommand,
                        onValueChange = { inputCommand = it },
                        placeholder = {
                            Text(
                                "Escribe comando o instrucción...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = LuxOnDarkMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activity_cli_input_text_field"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = { submitCommand(inputCommand) }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = LuxTerminalText,
                            unfocusedTextColor = LuxTerminalText,
                            cursorColor = LuxAccentGreen
                        ),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { submitCommand(inputCommand) },
                    enabled = inputCommand.isNotBlank(),
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (inputCommand.isNotBlank()) LuxAccentGreen else Color(0xFF1E2633))
                        .testTag("activity_cli_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Ejecutar comando",
                        tint = if (inputCommand.isNotBlank()) Color.Black else LuxOnDarkMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
