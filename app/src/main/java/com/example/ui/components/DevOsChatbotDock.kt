package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AiOrchestratorService
import com.example.data.engine.GeminiApiClient
import com.example.data.models.AgentMessageSender
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted

@Composable
fun DevOsChatbotDock(
    orchestratorService: AiOrchestratorService,
    onNavigateToTab: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var inputInstruction by remember { mutableStateOf("") }

    val messages by orchestratorService.messages.collectAsState()
    val isGenerating by orchestratorService.isGenerating.collectAsState()
    val isGeminiConfigured = remember { GeminiApiClient.isApiKeyConfigured() }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isGenerating, isExpanded) {
        if (isExpanded && messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputInstruction = spoken
            }
        }
    }

    val quickInstructions = listOf(
        "🍕 App Delivery Room",
        "📦 Compilar APK",
        "🗄️ Añadir SQLite",
        "🎨 Tema Cyber Amber",
        "⚡ Diagnóstico Kernel"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .testTag("devos_chatbot_dock")
    ) {
        // PANEL EXPANDIDO COMPACTO (No intrusivo)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .heightIn(min = 180.dp, max = 240.dp)
                    .border(
                        width = 0.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(LuxGoldPrimary.copy(alpha = 0.5f), LuxDarkBorder)
                        ),
                        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                    ),
                shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0F17))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val selectedModel by orchestratorService.selectedModel.collectAsState()
                    val isOfflineMode by orchestratorService.isOfflineMode.collectAsState()
                    val isOnline = orchestratorService.isNetworkAvailable()

                    // Header ultra-compacto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF141926))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Copiloto Rápido",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = LuxGoldLight
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isOfflineMode) Color(0xFF332014) else Color(0xFF14241B),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        0.5.dp,
                                        if (isOfflineMode) LuxGoldPrimary else LuxAccentGreen,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        orchestratorService.toggleOfflineMode()
                                    }
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                    .testTag("chatbot_toggle_offline")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.Wifi,
                                        contentDescription = null,
                                        tint = if (isOfflineMode) Color(0xFFFFB300) else LuxAccentGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isOfflineMode) "NPU" else "Online",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isOfflineMode) Color(0xFFFFD54F) else LuxAccentGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1F2636), RoundedCornerShape(6.dp))
                                    .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        val modelsList = orchestratorService.availableModels.map { it.first }
                                        val currentIndex = modelsList.indexOf(selectedModel)
                                        val nextModel = modelsList[(currentIndex + 1) % modelsList.size]
                                        orchestratorService.setSelectedModel(nextModel)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("chatbot_select_model_chip")
                            ) {
                                val chipLabel = when (selectedModel) {
                                    "hybrid-auto" -> "Auto"
                                    "google/functiongemma-270m" -> "Gemma 270m"
                                    "gemini-3.5-flash" -> "Gemini 3.5"
                                    else -> selectedModel.take(10)
                                }
                                Text(
                                    text = chipLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = LuxGoldLight
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { isExpanded = false },
                                modifier = Modifier.size(22.dp).testTag("chatbot_collapse_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Minimizar chat",
                                    tint = LuxOnDarkMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Lista compacta de mensajes
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("chatbot_messages_list")
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            val isUser = msg.sender == AgentMessageSender.USER
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    modifier = Modifier.widthIn(max = 280.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) Color(0xFF262115) else Color(0xFF161B29)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 0.5.dp,
                                        color = if (isUser) LuxGoldPrimary.copy(alpha = 0.35f) else LuxDarkBorder
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
                                            color = if (isUser) LuxGoldLight else LuxOnDark
                                        )

                                        if (!isUser) {
                                            var isPlaying by remember { mutableStateOf(false) }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                                IconButton(
                                                    onClick = {
                                                        isPlaying = !isPlaying
                                                        // TODO: Call orchestratorService.toggleTts(msg.text, isPlaying)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = "Escuchar",
                                                        tint = LuxGoldPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        // TODO: Copy to clipboard
                                                        Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = "Copiar",
                                                        tint = LuxGoldPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        // TODO: Share
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Share,
                                                        contentDescription = "Compartir",
                                                        tint = LuxGoldPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Acciones sugeridas como chips horizontales compactos (no apilados)
                                        if (msg.actions.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                msg.actions.forEach { action ->
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(LuxGoldContainer)
                                                            .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                            .clickable {
                                                                orchestratorService.executeAction(action, context, onNavigateToTab)
                                                            }
                                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = action.label,
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 9.5.sp,
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            color = LuxGoldPrimary,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (isGenerating) {
                            item {
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 1.5.dp,
                                        color = LuxGoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Procesando...",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = LuxGoldLight
                                    )
                                }
                            }
                        }
                    }

                    // Sugerencias rápidas en tira compacta de 22dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F131C))
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        quickInstructions.forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1B1812))
                                    .border(0.5.dp, LuxGoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        orchestratorService.sendMessage(suggestion, onNavigateToTab)
                                    }
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = suggestion,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = LuxGoldPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Input compacto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF141926))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputInstruction,
                            onValueChange = { inputInstruction = it },
                            placeholder = {
                                Text(
                                    "Instrucción rápida...",
                                    fontSize = 10.5.sp,
                                    color = LuxOnDarkMuted
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chatbot_input_field"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (inputInstruction.isNotBlank()) {
                                        orchestratorService.sendMessage(inputInstruction, onNavigateToTab)
                                        inputInstruction = ""
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuxGoldPrimary,
                                unfocusedBorderColor = LuxDarkBorder,
                                focusedTextColor = LuxOnDark,
                                unfocusedTextColor = LuxOnDark,
                                focusedContainerColor = Color(0xFF0A0D14),
                                unfocusedContainerColor = Color(0xFF0A0D14)
                            ),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla tu instrucción...")
                                }
                                try {
                                    speechLauncher.launch(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Micrófono no disponible", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E2838))
                                .testTag("chatbot_mic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Dictar instrucción",
                                tint = LuxAccentCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                if (inputInstruction.isNotBlank()) {
                                    orchestratorService.sendMessage(inputInstruction, onNavigateToTab)
                                    inputInstruction = ""
                                }
                            },
                            enabled = inputInstruction.isNotBlank() && !isGenerating,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputInstruction.isNotBlank()) LuxGoldPrimary else Color(0xFF2A2E3D)
                                )
                                .testTag("chatbot_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = if (inputInstruction.isNotBlank()) LuxDarkBackground else LuxOnDarkMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        // BARRA COLAPSADA ULTRA-COMPACTA DE 1 LÍNEA (No estorba al contenido)
        AnimatedVisibility(
            visible = !isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isExpanded = true }
                    .border(
                        width = 0.5.dp,
                        color = LuxGoldPrimary.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .testTag("chatbot_expand_button"),
                color = Color(0xFF101420),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        val selectedModel by orchestratorService.selectedModel.collectAsState()
                        val isOnline = orchestratorService.isNetworkAvailable()

                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOnline && isGeminiConfigured) "Copiloto IA ($selectedModel)" else "Copiloto Local (FunctionGemma)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = LuxGoldLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Chat",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = LuxAccentCyan
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ExpandLess,
                            contentDescription = null,
                            tint = LuxAccentCyan,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
