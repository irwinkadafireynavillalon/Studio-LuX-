package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ProjectSourceFile
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxAccentRed
import com.example.ui.theme.LuxDarkBorder
import com.example.ui.theme.LuxDarkSurface
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxOnDark
import com.example.ui.theme.LuxOnDarkMuted
import com.example.ui.theme.LuxPlatinumSecondary
import com.example.ui.theme.LuxTerminalBg
import com.example.ui.theme.LuxTerminalText
import com.example.ui.theme.LuxTerminalYellow

@Composable
fun CodeViewer(
    files: List<ProjectSourceFile>,
    modifier: Modifier = Modifier
) {
    if (files.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(LuxTerminalBg, RoundedCornerShape(12.dp))
                .border(1.dp, LuxDarkBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay archivos generados aún", color = LuxOnDarkMuted)
        }
        return
    }

    var selectedIndex by remember { mutableStateOf(0) }
    val currentFile = files.getOrNull(selectedIndex) ?: files.first()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .background(LuxTerminalBg, RoundedCornerShape(14.dp))
            .border(1.dp, LuxDarkBorder, RoundedCornerShape(14.dp))
            .testTag("code_viewer")
    ) {
        // Tab row of files
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LuxDarkSurface, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .border(
                    width = 0.5.dp,
                    color = LuxDarkBorder,
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                )
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            files.forEachIndexed { index, file ->
                val isSelected = index == selectedIndex
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedIndex = index },
                    label = {
                        Text(
                            text = file.fileName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) LuxGoldPrimary else LuxOnDarkMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = if (isSelected) LuxGoldPrimary else LuxOnDarkMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF141720),
                        selectedContainerColor = Color(0xFF262315)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) LuxGoldPrimary else LuxDarkBorder
                    )
                )
            }
        }

        // File Path bar + Copy button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0C0E14))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentFile.path,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                color = LuxPlatinumSecondary
            )

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Código ${currentFile.fileName}", currentFile.content))
                    Toast.makeText(context, "Código copiado al portapapeles", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copiar código",
                    tint = LuxGoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Code Content with Line Numbers and Token-Based Syntax Highlighting
        val lines = currentFile.content.lines()
        val horizontalScroll = rememberScrollState()
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
                .height(280.dp)
                .padding(8.dp)
        ) {
            itemsIndexed(lines) { index, line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll)
                        .padding(vertical = 1.dp)
                ) {
                    Text(
                        text = "${index + 1}".padStart(3, ' '),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = LuxOnDarkMuted.copy(alpha = 0.5f),
                        modifier = Modifier.width(36.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val annotatedLine = remember(line, detectedLang) {
                        CodeSyntaxHighlighter.highlightCode(line, detectedLang)
                    }

                    Text(
                        text = annotatedLine,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}
