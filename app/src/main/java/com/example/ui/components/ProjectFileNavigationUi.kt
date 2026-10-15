package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.KeyboardArrowDown

import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.data.models.GeneratedAppProject
import com.example.data.models.ProjectSourceFile
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentOrange
import com.example.ui.theme.LuxAccentRed
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
import com.example.ui.theme.LuxTerminalBg
import com.example.ui.theme.LuxTerminalText
import kotlinx.coroutines.launch

/**
 * Componente de Navegación de Archivos para visualizar y explorar la estructura
 * jerárquica del proyecto DevOS, con capacidad para seleccionar, inspeccionar y modificar
 * archivos fuente directamente en la interfaz.
 */
@Composable
fun ProjectFileNavigationUi(
    project: GeneratedAppProject,
    onSaveFileContent: (fileName: String, newContent: String) -> Unit,
    onAddNewFile: (newFile: ProjectSourceFile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedFileName by remember(project.id) {
        mutableStateOf(project.generatedFiles.firstOrNull()?.fileName ?: "")
    }
    var searchQuery by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf("") }
    var showNewFileDialog by remember { mutableStateOf(false) }

    // Estado de carpetas expandidas
    var expandedFolders by remember {
        mutableStateOf(setOf("app", "src/main", "java", "res"))
    }

    val currentFile = remember(project.generatedFiles, selectedFileName) {
        project.generatedFiles.find { it.fileName == selectedFileName }
            ?: project.generatedFiles.firstOrNull()
    }

    // Inicializar contenido a editar cuando cambia el archivo seleccionado
    var lastLoadedFile by remember { mutableStateOf("") }
    if (currentFile != null && currentFile.fileName != lastLoadedFile) {
        lastLoadedFile = currentFile.fileName
        editedContent = currentFile.content
        isEditMode = false
    }

    // Filtrar archivos según búsqueda
    val filteredFiles = remember(project.generatedFiles, searchQuery) {
        if (searchQuery.isBlank()) {
            project.generatedFiles
        } else {
            project.generatedFiles.filter {
                it.fileName.contains(searchQuery, ignoreCase = true) ||
                it.path.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Diálogo para nuevo archivo
    if (showNewFileDialog) {
        NewFileDialog(
            packageName = project.packageName,
            onDismiss = { showNewFileDialog = false },
            onCreate = { newFile ->
                onAddNewFile(newFile)
                selectedFileName = newFile.fileName
                editedContent = newFile.content
                isEditMode = true
                showNewFileDialog = false
                Toast.makeText(context, "Archivo '${newFile.fileName}' añadido", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LuxDarkSurface, RoundedCornerShape(16.dp))
            .border(1.dp, LuxDarkBorder, RoundedCornerShape(16.dp))
            .testTag("project_file_navigation")
    ) {
        // Cabecera del Navegador de Archivos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F131C), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .border(width = 0.5.dp, color = LuxDarkBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LuxGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = LuxGoldPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Estructura del Proyecto",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = LuxGoldLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E2838), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${project.generatedFiles.size} archivos",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = LuxAccentCyan
                            )
                        }
                    }
                    Text(
                        text = "${project.name} · ${project.packageName}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = LuxOnDarkMuted
                    )
                }
            }

            // Botón para crear nuevo archivo
            Button(
                onClick = { showNewFileDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldContainer),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_add_new_project_file")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo archivo",
                    tint = LuxGoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nuevo",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = LuxGoldPrimary
                )
            }
        }

        // Barra de búsqueda rápida de archivos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Buscar en el árbol de archivos (ej: MainActivity, Room, xml)...",
                        fontSize = 11.sp,
                        color = LuxOnDarkMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = LuxOnDarkMuted,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = LuxOnDarkMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_project_files"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxGoldPrimary,
                    unfocusedBorderColor = LuxDarkBorder,
                    focusedTextColor = LuxOnDark,
                    unfocusedTextColor = LuxOnDark,
                    focusedContainerColor = Color(0xFF0C0E14),
                    unfocusedContainerColor = Color(0xFF0C0E14)
                ),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
            )
        }

        // Tira horizontal o selector de archivos del proyecto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            filteredFiles.forEach { file ->
                val isSelected = file.fileName == currentFile?.fileName
                val (fileBadgeColor, fileExtLabel) = when {
                    file.fileName.endsWith(".kt") -> LuxAccentCyan to "KT"
                    file.fileName.endsWith(".xml") -> LuxAccentOrange to "XML"
                    file.fileName.endsWith(".gradle.kts") || file.fileName.endsWith(".kts") -> LuxAccentGreen to "GRADLE"
                    file.fileName.endsWith(".json") -> LuxGoldLight to "JSON"
                    else -> LuxPlatinumSecondary to "FILE"
                }

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedFileName = file.fileName
                        editedContent = file.content
                        isEditMode = false
                    },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = fileExtLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (isSelected) LuxDarkBackground else fileBadgeColor,
                                modifier = Modifier
                                    .background(
                                        if (isSelected) fileBadgeColor else fileBadgeColor.copy(alpha = 0.15f),
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = file.fileName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) LuxGoldPrimary else LuxOnDark
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Description else Icons.Default.Code,
                            contentDescription = null,
                            tint = if (isSelected) LuxGoldPrimary else LuxOnDarkMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    },


                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF10141F),
                        selectedContainerColor = Color(0xFF262315)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) LuxGoldPrimary else LuxDarkBorder
                    ),
                    modifier = Modifier.testTag("file_tab_${file.fileName}")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Vista de Detalle y Editor del Archivo Seleccionado
        if (currentFile != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .border(1.dp, LuxDarkBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LuxTerminalBg)
            ) {
                Column {
                    // Barra superior del archivo actual con ruta completa y controles de edición
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF090C12))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentFile.fileName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    ),
                                    color = LuxGoldLight
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEditMode) "• Editando" else "• Solo Lectura",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isEditMode) LuxAccentOrange else LuxAccentGreen
                                )
                            }
                            Text(
                                text = currentFile.path,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = LuxOnDarkMuted,
                                maxLines = 1
                            )
                        }

                        // Botones de acción del archivo: Copiar, Alternar Modo Edición, Guardar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Copiar al portapapeles
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText(currentFile.fileName, editedContent))
                                    Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp).testTag("btn_copy_file_content")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar archivo",
                                    tint = LuxOnDarkMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Alternar entre modo lectura y modo edición
                            Button(
                                onClick = {
                                    if (isEditMode) {
                                        // Descartar cambios
                                        editedContent = currentFile.content
                                        isEditMode = false
                                    } else {
                                        isEditMode = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isEditMode) Color(0xFF2E1C1A) else Color(0xFF161E2C)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("btn_toggle_edit_mode")
                            ) {
                                Icon(
                                    imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (isEditMode) LuxAccentRed else LuxAccentCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isEditMode) "Descartar" else "Modificar",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isEditMode) LuxAccentRed else LuxAccentCyan
                                )
                            }

                            // Botón de guardar cambios cuando está en modo edición
                            if (isEditMode) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        onSaveFileContent(currentFile.fileName, editedContent)
                                        isEditMode = false
                                        Toast.makeText(context, "Archivo '${currentFile.fileName}' guardado con éxito.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("btn_save_file_changes")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        tint = LuxDarkBackground,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Guardar",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = LuxDarkBackground
                                    )
                                }
                            }
                        }
                    }

                    // Snippets de inserción rápida de código (cuando está en modo edición)
                    AnimatedVisibility(
                        visible = isEditMode,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF121622))
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AssistChip(
                                onClick = {
                                    val snippet = """
                                    
                                    @Composable
                                    fun CustomWidget(modifier: Modifier = Modifier) {
                                        Text("Nuevo Widget generado por DevOS", color = Color.White)
                                    }
                                    """.trimIndent()
                                    editedContent = editedContent + "\n" + snippet
                                },
                                label = { Text("+ Composable UI", fontSize = 10.sp, color = LuxAccentCyan) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF182234))
                            )

                            AssistChip(
                                onClick = {
                                    val snippet = """
                                    
                                    @androidx.room.Dao
                                    interface ItemDao {
                                        @androidx.room.Query("SELECT * FROM items")
                                        fun getAll(): kotlinx.coroutines.flow.Flow<List<ItemEntity>>
                                    }
                                    """.trimIndent()
                                    editedContent = editedContent + "\n" + snippet
                                },
                                label = { Text("+ DAO Room SQLite", fontSize = 10.sp, color = LuxAccentGreen) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF13251D))
                            )

                            AssistChip(
                                onClick = {
                                    val snippet = """
                                    
                                    <uses-permission android:name="android.permission.INTERNET" />
                                    """.trimIndent()
                                    editedContent = editedContent + "\n" + snippet
                                },
                                label = { Text("+ Permiso Manifest", fontSize = 10.sp, color = LuxAccentOrange) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF281C12))
                            )
                        }
                    }

                    // Área de código: TextField editable en Modo Edición o Viewer con números de línea en Modo Lectura
                    if (isEditMode) {
                        val detectedLang = when {
                            currentFile.fileName.endsWith(".kt") -> "kotlin"
                            currentFile.fileName.endsWith(".xml") -> "xml"
                            currentFile.fileName.endsWith(".json") -> "json"
                            currentFile.fileName.endsWith(".kts") || currentFile.fileName.endsWith(".gradle") -> "gradle"
                            else -> currentFile.language
                        }

                        OutlinedTextField(
                            value = editedContent,
                            onValueChange = { editedContent = it },
                            visualTransformation = CodeSyntaxHighlighter.CodeVisualTransformation(detectedLang),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .padding(8.dp)
                                .testTag("file_editor_text_field"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuxGoldPrimary,
                                unfocusedBorderColor = LuxDarkBorder,
                                focusedTextColor = LuxTerminalText,
                                unfocusedTextColor = LuxTerminalText,
                                focusedContainerColor = Color(0xFF06080D),
                                unfocusedContainerColor = Color(0xFF06080D)
                            )
                        )
                    } else {
                        val lines = remember(currentFile.content) { currentFile.content.lines() }
                        val detectedLang = when {
                            currentFile.fileName.endsWith(".kt") -> "kotlin"
                            currentFile.fileName.endsWith(".xml") -> "xml"
                            currentFile.fileName.endsWith(".json") -> "json"
                            currentFile.fileName.endsWith(".kts") || currentFile.fileName.endsWith(".gradle") -> "gradle"
                            else -> currentFile.language
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color(0xFF05070B))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("file_view_lines_container")
                        ) {
                            itemsIndexed(lines) { index, line ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}".padStart(3, ' '),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        ),
                                        color = Color(0xFF4A5568),
                                        modifier = Modifier.width(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val annotatedLine = remember(line, detectedLang) {
                                        CodeSyntaxHighlighter.highlightCode(line, detectedLang)
                                    }
                                    Text(
                                        text = annotatedLine,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

/**
 * Diálogo modal interactivo para crear un nuevo archivo fuente en el proyecto DevOS.
 */
@Composable
fun NewFileDialog(
    packageName: String,
    onDismiss: () -> Unit,
    onCreate: (ProjectSourceFile) -> Unit
) {
    var fileName by remember { mutableStateOf("NewFeatureScreen.kt") }
    var selectedLanguage by remember { mutableStateOf("kotlin") }

    val languages = listOf("kotlin" to ".kt", "xml" to ".xml", "gradle" to ".gradle.kts", "json" to ".json")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = LuxGoldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Nuevo Archivo", color = LuxGoldLight, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Introduce el nombre y extensión del archivo para añadirlo a la estructura del proyecto:",
                    style = MaterialTheme.typography.bodySmall,
                    color = LuxOnDarkMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Nombre del archivo", color = LuxOnDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LuxGoldPrimary,
                        unfocusedBorderColor = LuxDarkBorder,
                        focusedTextColor = LuxOnDark,
                        unfocusedTextColor = LuxOnDark
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_new_file_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Tipo de Lenguaje:", style = MaterialTheme.typography.labelSmall, color = LuxOnDarkMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    languages.forEach { (lang, ext) ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = {
                                selectedLanguage = lang
                                if (!fileName.endsWith(ext)) {
                                    val base = fileName.substringBeforeLast(".")
                                    fileName = "$base$ext"
                                }
                            },
                            label = { Text(lang.replaceFirstChar { it.uppercase() }, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LuxGoldContainer,
                                selectedLabelColor = LuxGoldPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        val path = when {
                            fileName.endsWith(".xml") -> "app/src/main/res/values/$fileName"
                            fileName.endsWith(".kts") -> "app/$fileName"
                            else -> "app/src/main/java/${packageName.replace('.', '/')}/$fileName"
                        }
                        val template = when (selectedLanguage) {
                            "kotlin" -> """
                            package $packageName
                            
                            import androidx.compose.runtime.Composable
                            import androidx.compose.material3.Text
                            
                            @Composable
                            fun ${fileName.substringBeforeLast(".")}View() {
                                Text("Componente creado en DevOS")
                            }
                            """.trimIndent()
                            "xml" -> """
                            <?xml version="1.0" encoding="utf-8"?>
                            <resources>
                                <!-- Recursos creados en DevOS -->
                            </resources>
                            """.trimIndent()
                            else -> "// Archivo creado en DevOS: $fileName\n"
                        }

                        val newSourceFile = ProjectSourceFile(
                            fileName = fileName,
                            path = path,
                            language = selectedLanguage,
                            content = template
                        )
                        onCreate(newSourceFile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LuxGoldPrimary),
                modifier = Modifier.testTag("btn_confirm_create_file")
            ) {
                Text("Crear Archivo", color = LuxDarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar", color = LuxOnDarkMuted)
            }
        },
        containerColor = LuxDarkSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
