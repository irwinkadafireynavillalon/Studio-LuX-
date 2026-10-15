package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.models.GeneratedAppProject
import com.example.data.models.ProjectSourceFile
import com.example.data.repository.LuxRepository
import com.example.data.repository.SoundHapticService
import com.example.ui.components.CodeViewer
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
 * 6. Workspace Screen
 * Espacio de trabajo de desarrollo Android Studio / AI Studio:
 * - Selector y creador interactivo de proyectos
 * - Árbol de archivos navegable (src/main, manifests, res, gradle)
 * - Visor y editor de código fuente con resaltado de sintaxis
 * - Snippets de código y previsualización de componentes
 * - Compilación y distribución fluida sin botones que estorben
 */
@Composable
fun WorkspaceScreen(
    repository: LuxRepository,
    soundService: SoundHapticService,
    onNavigateToBuilder: () -> Unit,
    onNavigateToCli: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val projects by repository.projects.collectAsState()
    val activeProject by repository.activeProject.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Editor, 1: Árbol, 2: Snippets
    var selectedFile by remember { mutableStateOf<ProjectSourceFile?>(activeProject.generatedFiles.firstOrNull()) }
    var newProjectPrompt by remember { mutableStateOf("") }
    var isCreatingApp by remember { mutableStateOf(false) }

    val snippets = listOf(
        "M3 Scaffold & TopAppBar" to """
            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun ScreenLayout() {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Sys-LUX App") }) }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) { /* Content */ }
                }
            }
        """.trimIndent(),
        "Room Dao Coroutine" to """
            @Dao
            interface AppEntityDao {
                @Query("SELECT * FROM cached_items ORDER BY id DESC")
                fun getAllFlow(): Flow<List<CachedItem>>

                @Insert(onConflict = OnConflictStrategy.REPLACE)
                suspend fun insertItem(item: CachedItem)
            }
        """.trimIndent(),
        "Animated Visibility" to """
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card { Text("Elemento Reactivo") }
            }
        """.trimIndent()
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .imePadding()
    ) {
        // 1. Barra Superior del Workspace
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
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = LuxGoldPrimary, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("6. Workspace", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = LuxGoldLight)
                    Text("Proyecto: ${activeProject.name} (${activeProject.generatedFiles.size} archivos)", fontSize = 9.sp, color = LuxPlatinumSecondary)
                }
            }

            // Acciones Rápidas
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Compilar
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LuxGoldPrimary)
                        .clickable {
                            soundService.playSuccess()
                            coroutineScope.launch {
                                repository.triggerBuildPipeline(activeProject, soundService)
                            }
                            onNavigateToBuilder()
                        }
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = LuxDarkBackground, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Compilar", fontSize = 9.sp, color = LuxDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. Selector de Pestañas del Workspace: [Editor de Código] [Árbol de Archivos] [Snippets Compose]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            listOf("💻 Editor", "📁 Árbol Archivos", "🧩 Snippets").forEachIndexed { idx, title ->
                val isSel = selectedTabIndex == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(7.dp))
                        .background(if (isSel) LuxGoldContainer else Color(0xFF141924))
                        .border(0.6.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(7.dp))
                        .clickable {
                            soundService.playClick()
                            selectedTabIndex = idx
                        }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(title, fontSize = 10.sp, color = if (isSel) LuxGoldPrimary else LuxPlatinumSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. Contenido según la pestaña seleccionada
        when (selectedTabIndex) {
            0 -> {
                // PESTAÑA EDITOR DE CÓDIGO
                Column(modifier = Modifier.weight(1f)) {
                    // Pestañas horizontales de archivos abiertos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        activeProject.generatedFiles.forEach { file ->
                            val isSel = selectedFile?.fileName == file.fileName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color(0xFF1B2330) else Color(0xFF10141D))
                                    .border(0.5.dp, if (isSel) LuxGoldPrimary else LuxDarkBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        soundService.playClick()
                                        selectedFile = file
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    file.fileName,
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSel) LuxAccentCyan else LuxPlatinumSecondary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Visor de Código
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090D14))
                            .border(0.6.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        val curr = selectedFile ?: activeProject.generatedFiles.firstOrNull()
                        if (activeProject.generatedFiles.isNotEmpty()) {
                            CodeViewer(
                                files = activeProject.generatedFiles,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("No hay archivos en el proyecto", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            1 -> {
                // PESTAÑA ÁRBOL DE ARCHIVOS
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(activeProject.generatedFiles) { file ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color(0xFF121722))
                                    .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(7.dp))
                                    .clickable {
                                        soundService.playClick()
                                        selectedFile = file
                                        selectedTabIndex = 0
                                    }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (file.fileName.endsWith(".kt")) Icons.Default.Code else Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = if (file.fileName.endsWith(".kt")) LuxAccentCyan else LuxGoldPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(file.fileName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight, fontFamily = FontFamily.Monospace)
                                        Text(file.path, fontSize = 8.sp, color = LuxPlatinumSecondary)
                                    }
                                }
                                Text("${file.content.lines().size} lín", fontSize = 8.5.sp, color = LuxPlatinumSecondary)
                            }
                        }
                    }
                }
            }

            2 -> {
                // PESTAÑA SNIPPETS
                Column(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(snippets) { (title, code) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111620)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(LuxGoldContainer)
                                                .clickable {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Snippet", code))
                                                    Toast.makeText(context, "Snippet copiado", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Copiar", fontSize = 8.5.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = code,
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = LuxAccentCyan,
                                        lineHeight = 11.sp
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
