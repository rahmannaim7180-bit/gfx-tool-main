package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameTarget
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    uiState: UiState,
    onNavigateToGameSettings: () -> Unit,
    onNavigateToGyroBoost: () -> Unit,
    onNavigateToPerformanceEngine: () -> Unit,
    onNavigateToFramePacing: () -> Unit,
    onNavigateToBottleneck: () -> Unit,
    onNavigateToRawPerformance: () -> Unit,
    onNavigateToAdbShizuku: () -> Unit,
    onNavigateToBenchmark: () -> Unit,
    onNavigateToInputLab: () -> Unit,
    onNavigateToAdvancedSettings: () -> Unit,
    onNavigateToRefreshRate: () -> Unit,
    onNavigateToGameSession: () -> Unit,
    onOptimizeNow: () -> Unit,
    onSelectGame: (GameTarget) -> Unit
) {
    var showGamePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.15f))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "FrameForge Icon",
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "FrameForge ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "GFX",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToGameSession,
                        modifier = Modifier.testTag("active_session_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Active Session",
                            tint = NeonGreenActive
                        )
                    }
                    IconButton(
                        onClick = onNavigateToAdvancedSettings,
                        modifier = Modifier.testTag("advanced_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // 1. Device Card
        item {
            GamingCard(
                borderColor = GamingCardBorderGlow.copy(alpha = 0.5f),
                backgroundColor = GamingSurface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.deviceSummary.model.ifEmpty { "iQOO Neo 9 5G" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Android ${uiState.deviceSummary.androidVersion} • ${uiState.deviceSummary.manufacturer} OS",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    StatusBadge(
                        text = if (uiState.deviceSummary.isFlagshipClass) "Flagship" else "Supported",
                        color = NeonGreenActive
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = GamingCardBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryMiniItem(
                        icon = Icons.Default.Tv,
                        label = "Display",
                        value = "${uiState.diagnosticMetrics.refreshRateHz} Hz"
                    )
                    TelemetryMiniItem(
                        icon = Icons.Default.DeviceThermostat,
                        label = "Temp",
                        value = "${uiState.deviceSummary.batteryTempCelsius.toInt()}°C"
                    )
                    TelemetryMiniItem(
                        icon = Icons.Default.BatteryChargingFull,
                        label = "Battery",
                        value = "${uiState.deviceSummary.batteryPct}%"
                    )
                    TelemetryMiniItem(
                        icon = Icons.Default.Memory,
                        label = "RAM",
                        value = "${(uiState.deviceSummary.availableRamMb / 1024f).toInt()}GB Free"
                    )
                }
            }
        }

        // 2. Selected Game Card
        item {
            GamingCard(
                borderColor = ElectricCyan.copy(alpha = 0.3f),
                backgroundColor = GamingSurfaceVariant.copy(alpha = 0.6f),
                onClick = { showGamePicker = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(ElectricCyanContainer, VividVioletContainer))
                            )
                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Game Icon",
                            tint = ElectricCyan,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.selectedGame.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.selectedGame.variantLabel,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "# Selected Game",
                                fontSize = 11.sp,
                                color = NeonGreenActive,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.selectedGame.isInstalled) "Installed (${uiState.selectedGame.installedVersion ?: "v3.2"})" else "Package detected",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    TextButton(
                        onClick = { showGamePicker = true },
                        modifier = Modifier.testTag("change_game_button")
                    ) {
                        Text("Change", color = ElectricCyan, fontSize = 13.sp)
                    }
                }
            }
        }

        // 3. 2x2 Quick Action Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionDashboardCard(
                        title = "Performance\nEngine",
                        icon = Icons.Default.Speed,
                        tint = ElectricCyan,
                        onClick = onNavigateToPerformanceEngine,
                        modifier = Modifier.weight(1f)
                    )
                    ActionDashboardCard(
                        title = "GyroBoost\n(200%)",
                        icon = Icons.Default.CenterFocusStrong,
                        tint = VividViolet,
                        onClick = onNavigateToGyroBoost,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionDashboardCard(
                        title = "Frame Pacing\nStabilizer",
                        icon = Icons.Default.AutoGraph,
                        tint = NeonGreenActive,
                        onClick = onNavigateToFramePacing,
                        modifier = Modifier.weight(1f)
                    )
                    ActionDashboardCard(
                        title = "Bottleneck\nDetector",
                        icon = Icons.Default.WarningAmber,
                        tint = NeonAmberWarning,
                        onClick = onNavigateToBottleneck,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Hero Optimize Button
        item {
            GamingButton(
                text = "Optimize Now",
                onClick = onOptimizeNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_optimize_button")
            )
        }

        // 5. Additional Flagship Capabilities
        item {
            Text(
                text = "ALL-IN-ONE GAMING OPTIMIZATION TOOLS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickToolChip(
                    label = "Raw Perf Mode",
                    icon = Icons.Default.OfflineBolt,
                    onClick = onNavigateToRawPerformance,
                    modifier = Modifier.weight(1f)
                )
                QuickToolChip(
                    label = "Input Lab",
                    icon = Icons.Default.TouchApp,
                    onClick = onNavigateToInputLab,
                    modifier = Modifier.weight(1f)
                )
                QuickToolChip(
                    label = "Benchmark",
                    icon = Icons.Default.BarChart,
                    onClick = onNavigateToBenchmark,
                    modifier = Modifier.weight(1f)
                )
                QuickToolChip(
                    label = "ADB Tools",
                    icon = Icons.Default.Terminal,
                    onClick = onNavigateToAdbShizuku,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickToolChip(
                    label = "Display & 120Hz",
                    icon = Icons.Default.Tv,
                    onClick = onNavigateToRefreshRate,
                    modifier = Modifier.weight(1f)
                )
                QuickToolChip(
                    label = "Game Presets",
                    icon = Icons.Default.SportsEsports,
                    onClick = onNavigateToGameSettings,
                    modifier = Modifier.weight(1f)
                )
                QuickToolChip(
                    label = "Device Info",
                    icon = Icons.Default.PhoneAndroid,
                    onClick = onNavigateToAdvancedSettings,
                    modifier = Modifier.weight(1f)
                )
                QuickToolChip(
                    label = "Game Session",
                    icon = Icons.Default.PlayCircle,
                    onClick = onNavigateToGameSession,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Game Selection BottomSheet / Dialog
    if (showGamePicker) {
        AlertDialog(
            onDismissRequest = { showGamePicker = false },
            title = {
                Text("Select Game Target", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.availableGames.forEach { game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (game.id == uiState.selectedGame.id) ElectricCyanContainer else GamingSurfaceVariant)
                                .clickable {
                                    onSelectGame(game)
                                    showGamePicker = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(game.displayName, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(game.variantLabel, fontSize = 11.sp, color = TextSecondary)
                            }
                            if (game.id == uiState.selectedGame.id) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = ElectricCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGamePicker = false }) {
                    Text("Close", color = ElectricCyan)
                }
            },
            containerColor = GamingSurface,
            textContentColor = TextSecondary
        )
    }
}

@Composable
private fun TelemetryMiniItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = ElectricCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun ActionDashboardCard(
    title: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamingCard(
        modifier = modifier.height(115.dp),
        borderColor = tint.copy(alpha = 0.35f),
        backgroundColor = GamingSurface,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun QuickToolChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(10.dp)),
        color = GamingSurfaceVariant,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ElectricCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
