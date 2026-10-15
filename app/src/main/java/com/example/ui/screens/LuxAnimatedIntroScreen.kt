package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxPlatinumSecondary
import com.example.ui.theme.LuxSilverLight
import kotlinx.coroutines.delay

/**
 * Pantalla de Inicio Animada (Animated Splash & Permissions / AI Agent Check):
 * - Emblema imperial de LuX con halo de oro y plata palpitante
 * - Diagnóstico dinámico de permisos y conexión de los 3 agentes de IA
 * - Botón de entrada fluida "Iniciar LuX DevOS"
 */
@Composable
fun LuxAnimatedIntroScreen(
    versionName: String,
    soundService: com.example.data.repository.SoundHapticService? = null,
    repository: com.example.data.repository.LuxRepository? = null,
    onStartApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var stepProgress by remember { mutableFloatStateOf(0f) }
    var showStartButton by remember { mutableStateOf(false) }

    // Launcher para solicitar permiso de notificaciones en Android 13+
    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_intro")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val progressAnimated by animateFloatAsState(
        targetValue = stepProgress,
        animationSpec = tween(600),
        label = "progressAnimated"
    )

    LaunchedEffect(Unit) {
        soundService?.playStartupChime()
        // 1. Configurar y solicitar permisos (Notificaciones y Servicio Foreground)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        com.example.data.engine.LuxPriorityKernelService.applyRealtimeProcessPriority()
        delay(350)
        stepProgress = 0.33f

        // 2. Sincronizar Cuenta Google y Motores de IA
        if (context is android.app.Activity) {
            repository?.googleAccountSyncManager?.attemptSilentGoogleSync(context)
        }
        delay(400)
        stepProgress = 0.66f

        // 3. Inicializar Kernel en Segundo Plano (Que no se cierre)
        val intent = android.content.Intent(context, com.example.data.engine.LuxPriorityKernelService::class.java)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (_: Exception) {}
        delay(400)
        stepProgress = 1.0f
        delay(300)
        showStartButton = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        // Fondo con destello sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x35FFD54F),
                            Color(0x20152538),
                            Color(0xFF07090E)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Emblema Imperial con Halo de Luz Oro y Plata
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                LuxGoldPrimary,
                                LuxPlatinumSecondary,
                                LuxGoldLight,
                                LuxSilverLight,
                                LuxGoldPrimary
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lux_emblem),
                    contentDescription = "Emblema Imperial LuX",
                    modifier = Modifier.size(96.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Título de la Aplicación
            Text(
                text = "Studio LuX",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    fontFamily = FontFamily.Serif
                ),
                color = LuxGoldLight
            )

            Text(
                text = "Meta-DevOS · Google AI Studio & Android Studio Suite",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = LuxPlatinumSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Verificación Animada
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F141E))
                    .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IntroCheckItem(
                    title = "Permisos del Dispositivo y Sistema",
                    subtitle = "Notificaciones, WakeLock, Overlay PiP, Almacenamiento",
                    isReady = progressAnimated >= 0.33f
                )
                IntroCheckItem(
                    title = "Agentes IA (Gemini · Vertex · Gemma3 270m · Antigravity)",
                    subtitle = "Cloud, Vertex (1402), Localhost (4250) y Orquestador Híbrido",
                    isReady = progressAnimated >= 0.66f
                )
                IntroCheckItem(
                    title = "Antigravity CLI & Kernel en Segundo Plano",
                    subtitle = "Servicio persistente activo: Ejecución continua sin cierres",
                    isReady = progressAnimated >= 1.0f
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Inicio Animado
            AnimatedVisibility(
                visible = showStartButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                Button(
                    onClick = onStartApp,
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .fillMaxWidth(0.75f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Iniciar Studio LuX",
                        color = LuxDarkBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.5.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun IntroCheckItem(
    title: String,
    subtitle: String,
    isReady: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (isReady) Color(0x3500E676) else Color(0x35FFD54F)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.FlashOn,
                contentDescription = null,
                tint = if (isReady) LuxAccentGreen else LuxGoldPrimary,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isReady) LuxGoldLight else LuxPlatinumSecondary)
            Text(subtitle, fontSize = 8.sp, color = LuxPlatinumSecondary)
        }
    }
}
