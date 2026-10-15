package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.LuxScreenDestination
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

/**
 * Menú Desplegado (Navigation Drawer) de Sys-LUX
 * Cada botón separa nítidamente la interfaz correspondiente sin amontonadero:
 * 1. LuX Studio IA
 * 2. Termux LuX
 * 3. Permisos y ajustes
 * 4. Seguridad y cuentas
 * 5. Prueba
 * 6.1 Memoria caja de arena
 * 6. Workspace
 * 7. Ajustes LuX
 * 8. LuX App Sistema
 * Compilador APK
 */
@Composable
fun LuxNavigationDrawerContent(
    currentDestination: LuxScreenDestination,
    onSelectDestination: (LuxScreenDestination) -> Unit,
    onCloseDrawer: () -> Unit,
    versionName: String = "2.5.0",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(Color(0xF50D111A))
            .border(
                width = 1.dp,
                brush = LuxGoldSilverBorderBrush,
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .statusBarsPadding()
            .padding(14.dp)
    ) {
        // Cabecera del Menú Desplegado
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
                        .border(1.2.dp, LuxGoldSilverBorderBrush, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lux_emblem),
                        contentDescription = "Emblema Imperial LuX",
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sys-LUX",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = LuxGoldLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LuxGoldContainer)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("v$versionName", fontSize = 8.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Menú Desplegado de Interfaces", fontSize = 9.sp, color = LuxPlatinumSecondary)
                }
            }

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar Menú", tint = LuxPlatinumSecondary, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lista de Botones e Interfaces Desplegadas (Scroll Vertical)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DrawerDestinationItem(
                destination = LuxScreenDestination.STUDIO_IA,
                icon = Icons.Default.AutoAwesome,
                isSelected = currentDestination == LuxScreenDestination.STUDIO_IA,
                onClick = { onSelectDestination(LuxScreenDestination.STUDIO_IA) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.TERMUX_LUX,
                icon = Icons.Default.Terminal,
                isSelected = currentDestination == LuxScreenDestination.TERMUX_LUX,
                onClick = { onSelectDestination(LuxScreenDestination.TERMUX_LUX) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.PERMISSIONS_SETTINGS,
                icon = Icons.Default.Tune,
                isSelected = currentDestination == LuxScreenDestination.PERMISSIONS_SETTINGS,
                onClick = { onSelectDestination(LuxScreenDestination.PERMISSIONS_SETTINGS) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.SECURITY_ACCOUNTS,
                icon = Icons.Default.Security,
                isSelected = currentDestination == LuxScreenDestination.SECURITY_ACCOUNTS,
                onClick = { onSelectDestination(LuxScreenDestination.SECURITY_ACCOUNTS) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.TESTING_AUTOCORRECTION,
                icon = Icons.Default.Science,
                isSelected = currentDestination == LuxScreenDestination.TESTING_AUTOCORRECTION,
                onClick = { onSelectDestination(LuxScreenDestination.TESTING_AUTOCORRECTION) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.SANDBOX_MEMORY,
                icon = Icons.Default.Memory,
                isSelected = currentDestination == LuxScreenDestination.SANDBOX_MEMORY,
                onClick = { onSelectDestination(LuxScreenDestination.SANDBOX_MEMORY) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.WORKSPACE,
                icon = Icons.Default.Dashboard,
                isSelected = currentDestination == LuxScreenDestination.WORKSPACE,
                onClick = { onSelectDestination(LuxScreenDestination.WORKSPACE) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.LUX_SETTINGS,
                icon = Icons.Default.Settings,
                isSelected = currentDestination == LuxScreenDestination.LUX_SETTINGS,
                onClick = { onSelectDestination(LuxScreenDestination.LUX_SETTINGS) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.SYSTEM_APPS,
                icon = Icons.Default.Android,
                isSelected = currentDestination == LuxScreenDestination.SYSTEM_APPS,
                onClick = { onSelectDestination(LuxScreenDestination.SYSTEM_APPS) }
            )

            DrawerDestinationItem(
                destination = LuxScreenDestination.BUILDER,
                icon = Icons.Default.Android,
                isSelected = currentDestination == LuxScreenDestination.BUILDER,
                onClick = { onSelectDestination(LuxScreenDestination.BUILDER) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pie del Menú con Estado de Agentes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF111722))
                .border(0.5.dp, LuxDarkBorder, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column {
                Text("Agentes IA Conectados:", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = LuxGoldLight)
                Text("• Gemini Cloud (Google Sync & API Key)", fontSize = 8.sp, color = LuxAccentGreen)
                Text("• Gemma Vertex (90.11.29.1:1402)", fontSize = 8.sp, color = LuxAccentCyan)
                Text("• Gemma3 270m Localhost (https://90.11.29.1:4250)", fontSize = 8.sp, color = LuxGoldPrimary)
                Text("• Antigravity CLI Híbrido (Memoria Cloud/Drive)", fontSize = 8.sp, color = LuxPlatinumSecondary)
            }
        }
    }
}

@Composable
private fun DrawerDestinationItem(
    destination: LuxScreenDestination,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(listOf(Color(0x55FFD54F), Color(0x35E2E8F0)))
                } else {
                    SolidColor(Color(0xFF121722))
                }
            )
            .border(
                width = if (isSelected) 1.dp else 0.5.dp,
                brush = if (isSelected) LuxGoldSilverBorderBrush else Brush.linearGradient(listOf(LuxDarkBorder, LuxDarkBorder)),
                shape = RoundedCornerShape(9.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag(destination.tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) LuxGoldPrimary else Color(0xFF1A2230)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) LuxDarkBackground else LuxGoldLight,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(9.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = destination.title,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isSelected) LuxGoldLight else Color(0xFFE2E8F0)
            )
            Text(
                text = destination.subtitle,
                fontSize = 8.sp,
                color = if (isSelected) LuxGoldPrimary else LuxPlatinumSecondary,
                maxLines = 1
            )
        }
    }
}
