package com.example.ui.components

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.engine.LuxNotificationManager
import com.example.data.engine.LuxPriorityKernelService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxDarkSurfaceElevated
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LuxPermissionsDialog(
    onDismissRequest: () -> Unit,
    notificationManager: LuxNotificationManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Lista completa de permisos de tiempo de ejecución del dispositivo
    val systemPermissions = remember {
        val list = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(Manifest.permission.BLUETOOTH_CONNECT)
            list.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
            list.add(Manifest.permission.READ_MEDIA_IMAGES)
            list.add(Manifest.permission.READ_MEDIA_VIDEO)
            list.add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        list
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = systemPermissions)

    val isOverlayActive by LuxPriorityKernelService.isInAppFloatingHudEnabled.collectAsState()
    val allocatedMemoryMb by LuxPriorityKernelService.allocatedMemoryMb.collectAsState()
    val isHighPriorityActive by LuxPriorityKernelService.isHighPriorityActive.collectAsState()

    var canDrawOverlaySystem by remember { mutableStateOf(LuxPriorityKernelService.canDrawOverlays(context)) }
    var isBatteryUnrestricted by remember { mutableStateOf(LuxPriorityKernelService.isIgnoringBatteryOptimizations(context)) }
    var canInstallPackages by remember { mutableStateOf(LuxPriorityKernelService.canRequestPackageInstalls(context)) }

    // Permisos configurables para inyectar en los APKs compilados por LUX
    val apkManifestPermissions = remember {
        mutableStateMapOf(
            "android.permission.SYSTEM_ALERT_WINDOW" to true,
            "android.permission.FOREGROUND_SERVICE" to true,
            "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" to true,
            "android.permission.REQUEST_INSTALL_PACKAGES" to true,
            "android.permission.INTERNET" to true,
            "android.permission.ACCESS_NETWORK_STATE" to true,
            "android.permission.CAMERA" to true,
            "android.permission.RECORD_AUDIO" to true,
            "android.permission.ACCESS_FINE_LOCATION" to true,
            "android.permission.BLUETOOTH_CONNECT" to true,
            "android.permission.POST_NOTIFICATIONS" to true,
            "android.permission.VIBRATE" to true,
            "android.permission.READ_MEDIA_IMAGES" to true
        )
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 640.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(LuxDarkBackground)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(LuxGoldPrimary, LuxDarkBorder)
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
                .testTag("lux_permissions_control_panel")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header compacto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(LuxGoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Permisos, Prioridades y Superposición",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = LuxGoldLight
                            )
                            Text(
                                text = "Control Total del Dispositivo · Sobre Otras Apps · 500 MB Pool",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                color = LuxPlatinumSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(28.dp)
                            .background(LuxDarkSurfaceElevated, CircleShape)
                            .testTag("btn_close_permissions_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = LuxOnDarkMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // SECTION 0: SUPERPOSICIÓN SOBRE OTRAS APPS, PRIORIDAD MÁXIMA Y MEMORIA 500MB
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PRIORIDAD DE EJECUCIÓN, OVERLAY & CAPACIDAD 500 MB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = LuxGoldPrimary
                        )

                        // 1. Funcionar sobre otras aplicaciones (SYSTEM_ALERT_WINDOW + Floating Mini-HUD)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = LuxAccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Funcionar sobre otras aplicaciones",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = LuxOnDark
                                    )
                                    Text(
                                        text = if (isOverlayActive || canDrawOverlaySystem) "Activo: Mini-HUD flotante y ventana sobre otras apps" else "Permite mostrar consola flotante sobre cualquier app",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = LuxOnDarkMuted
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    val active = LuxPriorityKernelService.toggleSystemOverlay(context)
                                    canDrawOverlaySystem = LuxPriorityKernelService.canDrawOverlays(context)
                                    Toast.makeText(
                                        context,
                                        if (active) "Superposición LUX activada sobre aplicaciones" else "Superposición LUX minimizada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOverlayActive) LuxAccentGreen else LuxGoldContainer
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("btn_toggle_overlay_permission")
                            ) {
                                Text(
                                    text = if (isOverlayActive) "Activo" else "Activar",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOverlayActive) LuxDarkBackground else LuxGoldPrimary
                                )
                            }
                        }

                        // 2. Prioridad de Procesamiento y Sin Restricción de Batería
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = LuxGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Prioridad Máxima en Primer y Segundo Plano",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = LuxOnDark
                                    )
                                    Text(
                                        text = "URGENT_DISPLAY · Sin límite de batería ni suspensión",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = LuxOnDarkMuted
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    LuxPriorityKernelService.applyRealtimeProcessPriority()
                                    if (!isBatteryUnrestricted) {
                                        LuxPriorityKernelService.requestUnrestrictedBatteryPriority(context)
                                    }
                                    isBatteryUnrestricted = LuxPriorityKernelService.isIgnoringBatteryOptimizations(context)
                                    Toast.makeText(
                                        context,
                                        "Prioridad máxima del Kernel y exención de batería aplicadas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isHighPriorityActive) LuxAccentGreen.copy(alpha = 0.2f) else LuxGoldContainer
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("btn_priority_battery_unrestricted")
                            ) {
                                Text(
                                    text = "Prioridad Máx",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxAccentGreen
                                )
                            }
                        }

                        // 3. Capacidad de Memoria Extendida (500 MB Pool sin restricción)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = LuxAccentGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Capacidad Dedicada (${allocatedMemoryMb} MB LargeHeap)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = LuxOnDark
                                    )
                                    Text(
                                        text = "Memoria desbloqueada hasta 500 MB+ para modelos IA y compilador",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = LuxOnDarkMuted
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF14241B), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, LuxAccentGreen, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val nextMb = if (allocatedMemoryMb == 500) 750 else 500
                                        LuxPriorityKernelService.setAllocatedMemoryMb(nextMb)
                                        Toast.makeText(context, "Pool de memoria ajustado a $nextMb MB", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${allocatedMemoryMb} MB ACTIVO",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxAccentGreen
                                )
                            }
                        }

                        // 4. Instalar paquetes APK directamente
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InstallMobile,
                                    contentDescription = null,
                                    tint = LuxGoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Instalador Directo de APKs (Fuentes desconocidas)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = LuxOnDark
                                    )
                                    Text(
                                        text = "Autoriza instalar APKs generadas por LUX sin bloqueos",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = LuxOnDarkMuted
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    LuxPriorityKernelService.requestInstallPackagesPermission(context)
                                    canInstallPackages = LuxPriorityKernelService.canRequestPackageInstalls(context)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (canInstallPackages) "Autorizado" else "Habilitar",
                                    fontSize = 10.sp,
                                    color = if (canInstallPackages) LuxAccentGreen else LuxGoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // SECTION 1: PERMISOS DEL DISPOSITIVO (ACCOMPANIST)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LuxDarkBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "TODOS LOS PERMISOS DEL DISPOSITIVO (RUNTIME)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = LuxGoldPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        permissionsState.permissions.forEach { perm ->
                            val isGranted = perm.status.isGranted
                            val (icon, title, desc) = when (perm.permission) {
                                Manifest.permission.CAMERA -> Triple(
                                    Icons.Default.CameraAlt,
                                    "Cámara & Visión",
                                    "Escaneo QR, visión multimodal y captura"
                                )
                                Manifest.permission.RECORD_AUDIO -> Triple(
                                    Icons.Default.Mic,
                                    "Micrófono & Voz",
                                    "Dictado por voz para AI Studio y Terminal CLI"
                                )
                                Manifest.permission.POST_NOTIFICATIONS -> Triple(
                                    Icons.Default.Notifications,
                                    "Notificaciones Prioritarias",
                                    "Alertas de compilación APK y estado del Kernel"
                                )
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION -> Triple(
                                    Icons.Default.LocationOn,
                                    "Ubicación (${perm.permission.substringAfterLast('.')})",
                                    "Telemetría geoespacial y servicios locales"
                                )
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN -> Triple(
                                    Icons.Default.Bluetooth,
                                    "Bluetooth (${perm.permission.substringAfterLast('.')})",
                                    "Transferencia directa de APKs a dispositivos cercanos"
                                )
                                else -> Triple(
                                    Icons.Default.Folder,
                                    "Almacenamiento (${perm.permission.substringAfterLast('.')})",
                                    "Lectura/escritura de pesos IA (500MB) y binarios APK"
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isGranted) LuxAccentGreen.copy(alpha = 0.15f) else Color(0xFF281818)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isGranted) LuxAccentGreen else LuxAccentRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = LuxOnDark
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                            color = LuxOnDarkMuted
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isGranted) LuxAccentGreen.copy(alpha = 0.15f) else LuxAccentRed.copy(alpha = 0.15f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isGranted) "ACTIVO" else "CONCEDER",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = if (isGranted) LuxAccentGreen else LuxAccentRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    permissionsState.launchMultiplePermissionRequest()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_request_all_permissions")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = LuxDarkBackground,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Conceder Todos los Permisos",
                                    color = LuxDarkBackground,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    notificationManager.sendTestNotification(
                                        title = "LUX Kernel: Prioridad Máxima Activa",
                                        message = "Todos los permisos, superposición y pool de 500 MB operando al 100%."
                                    )
                                    Toast.makeText(context, "Notificación prioritaria enviada", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("btn_send_test_notification")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = LuxGoldLight,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Probar Alerta",
                                    color = LuxGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // SECTION 2: PERMISOS INYECTADOS EN APKS COMPILADOS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LuxDarkBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "PERMISOS INYECTADOS EN APKS COMPILADOS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = LuxGoldPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        apkManifestPermissions.keys.toList().forEach { permissionName ->
                            val isChecked = apkManifestPermissions[permissionName] == true
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = permissionName.removePrefix("android.permission."),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    color = LuxOnDark
                                )

                                Switch(
                                    checked = isChecked,
                                    onCheckedChange = { newValue ->
                                        apkManifestPermissions[permissionName] = newValue
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = LuxDarkBackground,
                                        checkedTrackColor = LuxGoldPrimary,
                                        uncheckedThumbColor = LuxOnDarkMuted,
                                        uncheckedTrackColor = Color(0xFF1E2430)
                                    ),
                                    modifier = Modifier.testTag("switch_perm_${permissionName.replace(".", "_")}")
                                )
                            }
                        }
                    }
                }

                // Confirm and Close Button (Compact 38.dp)
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = LuxDarkSurfaceElevated),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(LuxGoldPrimary, LuxDarkBorder))
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("btn_save_permissions_panel")
                ) {
                    Text(
                        text = "Guardar Configuración y Cerrar",
                        color = LuxGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
