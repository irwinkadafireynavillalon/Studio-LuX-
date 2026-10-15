package com.example.ui.components

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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliLogEntry
import com.example.data.models.CliLogType
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxTerminalBg
import com.example.ui.theme.LuxTerminalCyan
import com.example.ui.theme.LuxTerminalGreen
import com.example.ui.theme.LuxTerminalPurple
import com.example.ui.theme.LuxTerminalRed
import com.example.ui.theme.LuxTerminalText
import com.example.ui.theme.LuxTerminalYellow

@Composable
fun TerminalView(
    logs: List<CliLogEntry>,
    onExecuteCommand: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    suggestedCommands: List<String> = listOf(
        "fix --last-error",
        "upgrade --models",
        "evolve --feature \"Dashboard BI\"",
        "partner --ask \"¿Cómo optimizar WebSocket?\"",
        "build --apk",
        "status",
        "logs --tail",
        "help"
    )
) {
    var commandInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new log
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .background(LuxTerminalBg, RoundedCornerShape(16.dp))
            .border(1.dp, LuxDarkBorder, RoundedCornerShape(16.dp))
            .testTag("terminal_view")
    ) {
        // Terminal Window Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .border(
                    width = 0.5.dp,
                    color = LuxDarkBorder,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Window dots
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(LuxAccentRed))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(LuxTerminalYellow))
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(LuxAccentGreen))
                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = LuxGoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "activity_cli@lux-devos: ~",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = LuxOnDark
                )
            }

            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("clear_terminal_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Limpiar terminal",
                    tint = LuxOnDarkMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Quick Suggestions Horizontal Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                fontSize = 11.sp
                            ),
                            color = LuxGoldPrimary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF141720),
                        labelColor = LuxGoldPrimary
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = LuxDarkBorder
                    ),
                    modifier = Modifier.testTag("chip_$cmd")
                )
            }
        }

        // Logs Output Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            items(logs, key = { it.id }) { log ->
                val (color, prefix) = when (log.type) {
                    CliLogType.SUCCESS -> LuxTerminalGreen to "[SUCCESS]"
                    CliLogType.WARNING -> LuxTerminalYellow to "[WARN]"
                    CliLogType.ERROR -> LuxTerminalRed to "[ERR]"
                    CliLogType.AGENT -> LuxTerminalPurple to "[AGENT]"
                    CliLogType.COMMAND -> LuxTerminalCyan to ">"
                    CliLogType.SYSTEM -> LuxGoldPrimary to "[SYS]"
                    CliLogType.INFO -> LuxTerminalText to "[INFO]"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    if (log.timestamp.isNotEmpty()) {
                        Text(
                            text = log.timestamp.take(8),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = LuxOnDarkMuted,
                            modifier = Modifier.width(62.dp)
                        )
                    }

                    Text(
                        text = "$prefix ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = color
                    )

                    Text(
                        text = log.text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = when (log.type) {
                            CliLogType.COMMAND -> LuxAccentCyan
                            CliLogType.AGENT -> LuxTerminalPurple
                            CliLogType.SUCCESS -> LuxTerminalGreen
                            CliLogType.ERROR -> LuxTerminalRed
                            else -> LuxTerminalText
                        }
                    )
                }
            }
        }

        // Interactive Command Line Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D0F16))
                .border(
                    width = 0.5.dp,
                    color = LuxDarkBorder,
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "lux$",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = LuxGoldPrimary,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp)
            )

            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = {
                    Text(
                        "fix --last-error | evolve | build...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = LuxOnDarkMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("cli_text_input"),
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
                    focusedBorderColor = LuxGoldPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = LuxOnDark,
                    unfocusedTextColor = LuxOnDark,
                    cursorColor = LuxGoldPrimary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            )

            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        onExecuteCommand(commandInput)
                        commandInput = ""
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(LuxGoldPrimary, CircleShape)
                    .testTag("send_command_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Ejecutar comando",
                    tint = LuxTerminalBg,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
