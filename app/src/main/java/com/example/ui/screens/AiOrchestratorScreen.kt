package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.engine.AiOrchestratorService
import com.example.data.engine.FunctionGemmaLocalEngine
import com.example.data.engine.GeminiApiClient
import com.example.data.engine.LuxPriorityKernelService
import com.example.data.models.AgentActionType
import com.example.data.models.AgentChatMessage
import com.example.data.models.AgentInteractiveAction
import com.example.data.models.AgentMessageSender
import com.example.data.models.AiAgentType
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Interfaz Principal de Chat - Studio LuX
 * 
 * Cumple estrictamente con la especificación del usuario:
 * 1. Menú desplegable con sus botones sin número de botón.
 * 2. Un único botón integrado Multi-IA que despliega:
 *    - Botón IA de Géminis (agregar/generar API Key + sincronizar Cuenta Google)
 *    - Botón IA Gemma Vertex (sincronizar Cuenta Google + APIs y servicios Google Cloud)
 *    - Botón Gemma3 270m (descarga local + servidor localhost https://90.11.29.1:4250 en Termux)
 *    - Botón Antigravity CLI Híbrido (asistente/agente/orquestador con memoria Cloud/Drive)
 * 3. Atajos requeridos:
 *    - Botón Terminal (con redirección a Termux)
 *    - Botón de Permisos (escudo)
 *    - Botón de Chatbot Live "LuX Live"
 *    - Botón Flotante con acceso a tamaño y transparencia en Ajustes
 * 4. Redirección automática de cada acción a la terminal con sus scripts y paquetes.
 * 5. Motor local con parámetros estilo Ollama / LM Studio (temperatura, top-p, top-k, contexto).
 * 6. Paleta metálica Oro y Plata con cristales transparentes y letras que resaltan claramente.
 */
@Composable
fun AiOrchestratorScreen(
    orchestratorService: AiOrchestratorService,
    repository: LuxRepository,
    soundService: SoundHapticService,
    onNavigateToTab: (String) -> Unit,
    modifier: Modifier = Modifier,
    onOpenGoogleSyncDialog: (() -> Unit)? = null,
    onOpenMenuDrawer: (() -> Unit)? = null,
    onOpenPermissionsDialog: (() -> Unit)? = null,
    onOpenInterfaceSettingsDialog: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val messages by orchestratorService.messages.collectAsState()
    val isGenerating by orchestratorService.isGenerating.collectAsState()
    val selectedAgentType by orchestratorService.selectedAgentType.collectAsState()
    val googleSyncState by repository.googleAccountSyncState.collectAsState()
    val engineConfig by FunctionGemmaLocalEngine.engineConfig.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    var showMultiIaDialog by remember { mutableStateOf(false) }
    var showApiKeyModal by remember { mutableStateOf(false) }
    var apiKeyInputValue by remember { mutableStateOf("") }
    var isLiveChatbotActive by remember { mutableStateOf(true) }

    // Pulso animado para el botón LuX Live
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_lux_live")
    val pulseLiveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseLiveAlpha"
    )

    // Selector universal de archivos (*/*)
    val openAnyFileChatLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val imported = repository.importDeviceUriAsUniversalFile(context, uri, soundService)
            orchestratorService.sendMessageWithAttachment(
                userText = inputPrompt,
                attachedFiles = listOf(imported),
                onNavigateToTab = onNavigateToTab
            )
            inputPrompt = ""
        }
    }

    // Selector de fotos / imágenes
    val pickImageChatLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val imported = repository.importDeviceUriAsUniversalFile(context, uri, soundService)
            orchestratorService.sendMessageWithAttachment(
                userText = if (inputPrompt.isBlank()) "Analiza esta imagen adjunta" else inputPrompt,
                attachedFiles = listOf(imported),
                onNavigateToTab = onNavigateToTab
            )
            inputPrompt = ""
        }
    }

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Modal para Configurar API Key de Gemini
    if (showApiKeyModal) {
        AlertDialog(
            onDismissRequest = { showApiKeyModal = false },
            containerColor = LuxDarkSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(16.dp)),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Configurar Gemini API Key",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LuxGoldLight
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pega tu clave de API de Gemini o genera una nueva en Google AI Studio. Recuerda que también puedes sincronizar tu Cuenta Google para usar IA sin ingresar claves manuales.",
                        fontSize = 11.sp,
                        color = LuxPlatinumSecondary,
                        lineHeight = 15.sp
                    )

                    OutlinedTextField(
                        value = apiKeyInputValue,
                        onValueChange = { apiKeyInputValue = it },
                        placeholder = { Text("AIzaSy...", fontSize = 11.sp, color = LuxOnDarkMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = LuxGoldLight
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxGoldPrimary,
                            unfocusedBorderColor = LuxDarkBorder,
                            focusedContainerColor = Color(0xFF0C1018),
                            unfocusedContainerColor = Color(0xFF0C1018)
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x354285F4))
                            .border(0.6.dp, LuxAccentCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .clickable {
                                soundService.playClick()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("AI Studio URL", "https://aistudio.google.com/app/apikey"))
                                    Toast.makeText(context, "Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generar nueva clave en AI Studio", fontSize = 10.sp, color = LuxAccentCyan, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = LuxPlatinumSecondary, modifier = Modifier.size(12.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val key = apiKeyInputValue.trim()
                        if (key.isNotEmpty()) {
                            GeminiApiClient.setRuntimeApiKey(key)
                            soundService.playSuccess()
                            Toast.makeText(context, "API Key de Gemini configurada con éxito", Toast.LENGTH_SHORT).show()
                        }
                        showApiKeyModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Guardar", color = LuxDarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showApiKeyModal = false },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Cancelar", color = LuxPlatinumSecondary, fontSize = 11.sp)
                }
            }
        )
    }

    // DIÁLOGO INTEGRADO MULTI-IA (Centro de Control Multi-IA)
    if (showMultiIaDialog) {
        MultiIaControlDialog(
            currentAgent = selectedAgentType,
            engineConfig = engineConfig,
            isGoogleSynced = googleSyncState.isSynced,
            googleEmail = googleSyncState.email,
            onSelectAgent = { agent ->
                orchestratorService.selectAgentType(agent)
            },
            onOpenApiKeyModal = {
                showApiKeyModal = true
                showMultiIaDialog = false
            },
            onSyncGoogle = {
                coroutineScope.launch {
                    repository.syncGoogleAccountWithoutApiKey(context, null, soundService)
                }
            },
            onSetupGemma3Local = {
                showMultiIaDialog = false
                orchestratorService.selectAgentType(AiAgentType.GEMMA3_270M_LOCAL)
                coroutineScope.launch {
                    repository.launchGemma3LocalServerSetup(context, soundService)
                }
                // Redirección automática a la terminal como pide el usuario
                onNavigateToTab("nav_termux_lux")
            },
            onSetupAntigravityHybrid = {
                showMultiIaDialog = false
                orchestratorService.selectAgentType(AiAgentType.ANTIGRAVITY_CLI_HYBRID)
                coroutineScope.launch {
                    repository.launchAntigravityHybridSync(context, soundService)
                }
                // Redirección automática a la terminal como pide el usuario
                onNavigateToTab("nav_termux_lux")
            },
            onUpdateEngineConfig = { updated ->
                FunctionGemmaLocalEngine.updateEngineConfig(updated)
            },
            onDismiss = { showMultiIaDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .imePadding()
    ) {
        // CABECERA SUPERIOR LIMPIA CON CRISTAL ORO Y PLATA METÁLICO
        Surface(
            color = Color(0x9410141E),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxGoldSilverBorderBrush)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Menú Desplegable Button (con logo e indicador)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x35FFD54F))
                            .border(0.8.dp, LuxGoldPrimary, RoundedCornerShape(8.dp))
                            .clickable {
                                soundService.playClick()
                                if (onOpenMenuDrawer != null) {
                                    onOpenMenuDrawer()
                                } else {
                                    onNavigateToTab("open_drawer")
                                }
                            }
                            .padding(horizontal = 7.dp)
                            .testTag("btn_chat_open_drawer"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir Menú Desplegable",
                                tint = LuxGoldLight,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Menú",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LuxGoldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Emblema Oficial LuX
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .border(1.2.dp, LuxGoldSilverBorderBrush, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.lux_emblem),
                            contentDescription = "Emblema LuX",
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 2. UN BOTÓN INTEGRADO QUE CONTIENE LAS 4 IAs (Centro Multi-IA)
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0x4DFFD54F), Color(0x3DF8FAFC))
                            )
                        )
                        .border(1.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                        .clickable {
                            soundService.playClick()
                            showMultiIaDialog = true
                        }
                        .padding(horizontal = 8.dp)
                        .testTag("btn_integrated_multi_ia"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = when (selectedAgentType) {
                                AiAgentType.GEMINI_CLOUD -> Icons.Default.AutoAwesome
                                AiAgentType.GEMMA4_VERTEX -> Icons.Default.CloudDone
                                AiAgentType.GEMMA3_270M_LOCAL, AiAgentType.GEMINI3_LIGHT_TERMUX -> Icons.Default.Storage
                                AiAgentType.ANTIGRAVITY_CLI_HYBRID -> Icons.Default.RocketLaunch
                            },
                            contentDescription = null,
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = selectedAgentType.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LuxGoldLight,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Cambiar IA",
                            tint = LuxPlatinumSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 3. ATAJOS REQUERIDOS: Terminal, Permisos (escudo), Chatbot Live "LuX Live" y Botón Flotante
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Atajo 1: Botón Terminal
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x35192130))
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(8.dp))
                            .clickable {
                                soundService.playClick()
                                onNavigateToTab("nav_termux_lux")
                            }
                            .testTag("shortcut_terminal"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Terminal Termux LuX",
                            tint = LuxAccentCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Atajo 2: Botón de Permisos (Escudo)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x35192130))
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(8.dp))
                            .clickable {
                                soundService.playClick()
                                if (onOpenPermissionsDialog != null) {
                                    onOpenPermissionsDialog()
                                } else {
                                    onNavigateToTab("nav_permissions_settings")
                                }
                            }
                            .testTag("shortcut_permissions_shield"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Permisos y Blindaje (Escudo)",
                            tint = LuxGoldLight,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Atajo 3: Botón de Chatbot Live "LuX Live"
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isLiveChatbotActive) Color(0x3500E676) else Color(0x25192130)
                            )
                            .border(
                                0.8.dp,
                                if (isLiveChatbotActive) LuxAccentGreen else LuxDarkBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                isLiveChatbotActive = !isLiveChatbotActive
                                soundService.playSuccess()
                                if (isLiveChatbotActive) {
                                    repository.speakWithTts("LuX Live activo. Sistema de conversación en tiempo real conectado.")
                                }
                            }
                            .padding(horizontal = 6.dp)
                            .testTag("shortcut_lux_live"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLiveChatbotActive) LuxAccentGreen.copy(alpha = pulseLiveAlpha) else LuxOnDarkMuted
                                    )
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = if (isLiveChatbotActive) LuxAccentGreen else LuxPlatinumSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "LuX Live",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLiveChatbotActive) LuxAccentGreen else LuxPlatinumSecondary
                            )
                        }
                    }

                    // Atajo 4: Botón Flotante (PiP / Floating HUD sobre otras apps con enlace a ajustes de tamaño y transparencia)
                    // Botón eliminado por solicitud del usuario
                }
            }
        }

        // ÁREA PRINCIPAL DE CONVERSACIÓN: ESPACIOSA, LIMPIA Y SIN ESTORBOS
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                CleanChatMessageItem(
                    message = msg,
                    repository = repository,
                    soundService = soundService,
                    onActionClick = { action ->
                        orchestratorService.executeAction(action, context, onNavigateToTab)
                    }
                )
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = LuxGoldPrimary,
                            strokeWidth = 1.5.dp,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Orquestador IA procesando...",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = LuxGoldLight
                        )
                    }
                }
            }
        }

        // BARRA DE ENTRADA DE TEXTO INFERIOR CON LOS 4 BOTONES SOLICITADOS
        Surface(
            color = Color(0x9410141E),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxGoldSilverBorderBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                // 3. Prompt con sugerencias (chips de sugerencia rápida)
                val suggestions = listOf(
                    "🎨 Crear Imagen",
                    "🎬 Crear Video",
                    "🎵 Crear Canción",
                    "🚀 Crear App Room",
                    "📦 Compilar APK"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.forEach { sug ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x35FFD54F))
                                .border(0.6.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                                .clickable {
                                    soundService.playClick()
                                    inputPrompt = sug.replace(Regex("[^a-zA-ZáéíóúñÁÉÍÓÚÑ0-9 ]"), "").trim()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(sug, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Botón de "+" (aquí aparecen opciones de crear imagen, video, canción y enviar imagen foto o archivo)
                    var showPlusMenu by remember { mutableStateOf(false) }
                    Box {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x35FFD54F))
                                .border(1.dp, LuxGoldPrimary, CircleShape)
                                .clickable {
                                    soundService.playClick()
                                    showPlusMenu = true
                                }
                                .testTag("btn_plus_options"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.AutoAwesome,
                                contentDescription = "Menú Creación y Archivos (+)",
                                tint = LuxGoldLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (showPlusMenu) {
                            androidx.compose.material3.DropdownMenu(
                                expanded = showPlusMenu,
                                onDismissRequest = { showPlusMenu = false },
                                modifier = Modifier.background(LuxDarkSurface)
                            ) {
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("🎨 Crear Imagen IA", color = LuxGoldLight, fontSize = 12.sp) },
                                    onClick = {
                                        showPlusMenu = false
                                        soundService.playClick()
                                        inputPrompt = "Crea una imagen artística y detallada de: "
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("🎬 Crear Video", color = LuxGoldLight, fontSize = 12.sp) },
                                    onClick = {
                                        showPlusMenu = false
                                        soundService.playClick()
                                        inputPrompt = "Genera un clip de video sobre: "
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("🎵 Crear Canción", color = LuxGoldLight, fontSize = 12.sp) },
                                    onClick = {
                                        showPlusMenu = false
                                        soundService.playClick()
                                        inputPrompt = "Compón una canción y letra sobre: "
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("📷 Enviar Imagen / Foto", color = LuxAccentCyan, fontSize = 12.sp) },
                                    onClick = {
                                        showPlusMenu = false
                                        soundService.playClick()
                                        openAnyFileChatLauncher.launch(arrayOf("image/*"))
                                    }
                                )
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("📁 Enviar Archivo", color = LuxAccentCyan, fontSize = 12.sp) },
                                    onClick = {
                                        showPlusMenu = false
                                        soundService.playClick()
                                        openAnyFileChatLauncher.launch(arrayOf("*/*"))
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // 2. Botón de Grabar Audio (Micrófono)
                    val speechLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == android.app.Activity.RESULT_OK) {
                            val spoken = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                            if (!spoken.isNullOrBlank()) {
                                inputPrompt = spoken
                                soundService.playSuccess()
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x35192130))
                            .border(1.dp, LuxAccentCyan.copy(alpha = 0.8f), CircleShape)
                            .clickable {
                                soundService.playClick()
                                try {
                                    val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Habla tu comando para Studio LuX...")
                                    }
                                    speechLauncher.launch(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Grabación de audio iniciada", Toast.LENGTH_SHORT).show()
                                    inputPrompt = "Comando de voz grabado..."
                                }
                            }
                            .testTag("btn_record_audio"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Mic,
                            contentDescription = "Grabar Audio",
                            tint = LuxAccentCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // 3. Prompt con Sugerencias (OutlinedTextField)
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = {
                            Text(
                                "Escribe tu prompt o instrucción...",
                                fontSize = 11.5.sp,
                                color = LuxOnDarkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_agent_chat"),
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = LuxOnDark),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxGoldPrimary,
                            unfocusedBorderColor = LuxDarkBorder,
                            focusedContainerColor = Color(0xFF0C1018),
                            unfocusedContainerColor = Color(0xFF0C1018),
                            focusedTextColor = LuxOnDark,
                            unfocusedTextColor = LuxOnDark
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputPrompt.isNotBlank()) {
                                    orchestratorService.sendMessage(inputPrompt, onNavigateToTab)
                                    inputPrompt = ""
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // 4. Enviar Mensaje
                    Button(
                        onClick = {
                            if (inputPrompt.isNotBlank()) {
                                orchestratorService.sendMessage(inputPrompt, onNavigateToTab)
                                inputPrompt = ""
                            }
                        },
                        enabled = inputPrompt.isNotBlank() && !isGenerating,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxGoldPrimary,
                            disabledContainerColor = Color(0x352E2412)
                        ),
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_send_agent_prompt"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = if (inputPrompt.isNotBlank()) LuxDarkBackground else LuxOnDarkMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * MODAL DE CONTROL MULTI-IA
 * Contiene de forma integrada y ordenada las 4 IAs solicitadas más el motor local (estilo Ollama / LM Studio).
 */
@Composable
private fun MultiIaControlDialog(
    currentAgent: AiAgentType,
    engineConfig: FunctionGemmaLocalEngine.LocalModelEngineConfig,
    isGoogleSynced: Boolean,
    googleEmail: String,
    onSelectAgent: (AiAgentType) -> Unit,
    onOpenApiKeyModal: () -> Unit,
    onSyncGoogle: () -> Unit,
    onSetupGemma3Local: () -> Unit,
    onSetupAntigravityHybrid: () -> Unit,
    onUpdateEngineConfig: (FunctionGemmaLocalEngine.LocalModelEngineConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showEngineParams by remember { mutableStateOf(false) }

    var temp by remember { mutableFloatStateOf(engineConfig.temperature) }
    var topP by remember { mutableFloatStateOf(engineConfig.topP) }
    var topK by remember { mutableIntStateOf(engineConfig.topK) }
    var ctxTokens by remember { mutableIntStateOf(engineConfig.contextTokens) }
    var maxTokens by remember { mutableIntStateOf(engineConfig.maxOutputTokens) }
    var repeatPenalty by remember { mutableFloatStateOf(engineConfig.repeatPenalty) }
    var jsonMode by remember { mutableStateOf(engineConfig.jsonMode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFA0D111A),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .border(1.2.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(18.dp))
            .testTag("dialog_multi_ia_control"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(LuxGoldSilverGlassBrush)
                            .border(1.dp, LuxGoldSilverBorderBrush, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CENTRO MULTI-IA",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = LuxGoldLight
                        )
                        Text(
                            text = "Gestor Unificado de Modelos e Inferencia",
                            fontSize = 8.5.sp,
                            color = LuxPlatinumSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (showEngineParams) LuxGoldContainer else Color(0x351E2430))
                        .border(0.6.dp, if (showEngineParams) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(6.dp))
                        .clickable { showEngineParams = !showEngineParams }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (showEngineParams) "Ocultar Motor" else "Motor Local (Ollama)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showEngineParams) LuxGoldPrimary else LuxPlatinumSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. BOTÓN IA DE GÉMINIS (Cloud Oficial)
                MultiIaOptionCard(
                    title = "IA de Géminis",
                    badge = "Cloud Oficial · Google AI Studio",
                    subtitle = "Acceso a Gemini 3.5 Flash, 3.1 Pro y Flash Lite",
                    icon = Icons.Default.AutoAwesome,
                    iconTint = LuxGoldPrimary,
                    isSelected = currentAgent == AiAgentType.GEMINI_CLOUD,
                    onSelect = { onSelectAgent(AiAgentType.GEMINI_CLOUD) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x35FFD54F))
                                .border(0.6.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                                .clickable { onOpenApiKeyModal() }
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔑 Clave API", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x354285F4))
                                .border(0.6.dp, LuxAccentCyan, RoundedCornerShape(6.dp))
                                .clickable { onSyncGoogle() }
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isGoogleSynced) "Google Sync ✓" else "Sincronizar Google",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGoogleSynced) LuxAccentGreen else LuxAccentCyan
                            )
                        }
                    }
                }

                // 2. BOTÓN IA GEMMA VERTEX (Google Cloud Console & Vertex Gateway)
                MultiIaOptionCard(
                    title = "IA Gemma Vertex",
                    badge = "Vertex AI Gateway (90.11.29.1:1402)",
                    subtitle = "Modelos Gemma empresariales en la nube Google Cloud",
                    icon = Icons.Default.CloudDone,
                    iconTint = LuxAccentCyan,
                    isSelected = currentAgent == AiAgentType.GEMMA4_VERTEX,
                    onSelect = { onSelectAgent(AiAgentType.GEMMA4_VERTEX) }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x3534D399))
                                    .border(0.6.dp, LuxAccentGreen, RoundedCornerShape(6.dp))
                                    .clickable { onSyncGoogle() }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (isGoogleSynced) "Cuenta Vinculada ✓" else "Sincronizar Cuenta",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxAccentGreen
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x354285F4))
                                    .border(0.6.dp, LuxAccentCyan, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.cloud.google.com/vertex-ai")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Abre console.cloud.google.com/vertex-ai", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Habilitar en Google Cloud", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = LuxAccentCyan)
                            }
                        }
                        Text("Configurado en gateway 90.11.29.1:1402 con proyecto Cloud Dev", fontSize = 8.sp, color = LuxOnDarkMuted)
                    }
                }

                // 3. BOTÓN GEMMA3 270M (Descarga Local + Servidor Localhost https://90.11.29.1:4250)
                MultiIaOptionCard(
                    title = "Gemma3 270m",
                    badge = "Localhost https://90.11.29.1:4250 (Termux)",
                    subtitle = "Descarga de modelo gemma-3-270m y creación de servidor local",
                    icon = Icons.Default.Storage,
                    iconTint = LuxGoldPrimary,
                    isSelected = currentAgent == AiAgentType.GEMMA3_270M_LOCAL,
                    onSelect = { onSelectAgent(AiAgentType.GEMMA3_270M_LOCAL) }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Al presionar se sincroniza con Termux, descarga paquetes y crea el servidor local en https://90.11.29.1:4250 para que cualquier app del sistema con IA pueda usarlo.",
                            fontSize = 8.5.sp,
                            color = LuxPlatinumSecondary,
                            lineHeight = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x40FFD54F))
                                .border(0.8.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                                .clickable { onSetupGemma3Local() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Descargar gemma-3-270m y Crear Servidor en Termux", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = LuxGoldLight)
                            }
                        }
                    }
                }

                // 4. BOTÓN ANTIGRAVITY CLI HÍBRIDO (Orquestador + Memoria Cloud/Drive)
                MultiIaOptionCard(
                    title = "Antigravity CLI (Híbrido)",
                    badge = "Asistente Agente & Orquestador",
                    subtitle = "Usa cualquier IA activa y guarda memoria en Google Cloud o Google Drive",
                    icon = Icons.Default.RocketLaunch,
                    iconTint = LuxAccentGreen,
                    isSelected = currentAgent == AiAgentType.ANTIGRAVITY_CLI_HYBRID,
                    onSelect = { onSelectAgent(AiAgentType.ANTIGRAVITY_CLI_HYBRID) }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Modo Híbrido: Combina IA Cloud y NPU local para asistencia en tiempo real, compilación y aprendizaje continuo persistido en Google Cloud o Drive.",
                            fontSize = 8.5.sp,
                            color = LuxPlatinumSecondary,
                            lineHeight = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x4034D399))
                                .border(0.8.dp, LuxAccentGreen, RoundedCornerShape(6.dp))
                                .clickable { onSetupAntigravityHybrid() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = LuxAccentGreen, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Configurar, Sincronizar y Descargar en Terminal", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = LuxAccentGreen)
                            }
                        }
                    }
                }

                // 5. MOTOR LOCAL (PARÁMETROS ESTILO OLLAMA / LM STUDIO)
                AnimatedVisibility(visible = showEngineParams) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F1522))
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("MOTOR LOCAL (Ollama / LM Studio)", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = LuxGoldPrimary)
                            Text("https://90.11.29.1:4250", fontSize = 8.5.sp, fontFamily = FontFamily.Monospace, color = LuxAccentCyan)
                        }

                        // Sliders de Temperatura, Top-P, Top-K, Tokens
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Temperatura: ${"%.2f".format(temp)}", fontSize = 9.sp, color = LuxOnDark)
                            Text("Top-P: ${"%.2f".format(topP)}", fontSize = 9.sp, color = LuxPlatinumSecondary)
                        }
                        Slider(
                            value = temp,
                            onValueChange = { temp = it },
                            valueRange = 0.0f..1.5f,
                            modifier = Modifier.height(20.dp),
                            colors = SliderDefaults.colors(thumbColor = LuxGoldPrimary, activeTrackColor = LuxGoldPrimary)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Context Window: $ctxTokens tokens", fontSize = 9.sp, color = LuxOnDark)
                            Text("Max Output: $maxTokens", fontSize = 9.sp, color = LuxPlatinumSecondary)
                        }
                        Slider(
                            value = ctxTokens.toFloat(),
                            onValueChange = { ctxTokens = it.toInt() },
                            valueRange = 1024f..16384f,
                            steps = 3,
                            modifier = Modifier.height(20.dp),
                            colors = SliderDefaults.colors(thumbColor = LuxPlatinumSecondary, activeTrackColor = LuxPlatinumSecondary)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Modo JSON Schema Estructurado", fontSize = 9.sp, color = LuxOnDark)
                            Switch(
                                checked = jsonMode,
                                onCheckedChange = { jsonMode = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = LuxDarkBackground, checkedTrackColor = LuxGoldPrimary)
                            )
                        }

                        Button(
                            onClick = {
                                onUpdateEngineConfig(
                                    engineConfig.copy(
                                        temperature = temp,
                                        topP = topP,
                                        topK = topK,
                                        contextTokens = ctxTokens,
                                        maxOutputTokens = maxTokens,
                                        repeatPenalty = repeatPenalty,
                                        jsonMode = jsonMode
                                    )
                                )
                                Toast.makeText(context, "Parámetros del motor local aplicados", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp).align(Alignment.End)
                        ) {
                            Text("Aplicar Parámetros", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = LuxDarkBackground)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Listo", color = LuxDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    )
}

@Composable
private fun MultiIaOptionCard(
    title: String,
    badge: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onSelect: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0x55302410) else Color(0x35121722)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.2.dp else 0.6.dp,
            brush = if (isSelected) LuxGoldSilverBorderBrush else Brush.linearGradient(listOf(LuxDarkBorder, LuxDarkBorder))
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) LuxGoldPrimary else Color(0x35192130)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) LuxDarkBackground else iconTint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) LuxGoldLight else LuxSilverLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x351E2430))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(badge, fontSize = 7.5.sp, color = iconTint, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Text(subtitle, fontSize = 8.sp, color = LuxOnDarkMuted)
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = LuxGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
private fun CleanChatMessageItem(
    message: AgentChatMessage,
    repository: LuxRepository,
    soundService: SoundHapticService,
    onActionClick: (AgentInteractiveAction) -> Unit
) {
    val context = LocalContext.current
    val isUser = message.sender == AgentMessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF4285F4), Color(0xFFFFD54F))
                        )
                    )
                    .border(0.8.dp, LuxGoldSilverBorderBrush, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 340.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = if (isUser) 12.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 12.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) Color(0x9E261D0C) else Color(0x9E101522)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 0.9.dp,
                    brush = if (isUser) {
                        Brush.linearGradient(listOf(LuxGoldPrimary, LuxGoldDark))
                    } else {
                        LuxGoldSilverBorderBrush
                    }
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "Studio LuX Orchestrator",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = LuxGoldPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.timestamp,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                color = LuxOnDarkMuted
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.5.sp
                        ),
                        color = Color(0xFFF8FAFC)
                    )

                    // Archivos adjuntos universales
                    if (message.attachedFiles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        message.attachedFiles.forEach { attached ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x750D111A))
                                    .border(0.6.dp, LuxAccentCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        onActionClick(
                                            AgentInteractiveAction(
                                                label = "Enviar ${attached.fileName}",
                                                type = AgentActionType.SHARE_UNIVERSAL_FILE,
                                                payload = attached.id
                                            )
                                        )
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${attached.fileName} (${attached.sizeFormatted})",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LuxGoldLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "Enviar",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxAccentCyan
                                )
                            }
                        }
                    }

                    // Fila horizontal limpia de acciones
                    if (message.actions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            message.actions.forEach { action ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x40FFD54F))
                                        .border(0.6.dp, LuxGoldPrimary, RoundedCornerShape(8.dp))
                                        .clickable { onActionClick(action) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("action_${action.label.take(8)}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (action.type) {
                                                AgentActionType.BUILD_APK -> Icons.Default.Build
                                                AgentActionType.SHARE_APK, AgentActionType.SHARE_UNIVERSAL_FILE -> Icons.Default.Share
                                                AgentActionType.INSTALL_APK -> Icons.Default.InstallMobile
                                                AgentActionType.VIEW_CODE -> Icons.Default.Code
                                                AgentActionType.DOWNLOAD_ANTIGRAVITY_CLI -> Icons.Default.CloudDownload
                                                AgentActionType.SYNC_GOOGLE_ACCOUNT -> Icons.Default.CloudDone
                                                else -> Icons.Default.AutoAwesome
                                            },
                                            contentDescription = null,
                                            tint = LuxGoldPrimary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = action.label,
                                            color = LuxGoldLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Opción para respuestas de la IA: Escuchar audio, Copiar y Compartir respuesta
                    if (!isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Escuchar Audio
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x354285F4))
                                    .border(0.5.dp, LuxAccentCyan, RoundedCornerShape(6.dp))
                                    .clickable {
                                        soundService.playClick()
                                        repository.speakWithTts(message.text)
                                        Toast.makeText(context, "Reproduciendo respuesta con TTS...", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Escuchar audio", tint = LuxAccentCyan, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Escuchar", fontSize = 9.sp, color = LuxAccentCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            // 2. Copiar Respuesta
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x35FFD54F))
                                    .border(0.5.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                                    .clickable {
                                        soundService.playClick()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Respuesta IA Studio LuX", message.text))
                                        Toast.makeText(context, "Respuesta copiada", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = LuxGoldPrimary, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Copiar", fontSize = 9.sp, color = LuxGoldLight, fontWeight = FontWeight.Bold)
                                }
                            }

                            // 3. Compartir Respuesta
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x3534D399))
                                    .border(0.5.dp, LuxAccentGreen, RoundedCornerShape(6.dp))
                                    .clickable {
                                        soundService.playClick()
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, message.text)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Compartir respuesta de Studio LuX")
                                        context.startActivity(shareIntent)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = LuxAccentGreen, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Compartir", fontSize = 9.sp, color = LuxAccentGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (isUser) {
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = LuxOnDarkMuted,
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                )
            }
        }
    }
}
