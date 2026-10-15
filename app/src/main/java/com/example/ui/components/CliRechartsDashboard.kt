package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CliTelemetryMetrics
import com.example.data.models.ErrorTrendPoint
import com.example.data.models.GradleTaskLatencyPoint

@Composable
fun CliRechartsDashboard(
    metrics: CliTelemetryMetrics,
    onTriggerRecoveryMode: () -> Unit,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCloseDashboard: (() -> Unit)? = null
) {
    var selectedTimeWindow by remember { mutableStateOf("Todo") }
    var selectedPointIndex by remember { mutableIntStateOf(-1) }
    var selectedTaskIndex by remember { mutableIntStateOf(-1) }

    // Pulso para telemetría en vivo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Paleta Recharts Dark Mode
    val bgDark = Color(0xFF070B10)
    val cardBg = Color(0xFF0D141F)
    val borderColor = Color(0xFF1E293B)
    val neonCyan = Color(0xFF00E5FF)
    val neonGreen = Color(0xFF00FF66)
    val neonRed = Color(0xFFFF3856)
    val neonAmber = Color(0xFFFFB300)
    val neonPurple = Color(0xFFC084FC)
    val textPrimary = Color(0xFFF8FAFC)
    val textMuted = Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgDark)
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("recharts_cli_dashboard")
    ) {
        // Cabecera Recharts Dashboard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(neonCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Recharts Dashboard",
                        tint = neonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RECHARTS TELEMETRY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Badge LIVE pulsante
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(neonGreen.copy(alpha = 0.2f))
                                .border(1.dp, neonGreen.copy(alpha = pulseAlpha), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(neonGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = neonGreen
                                )
                            }
                        }
                    }
                    Text(
                        text = "Visualización reactiva de logs, fallos y latencias de compilación",
                        style = MaterialTheme.typography.bodySmall,
                        color = textMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (onCloseDashboard != null) {
                IconButton(onClick = onCloseDashboard) {
                    Icon(Icons.Default.Refresh, contentDescription = "Cerrar o refrescar", tint = textMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selector de Ventana de Tiempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("1m", "5m", "15m", "Todo").forEach { window ->
                val isSelected = selectedTimeWindow == window
                AssistChip(
                    onClick = { selectedTimeWindow = window },
                    label = {
                        Text(
                            text = window,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) neonCyan.copy(alpha = 0.15f) else cardBg,
                        labelColor = if (isSelected) neonCyan else textMuted
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = if (isSelected) neonCyan else borderColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid de 4 KPIs Recharts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // KPI 1: Tasa de Errores
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, if (metrics.errorRatePercent > 5) neonRed.copy(alpha = 0.4f) else borderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "TASA DE ERRORES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${metrics.errorRatePercent}%",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (metrics.errorRatePercent > 0) neonRed else neonGreen
                    )
                    Text(
                        text = "${metrics.errorCount} de ${metrics.totalLogs} logs",
                        fontSize = 10.sp,
                        color = textMuted
                    )
                }
            }

            // KPI 2: Latencia de Compilación Gradle
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "LATENCIA GRADLE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${metrics.avgCompilationLatencyMs} ms",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = neonCyan
                    )
                    Text(
                        text = "Pico: ${metrics.maxCompilationLatencyMs} ms",
                        fontSize = 10.sp,
                        color = textMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // KPI 3: Throughput de Logs
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "VELOCIDAD STREAM",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${metrics.throughputLogsPerSec} /s",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = neonGreen
                    )
                    Text(
                        text = "${metrics.totalLogs} total capturados",
                        fontSize = 10.sp,
                        color = textMuted
                    )
                }
            }

            // KPI 4: Disponibilidad
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "DISPONIBILIDAD",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${metrics.systemAvailabilityPercent}%",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = neonPurple
                    )
                    Text(
                        text = "Self-Healing Activo",
                        fontSize = 10.sp,
                        color = neonGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GRÁFICO 1 RECHARTS: Tasa de Errores en Tiempo Real (Area Chart con Gradiente)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TASA DE ERRORES EN TIEMPO REAL (ÁREA RECHARTS)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textPrimary
                        )
                        Text(
                            text = "Evolución porcentual de fallos interceptados en el log stream",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }

                    if (metrics.errorCount > 0) {
                        Button(
                            onClick = onTriggerRecoveryMode,
                            colors = ButtonDefaults.buttonColors(containerColor = neonRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Healing, null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Fix", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Canvas Recharts Area Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color(0xFF06090E), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF131D2A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    RechartsAreaChart(
                        trendPoints = metrics.errorTrendPoints,
                        selectedPointIndex = selectedPointIndex,
                        onSelectPoint = { selectedPointIndex = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Leyenda Recharts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(neonRed))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tasa de Fallos (%)", fontSize = 10.sp, color = textMuted, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(neonGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Logs Saludables", fontSize = 10.sp, color = textMuted, fontFamily = FontFamily.Monospace)
                    }

                    if (selectedPointIndex in metrics.errorTrendPoints.indices) {
                        val pt = metrics.errorTrendPoints[selectedPointIndex]
                        Text(
                            text = "[${pt.timeLabel}] ${pt.errorRatePercent}% (${pt.errorCount} errs)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = neonRed
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GRÁFICO 2 RECHARTS: Latencia de Compilación Gradle por Tarea (Bar Chart)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LATENCIA DE COMPILACIÓN GRADLE (BARRAS RECHARTS)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = textPrimary
                        )
                        Text(
                            text = "Desglose por tarea de compilación en milisegundos (ms)",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(neonCyan.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Prom: ${metrics.avgCompilationLatencyMs}ms",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = neonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Canvas Recharts Bar Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(Color(0xFF06090E), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF131D2A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    RechartsBarChart(
                        tasks = metrics.gradleTaskLatencies,
                        selectedTaskIndex = selectedTaskIndex,
                        onSelectTask = { selectedTaskIndex = it },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detalle de tarea seleccionada
                if (selectedTaskIndex in metrics.gradleTaskLatencies.indices) {
                    val task = metrics.gradleTaskLatencies[selectedTaskIndex]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF121B27))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.taskName,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = neonCyan
                        )
                        Text(
                            text = "${task.durationMs} ms ${if (task.isCached) "(CACHE)" else "(EXEC)"}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (task.isCached) neonGreen else neonAmber
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GRÁFICO 3 RECHARTS: Distribución Proporcional de Logs (Segmented Distribution Ring)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "DISTRIBUCIÓN DEL LOG STREAM POR SEVERIDAD",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Barra Segmentada Proporcional
                val total = metrics.totalLogs.coerceAtLeast(1).toFloat()
                val infoRatio = metrics.infoCount / total
                val successRatio = metrics.successCount / total
                val warnRatio = metrics.warningCount / total
                val errorRatio = metrics.errorCount / total

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFF1A2333))
                ) {
                    if (successRatio > 0) {
                        Box(modifier = Modifier.weight(successRatio.coerceAtLeast(0.01f)).fillMaxSize().background(neonGreen))
                    }
                    if (infoRatio > 0) {
                        Box(modifier = Modifier.weight(infoRatio.coerceAtLeast(0.01f)).fillMaxSize().background(neonCyan))
                    }
                    if (warnRatio > 0) {
                        Box(modifier = Modifier.weight(warnRatio.coerceAtLeast(0.01f)).fillMaxSize().background(neonAmber))
                    }
                    if (errorRatio > 0) {
                        Box(modifier = Modifier.weight(errorRatio.coerceAtLeast(0.01f)).fillMaxSize().background(neonRed))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Leyenda porcentual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem("INFO", metrics.infoCount, neonCyan)
                    LegendItem("SUCCESS", metrics.successCount, neonGreen)
                    LegendItem("WARN", metrics.warningCount, neonAmber)
                    LegendItem("ERROR", metrics.errorCount, neonRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Botón de Ejecutar Telemetría en el CLI
        Button(
            onClick = { onExecuteCommand("antigravity telemetry") },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("btn_export_telemetry_cli"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF122033)),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(neonCyan, neonGreen))
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Timeline, null, tint = neonCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Exportar Reporte a ActivityTerminal (CLI)",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = neonCyan
            )
        }
    }
}

