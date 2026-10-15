package com.example.ui.components

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material.icons.filled.Sync
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
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

/**
 * Diálogo de Sincronización de Cuenta Google (Credential Manager + OAuth2 Bridge)
 * para autenticar directamente la cuenta de Google del usuario y EVITAR necesitar una Google API Key manual.
 */
@Composable
fun GoogleAccountSyncDialog(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val syncState by repository.googleAccountSyncState.collectAsState()
    val isSyncing by repository.isGoogleSyncing.collectAsState()
    var emailInput by remember(syncState.email) { mutableStateOf(syncState.email) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .border(1.4.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(18.dp))
            .testTag("dialog_google_account_sync"),
        containerColor = Color(0xF2101520),
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4285F4), LuxGoldPrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Sincronizar Cuenta Google",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        ),
                        color = LuxGoldLight
                    )
                    Text(
                        text = "Evita requerir Google API Key · OAuth2 & Credential Manager",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = LuxPlatinumSecondary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Tarjeta de Estado Actual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (syncState.isSynced) Color(0x40102A1D) else Color(0x402A1A10)
                        )
                        .border(
                            0.9.dp,
                            if (syncState.isSynced) LuxAccentGreen.copy(alpha = 0.7f) else LuxGoldPrimary.copy(alpha = 0.7f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (syncState.isSynced) Icons.Default.CheckCircle else Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = if (syncState.isSynced) LuxAccentGreen else LuxGoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (syncState.isSynced) "CUENTA GOOGLE SINCRONIZADA" else "SINCRONIZACIÓN PENDIENTE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    ),
                                    color = if (syncState.isSynced) LuxAccentGreen else LuxGoldLight
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33FFD54F))
                                    .border(0.6.dp, LuxGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.KeyOff,
                                        contentDescription = null,
                                        tint = LuxGoldPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "SIN API KEY",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = LuxGoldLight
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Cuenta activa: ${syncState.email}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = LuxOnDark
                        )
                        Text(
                            text = "Proveedor: ${syncState.authProvider}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = LuxPlatinumSecondary
                        )
                        Text(
                            text = "Token OAuth: ${syncState.sessionTokenPreview}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp
                            ),
                            color = LuxAccentCyan
                        )
                    }
                }

                // Campo para cuenta Google personalizada o selección directa
                Text(
                    text = "Cuenta de Google vinculada a IA Studios & Compilador:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    ),
                    color = LuxGoldLight
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text("tu.cuenta@gmail.com", fontSize = 11.sp, color = LuxOnDarkMuted) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LuxGoldPrimary,
                        unfocusedBorderColor = LuxDarkBorder,
                        focusedTextColor = LuxOnDark,
                        unfocusedTextColor = LuxOnDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_google_sync_email")
                )

                // Botón Principal: Sincronizar con Google Credential Manager (Evitar API Key)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val updated = repository.syncGoogleAccountWithoutApiKey(
                                context = context,
                                customEmail = emailInput,
                                soundService = soundService
                            )
                            Toast.makeText(
                                context,
                                "✔ Cuenta Google '${updated.email}' sincronizada (Modo Sin API Key activo)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4285F4), LuxGoldPrimary)
                            ),
                            RoundedCornerShape(10.dp)
                        )
                        .testTag("btn_confirm_google_account_sync")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sincronizando con Google Identity...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sincronizar Cuenta Google (Sin API Key)",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Explicación técnica de Zero-API-Key
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF141926))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = LuxAccentCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Al sincronizar tu cuenta Google, todas las consultas de IA Studios, Auto-DevOS y Antigravity CLI se autentican automáticamente sin pedir Google API Key.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, lineHeight = 13.sp),
                        color = LuxPlatinumSecondary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        repository.syncGoogleAccountWithoutApiKey(
                            context = context,
                            customEmail = emailInput,
                            soundService = soundService
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("btn_save_and_close_google_sync")
            ) {
                Text("Listo", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Cerrar", color = LuxOnDarkMuted, fontSize = 11.sp)
            }
        }
    )
}
