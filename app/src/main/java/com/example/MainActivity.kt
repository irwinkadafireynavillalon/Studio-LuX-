package com.example

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AiOrchestratorService
import com.example.data.engine.LuxPriorityKernelService
import com.example.data.models.LuxScreenDestination
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.components.DevOsChatbotDock
import com.example.ui.components.GoogleAccountSyncDialog
import com.example.ui.components.LuxHeader
import com.example.ui.components.LuxInterfaceSettingsDialog
import com.example.ui.components.LuxNavigationDrawerContent
import com.example.ui.components.LuxPermissionsDialog
import com.example.ui.screens.AiOrchestratorScreen
import com.example.ui.screens.AndroidStudioScreen
import com.example.ui.screens.ApkBuilderScreen
import com.example.ui.screens.LuxAnimatedIntroScreen
import com.example.ui.screens.LuxSettingsScreen
import com.example.ui.screens.PermissionsAndSettingsScreen
import com.example.ui.screens.SandboxMemoryScreen
import com.example.ui.screens.SecurityAndAccountsScreen
import com.example.ui.screens.SystemAppsScreen
import com.example.ui.screens.TermuxLuxScreen
import com.example.ui.screens.TestingAndCorrectionScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.LUXTheme
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

/**
 * Pestañas rápidas en la barra superior (compatibilidad ergonómica directa)
 */
enum class LuxNavTab(
    val title: String,
    val icon: ImageVector,
    val tag: String
) {
    ORCHESTRATOR("IA Studios", Icons.Default.AutoAwesome, "tab_orchestrator"),
    STUDIO("Workspace", Icons.Default.Dashboard, "tab_studio"),
    CLI("Terminal", Icons.Default.Terminal, "tab_cli"),
    BUILDER("Compilador APK", Icons.Default.Android, "tab_builder")
}

class MainActivity : ComponentActivity() {

    private val repository = LuxRepository()
    private lateinit var soundService: SoundHapticService
    private var ttsService: com.example.data.engine.LuxTtsService? = null
    private lateinit var notificationManager: com.example.data.engine.LuxNotificationManager
    private lateinit var orchestratorService: AiOrchestratorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LuxPriorityKernelService.applyRealtimeProcessPriority()

        soundService = SoundHapticService(applicationContext)
        notificationManager = com.example.data.engine.LuxNotificationManager(applicationContext, soundService)
        val roomDb = com.example.data.db.LuxRoomDatabase.getInstance(applicationContext)

        repository.notificationManager = notificationManager
        repository.roomDatabase = roomDb

        ttsService = com.example.data.engine.LuxTtsService(applicationContext) { text, type, tag ->
            repository.addCliLog(text, type, tag)
        }
        repository.ttsService = ttsService
        orchestratorService = AiOrchestratorService(repository, soundService, applicationContext)

        // Inicializar caché de archivos fuente en Room y sincronización silenciosa de Cuenta Google
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            repository.projects.value.forEach { proj ->
                repository.cacheProjectFilesToRoom(proj)
            }
            repository.googleAccountSyncManager.attemptSilentGoogleSync(this@MainActivity)
        }

        setContent {
            LUXTheme {
                LuxAppRoot(
                    repository = repository,
                    soundService = soundService,
                    notificationManager = notificationManager,
                    orchestratorService = orchestratorService,
                    onEnterPipMode = { enterFloatingPipOverlay() }
                )
            }
        }
    }

    private fun enterFloatingPipOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(params)
            } catch (_: Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsService?.shutdown()
    }
}

