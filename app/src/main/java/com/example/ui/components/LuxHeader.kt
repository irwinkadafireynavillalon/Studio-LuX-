package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LuxNavTab
import com.example.R
import com.example.ui.theme.LuxAccentCyan
import com.example.ui.theme.LuxAccentGreen
import com.example.ui.theme.LuxGoldContainer
import com.example.ui.theme.LuxGoldLight
import com.example.ui.theme.LuxGoldPrimary
import com.example.ui.theme.LuxGoldSilverBorderBrush
import com.example.ui.theme.LuxPlatinumSecondary
import com.example.ui.theme.LuxSilverLight

@Composable
fun LuxHeader(
    isServerActive: Boolean,
    latencyMs: Int,
    modifier: Modifier = Modifier,
    versionName: String = "2.5.0",
    selectedTab: LuxNavTab = LuxNavTab.ORCHESTRATOR,
    onSelectTab: ((LuxNavTab) -> Unit)? = null,
    onOpenMenuDrawer: (() -> Unit)? = null,
    isGoogleAccountSynced: Boolean = true,
    syncedGoogleEmail: String = "francisco.reyna2077@gmail.com",
    onOpenGoogleSyncDialog: (() -> Unit)? = null,
    onQuickDownloadAntigravityCli: (() -> Unit)? = null,
    onOpenPermissionsPanel: (() -> Unit)? = null,
    onShareApk: (() -> Unit)? = null,
    onNavigateToOrchestrator: (() -> Unit)? = null,
    onAutoEvolveAndUpdate: (() -> Unit)? = null,
    onOpenInterfaceSettings: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xAD19140B), // Oro Translúcido
                        Color(0xA6101520), // Plata Obsidiana Translúcida
                        Color(0xAD19140B)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = LuxGoldSilverBorderBrush,
                shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
            )
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("lux_header")
    ) {
        // FILA 1: Emblema + Estado + Sincronización Google (Sin API Key) + Acciones Rápidas
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Compact Emblem + Brand + Live Version Badge + Google Sync Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onOpenMenuDrawer != null) {
                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(LuxGoldContainer)
                            .border(0.7.dp, LuxGoldPrimary, RoundedCornerShape(6.dp))
                            .clickable { onOpenMenuDrawer() }
                            .padding(horizontal = 6.dp)
                            .testTag("btn_header_open_menu_drawer"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú LuX", tint = LuxGoldPrimary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Menú", fontSize = 9.sp, color = LuxGoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    LuxGoldPrimary,
                                    LuxPlatinumSecondary,
                                    LuxGoldLight,
                                    LuxSilverLight,
                                    LuxGoldPrimary
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lux_emblem),
                        contentDescription = "Emblema Imperial LUX",
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(7.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LUX",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.4.sp,
                                fontSize = 13.5.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = LuxGoldLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0x40FFD54F), Color(0x38E2E8F0))
                                    )
                                )
                                .border(0.7.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(5.dp))
                                .clickable(enabled = onAutoEvolveAndUpdate != null) {
                                    onAutoEvolveAndUpdate?.invoke()
                                }
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                .testTag("badge_lux_version_header")
                        ) {
                            Text(
                                text = "v$versionName",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                ),
                                color = LuxGoldLight
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        // Chip de Sincronización Cuenta Google (Sin Google API Key)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    if (isGoogleAccountSynced) Color(0x40123022) else Color(0x402E1C12)
                                )
                                .border(
                                    0.7.dp,
                                    if (isGoogleAccountSynced) LuxAccentGreen.copy(alpha = 0.65f) else LuxGoldPrimary,
                                    RoundedCornerShape(7.dp)
                                )
                                .clickable { onOpenGoogleSyncDialog?.invoke() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("btn_header_google_sync")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isGoogleAccountSynced) LuxAccentGreen.copy(alpha = pulseAlpha) else LuxGoldPrimary
                                        )
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isGoogleAccountSynced) "Google Sync · Sin API Key" else "Sincronizar Google",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 8.5.sp
                                    ),
                                    color = if (isGoogleAccountSynced) LuxAccentGreen else LuxGoldLight,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isGoogleAccountSynced) {
                            "Cuenta Google: $syncedGoogleEmail · Antigravity CLI Activo"
                        } else {
                            "Auto-DevOS · Cristal Oro y Plata · ${latencyMs}ms"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                        color = LuxPlatinumSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right: Compact Non-Intrusive Action Icons (27.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onOpenGoogleSyncDialog != null) {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0x554285F4), Color(0x4530E389))
                                )
                            )
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(7.dp))
                            .clickable { onOpenGoogleSyncDialog() }
                            .testTag("btn_header_google_account_icon"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Sincronizar Cuenta Google sin API Key",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (onQuickDownloadAntigravityCli != null) {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0x47152530))
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(7.dp))
                            .clickable { onQuickDownloadAntigravityCli() }
                            .testTag("btn_header_download_cli"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Descargar Antigravity CLI",
                            tint = LuxAccentCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (onOpenInterfaceSettings != null) {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0x47FFD54F), Color(0x42E2E8F0))
                                )
                            )
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(7.dp))
                            .clickable { onOpenInterfaceSettings() }
                            .testTag("btn_header_interface_settings"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Ajustes de Interfaz Oro y Plata",
                            tint = LuxGoldLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (onOpenPermissionsPanel != null) {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0x471E2430))
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(7.dp))
                            .clickable { onOpenPermissionsPanel() }
                            .testTag("btn_header_permissions"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Panel de Permisos",
                            tint = LuxPlatinumSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                if (onShareApk != null) {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0x47332811))
                            .border(0.8.dp, LuxGoldSilverBorderBrush, RoundedCornerShape(7.dp))
                            .clickable { onShareApk() }
                            .testTag("btn_header_share_apk"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir APK de LUX",
                            tint = LuxGoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // FILA 2 (ARRIBA): Los 4 Botones Principales de Navegación solicitados arriba:
        // 1. IA Studios | 2. Workspace | 3. Terminal | 4. Compilador APK
        if (onSelectTab != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("top_navigation_bar_row"),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LuxNavTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    TopNavButtonPill(
                        title = tab.title,
                        icon = tab.icon,
                        isSelected = isSelected,
                        tag = tab.tag,
                        onClick = { onSelectTab(tab) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopNavButtonPill(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(31.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(Color(0x66FFD54F), Color(0x52E2E8F0))
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(Color(0x59121722), Color(0x59181F2C))
                    )
                }
            )
            .border(
                width = if (isSelected) 1.1.dp else 0.6.dp,
                brush = if (isSelected) {
                    LuxGoldSilverBorderBrush
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            LuxPlatinumSecondary.copy(alpha = 0.35f),
                            LuxGoldPrimary.copy(alpha = 0.3f)
                        )
                    )
                },
                shape = RoundedCornerShape(9.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) LuxGoldPrimary else LuxPlatinumSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                ),
                color = if (isSelected) LuxGoldLight else LuxPlatinumSecondary
            )
        }
    }
}
