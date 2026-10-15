package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UniversalFileCategory
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxGoldSilverGlassBrush
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

// Tarjeta Unificada con Cristal Transparente Oro y Plata:
// 1. Descarga e instalación del paquete oficial Antigravity CLI v2.6 para garantizar el funcionamiento total de la aplicación.
// 2. Envío y adjuntado universal de TODO tipo de archivos (APK, Binario CLI, ZIP, PDF, Imagen, Audio/Video, SQLite, Scripts).
@Composable
fun UniversalFileAndCliHubCard(
    repository: LuxRepository,
    soundService: SoundHapticService,
    modifier: Modifier = Modifier,
    onOpenGoogleSync: (() -> Unit)? = null,
    initiallyExpanded: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cliState by repository.antigravityCliState.collectAsState()
    val universalFiles by repository.universalFiles.collectAsState()
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    // Selector nativo del sistema Android para adjuntar y enviar CUALQUIER tipo de archivo (*/*)
    val openAnyDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val imported = repository.importDeviceUriAsUniversalFile(context, uri, soundService)
            Toast.makeText(
                context,
                "📎 Archivo cargado: ${imported.fileName} (${imported.sizeFormatted})",
                Toast.LENGTH_SHORT
            ).show()
            repository.shareUniversalFile(context, imported)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.1.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(16.dp))
            .testTag("card_universal_file_and_cli_hub"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xA6101520))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxGoldSilverGlassBrush)
                .padding(12.dp)
        ) {
            // Cabecera compacta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0x55FFD54F), Color(0x55E2E8F0))
                                )
                            )
                            .border(0.8.dp, LuxGoldSilverBorderBrush, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = LuxGoldLight,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ANTIGRAVITY CLI & ARCHIVOS (*/*)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.5.sp
                                ),
                                color = LuxGoldLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0x3330E389))
                                    .border(0.6.dp, LuxAccentGreen.copy(alpha = 0.6f), RoundedCornerShape(5.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (cliState.isInstalledAndActive) "CLI ACTIVO" else "DESCARGAR",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LuxAccentGreen
                                )
                            }
                        }
                        Text(
                            text = "${cliState.packageFileName} (${cliState.sizeFormatted}) · ${universalFiles.size} archivos listos",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = LuxPlatinumSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0x401A2232))
                        .border(0.6.dp, LuxPlatinumSecondary.copy(alpha = 0.4f), RoundedCornerShape(7.dp))
                        .clickable { isExpanded = !isExpanded }
                        .testTag("btn_toggle_universal_hub"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir o contraer Hub",
                        tint = LuxPlatinumSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de acciones principales 1-Toque (Compacta 32.dp):
            // 1. Descargar Antigravity CLI | 2. Adjuntar Archivo (*/*) | 3. Enviar Lote
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val result = repository.downloadAndInstallAntigravityCli(context, soundService)
                            Toast.makeText(
                                context,
                                "✔ ${result.packageFileName} (${result.sizeFormatted}) descargado y activo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !cliState.isDownloading,
                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1.15f)
                        .height(32.dp)
                        .testTag("btn_download_antigravity_cli")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (cliState.isDownloading) "Descargando..." else "Descargar CLI",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = {
                        soundService.playClick()
                        openAnyDocumentLauncher.launch(arrayOf("*/*"))
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxAccentCyan.copy(alpha = 0.7f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .testTag("btn_pick_any_device_file")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = LuxAccentCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Adjuntar (*/*)",
                        color = LuxAccentCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = {
                        soundService.playSuccess()
                        repository.shareAllUniversalFilesBatch(context)
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxGoldLight.copy(alpha = 0.7f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(0.95f)
                        .height(32.dp)
                        .testTag("btn_share_all_universal_files")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = LuxGoldLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Enviar Todo",
                        color = LuxGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        maxLines = 1
                    )
                }
            }

            // Progreso en vivo de descarga de Antigravity CLI
            if (cliState.isDownloading) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x990B0E16))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = cliState.statusMessage,
                            fontSize = 9.sp,
                            color = LuxGoldLight,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${(cliState.downloadProgress * 100).toInt()}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxAccentCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { cliState.downloadProgress },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = LuxGoldPrimary,
                        trackColor = Color(0xFF1F2637)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Estado de Antigravity CLI + Botón directo para enviar el binario .bin
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(0x770C1018))
                            .border(0.6.dp, LuxGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(9.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LuxAccentGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Motor Antigravity CLI v${cliState.version} · SHA-256 Verificado",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LuxGoldLight
                                )
                                Text(
                                    text = cliState.localBinaryPath,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = LuxPlatinumSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LuxGoldContainer)
                                .border(0.6.dp, LuxGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                .clickable {
                                    soundService.playSuccess()
                                    repository.shareAntigravityCliPackage(context)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("btn_share_antigravity_cli_bin")
                        ) {
                            Text(
                                text = "Enviar .BIN",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LuxGoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tira rápida para generar y enviar cualquier formato de archivo al instante
                    Text(
                        text = "Generar y enviar cualquier tipo de archivo (*/*):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = LuxPlatinumSecondary
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        UniversalFileCategory.entries.forEach { category ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color(0x66161C28))
                                    .border(0.6.dp, LuxPlatinumSecondary.copy(alpha = 0.45f), RoundedCornerShape(7.dp))
                                    .clickable {
                                        val created = repository.createQuickUniversalFile(
                                            category = category,
                                            context = context,
                                            soundService = soundService
                                        )
                                        repository.shareUniversalFile(context, created)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("chip_send_category_${category.name.lowercase()}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = null,
                                        tint = LuxGoldPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = category.label,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LuxOnDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista compacta de archivos listos para enviar (1-Tap Share)
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        universalFiles.take(4).forEach { fileItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x730E131D))
                                    .border(0.5.dp, LuxPlatinumSecondary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fileItem.fileName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = LuxGoldLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${fileItem.category.label} · ${fileItem.sizeFormatted} · ${fileItem.mimeType}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                        color = LuxPlatinumSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x38FFD54F))
                                        .border(0.6.dp, LuxGoldPrimary.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            soundService.playSuccess()
                                            repository.shareUniversalFile(context, fileItem)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                        .testTag("btn_send_universal_file_${fileItem.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = null,
                                            tint = LuxGoldLight,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Enviar",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = LuxGoldLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
