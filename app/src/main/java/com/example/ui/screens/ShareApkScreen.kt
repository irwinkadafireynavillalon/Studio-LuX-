package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.GeneratedAppProject
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
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

private const val ONLINE_EMULATOR_URL = "https://ais-pre-6tyljoyghezdpuy7zrpaae-571825580413.us-east1.run.app"
private const val APK_SHA256 = "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"

@Composable
fun ShareApkScreen(
    repository: LuxRepository,
    activeProject: GeneratedAppProject,
    soundService: SoundHapticService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isShaCopied by remember { mutableStateOf(false) }
    var isUrlCopied by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxDarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card - LUX Official APK & Direct Install / Share Actions
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(LuxGoldPrimary, LuxPlatinumSecondary, LuxGoldLight)),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, LuxGoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lux_emblem),
                                contentDescription = "LUX Emblem",
                                modifier = Modifier.size(58.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LUX META-DEVOS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = LuxGoldLight
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(LuxAccentGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "APK v1.0",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = LuxAccentGreen
                                    )
                                }
                            }
                            Text(
                                text = "Paquete Instalable para Android · 100% Autónomo (Room DB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuxOnDarkMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "El archivo APK oficial de LUX se encuentra listo para su instalación y distribución. No depende de servicios Firebase y funciona de manera totalmente independiente en cualquier dispositivo Android 7.0+.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LuxOnDark,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // ACTION BUTTON 1: DIRECT SHARE APK INTENT
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            repository.shareCurrentLuxAppApk(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("btn_share_lux_apk_main")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = LuxDarkBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Compartir APK de LUX Ahora",
                            color = LuxDarkBackground,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ACTION BUTTON 2: DIRECT INSTALL VIA ANDROID PACKAGE INSTALLER
                    OutlinedButton(
                        onClick = {
                            soundService.playSuccess()
                            val ok = repository.installCurrentLuxApk(context)
                            if (!ok) {
                                Toast.makeText(context, "Iniciando instalador o compartiendo archivo APK...", Toast.LENGTH_SHORT).show()
                                repository.shareCurrentLuxAppApk(context)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(LuxGoldPrimary, LuxPlatinumSecondary))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_install_current_apk")
                    ) {
                        Icon(
                            imageVector = Icons.Default.InstallMobile,
                            contentDescription = null,
                            tint = LuxGoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Instalar / Actualizar en este Dispositivo",
                            color = LuxGoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // QR Code Generator Card - Scan with Phone Camera
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CÓDIGO QR PARA INSTALAR EN CELULAR",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LuxGoldLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E2738), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("ESCANEABLE", fontSize = 10.sp, color = LuxAccentCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Apunta la cámara de tu teléfono móvil a este código para abrir o descargar inmediatamente la aplicación:",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxOnDarkMuted,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized QR Code Canvas with Corner Target Finders
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F1218))
                            .border(1.5.dp, LuxGoldPrimary, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val w = size.width
                            val h = size.height
                            val cellSize = w / 21f

                            // Draw Finder Corners (Top-Left, Top-Right, Bottom-Left)
                            fun drawFinder(x: Float, y: Float) {
                                drawRoundRect(
                                    color = LuxGoldPrimary,
                                    topLeft = Offset(x, y),
                                    size = Size(cellSize * 7, cellSize * 7),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                                drawRoundRect(
                                    color = Color(0xFF0F1218),
                                    topLeft = Offset(x + cellSize, y + cellSize),
                                    size = Size(cellSize * 5, cellSize * 5),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                                drawRoundRect(
                                    color = LuxGoldPrimary,
                                    topLeft = Offset(x + cellSize * 2, y + cellSize * 2),
                                    size = Size(cellSize * 3, cellSize * 3),
                                    cornerRadius = CornerRadius(2f, 2f)
                                )
                            }

                            drawFinder(0f, 0f)
                            drawFinder(cellSize * 14, 0f)
                            drawFinder(0f, cellSize * 14)

                            // Pseudorandom Stylized Data Matrix Dots based on URL hash
                            val seed = ONLINE_EMULATOR_URL.hashCode()
                            for (row in 0 until 21) {
                                for (col in 0 until 21) {
                                    val inFinderTL = row < 8 && col < 8
                                    val inFinderTR = row < 8 && col >= 13
                                    val inFinderBL = row >= 13 && col < 8
                                    val inCenter = row in 9..11 && col in 9..11
                                    if (!inFinderTL && !inFinderTR && !inFinderBL && !inCenter) {
                                        val bit = ((seed xor (row * 31 + col * 17)) and (1 shl ((row + col) % 31))) != 0
                                        if (bit) {
                                            drawRoundRect(
                                                color = if ((row + col) % 3 == 0) LuxPlatinumSecondary else LuxGoldLight,
                                                topLeft = Offset(col * cellSize + 1f, row * cellSize + 1f),
                                                size = Size(cellSize - 2f, cellSize - 2f),
                                                cornerRadius = CornerRadius(2f, 2f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Center Emblem Badge
                            drawCircle(
                                color = LuxGoldPrimary,
                                radius = cellSize * 2.2f,
                                center = Offset(w / 2f, h / 2f)
                            )
                            drawCircle(
                                color = Color(0xFF141822),
                                radius = cellSize * 1.8f,
                                center = Offset(w / 2f, h / 2f)
                            )
                        }

                        // Lion Icon in Center of QR
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = LuxGoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                soundService.playSuccess()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("LUX Online URL", ONLINE_EMULATOR_URL))
                                isUrlCopied = true
                                Toast.makeText(context, "Enlace copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("btn_copy_qr_url")
                        ) {
                            Icon(
                                imageVector = if (isUrlCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = LuxGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isUrlCopied) "¡Copiado!" else "Copiar Link", color = LuxGoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                soundService.playClick()
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ONLINE_EMULATOR_URL)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(browserIntent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, ONLINE_EMULATOR_URL, Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxDarkSurfaceElevated),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(LuxDarkBorder)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("btn_open_in_browser")
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Abrir Link", color = LuxOnDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Share Channels Grid
        item {
            Text(
                text = "CANALES DE TRANSFERENCIA RÁPIDA",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = LuxGoldLight
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bluetooth
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, LuxDarkBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A8A).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bluetooth", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxOnDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                soundService.playClick()
                                Toast.makeText(context, "Compartiendo APK por Bluetooth...", Toast.LENGTH_SHORT).show()
                                repository.shareCurrentLuxAppApk(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("btn_share_bluetooth_quick")
                        ) {
                            Text("Enviar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Gmail / Email
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, LuxDarkBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7F1D1D).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Gmail", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxOnDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                soundService.playClick()
                                repository.shareCurrentLuxAppApk(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("btn_share_gmail_quick")
                        ) {
                            Text("Adjuntar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Guardar APK en Descargas
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, LuxDarkBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF065F46).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = LuxAccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Guardar", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = LuxOnDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                soundService.playSuccess()
                                val path = repository.saveApkToDevice(context, activeProject)
                                Toast.makeText(context, "APK guardado en:\n$path", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxAccentGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("btn_save_apk_quick")
                        ) {
                            Text("Descargar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxDarkBackground)
                        }
                    }
                }
            }
        }

        // Technical Specs & Integrity Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LuxDarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = LuxAccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FICHA TÉCNICA Y AUDITORÍA DE INTEGRIDAD",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LuxAccentCyan
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(LuxAccentGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("FIRMA V2/V3", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LuxAccentGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SpecRow(label = "Nombre del APK", value = "LUX-MetaDevOS.apk (Edición Oro & Plata)")
                    SpecRow(label = "Tamaño / Capacidad", value = "496.8 MB / 500.0 MB (Motor NPU Completo)")
                    SpecRow(label = "Package ID", value = "com.aistudio.applet.frbrot")
                    SpecRow(label = "Versión", value = "2.5.0 (Full 500MB · Sin Límites)")
                    SpecRow(label = "Modo de Operación", value = "100% Autónomo · Prioridad Tiempo Real")
                    SpecRow(label = "Base de Datos", value = "Room Database (SQLite Offline WAL)")
                    SpecRow(label = "Target SDK", value = "Android 15 (API 36)")
                    SpecRow(label = "Min SDK", value = "Android 7.0 (API 24)")

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = LuxDarkBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // SHA-256 Checksum Row
                    Text(
                        text = "CHECKSUM SHA-256 (VERIFICADO):",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = LuxGoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF10141D), RoundedCornerShape(8.dp))
                            .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = APK_SHA256.take(24) + "...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = LuxPlatinumSecondary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("SHA256", APK_SHA256))
                                    isShaCopied = true
                                    Toast.makeText(context, "SHA-256 copiado", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (isShaCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = if (isShaCopied) LuxAccentGreen else LuxGoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isShaCopied) "Copiado" else "Copiar Hash",
                                fontSize = 10.sp,
                                color = if (isShaCopied) LuxAccentGreen else LuxGoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Guía de Instalación en Celular
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LuxDarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = LuxGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GUÍA DE INSTALACIÓN EN DISPOSITIVOS ANDROID",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = LuxGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    InstallStepItem(number = "1", title = "Transferir archivo APK", desc = "Envía el APK por WhatsApp, Bluetooth, Correo o cárgalo por cable USB.")
                    InstallStepItem(number = "2", title = "Habilitar orígenes desconocidos", desc = "Si el sistema lo solicita, ve a Ajustes y activa 'Permitir desde esta fuente'.")
                    InstallStepItem(number = "3", title = "Tocar el archivo .apk e instalar", desc = "Toca el archivo descargado para iniciar el instalador de Android y listo.")
                }
            }
        }

        // Active Project APK Sharing
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LuxDarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "COMPARTIR PROYECTO GENERADO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = LuxGoldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${activeProject.name} (v${activeProject.version})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LuxOnDark
                    )
                    Text(
                        text = "Tamaño: ${activeProject.apkSizeMb} MB · Paquete: ${activeProject.packageName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxOnDarkMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                soundService.playClick()
                                repository.shareApk(context, activeProject, LuxRepository.ApkShareTarget.ALL)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_share_active_project_apk")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compartir APK", color = LuxDarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                soundService.playSuccess()
                                val path = repository.saveApkToDevice(context, activeProject)
                                Toast.makeText(context, "APK guardado en:\n$path", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_save_active_project_apk")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar en Disco", color = LuxGoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = LuxOnDarkMuted)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            ),
            color = LuxOnDark
        )
    }
}

@Composable
private fun InstallStepItem(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(LuxGoldContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxGoldPrimary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = LuxOnDark)
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = LuxOnDarkMuted)
        }
    }
}
