package com.example.ui.components

import android.app.Activity
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.LuxDeveloperAuthState
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
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

/**
 * Diálogo de Autenticación de Desarrolladores para Studio LuX con Firebase Auth
 */
@Composable
fun LuxDeveloperAuthDialog(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authManager = remember(context) { repository.getOrCreateFirebaseAuth(context) }
    val authState by authManager.authState.collectAsState()
    val isAuthenticating by authManager.isAuthenticating.collectAsState()

    var emailInput by remember { mutableStateOf("") }
    var tokenInput by remember { mutableStateOf("") }
    var showManualTokenForm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .border(1.2.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(18.dp))
            .testTag("dialog_firebase_developer_auth"),
        containerColor = Color(0xF20F1420),
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(LuxGoldPrimary, Color(0xFFF58220))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = LuxDarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Firebase Auth · Studio LuX",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        ),
                        color = LuxGoldLight
                    )
                    Text(
                        text = "Autenticación segura para desarrolladores",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = LuxPlatinumSecondary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tarjeta de Estado de Sesión
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (authState.isAuthenticated) Color(0x35102E20) else Color(0x35281814)
                        )
                        .border(
                            0.8.dp,
                            if (authState.isAuthenticated) LuxAccentGreen else LuxDarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (authState.isAuthenticated) Icons.Default.CheckCircle else Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = if (authState.isAuthenticated) LuxAccentGreen else LuxGoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (authState.isAuthenticated) "SESIÓN ACTIVA" else "SIN AUTENTICAR",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (authState.isAuthenticated) LuxAccentGreen else LuxGoldPrimary
                                )
                            }
                            Text(
                                text = authState.role,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = LuxPlatinumSecondary
                            )
                        }

                        if (authState.isAuthenticated && authState.email.isNotBlank()) {
                            Text(
                                text = "Desarrollador: ${authState.email}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LuxGoldLight
                            )
                            Text(
                                text = "UID: ${authState.uid} · ${authState.authProvider}",
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = LuxPlatinumSecondary
                            )
                            Text(
                                text = "Último acceso: ${authState.lastLoginFormatted}",
                                fontSize = 8.sp,
                                color = LuxAccentCyan
                            )
                        } else {
                            Text(
                                text = "Inicia sesión con tu Cuenta Google o credencial de desarrollador para vincular tu entorno y sincronizar tus proyectos de Studio LuX.",
                                fontSize = 9.5.sp,
                                color = LuxPlatinumSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                if (!authState.isAuthenticated) {
                    // Botón Principal: Google Sign-In con Firebase Auth
                    Button(
                        onClick = {
                            soundService.playClick()
                            if (context is Activity) {
                                coroutineScope.launch {
                                    val res = authManager?.signInWithGoogle(context)
                                    if (res != null && res.isSuccess) {
                                        soundService.playSuccess()
                                        val email = res.getOrNull()?.email ?: ""
                                        repository.setUserEmail(email)
                                        Toast.makeText(context, "Bienvenido, $email", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        enabled = !isAuthenticating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("btn_firebase_google_sign_in"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxGoldPrimary,
                            disabledContainerColor = Color(0x35FFD54F)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 1.8.dp,
                                color = LuxDarkBackground
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Autenticando...", color = LuxDarkBackground, fontSize = 11.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = LuxDarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Iniciar Sesión con Google (Firebase Auth)",
                                color = LuxDarkBackground,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Botón para desplegar formulario manual de desarrollador
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x251E2432))
                            .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                soundService.playClick()
                                showManualTokenForm = !showManualTokenForm
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (showManualTokenForm) "Ocultar formulario de credencial" else "¿Autenticar con Correo y Token de Desarrollador?",
                            fontSize = 9.sp,
                            color = LuxAccentCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showManualTokenForm) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("tu.correo@dev.io", fontSize = 11.sp, color = LuxOnDarkMuted) },
                            singleLine = true,
                            label = { Text("Correo de Desarrollador", fontSize = 9.5.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_dev_auth_email"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuxGoldPrimary,
                                unfocusedBorderColor = LuxDarkBorder,
                                focusedTextColor = LuxOnDark,
                                unfocusedTextColor = LuxOnDark,
                                focusedContainerColor = Color(0xFF0C1018),
                                unfocusedContainerColor = Color(0xFF0C1018)
                            )
                        )

                        OutlinedTextField(
                            value = tokenInput,
                            onValueChange = { tokenInput = it },
                            placeholder = { Text("dev_tok_sec_...", fontSize = 11.sp, color = LuxOnDarkMuted) },
                            singleLine = true,
                            label = { Text("Token de Seguridad (Opcional)", fontSize = 9.5.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_dev_auth_token"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuxGoldPrimary,
                                unfocusedBorderColor = LuxDarkBorder,
                                focusedTextColor = LuxOnDark,
                                unfocusedTextColor = LuxOnDark,
                                focusedContainerColor = Color(0xFF0C1018),
                                unfocusedContainerColor = Color(0xFF0C1018)
                            )
                        )

                        Button(
                            onClick = {
                                soundService.playClick()
                                val res = authManager?.signInWithDeveloperCredentials(emailInput, tokenInput)
                                if (res != null && res.isSuccess) {
                                    soundService.playSuccess()
                                    repository.setUserEmail(emailInput)
                                    Toast.makeText(context, "Autenticado: $emailInput", Toast.LENGTH_SHORT).show()
                                } else {
                                    soundService.playError()
                                    Toast.makeText(context, res?.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = emailInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(34.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LuxAccentGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Validar y Conectar Credencial", color = LuxDarkBackground, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Botón Cerrar Sesión
                    OutlinedButton(
                        onClick = {
                            soundService.playClick()
                            coroutineScope.launch {
                                authManager?.signOut(context as? Activity)
                                repository.setUserEmail("")
                                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("btn_firebase_sign_out"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxAccentRed)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = LuxAccentRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cerrar Sesión de Desarrollador", color = LuxAccentRed, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
                Text("Listo", color = LuxDarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}
