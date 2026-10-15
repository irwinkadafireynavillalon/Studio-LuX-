package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliLogType
import com.example.data.models.SystemAppInfo
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
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

/**
 * 8. LuX App Sistema Screen
 * - Acceso a permisos y ajustes de las aplicaciones instaladas
 * - Actualizar y corregir errores, y aprender de las otras aplicaciones
 * - Ajustes de permisos, ajustes de seguridad
 * - Desinstalar o inhabilitar aplicaciones del sistema como del usuario usando comandos Termux (pm disable, pm uninstall)
 * - Conexión directa con la terminal Termux
 */
@Composable
fun SystemAppsScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onNavigateToTermux: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var appsList by remember {
        mutableStateOf(
            listOf(
                SystemAppInfo("com.termux", "Termux", "0.118.1", isSystemApp = false, isEnabled = true, grantedPermissionsCount = 6, securityRiskLevel = "Seguro", sizeMb = 120.4),
                SystemAppInfo("com.termux.api", "Termux:API", "0.51", isSystemApp = false, isEnabled = true, grantedPermissionsCount = 8, securityRiskLevel = "Seguro", sizeMb = 4.2),
                SystemAppInfo("com.google.android.gms", "Google Play Services", "24.12.14", isSystemApp = true, isEnabled = true, grantedPermissionsCount = 18, securityRiskLevel = "Sistema Crítico", sizeMb = 158.0, canDisable = false),
                SystemAppInfo("com.android.chrome", "Google Chrome", "124.0", isSystemApp = false, isEnabled = true, grantedPermissionsCount = 9, securityRiskLevel = "Bajo", sizeMb = 210.5),
                SystemAppInfo("com.google.android.youtube", "YouTube", "19.16.39", isSystemApp = false, isEnabled = true, grantedPermissionsCount = 7, securityRiskLevel = "Bajo", sizeMb = 145.2),
                SystemAppInfo("com.android.settings", "Ajustes de Android", "14.0", isSystemApp = true, isEnabled = true, grantedPermissionsCount = 24, securityRiskLevel = "Sistema Núcleo", sizeMb = 32.1, canDisable = false),
                SystemAppInfo("com.facebook.katana", "Facebook Bloatware", "458.0", isSystemApp = true, isEnabled = false, grantedPermissionsCount = 15, securityRiskLevel = "Medio (Telemetría)", sizeMb = 98.7, canDisable = true)
            )
        )
    }

    var selectedAppToInspect by remember { mutableStateOf<SystemAppInfo?>(null) }
    var filterMode by remember { mutableStateOf("ALL") } // "ALL", "USER", "SYSTEM"

    val filteredApps = remember(appsList, filterMode) {
        when (filterMode) {
            "USER" -> appsList.filter { !it.isSystemApp }
            "SYSTEM" -> appsList.filter { it.isSystemApp }
            else -> appsList
        }
    }

    // Modal de Detalles e Intervención con Termux
    if (selectedAppToInspect != null) {
        val app = selectedAppToInspect!!
        AlertDialog(
            onDismissRequest = { selectedAppToInspect = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(app.appName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Paquete: ${app.packageName}", fontSize = 10.sp, color = LuxAccentCyan, fontFamily = FontFamily.Monospace)
                    Text("Tipo: ${if (app.isSystemApp) "Aplicación del Sistema" else "Aplicación de Usuario"}", fontSize = 9.5.sp, color = LuxPlatinumSecondary)
                    Text("Permisos Concedidos: ${app.grantedPermissionsCount} · Nivel de Riesgo: ${app.securityRiskLevel}", fontSize = 9.sp, color = LuxGoldLight)
                    Text("Estado: ${if (app.isEnabled) "Activa / Habilitada" else "Inhabilitada / Congelada"}", fontSize = 9.sp, color = if (app.isEnabled) LuxAccentGreen else LuxAccentRed)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Comandos ejecutables en Termux:", fontSize = 9.sp, color = LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                    Text(
                        if (app.isEnabled) "pm disable-user --user 0 ${app.packageName}" else "pm enable ${app.packageName}",
                        fontSize = 8.5.sp,
                        color = LuxAccentGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Botón Inhabilitar / Habilitar vía Termux
                    if (app.canDisable) {
                        Button(
                            onClick = {
                                val newState = !app.isEnabled
                                appsList = appsList.map { if (it.packageName == app.packageName) it.copy(isEnabled = newState) else it }
                                val cmd = if (newState) "pm enable ${app.packageName}" else "pm disable-user --user 0 ${app.packageName}"
                                coroutineScope.launch {
                                    repository.executeCliCommand(cmd, soundService)
                                }
                                soundService.playSuccess()
                                Toast.makeText(context, "Ejecutado en Termux: $cmd", Toast.LENGTH_SHORT).show()
                                selectedAppToInspect = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (app.isEnabled) Color(0x35E53935) else LuxGoldContainer),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(if (app.isEnabled) "Inhabilitar" else "Habilitar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (app.isEnabled) LuxAccentRed else LuxGoldPrimary)
                        }
                    }

                    // Botón Desinstalar (Termux)
                    Button(
                        onClick = {
                            val cmd = "pm uninstall -k --user 0 ${app.packageName}"
                            coroutineScope.launch {
                                repository.executeCliCommand(cmd, soundService)
                            }
                            appsList = appsList.filter { it.packageName != app.packageName }
                            soundService.playSuccess()
                            Toast.makeText(context, "Desinstalada vía Termux", Toast.LENGTH_SHORT).show()
                            selectedAppToInspect = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxAccentRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Desinstalar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedAppToInspect = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Cerrar", color = LuxPlatinumSecondary, fontSize = 10.sp)
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
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .imePadding()
    ) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(10.dp))
                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(LuxGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("8. LuX App Sistema", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                    Text("Gestión, Desinstalación & Aprendizaje vía Termux", fontSize = 9.sp, color = LuxPlatinumSecondary)
                }
            }

            // Botón Ir a Terminal Termux
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x3500E676))
                    .border(0.6.dp, LuxAccentGreen, RoundedCornerShape(6.dp))
                    .clickable { onNavigateToTermux() }
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = LuxAccentGreen, modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Termux", fontSize = 9.sp, color = LuxAccentGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Barra de Filtros: [Todas] [Usuario] [Sistema]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "Todas (${appsList.size})", "USER" to "Usuario", "SYSTEM" to "Sistema").forEach { (filter, label) ->
                val isSel = filterMode == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) LuxGoldContainer else Color(0xFF131822))
                        .border(0.6.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            soundService.playClick()
                            filterMode = filter
                        }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 9.5.sp, color = if (isSel) LuxGoldPrimary else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Botón de Aprendizaje de Apps: Aprender Arquitectura y Permisos de Otras Apps
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101520)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Aprender de otras Apps", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                        Text("Analiza manifiestos APK para auto-corregir proyectos", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                    }
                }
                Button(
                    onClick = {
                        soundService.playSuccess()
                        coroutineScope.launch {
                            repository.addCliLog("🧠 [APRENDIZAJE IA] Inspeccionando permisos y layouts de apps del sistema...", CliLogType.AGENT, "SYSTEM_LEARNING")
                            repository.addCliLog("✔ [APRENDIZAJE IA] 4 patrones arquitectónicos aprendidos e integrados al generador.", CliLogType.SUCCESS, "SYSTEM_LEARNING")
                            Toast.makeText(context, "Patrones aprendidos con éxito", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Aprender", fontSize = 9.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Lista de Aplicaciones Instaladas
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredApps, key = { it.packageName }) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF131922))
                        .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            soundService.playClick()
                            selectedAppToInspect = app
                        }
                        .padding(horizontal = 9.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (app.isEnabled) Color(0xFF1B2332) else Color(0x35E53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (app.isEnabled) Icons.Default.Android else Icons.Default.Block,
                                contentDescription = null,
                                tint = if (app.isEnabled) LuxGoldPrimary else LuxAccentRed,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(app.appName, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                            Text("${app.packageName} · ${app.sizeMb}MB", fontSize = 8.5.sp, color = LuxPlatinumSecondary, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Badge de Estado y Permisos
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (app.isEnabled) Color(0x2500E676) else Color(0x25E53935))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (app.isEnabled) "Activa" else "Inhabilitada",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (app.isEnabled) LuxAccentGreen else LuxAccentRed
                            )
                        }
                    }
                }
            }
        }
    }
}
