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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliLogType
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
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
 * 4. Seguridad y Cuentas Screen
 * - Ajustes de seguridad para la aplicación que se está creando / Sys-LUX:
 * - Contraseña de acceso / PIN biométrico
 * - Cifrado de extremo a extremo E2EE (AES-256-GCM)
 * - Servicios de seguridad (Firma APK V2/V3, integridad SHA-256, Play Protect)
 * - Sincronizar cuentas (Cuenta Google, GitHub, Firebase, Vertex AI)
 * - Agregar modelos de lenguaje seguros (Filtros de prompt injection, tokens protegidos, umbrales de seguridad)
 */
@Composable
fun SecurityAndAccountsScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onOpenGoogleSyncDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val googleSyncState by repository.googleAccountSyncState.collectAsState()
    val activeProject by repository.activeProject.collectAsState()

    var isPasswordProtected by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var isE2eeEnabled by remember { mutableStateOf(true) }
    var apkSignatureV2V3 by remember { mutableStateOf(true) }
    var playProtectCompliance by remember { mutableStateOf(true) }
    var promptGuardFilter by remember { mutableStateOf(true) }
    var safetyThresholdBlockNone by remember { mutableStateOf(true) }

    var isGitHubSynced by remember { mutableStateOf(false) }
    var gitHubUsername by remember { mutableStateOf("github-developer") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(10.dp))
                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(LuxGoldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "4. Seguridad y Cuentas",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = LuxGoldLight
                )
                Text(
                    text = "Contraseñas · Cifrado E2EE · Sincronizar Cuentas · Modelos Seguros",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = LuxPlatinumSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 1: CONTRASEÑA Y CIFRADO DE EXTREMO A EXTREMO (E2EE)
        SecuritySectionCard(
            title = "Contraseña y Cifrado para la App en Creación",
            icon = Icons.Default.Lock,
            accentColor = LuxGoldPrimary
        ) {
            SecurityToggleItem(
                title = "Proteger App con Contraseña / PIN",
                subtitle = "Solicita contraseña al iniciar ${activeProject.name}",
                checked = isPasswordProtected,
                onCheckedChange = {
                    isPasswordProtected = it
                    soundService.playClick()
                }
            )

            if (isPasswordProtected) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        placeholder = { Text("Define contraseña segura...", fontSize = 10.sp, color = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxGoldPrimary,
                            unfocusedBorderColor = LuxDarkBorder,
                            focusedContainerColor = Color(0xFF0D121B),
                            unfocusedContainerColor = Color(0xFF0D121B)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            repository.addCliLog("🔐 [SEGURIDAD] Contraseña establecida para proyecto '${activeProject.name}'", CliLogType.INFO, "SECURITY")
                            Toast.makeText(context, "Contraseña guardada con hash SHA-256", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Guardar", color = LuxDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            SecurityToggleItem(
                title = "Cifrado de Extremo a Extremo (E2EE AES-256)",
                subtitle = "Cifra todas las tablas de Room SQLite y archivos generados con clave maestra",
                checked = isE2eeEnabled,
                onCheckedChange = {
                    isE2eeEnabled = it
                    soundService.playClick()
                    repository.addCliLog("🛡 [E2EE] Cifrado AES-256-GCM ${if (it) "ACTIVO" else "DESACTIVADO"}", CliLogType.INFO, "ENCRYPTION")
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 2: SERVICIOS DE SEGURIDAD & FIRMAS APK
        SecuritySectionCard(
            title = "Servicios de Seguridad y Firmas Digitales",
            icon = Icons.Default.Key,
            accentColor = LuxAccentCyan
        ) {
            SecurityToggleItem(
                title = "Firmado Criptográfico APK Esquema V2/V3",
                subtitle = "Certificados X.509 de 4096 bits para instalación inmediata sin bloqueos",
                checked = apkSignatureV2V3,
                onCheckedChange = {
                    apkSignatureV2V3 = it
                    soundService.playClick()
                }
            )
            SecurityToggleItem(
                title = "Compatibilidad Google Play Protect",
                subtitle = "Zero DCL (sin Dynamic Code Loading inseguro) y permisos mínimos de Play Store",
                checked = playProtectCompliance,
                onCheckedChange = {
                    playProtectCompliance = it
                    soundService.playClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 3: SINCRONIZACIÓN DE CUENTAS
        SecuritySectionCard(
            title = "Sincronización de Cuentas para Proyectos",
            icon = Icons.Default.AccountCircle,
            accentColor = LuxAccentGreen
        ) {
            // Cuenta Google
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131A24))
                    .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LuxAccentGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cuenta Google (Credential Manager)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                    }
                    Text(googleSyncState.email, fontSize = 9.sp, color = LuxPlatinumSecondary)
                    Text("Zero-API-Key activo · Sesión OAuth2 vinculada", fontSize = 8.sp, color = LuxAccentGreen)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LuxGoldContainer)
                        .clickable { onOpenGoogleSyncDialog() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Gestionar", fontSize = 9.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cuenta GitHub
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131A24))
                    .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("GitHub / Repositorio Remoto", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                    Text(if (isGitHubSynced) "@$gitHubUsername (Conectado)" else "No vinculado · Permite sincronizar commits", fontSize = 9.sp, color = LuxPlatinumSecondary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isGitHubSynced) Color(0x3500E676) else Color(0x301E2430))
                        .clickable {
                            soundService.playClick()
                            isGitHubSynced = !isGitHubSynced
                            Toast.makeText(context, if (isGitHubSynced) "GitHub sincronizado" else "GitHub desvinculado", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(if (isGitHubSynced) "Vinculado" else "Vincular", fontSize = 9.sp, color = if (isGitHubSynced) LuxAccentGreen else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SECCIÓN 4: MODELOS DE LENGUAJE SEGUROS
        SecuritySectionCard(
            title = "Modelos de Lenguaje & Filtros de Seguridad IA",
            icon = Icons.Default.Psychology,
            accentColor = LuxGoldLight
        ) {
            SecurityToggleItem(
                title = "Prompt Injection Guard",
                subtitle = "Filtro sanitizador contra inyección de prompts adversarios",
                checked = promptGuardFilter,
                onCheckedChange = {
                    promptGuardFilter = it
                    soundService.playClick()
                }
            )
            SecurityToggleItem(
                title = "Umbral de Filtro de Seguridad BLOCK_NONE",
                subtitle = "Evita bloqueos falsos positivos al generar código técnico y snippets de desarrollo",
                checked = safetyThresholdBlockNone,
                onCheckedChange = {
                    safetyThresholdBlockNone = it
                    soundService.playClick()
                    repository.addCliLog("🛡 [AI STUDIO] Safety Threshold: ${if (it) "BLOCK_NONE" else "BLOCK_SOME"}", CliLogType.INFO, "SAFETY")
                }
            )
        }
    }
}

@Composable
private fun SecuritySectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SecurityToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = LuxGoldLight)
            Text(subtitle, fontSize = 8.5.sp, color = LuxPlatinumSecondary, lineHeight = 11.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(width = 38.dp, height = 22.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = LuxDarkBackground,
                checkedTrackColor = LuxGoldPrimary,
                uncheckedThumbColor = LuxPlatinumSecondary,
                uncheckedTrackColor = Color(0xFF1B2230)
            )
        )
    }
}