@Composable
private fun LegendItem(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: $count",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
        )
    }
}

/**
 * Gráfico de Área Recharts con Cuadrícula Punteada, Degradado Carmesí y Puntos Reactivos.
 */
@Composable
fun RechartsAreaChart(
    trendPoints: List<ErrorTrendPoint>,
    selectedPointIndex: Int,
    onSelectPoint: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val neonRed = Color(0xFFFF3856)
    val gridColor = Color(0xFF1F2B3E)

    Canvas(
        modifier = modifier.pointerInput(trendPoints) {
            detectTapGestures { offset ->
                if (trendPoints.isEmpty()) return@detectTapGestures
                val stepX = size.width / (trendPoints.size - 1).coerceAtLeast(1)
                val tappedIndex = ((offset.x + (stepX / 2)) / stepX).toInt().coerceIn(0, trendPoints.lastIndex)
                onSelectPoint(tappedIndex)
            }
        }
    ) {
        val width = size.width
        val height = size.height
        val maxVal = (trendPoints.maxOfOrNull { it.errorRatePercent } ?: 10.0).coerceAtLeast(5.0)

        // 1. Líneas de Cuadrícula Punteadas (Grid Recharts)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f,
                pathEffect = dashEffect
            )
        }

        if (trendPoints.size < 2) return@Canvas

        val stepX = width / (trendPoints.size - 1).toFloat()
        val path = Path()
        val areaPath = Path()

        // Puntos de coordenadas
        val coords = trendPoints.mapIndexed { idx, pt ->
            val x = idx * stepX
            val normalizedY = (pt.errorRatePercent / maxVal).coerceIn(0.0, 1.0).toFloat()
            val y = height - (normalizedY * (height - 20f)) - 10f
            Offset(x, y)
        }

        // Construir Path y Area
        coords.forEachIndexed { idx, pt ->
            if (idx == 0) {
                path.moveTo(pt.x, pt.y)
                areaPath.moveTo(pt.x, height)
                areaPath.lineTo(pt.x, pt.y)
            } else {
                path.lineTo(pt.x, pt.y)
                areaPath.lineTo(pt.x, pt.y)
            }
        }
        areaPath.lineTo(coords.last().x, height)
        areaPath.close()

        // Dibujar relleno de Área con Gradiente Recharts
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(neonRed.copy(alpha = 0.45f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Dibujar línea principal con trazo
        drawPath(
            path = path,
            color = neonRed,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Dibujar puntos de datos
        coords.forEachIndexed { idx, pt ->
            val isSelected = idx == selectedPointIndex
            drawCircle(
                color = if (isSelected) Color.White else neonRed,
                radius = if (isSelected) 6f else 4f,
                center = pt
            )
            drawCircle(
                color = Color(0xFF06090E),
                radius = if (isSelected) 3f else 2f,
                center = pt
            )
        }
    }
}

/**
 * Gráfico de Barras Recharts con Gradiente y Etiquetas de Tarea.
 */
@Composable
fun RechartsBarChart(
    tasks: List<GradleTaskLatencyPoint>,
    selectedTaskIndex: Int,
    onSelectTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val neonCyan = Color(0xFF00E5FF)
    val neonGreen = Color(0xFF00FF66)
    val neonAmber = Color(0xFFFFB300)
    val gridColor = Color(0xFF1F2B3E)

    Canvas(
        modifier = modifier.pointerInput(tasks) {
            detectTapGestures { offset ->
                if (tasks.isEmpty()) return@detectTapGestures
                val slotWidth = size.width / tasks.size
                val tappedIndex = (offset.x / slotWidth).toInt().coerceIn(0, tasks.lastIndex)
                onSelectTask(tappedIndex)
            }
        }
    ) {
        val width = size.width
        val height = size.height
        val maxDuration = (tasks.maxOfOrNull { it.durationMs } ?: 450L).coerceAtLeast(100L).toFloat()

        // Línea SLA horizontal Recharts (Target 400ms)
        val slaY = height - (400f / maxDuration).coerceIn(0f, 1f) * (height - 30f) - 15f
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        drawLine(
            color = neonAmber.copy(alpha = 0.6f),
            start = Offset(0f, slaY),
            end = Offset(width, slaY),
            strokeWidth = 1.2f,
            pathEffect = dashEffect
        )

        if (tasks.isEmpty()) return@Canvas

        val slotWidth = width / tasks.size
        val barWidth = (slotWidth * 0.58f).coerceAtLeast(10f)

        tasks.forEachIndexed { idx, task ->
            val isSelected = idx == selectedTaskIndex
            val centerX = (idx * slotWidth) + (slotWidth / 2)
            val barHeight = ((task.durationMs / maxDuration) * (height - 30f)).coerceAtLeast(6f)
            val topY = height - barHeight - 8f

            val barBrush = if (task.isCached) {
                Brush.verticalGradient(listOf(neonGreen, neonGreen.copy(alpha = 0.4f)))
            } else {
                Brush.verticalGradient(listOf(neonCyan, Color(0xFF0284C7).copy(alpha = 0.5f)))
            }

            // Barra
            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(centerX - (barWidth / 2), topY),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )

            // Resaltado de selección
            if (isSelected) {
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(centerX - (barWidth / 2) - 2f, topY - 2f),
                    size = Size(barWidth + 4f, barHeight + 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}