@Composable
fun LuxAppRoot(
    repository: LuxRepository,
    soundService: SoundHapticService,
    notificationManager: com.example.data.engine.LuxNotificationManager,
    orchestratorService: AiOrchestratorService,
    onEnterPipMode: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("lux_devos_prefs", android.content.Context.MODE_PRIVATE) }
    // Estado de Inicio Animado de la Aplicación (Splash / Intro al abrirla por primera vez)
    var isIntroShowing by remember { mutableStateOf(sharedPrefs.getBoolean("is_first_run", true)) }

    // Destino Principal según el Menú Desplegado (Botones e Interfaces)
    var currentDestination by remember { mutableStateOf(LuxScreenDestination.STUDIO_IA) }
    var currentTab by remember { mutableStateOf(LuxNavTab.ORCHESTRATOR) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showInterfaceSettingsDialog by remember { mutableStateOf(false) }
    var showGoogleSyncDialog by remember { mutableStateOf(false) }
    var showAndroidStudioSuite by remember { mutableStateOf(false) }
    var showLiveChatbotOverlay by remember { mutableStateOf(false) }

    val projects by repository.projects.collectAsState()
    val activeProject by repository.activeProject.collectAsState()
    val isServerActive by repository.isLocalServerRunning.collectAsState()
    val latencyMs by repository.serverLatencyMs.collectAsState()
    val isBuilding by repository.isBuilding.collectAsState()
    val currentBuildStep by repository.currentBuildStep.collectAsState()
    val isFloatingHudEnabled by LuxPriorityKernelService.isInAppFloatingHudEnabled.collectAsState()
    val allocatedMemoryMb by LuxPriorityKernelService.allocatedMemoryMb.collectAsState()
    val luxVersionName by repository.luxVersionName.collectAsState()
    val interfaceSettings by repository.interfaceSettings.collectAsState()
    val googleSyncState by repository.googleAccountSyncState.collectAsState()

    // 0. PANTALLA DE INICIO ANIMADA CON PERMISOS Y CHEQUEO DE AGENTES AL ABRIR POR PRIMERA VEZ
    if (isIntroShowing) {
        LuxAnimatedIntroScreen(
            versionName = luxVersionName,
            soundService = soundService,
            repository = repository,
            onStartApp = {
                soundService.playSuccess()
                sharedPrefs.edit().putBoolean("is_first_run", false).apply()
                isIntroShowing = false
            }
        )
        return
    }

    // Modal de Permisos
    if (showPermissionsDialog) {
        LuxPermissionsDialog(
            onDismissRequest = { showPermissionsDialog = false },
            notificationManager = notificationManager
        )
    }

    // Modal de Ajustes de Interfaz
    if (showInterfaceSettingsDialog) {
        LuxInterfaceSettingsDialog(
            currentSettings = interfaceSettings,
            onUpdateSettings = { updated -> repository.updateInterfaceSettings(updated) },
            onDismiss = { showInterfaceSettingsDialog = false }
        )
    }

    // Modal de Sincronización Google Account
    if (showGoogleSyncDialog) {
        GoogleAccountSyncDialog(
            repository = repository,
            soundService = soundService,
            onDismiss = { showGoogleSyncDialog = false }
        )
    }

    // Manejo de botón Atrás del sistema Android
    BackHandler(enabled = drawerState.isOpen || showAndroidStudioSuite || currentDestination != LuxScreenDestination.STUDIO_IA) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (showAndroidStudioSuite) {
            showAndroidStudioSuite = false
        } else {
            currentDestination = LuxScreenDestination.STUDIO_IA
            currentTab = LuxNavTab.ORCHESTRATOR
        }
    }

    val backgroundDrawableRes = if (interfaceSettings.backgroundImageKey == "lux_icon") {
        R.drawable.lux_icon_1791488984626
    } else {
        R.drawable.lux_emblem
    }

    // MENÚ DESPLEGADO (MODAL NAVIGATION DRAWER)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Transparent,
                drawerContentColor = LuxGoldLight
            ) {
                LuxNavigationDrawerContent(
                    currentDestination = currentDestination,
                    versionName = luxVersionName,
                    onSelectDestination = { dest ->
                        soundService.playClick()
                        showAndroidStudioSuite = false
                        currentDestination = dest
                        // Sincronizar pestaña superior si aplica
                        when (dest) {
                            LuxScreenDestination.STUDIO_IA -> currentTab = LuxNavTab.ORCHESTRATOR
                            LuxScreenDestination.TERMUX_LUX -> currentTab = LuxNavTab.CLI
                            LuxScreenDestination.WORKSPACE -> currentTab = LuxNavTab.STUDIO
                            LuxScreenDestination.BUILDER -> currentTab = LuxNavTab.BUILDER
                            else -> {}
                        }
                        coroutineScope.launch { drawerState.close() }
                    },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LuxDarkBackground)
                .testTag("lux_root_background_box"),
            contentAlignment = Alignment.Center
        ) {
            // 1. Imagen de Fondo de LUX a Pantalla Completa
            Image(
                painter = painterResource(id = backgroundDrawableRes),
                contentDescription = "Imagen de Fondo Imperial LUX Oro y Plata",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(interfaceSettings.backgroundImageAlpha)
                    .testTag("lux_background_image")
            )

            // 2. Velo Atmosférico Transparente
            if (interfaceSettings.showBackgroundVignette) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x880B0D14),
                                    Color(0x40261E0B),
                                    Color(0x451A2230),
                                    Color(0x99080A10)
                                )
                            )
                        )
                )
            }

            // 3. Estructura Principal Scaffold con Menú Desplegado y Pestañas
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp),
                containerColor = Color.Transparent,
                topBar = {
                    val context = LocalContext.current
                    LuxHeader(
                        isServerActive = isServerActive,
                        latencyMs = latencyMs,
                        versionName = luxVersionName,
                        selectedTab = currentTab,
                        onOpenMenuDrawer = {
                            soundService.playClick()
                            coroutineScope.launch { drawerState.open() }
                        },
                        onSelectTab = { tab ->
                            showAndroidStudioSuite = false
                            soundService.playClick()
                            currentTab = tab
                            when (tab) {
                                LuxNavTab.ORCHESTRATOR -> currentDestination = LuxScreenDestination.STUDIO_IA
                                LuxNavTab.STUDIO -> currentDestination = LuxScreenDestination.WORKSPACE
                                LuxNavTab.CLI -> currentDestination = LuxScreenDestination.TERMUX_LUX
                                LuxNavTab.BUILDER -> currentDestination = LuxScreenDestination.BUILDER
                            }
                        },
                        isGoogleAccountSynced = googleSyncState.isSynced,
                        syncedGoogleEmail = googleSyncState.email,
                        onOpenGoogleSyncDialog = {
                            soundService.playClick()
                            showGoogleSyncDialog = true
                        },
                        onQuickDownloadAntigravityCli = {
                            coroutineScope.launch {
                                val st = repository.downloadAndInstallAntigravityCli(context, soundService)
                                android.widget.Toast.makeText(
                                    context,
                                    "✔ ${st.packageFileName} (${st.sizeFormatted}) descargado y activo",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onOpenPermissionsPanel = { showPermissionsDialog = true },
                        onShareApk = {
                            soundService.playSuccess()
                            repository.shareCurrentLuxAppApk(context)
                        },
                        onNavigateToOrchestrator = {
                            showAndroidStudioSuite = false
                            currentDestination = LuxScreenDestination.STUDIO_IA
                            currentTab = LuxNavTab.ORCHESTRATOR
                        },
                        onAutoEvolveAndUpdate = {
                            coroutineScope.launch {
                                repository.runFullAutonomousEvolutionCycle(soundService)
                                android.widget.Toast.makeText(
                                    context,
                                    "Auto-Corregido, Auto-Mejorado y Actualizado a LUX v${repository.luxVersionName.value}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onOpenInterfaceSettings = {
                            soundService.playClick()
                            showInterfaceSettingsDialog = true
                        }
                    )
                },
                bottomBar = {
                    // Chatbot Dock en vivo en pantallas donde se requiera soporte rápido
                    val showBottomChatDock = !showAndroidStudioSuite &&
                        (currentDestination == LuxScreenDestination.WORKSPACE || currentDestination == LuxScreenDestination.BUILDER)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        AnimatedVisibility(visible = showBottomChatDock) {
                            DevOsChatbotDock(
                                orchestratorService = orchestratorService,
                                onNavigateToTab = { tabTag ->
                                    when (tabTag) {
                                        "tab_builder", "nav_builder" -> {
                                            currentTab = LuxNavTab.BUILDER
                                            currentDestination = LuxScreenDestination.BUILDER
                                        }
                                        "tab_cli", "nav_termux_lux" -> {
                                            currentTab = LuxNavTab.CLI
                                            currentDestination = LuxScreenDestination.TERMUX_LUX
                                        }
                                        "tab_studio", "nav_workspace" -> {
                                            currentTab = LuxNavTab.STUDIO
                                            currentDestination = LuxScreenDestination.WORKSPACE
                                        }
                                        else -> {
                                            currentTab = LuxNavTab.ORCHESTRATOR
                                            currentDestination = LuxScreenDestination.STUDIO_IA
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (showAndroidStudioSuite) {
                        AndroidStudioScreen(
                            repository = repository,
                            activeProject = activeProject,
                            soundService = soundService,
                            onNavigateBack = { showAndroidStudioSuite = false },
                            onNavigateToBuilder = {
                                showAndroidStudioSuite = false
                                currentDestination = LuxScreenDestination.BUILDER
                                currentTab = LuxNavTab.BUILDER
                            },
                            onNavigateToCli = {
                                showAndroidStudioSuite = false
                                currentDestination = LuxScreenDestination.TERMUX_LUX
                                currentTab = LuxNavTab.CLI
                            },
                            onNavigateToShareApk = {
                                showAndroidStudioSuite = false
                                currentDestination = LuxScreenDestination.BUILDER
                            }
                        )
                    } else {
                        // NAVEGACIÓN MODULAR ENTRE LAS 8 INTERFACES DEL MENÚ DESPLEGADO
                        when (currentDestination) {
                            // 1. LuX Studio IA (Google AI Studio + Android Studio & Agentes)
                            LuxScreenDestination.STUDIO_IA -> AiOrchestratorScreen(
                                orchestratorService = orchestratorService,
                                repository = repository,
                                soundService = soundService,
                                onNavigateToTab = { route ->
                                    when (route) {
                                        "nav_termux_lux", "tab_cli" -> {
                                            currentDestination = LuxScreenDestination.TERMUX_LUX
                                            currentTab = LuxNavTab.CLI
                                        }
                                        "nav_permissions_settings" -> currentDestination = LuxScreenDestination.PERMISSIONS_SETTINGS
                                        "nav_security_accounts" -> currentDestination = LuxScreenDestination.SECURITY_ACCOUNTS
                                        "nav_testing_autocorrection" -> currentDestination = LuxScreenDestination.TESTING_AUTOCORRECTION
                                        "nav_sandbox_memory" -> currentDestination = LuxScreenDestination.SANDBOX_MEMORY
                                        "nav_workspace", "tab_studio" -> {
                                            currentDestination = LuxScreenDestination.WORKSPACE
                                            currentTab = LuxNavTab.STUDIO
                                        }
                                        "nav_lux_settings" -> currentDestination = LuxScreenDestination.LUX_SETTINGS
                                        "nav_system_apps" -> currentDestination = LuxScreenDestination.SYSTEM_APPS
                                        "nav_builder", "tab_builder" -> {
                                            currentDestination = LuxScreenDestination.BUILDER
                                            currentTab = LuxNavTab.BUILDER
                                        }
                                        "open_drawer" -> {
                                            coroutineScope.launch { drawerState.open() }
                                        }
                                        else -> currentDestination = LuxScreenDestination.STUDIO_IA
                                    }
                                },
                                onOpenGoogleSyncDialog = { showGoogleSyncDialog = true },
                                onOpenMenuDrawer = { coroutineScope.launch { drawerState.open() } },
                                onOpenPermissionsDialog = { showPermissionsDialog = true },
                                onOpenInterfaceSettingsDialog = { showInterfaceSettingsDialog = true }
                            )

                            // 2. Termux LuX (Terminal amplia, Teclado libre, Teclas auxiliares, Antigravity CLI)
                            LuxScreenDestination.TERMUX_LUX -> TermuxLuxScreen(
                                repository = repository,
                                soundService = soundService,
                                orchestratorService = orchestratorService
                            )

                            // 3. Permisos y Ajustes (Batería, Permisos, Paletas, Temas, Vinculaciones)
                            LuxScreenDestination.PERMISSIONS_SETTINGS -> PermissionsAndSettingsScreen(
                                repository = repository,
                                soundService = soundService
                            )

                            // 4. Seguridad y Cuentas (Contraseñas, Cifrado E2EE, Firmas APK, Modelos Seguros)
                            LuxScreenDestination.SECURITY_ACCOUNTS -> SecurityAndAccountsScreen(
                                repository = repository,
                                soundService = soundService,
                                onOpenGoogleSyncDialog = { showGoogleSyncDialog = true }
                            )

                            // 5. Prueba & Auto-Corrección (Diagnóstico AST, Motores Offline, Compilar y Combinar)
                            LuxScreenDestination.TESTING_AUTOCORRECTION -> TestingAndCorrectionScreen(
                                repository = repository,
                                soundService = soundService,
                                onNavigateToBuilder = {
                                    currentDestination = LuxScreenDestination.BUILDER
                                    currentTab = LuxNavTab.BUILDER
                                }
                            )

                            // 6.1 Memoria Caja de Arena (Optimizador RAM, Bóveda Multimedia, Lector */*)
                            LuxScreenDestination.SANDBOX_MEMORY -> SandboxMemoryScreen(
                                repository = repository,
                                soundService = soundService
                            )

                            // 6. Workspace (Editor, Árbol de archivos, Snippets Compose, Compilación)
                            LuxScreenDestination.WORKSPACE -> WorkspaceScreen(
                                repository = repository,
                                soundService = soundService,
                                onNavigateToBuilder = {
                                    currentDestination = LuxScreenDestination.BUILDER
                                    currentTab = LuxNavTab.BUILDER
                                },
                                onNavigateToCli = {
                                    currentDestination = LuxScreenDestination.TERMUX_LUX
                                    currentTab = LuxNavTab.CLI
                                }
                            )

                            // 7. Ajustes LuX (Configuración de Sys-LUX, Temas, API Key manual, Tiempos OTA)
                            LuxScreenDestination.LUX_SETTINGS -> LuxSettingsScreen(
                                repository = repository,
                                soundService = soundService,
                                onOpenGoogleSyncDialog = { showGoogleSyncDialog = true }
                            )

                            // 8. LuX App Sistema (Gestión e inhabilitación de apps vía Termux)
                            LuxScreenDestination.SYSTEM_APPS -> SystemAppsScreen(
                                repository = repository,
                                soundService = soundService,
                                onNavigateToTermux = {
                                    currentDestination = LuxScreenDestination.TERMUX_LUX
                                    currentTab = LuxNavTab.CLI
                                }
                            )

                            // Compilador APK Nativo
                            LuxScreenDestination.BUILDER -> ApkBuilderScreen(
                                repository = repository,
                                projects = projects,
                                activeProject = activeProject,
                                isBuilding = isBuilding,
                                currentBuildStep = currentBuildStep,
                                soundService = soundService
                            )
                        }
                    }

                    // Botón Flotante para Chatbot en Vivo (Disponible cuando no estamos en la pantalla principal de chat)
                    if (currentDestination != LuxScreenDestination.STUDIO_IA) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 12.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xE6101624))
                                .border(1.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(20.dp))
                                .clickable {
                                    soundService.playClick()
                                    currentDestination = LuxScreenDestination.STUDIO_IA
                                    currentTab = LuxNavTab.ORCHESTRATOR
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("fab_live_chatbot_shortcut"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(LuxAccentGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(Icons.Default.Chat, contentDescription = "Chatbot IA en Vivo", tint = LuxGoldLight, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Chatbot LuX",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxGoldLight
                                )
                            }
                        }
                    }

                    // Mini-HUD Flotante no intrusivo con Cristal Oro y Plata cuando el modo "Sobre otras aplicaciones" está activo
                    if (isFloatingHudEnabled) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xB3101520))
                                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(16.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("floating_priority_hud"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LuxAccentGreen)
                            )
                            Text(
                                text = "PRIORIDAD MÁX · ${allocatedMemoryMb}MB",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = LuxGoldLight
                            )
                            if (onEnterPipMode != null) {
                                Icon(
                                    imageVector = Icons.Default.PictureInPictureAlt,
                                    contentDescription = "Ventana flotante PiP sobre otras apps",
                                    tint = LuxAccentCyan,
                                    modifier = Modifier
                                        .size(13.dp)
                                        .clickable { onEnterPipMode() }
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Ocultar Mini-HUD",
                                tint = LuxPlatinumSecondary,
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable { LuxPriorityKernelService.toggleInAppFloatingHud(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}
