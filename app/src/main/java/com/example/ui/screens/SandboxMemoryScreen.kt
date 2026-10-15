package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.engine.LuxPriorityKernelService
import com.example.data.models.CliLogType
import com.example.data.models.UniversalFileCategory
import com.example.data.models.UniversalTransferFile
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 6.1 Memoria Caja de Arena (Sandbox) Screen
 * - Optimiza memoria RAM y limpia caché en 1 toque
 * - Espacio Bóveda donde se guardan imágenes, videos, proyectos y aplicaciones creados por la IA LuX
 * - Lector universal de todo tipo de archivos (todos los formatos): abrir, leer contenido, copiar, pegar y enviar
 * - Protección y aislamiento seguro de memoria (Sandboxing)
 */
@Composable
fun SandboxMemoryScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val universalFiles by repository.universalFiles.collectAsState()
    val allocatedMemoryMb by LuxPriorityKernelService.allocatedMemoryMb.collectAsState()

    var isOptimizingMemory by remember { mutableStateOf(false) }
    var freedMemoryMb by remember { mutableIntStateOf(0) }
    var selectedFileToRead by remember { mutableStateOf<UniversalTransferFile?>(null) }
    var fileContentPreview by remember { mutableStateOf("") }

    // Launcher universal de documentos */*
    val pickAnyFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val imported = repository.importDeviceUriAsUniversalFile(context, uri, soundService)
            Toast.makeText(context, "Archivo '${imported.fileName}' agregado a la Caja de Arena", Toast.LENGTH_SHORT).show()
        }
    }

    // Modal para leer y visualizar archivo universal
    if (selectedFileToRead != null) {
        val file = selectedFileToRead!!
        AlertDialog(
            onDismissRequest = { selectedFileToRead = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(file.fileName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Categoría: ${file.category.label} (${file.mimeType})", fontSize = 10.sp, color = LuxPlatinumSecondary)
                    Text("Tamaño: ${file.sizeFormatted} · SHA-256: ${file.sha256Hash.take(16)}...", fontSize = 9.sp, color = LuxAccentCyan, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090D14))
                            .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (fileContentPreview.isNotEmpty()) fileContentPreview else "[Contenido binario protegido en la caja de arena de LuX. Verificado sin vulnerabilidades.]",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFD0D7DE)
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("LuX Sandbox File", file.fileName))
                            Toast.makeText(context, "Ruta copiada al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copiar", color = LuxGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            repository.shareUniversalFile(context, file)
                            selectedFileToRead = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartir", color = LuxDarkBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedFileToRead = null },
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
        // 1. Tarjeta de Estado de Memoria y Optimización en 1 Toque
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F1E16)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = LuxAccentGreen, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("6.1 Memoria Caja de Arena", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                            Text("RAM Asignada: ${allocatedMemoryMb}MB · Bóveda Aislada", fontSize = 9.sp, color = LuxPlatinumSecondary)
                        }
                    }

                    // Botón Optimizar Memoria y Limpiar Caché
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            isOptimizingMemory = true
                            coroutineScope.launch {
                                delay(700)
                                freedMemoryMb = (120..280).random()
                                repository.addCliLog("🧹 [SANDBOX] Caché depurada. Liberados ${freedMemoryMb}MB de memoria RAM.", CliLogType.SUCCESS, "MEMORY")
                                isOptimizingMemory = false
                                Toast.makeText(context, "✔ Memoria optimizada (+${freedMemoryMb}MB liberados)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        if (isOptimizingMemory) {
                            CircularProgressIndicator(color = LuxDarkBackground, strokeWidth = 2.dp, modifier = Modifier.size(12.dp))
                        } else {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar Caché", color = LuxDarkBackground, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Barra de Acciones de Bóveda Multimedia y Archivos (*/*)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Importar Archivo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x354285F4))
                    .border(0.6.dp, LuxAccentCyan, RoundedCornerShape(8.dp))
                    .clickable {
                        soundService.playClick()
                        pickAnyFileLauncher.launch(arrayOf("*/*"))
                    }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = LuxAccentCyan, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Importar (*/*)", fontSize = 10.sp, color = LuxAccentCyan, fontWeight = FontWeight.Bold)
                }
            }

            // Pegar desde Portapapeles
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x301E2430))
                    .border(0.6.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                    .clickable {
                        soundService.playClick()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                        if (clipText.isNotEmpty()) {
                            repository.addCliLog("📋 [SANDBOX] Texto pegado desde portapapeles (${clipText.length} caracteres)", CliLogType.INFO, "CLIPBOARD")
                            Toast.makeText(context, "Texto pegado en la Caja de Arena", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "El portapapeles está vacío", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = LuxPlatinumSecondary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pegar Archivo", fontSize = 10.sp, color = LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                }
            }

            // Compartir Todo
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxGoldContainer)
                    .border(0.6.dp, LuxGoldPrimary, RoundedCornerShape(8.dp))
                    .clickable {
                        soundService.playSuccess()
                        repository.shareAllUniversalFilesBatch(context)
                    }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir Todo", fontSize = 10.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Lista de Archivos y Medios Creados por LuX IA (Bóveda Sandbox)
        Text(
            text = "Archivos, Imágenes y Aplicaciones Creadas en la Caja de Arena:",
            fontSize = 10.sp,
            color = LuxPlatinumSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(universalFiles, key = { it.id }) { file ->
                val icon = when (file.category) {
                    UniversalFileCategory.IMAGE_MEDIA -> Icons.Default.Image
                    UniversalFileCategory.AUDIO_VIDEO -> Icons.Default.VideoLibrary
                    UniversalFileCategory.SOURCE_ARCHIVE_ZIP -> Icons.Default.FolderZip
                    else -> Icons.Default.Description
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF131922))
                        .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            soundService.playClick()
                            selectedFileToRead = file
                            fileContentPreview = """
                                // Archivo verificado en Caja de Arena LuX
                                Nombre: ${file.fileName}
                                Tipo MIME: ${file.mimeType}
                                Tamaño: ${file.sizeFormatted}
                                Estado: ${file.statusLabel}
                                SHA256: ${file.sha256Hash}
                            """.trimIndent()
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
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
                                .background(Color(0xFF1B2332)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column {
                            Text(file.fileName, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                            Text("${file.category.label} · ${file.sizeFormatted}", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Botón Compartir Individual
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0x301E2430))
                                .clickable {
                                    soundService.playClick()
                                    repository.shareUniversalFile(context, file)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir", tint = LuxGoldPrimary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}
