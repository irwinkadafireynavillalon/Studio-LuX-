package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AiOrchestratorService
import com.example.data.models.CliLogEntry
import com.example.data.models.CliLogType
import com.example.data.models.TerminalThemeMode
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
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

/**
 * 2. Termux LuX Screen
 * Pantalla de terminal amplia vinculada con Termux y Termux API:
 * - Amplio visor con scroll táctil responsivo y alto contraste.
 * - Teclado no estorba: maneja imePadding() y auto-scroll inteligente.
 * - Teclas virtuales auxiliares: ESC, TAB, CTRL, ALT, Flechas (↑, ↓, ←, →), |, ~.
 * - Barra de predicción rápida de palabras y comandos de Termux / Antigravity.
 * - Mini-barra superior con botones pequeños para descargar herramientas (pkg install) y activar Antigravity CLI.
 * - Fácil de copiar y fácil de borrar escritura.
 */
@Composable
fun TermuxLuxScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    orchestratorService: AiOrchestratorService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val logs by repository.cliLogs.collectAsState()
    val currentTheme by repository.terminalTheme.collectAsState()
    val commandHistory by repository.commandHistory.collectAsState()

    var inputCommand by remember { mutableStateOf("") }
    var ctrlActive by remember { mutableStateOf(false) }
    var altActive by remember { mutableStateOf(false) }
    var showPackagesDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val termuxPackages = listOf(
        "python" to "Python 3.12 & pip",
        "git" to "Git Distributed SCM",
        "openjdk-17" to "OpenJDK Java 17 Development Kit",
        "nodejs" to "Node.js 22 LTS & npm",
        "clang" to "Clang C/C++ Compiler & Make",
        "curl" to "cURL HTTP Transfer Tool",
        "termux-api" to "Termux API (SMS, Sensor, Batería)",
        "lifertl" to "Lifertl Local Inference Gateway"
    )

    val commandPredictions = remember(inputCommand) {
        val trimmed = inputCommand.trim().lowercase()
        val allSuggestions = listOf(
            "antigravity run \"diagnostico\"",
            "pkg update && pkg upgrade",
            "pkg install python git openjdk-17",
            "termux-battery-status",
            "termux-toast \"LuX Termux Activo\"",
            "lifertl --port 4250 --daemon",
            "status",
            "build --apk",
            "clear",
            "pm list packages",
            "ls -la",
            "whoami",
            "uname -a"
        )
        if (trimmed.isEmpty()) {
            allSuggestions.take(6)
        } else {
            allSuggestions.filter { it.lowercase().contains(trimmed) }.take(6)
        }
    }

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    // Modal para descargar herramientas de Termux
    if (showPackagesDialog) {
        AlertDialog(
            onDismissRequest = { showPackagesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Descargar Herramientas Termux", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Selecciona un paquete para instalar automáticamente en el entorno Termux:",
                        fontSize = 11.sp,
                        color = LuxPlatinumSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    termuxPackages.forEach { (pkg, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141A24))
                                .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    soundService.playClick()
                                    val cmd = "pkg install -y $pkg"
                                    coroutineScope.launch {
                                        repository.executeCliCommand(cmd, soundService)
                                    }
                                    inputCommand = ""
                                    showPackagesDialog = false
                                    Toast.makeText(context, "Instalando $pkg en Termux...", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pkg, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxAccentCyan, fontFamily = FontFamily.Monospace)
                                Text(desc, fontSize = 9.sp, color = LuxOnDarkMuted)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(LuxGoldContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Instalar", fontSize = 9.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { showPackagesDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Cerrar", color = LuxPlatinumSecondary, fontSize = 11.sp)
                }
            },
            containerColor = LuxDarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal para cambiar tema de la terminal
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text("Tema de Terminal Termux", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TerminalThemeMode.entries.forEach { theme ->
                        val isSel = theme == currentTheme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) LuxGoldContainer else Color(0xFF141A24))
                                .border(0.5.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                soundService.playClick()
                                repository.setTerminalTheme(theme)
                                showThemeDialog = false
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(theme.displayName, fontSize = 11.sp, color = if (isSel) LuxGoldPrimary else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { showThemeDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Listo", color = LuxPlatinumSecondary, fontSize = 11.sp)
                }
            },
            containerColor = LuxDarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .imePadding()
    ) {
        // 1. Mini-Barra Superior Termux con Botones Pequeños No Intrusivos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(10.dp))
                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Izquierda: Título Termux & Indicador de Estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1E16)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = LuxAccentGreen, modifier = Modifier.size(12.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "termux@lux:~$",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = LuxAccentGreen
                )
            }

            // Derecha: Botones Pequeños de Acción
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón Descargar Herramientas
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x354285F4))
                        .border(0.6.dp, LuxAccentCyan.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .clickable {
                            soundService.playClick()
                            showPackagesDialog = true
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("btn_termux_download_tools")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("⬇ Herramientas", fontSize = 9.sp, color = LuxAccentCyan, fontWeight = FontWeight.Bold)
                    }
                }

                // Botón Activar Antigravity CLI en Termux
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x40FFD54F))
                        .border(0.8.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                        .clickable {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                val state = repository.downloadAndInstallAntigravityCli(context, soundService)
                                repository.executeCliCommand("antigravity --activate-termux", soundService)
                                Toast.makeText(context, "✔ Antigravity CLI vinculado con Termux", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("btn_termux_activate_antigravity")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("⚡ Antigravity CLI", fontSize = 9.sp, color = LuxGoldLight, fontWeight = FontWeight.ExtraBold)
                    }
                }

                // Selector de Temas
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x301E2430))
                        .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            soundService.playClick()
                            showThemeDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "Temas", tint = LuxPlatinumSecondary, modifier = Modifier.size(12.dp))
                }

                // Limpiar Terminal
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x301E2430))
                        .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            soundService.playClick()
                            repository.clearCliLogs()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Limpiar", tint = LuxPlatinumSecondary, modifier = Modifier.size(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Pantalla de Terminal Amplia (Canvas de Terminal)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xE6080B10))
                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                .padding(6.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("termux_lazy_column")
            ) {
                itemsIndexed(logs, key = { _, item -> item.id }) { _, log ->
                    val color = when (log.type) {
                        CliLogType.SUCCESS -> LuxAccentGreen
                        CliLogType.ERROR -> LuxAccentRed
                        CliLogType.WARNING -> LuxGoldPrimary
                        CliLogType.COMMAND -> LuxAccentCyan
                        CliLogType.AGENT -> LuxGoldLight
                        else -> Color(0xFFC0CAD8)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Tocar log para copiar fácilmente al portapapeles
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Termux Log", log.text))
                                Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 1.5.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = log.text,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                lineHeight = 13.5.sp
                            ),
                            color = color
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3. Barra de Predicción de Palabras y Comandos (Scroll Horizontal)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            commandPredictions.forEach { pred ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x35192130))
                        .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            soundService.playClick()
                            inputCommand = pred
                        }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = pred,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        ),
                        color = LuxAccentCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 4. Barra de Teclas Especiales de Termux (ESC, TAB, CTRL, ALT, Flechas ↑ ↓ ← →, |, ~)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tecla ESC
            TermuxKeyPill(label = "ESC", onClick = {
                soundService.playClick()
                inputCommand = ""
            })

            // Tecla TAB (Auto-completar)
            TermuxKeyPill(label = "TAB", onClick = {
                soundService.playClick()
                val match = commandPredictions.firstOrNull()
                if (match != null) inputCommand = match
            })

            // Tecla CTRL (Toggle)
            TermuxKeyPill(label = "CTRL", isActive = ctrlActive, onClick = {
                soundService.playClick()
                ctrlActive = !ctrlActive
            })

            // Tecla ALT (Toggle)
            TermuxKeyPill(label = "ALT", isActive = altActive, onClick = {
                soundService.playClick()
                altActive = !altActive
            })

            // Flechas de navegación de comandos
            TermuxKeyPill(label = "↑", onClick = {
                soundService.playClick()
                if (commandHistory.isNotEmpty()) {
                    inputCommand = commandHistory.last()
                }
            })

            TermuxKeyPill(label = "↓", onClick = {
                soundService.playClick()
                if (commandHistory.size >= 2) {
                    inputCommand = commandHistory[commandHistory.size - 2]
                } else {
                    inputCommand = ""
                }
            })

            TermuxKeyPill(label = "←", onClick = {
                soundService.playClick()
                if (inputCommand.length > 1) {
                    inputCommand = inputCommand.dropLast(1)
                }
            })

            TermuxKeyPill(label = "→", onClick = {
                soundService.playClick()
                val match = commandPredictions.firstOrNull()
                if (match != null && match.startsWith(inputCommand)) {
                    inputCommand = match
                }
            })

            TermuxKeyPill(label = "|", onClick = {
                soundService.playClick()
                inputCommand += " | "
            })

            TermuxKeyPill(label = "~", onClick = {
                soundService.playClick()
                inputCommand += "~/"
            })

            TermuxKeyPill(label = "-", onClick = {
                soundService.playClick()
                inputCommand += "-"
            })

            // Botón de Borrado Rápido
            Box(
                modifier = Modifier
                    .height(26.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x35331515))
                    .border(0.5.dp, LuxAccentRed.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                    .clickable {
                        soundService.playClick()
                        inputCommand = ""
                    }
                    .padding(horizontal = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Borrar", fontSize = 9.sp, color = LuxAccentRed, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 5. Input de Comando con Envío Rápido y Portapapeles (Teclado Amigable)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputCommand,
                onValueChange = { inputCommand = it },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("termux_command_input"),
                placeholder = {
                    Text("$ escribe comando o ayuda...", fontSize = 11.sp, color = LuxOnDarkMuted, fontFamily = FontFamily.Monospace)
                },
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = LuxAccentGreen
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxGoldPrimary,
                    unfocusedBorderColor = LuxDarkBorder,
                    focusedContainerColor = Color(0xFF0C1018),
                    unfocusedContainerColor = Color(0xFF0C1018)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        val cmd = inputCommand.trim()
                        if (cmd.isNotEmpty()) {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.executeCliCommand(cmd, soundService)
                            }
                            inputCommand = ""
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(5.dp))

            // Botón Enviar Comando
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxGoldPrimary)
                    .clickable {
                        val cmd = inputCommand.trim()
                        if (cmd.isNotEmpty()) {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.executeCliCommand(cmd, soundService)
                            }
                            inputCommand = ""
                        }
                    }
                    .testTag("btn_termux_send_command"),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Ejecutar", tint = LuxDarkBackground, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun TermuxKeyPill(
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(26.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (isActive) LuxGoldPrimary else Color(0xFF141924))
            .border(
                0.6.dp,
                if (isActive) LuxGoldLight else LuxDarkBorder,
                RoundedCornerShape(5.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (isActive) LuxDarkBackground else LuxPlatinumSecondary
        )
    }
}
