package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.GemmaModelManager
import com.example.data.models.GemmaModelFile
import com.example.data.models.ModelDownloadStatus

@Composable
fun GemmaModelManagerComponent(
    modelManager: GemmaModelManager,
    modifier: Modifier = Modifier,
    onExecuteCliCommand: ((String) -> Unit)? = null
) {
    val models by modelManager.models.collectAsState()
    val activeModel by modelManager.activeModel.collectAsState()
    val totalStorageUsed by modelManager.totalStorageUsedBytes.collectAsState()

    val cardBg = Color(0xFF0C121B)
    val borderColor = Color(0xFF1E2B3E)
    val promptGreen = Color(0xFF00FF66)
    val promptCyan = Color(0xFF00E5FF)
    val promptAmber = Color(0xFFFFB300)
    val promptRed = Color(0xFFFF3856)
    val textPrimary = Color(0xFFF1F5F9)
    val textMuted = Color(0xFF64748B)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .testTag("gemma_model_manager_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header del Gestor de Modelos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(promptCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Gemma Model Manager",
                            tint = promptCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GESTOR DE MODELOS GEMMA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(promptGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SHA-256 VERIFIED",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = promptGreen
                                )
                            }
                        }
                        Text(
                            text = "Descarga local con FileDownloader y verificación criptográfica para AntigravityCLI",
                            style = MaterialTheme.typography.bodySmall,
                            color = textMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de almacenamiento local
            val storageMb = (totalStorageUsed / (1024.0 * 1024.0)).toInt()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF080D14))
                    .border(1.dp, Color(0xFF162334), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, null, tint = promptCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Almacenamiento Local Ocupado:",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$storageMb MB",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = promptGreen
                    )
                }

                if (activeModel != null) {
                    Text(
                        text = "CLI Activo: ${activeModel?.id}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = promptAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de Modelos Gemma en el catálogo
            models.forEach { model ->
                GemmaModelItemRow(
                    model = model,
                    isActive = model.id == activeModel?.id,
                    onDownload = {
                        modelManager.downloadModel(model.id)
                        onExecuteCliCommand?.invoke("antigravity download ${model.id}")
                    },
                    onVerify = {
                        modelManager.verifyModelIntegrity(model.id)
                        onExecuteCliCommand?.invoke("antigravity verify ${model.id}")
                    },
                    onLoadIntoCli = {
                        modelManager.loadModelIntoCli(model.id)
                        onExecuteCliCommand?.invoke("antigravity load ${model.id}")
                    },
                    onDelete = {
                        modelManager.deleteModel(model.id)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun GemmaModelItemRow(
    model: GemmaModelFile,
    isActive: Boolean,
    onDownload: () -> Unit,
    onVerify: () -> Unit,
    onLoadIntoCli: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isActive) Color(0xFF00FF66).copy(alpha = 0.5f) else Color(0xFF192535)
    val bg = if (isActive) Color(0xFF07140E) else Color(0xFF090E16)
    val promptGreen = Color(0xFF00FF66)
    val promptCyan = Color(0xFF00E5FF)
    val promptAmber = Color(0xFFFFB300)
    val promptRed = Color(0xFFFF3856)
    val textPrimary = Color(0xFFF1F5F9)
    val textMuted = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(10.dp)
            .testTag("model_item_${model.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = model.name,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isActive) promptGreen else textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(promptGreen)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "NPU ON",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
                Text(
                    text = "${model.sizeFormatted} · ${model.quantization} · ${model.targetEngine}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = textMuted
                )
            }

            // Botones de acción según estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (model.downloadStatus) {
                    ModelDownloadStatus.DOWNLOADING -> {
                        Text(
                            text = "${(model.downloadProgress * 100).toInt()}%",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = promptCyan
                        )
                    }

                    ModelDownloadStatus.VERIFYING_SHA256 -> {
                        Text(
                            text = "VERIFICANDO",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = promptAmber
                        )
                    }

                    ModelDownloadStatus.VERIFIED_SUCCESS, ModelDownloadStatus.ACTIVE_CLI -> {
                        if (!isActive) {
                            Button(
                                onClick = onLoadIntoCli,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F261B)),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(promptGreen.copy(alpha = 0.6f))
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Cargar en CLI", fontSize = 10.sp, color = promptGreen, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        IconButton(
                            onClick = onVerify,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Security, "Verificar SHA-256", tint = promptGreen, modifier = Modifier.size(16.dp))
                        }

                        if (!isActive) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = textMuted, modifier = Modifier.size(15.dp))
                            }
                        }
                    }

                    ModelDownloadStatus.CORRUPTED_ERROR -> {
                        Button(
                            onClick = onDownload,
                            colors = ButtonDefaults.buttonColors(containerColor = promptRed),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Reintentar", fontSize = 10.sp, color = Color.White)
                        }
                    }

                    ModelDownloadStatus.IDLE -> {
                        Button(
                            onClick = onDownload,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF132238)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(promptCyan.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, null, tint = promptCyan, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Descargar", fontSize = 10.sp, color = promptCyan, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Barra de progreso y estado de descarga
        if (model.downloadStatus == ModelDownloadStatus.DOWNLOADING || model.downloadStatus == ModelDownloadStatus.VERIFYING_SHA256) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { model.downloadProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (model.downloadStatus == ModelDownloadStatus.VERIFYING_SHA256) promptAmber else promptCyan,
                trackColor = Color(0xFF131D2A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (model.downloadStatus == ModelDownloadStatus.VERIFYING_SHA256) "Calculando digest SHA-256..." else "${model.downloadSpeedMbps.toInt()} Mbps",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = textMuted
                )
                Text(
                    text = if (model.downloadEtaSeconds > 0) "ETA: ${model.downloadEtaSeconds}s" else "",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = textMuted
                )
            }
        }

        // Mensaje de comprobación de integridad
        if (model.isIntegrityVerified) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = promptGreen, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SHA-256: ${model.verifiedSha256?.take(18) ?: model.expectedSha256.take(18)}... (Integridad 100% OK)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = promptGreen
                )
            }
        }
    }
}
