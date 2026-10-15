package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BuildStatus
import com.example.data.models.GeneratedAppProject
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.components.AutoDevOsModuleCard
import com.example.ui.components.CodeViewer
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxDarkBackground
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxDarkSurfaceElevated
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import kotlinx.coroutines.launch

@Composable
fun ApkBuilderScreen(
    repository: LuxRepository,
    projects: List<GeneratedAppProject>,
    activeProject: GeneratedAppProject,
    isBuilding: Boolean,
    currentBuildStep: String,
    soundService: SoundHapticService,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var appPrompt by remember { mutableStateOf("") }
    var appNameInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Herramientas & IA") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Herramientas & IA",
        "Inventario & Negocios",
        "Fintech & Crypto",
        "Salud & Fitness",
        "Redes & Chat",
        "E-Commerce"
    )

    val presetPrompts = listOf(
        "Crea una app de gestión de inventarios con escaneo de códigos de barra y base de datos local Room",
        "Billetera digital con WebSocket local de cotizaciones en tiempo real",
        "Sistema de monitoreo de sensores IoT industriales con alertas instantáneas",
        "App de telemedicina con videollamada y base de datos offline SQLite"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Builder Header Card con bordes Oro y Plata
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "FABRICADOR DE APKs NATIVAS",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LuxGoldLight
                            )
                            Text(
                                text = "De la Intención al Binario APK Instalable en 6 Fases",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuxOnDarkMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // App Name Field
                    OutlinedTextField(
                        value = appNameInput,
                        onValueChange = { appNameInput = it },
                        label = { Text("Nombre de la Aplicación", color = LuxOnDarkMuted) },
                        placeholder = { Text("Ej. SmartInventory Pro", color = LuxOnDarkMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_app_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxGoldPrimary,
                            unfocusedBorderColor = LuxDarkBorder,
                            focusedTextColor = LuxOnDark,
                            unfocusedTextColor = LuxOnDark
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Categories Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = cat == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color(0xFF141720),
                                    selectedContainerColor = LuxGoldContainer
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) LuxGoldPrimary else LuxDarkBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prompt Input
                    OutlinedTextField(
                        value = appPrompt,
                        onValueChange = { appPrompt = it },
                        label = { Text("Prompt Técnico (Especificación de la App)", color = LuxOnDarkMuted) },
                        placeholder = { Text("Describe los componentes, navegación, entidades y persistencia local...", color = LuxOnDarkMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("input_app_prompt"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxGoldPrimary,
                            unfocusedBorderColor = LuxDarkBorder,
                            focusedTextColor = LuxOnDark,
                            unfocusedTextColor = LuxOnDark
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Presets chips
                    Text(
                        text = "Sugerencias de Arquitectura:",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuxOnDarkMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetPrompts.forEach { preset ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    appPrompt = preset
                                    if (appNameInput.isBlank()) {
                                        appNameInput = preset.split(" ").take(3).joinToString(" ").replace("Crea una app de ", "")
                                    }
                                },
                                label = { Text(preset.take(34) + "...", fontSize = 11.sp, color = LuxPlatinumSecondary) },
                                colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFF161A24)),
                                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = LuxDarkBorder)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (appPrompt.isNotBlank()) {
                                soundService.playSuccess()
                                coroutineScope.launch {
                                    val newApp = repository.createNewAppFromPrompt(
                                        prompt = appPrompt,
                                        appName = appNameInput.ifBlank { "NovaApp" },
                                        category = selectedCategory
                                    )
                                    repository.triggerBuildPipeline(newApp, soundService)
                                    showSuccessDialog = true
                                }
                            } else {
                                soundService.playClick()
                                Toast.makeText(context, "Ingresa un prompt para construir la app", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        enabled = !isBuilding,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("btn_build_now")
                    ) {
                        if (isBuilding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = LuxDarkBackground,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Compilando en Cloud Run...", color = LuxDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = LuxDarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generar y Compilar APK", color = LuxDarkBackground, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // Live Build Pipeline Progress Indicator
        if (isBuilding || currentBuildStep.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LuxGoldPrimary, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = LuxDarkSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "PIPELINE DE CONSTRUCCIÓN",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LuxGoldPrimary
                            )
                            Text(
                                text = "Google Cloud Run / Gradle (Hasta 500 MB)",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuxPlatinumSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = LuxGoldPrimary,
                            trackColor = Color(0xFF1E2430)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentBuildStep,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            color = LuxAccentCyan
                        )
                    }
                }
            }
        }

        // Active Project Code & Artifact Inspector + Auto-Evolution Controls
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROYECTO SELECCIONADO · v${activeProject.version} (build ${activeProject.versionCode})",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp, fontWeight = FontWeight.Bold),
                            color = LuxGoldPrimary
                        )
                        Text(
                            text = "${activeProject.name} (${activeProject.packageName})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LuxOnDark
                        )
                        Text(
                            text = "Fixes: ${activeProject.autoCorrectedCount} · Mejoras: #${activeProject.autoImprovedCount} · ${activeProject.lastEvolutionSummary}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = LuxAccentCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(LuxAccentGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "APK (${activeProject.apkSizeMb} MB)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = LuxAccentGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Barra compacta de Auto-Corrección, Auto-Mejora y Auto-Actualización de Versión del Proyecto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.autoCorrectLuxAndGeneratedApps(activeProject, soundService)
                                Toast.makeText(context, "Errores auto-corregidos en '${activeProject.name}'", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxAccentGreen.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f).height(30.dp).testTag("btn_builder_autocorrect")
                    ) {
                        Text("Auto-Corregir", color = LuxAccentGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.autoImproveLuxAndGeneratedApps(activeProject, soundService)
                                Toast.makeText(context, "Código y arquitectura auto-mejorados en '${activeProject.name}'", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxAccentCyan.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f).height(30.dp).testTag("btn_builder_autoimprove")
                    ) {
                        Text("Auto-Mejorar", color = LuxAccentCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.autoUpdateAppAndProjectsVersion("MINOR", activeProject, soundService)
                                Toast.makeText(context, "Versión auto-actualizada para '${activeProject.name}' y LUX", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxGoldPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f).height(30.dp).testTag("btn_builder_autoupdate_version")
                    ) {
                        Text("Subir Versión", color = LuxGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }

        // Módulo Auto-DevOS para Analizar Logs y Parchear el APK Seleccionado sin Recompilar
        item {
            AutoDevOsModuleCard(
                repository = repository,
                soundService = soundService,
                initiallyExpanded = false
            )
        }

        // Code Inspector Tabs
        item {
            CodeViewer(files = activeProject.generatedFiles)
        }

        // Gradle assembleRelease Trigger Button for Current Project
        item {
            Button(
                onClick = {
                    soundService.playSuccess()
                    coroutineScope.launch {
                        repository.triggerBuildPipeline(activeProject, soundService)
                        Toast.makeText(
                            context,
                            "Ejecutando :app:assembleRelease para '${activeProject.name}'...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = !isBuilding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B2A1E),
                    contentColor = LuxAccentGreen
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxAccentGreen.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("btn_compile_active_assemble_release")
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = "Ejecutar assembleRelease",
                    tint = LuxAccentGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Compilar Proyecto Actual (:app:assembleRelease)",
                    color = LuxAccentGreen,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Unified Distribution & Artifact Delivery Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LuxGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxDarkSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .background(LuxGoldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = LuxGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "DISTRIBUCIÓN Y DESCARGA",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
                                    color = LuxGoldLight
                                )
                                Text(
                                    text = "${activeProject.name} · ${activeProject.apkSizeMb} MB (Firmado · Hasta 500 MB)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = LuxPlatinumSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Comparte el APK empaquetado por Bluetooth, WhatsApp, Gmail, Drive o instálalo directamente:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = LuxOnDarkMuted,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Primary Share Button (Android Native Chooser)
                    Button(
                        onClick = {
                            soundService.playSuccess()
                            repository.shareApk(context, activeProject, LuxRepository.ApkShareTarget.ALL)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("btn_share_active_project_apk_unified")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = LuxDarkBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Compartir APK (${activeProject.name})",
                            color = LuxDarkBackground,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row of Action Buttons (Install & Save Device)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                soundService.playSuccess()
                                val ok = repository.installCurrentLuxApk(context)
                                if (!ok) repository.shareCurrentLuxAppApk(context)
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(LuxGoldPrimary, LuxPlatinumSecondary)
                                )
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_install_apk_unified")
                        ) {
                            Icon(Icons.Default.InstallMobile, contentDescription = null, tint = LuxGoldLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Instalar APK", color = LuxGoldLight, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                soundService.playSuccess()
                                val path = repository.saveApkToDevice(context, activeProject)
                                Toast.makeText(context, "APK guardado en:\n$path", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxAccentGreen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_save_apk_device_unified")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Guardar en Disco", color = LuxDarkBackground, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row of Action Buttons (Save ZIP source code & Share LUX APK)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                soundService.playSuccess()
                                val zipPath = repository.saveProjectArchiveToDevice(context, activeProject)
                                Toast.makeText(context, "Código exportado en:\n$zipPath", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(LuxDarkBorder)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_export_zip_unified")
                        ) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, tint = LuxGoldLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Guardar ZIP", color = LuxGoldLight, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                soundService.playSuccess()
                                repository.shareCurrentLuxAppApk(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_share_lux_itself_unified")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Compartir LUX", color = LuxGoldPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareChannelButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(0.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = LuxDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = LuxOnDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .padding(horizontal = 6.dp)
                    .testTag(testTag)
            ) {
                Text("Enviar", fontSize = 9.sp, color = accentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}
