package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameTarget
import com.example.data.model.ProfileStateBadge
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    uiState: UiState,
    onNavigateToGameSettings: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToCommandTestCenter: () -> Unit,
    onNavigateToSavEditor: () -> Unit,
    onNavigateToGyroBoost: () -> Unit,
    onNavigateToPerformanceEngine: () -> Unit,
    onNavigateToGameSession: () -> Unit,
    onStartGameSession: () -> Unit,
    onStopGameSession: () -> Unit,
    onSelectGame: (GameTarget) -> Unit,
    onNavigateToNetworkQuality: () -> Unit = {},
    onNavigateToCompatibility: () -> Unit = {},
    onNavigateToDeviceCapabilities: () -> Unit = {},
    onNavigateToFramePacing: () -> Unit = {},
    onNavigateToTouchLab: () -> Unit = {},
    onNavigateToGameplayLab: () -> Unit = {},
    onNavigateToSelfAudit: () -> Unit = {},
    onEvaluate8v8Readiness: () -> Unit = {},
    onLaunchZeroOverheadGame: (GameTarget, com.example.data.model.GraphicsProfile) -> Unit = { _, _ -> }
) {
    var showGamePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
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
                            contentDescription = "FrameForge Logo",
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row {
                            Text(
                                text = "FrameForge ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "GFX 3.0",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ElectricCyan
                            )
                        }
                        Text(
                            text = "Universal Android Gaming Engine",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToDiagnostics,
                        modifier = Modifier.testTag("nav_diagnostics_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Troubleshoot,
                            contentDescription = "Diagnostics",
                            tint = ElectricCyan
                        )
                    }
                }
            }
        }

        // 1. Target Device & Selected Game Card
        item {
            GamingCard(
                borderColor = ElectricCyan.copy(alpha = 0.4f),
                backgroundColor = GamingSurface,
                onClick = { showGamePicker = true },
                modifier = Modifier.testTag("target_game_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSurfaceVariant)
                                .border(1.dp, GamingCardBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Selected Game (Tap to change)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = uiState.selectedGame.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${uiState.selectedGame.packageName} • ${uiState.selectedGame.variantLabel}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Switch Game",
                        tint = ElectricCyan
                    )
                }
            }
        }

        // 8v8 WOW Match Readiness Card
        item {
            GamingCard(
                borderColor = when (uiState.thermalSnapshot.thermalState) {
                    com.example.core.thermal.ThermalState.COOL -> ElectricCyan.copy(alpha = 0.5f)
                    com.example.core.thermal.ThermalState.WARM -> NeonGold.copy(alpha = 0.5f)
                    else -> CoralRed.copy(alpha = 0.5f)
                },
                backgroundColor = GamingSurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = when (uiState.thermalSnapshot.thermalState) {
                                    com.example.core.thermal.ThermalState.COOL -> ElectricCyan
                                    com.example.core.thermal.ThermalState.WARM -> NeonGold
                                    else -> CoralRed
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "8v8 WOW MATCH READINESS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        }

                        Surface(
                            color = when (uiState.thermalSnapshot.thermalState) {
                                com.example.core.thermal.ThermalState.COOL -> ElectricCyan.copy(alpha = 0.15f)
                                com.example.core.thermal.ThermalState.WARM -> NeonGold.copy(alpha = 0.15f)
                                else -> CoralRed.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "STATE: ${uiState.thermalSnapshot.thermalState.displayName.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (uiState.thermalSnapshot.thermalState) {
                                    com.example.core.thermal.ThermalState.COOL -> ElectricCyan
                                    com.example.core.thermal.ThermalState.WARM -> NeonGold
                                    else -> CoralRed
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Battery Temp", fontSize = 10.sp, color = TextMuted)
                            Text(
                                "${String.format("%.1f", uiState.thermalSnapshot.batteryTempCelsius)}°C",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.thermalSnapshot.batteryTempCelsius < 40f) TextPrimary else CoralRed
                            )
                        }
                        Column {
                            Text("Thermal Headroom", fontSize = 10.sp, color = TextMuted)
                            Text(
                                "${(uiState.thermalSnapshot.thermalHeadroom * 100).toInt()}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricCyan
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Readiness Score", fontSize = 10.sp, color = TextMuted)
                            val score = uiState.matchReadinessReport?.score ?: 92
                            Text(
                                "$score / 100",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (score >= 80) ElectricCyan else NeonGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onEvaluate8v8Readiness,
                        modifier = Modifier.fillMaxWidth().testTag("eval_8v8_readiness_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceLight)
                    ) {
                        Text("Audit 8v8 Match Readiness", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        }

        // 2. Focused Status Grid (Refresh Rate, Measured FPS status, GyroBoost, Shizuku)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Refresh Rate Card
                GamingCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = GamingSurface
                ) {
                    Text(text = "Refresh Rate", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.deviceSummary.currentRefreshRateHz.toInt()} Hz",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                    Text(
                        text = "Panel Max: ${uiState.deviceSummary.maxRefreshRateHz}Hz",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                // In-Game FPS Status Card
                GamingCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = GamingSurface
                ) {
                    Text(text = "In-Game FPS", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.diagnosticMetrics.isGameFpsDirectlyMeasurable) "${uiState.diagnosticMetrics.fpsAvg.toInt()} FPS" else "Target: 120",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonGreenActive
                    )
                    Text(
                        text = "Official: 120 FPS",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // GyroBoost Status Card
                GamingCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = GamingSurface,
                    onClick = onNavigateToGyroBoost
                ) {
                    Text(text = "GyroBoost", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.gyroConfig.currentSamplingRateHz.toInt()} Hz",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Jitter: ${uiState.gyroConfig.sampleJitterMs}ms",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                // Shizuku / ADB Status Card
                GamingCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = GamingSurface,
                    onClick = onNavigateToCommandTestCenter
                ) {
                    Text(text = "ADB / Shizuku", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.shizukuStatus.isAuthorized) "Authorized" else "Ready to Test",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.shizukuStatus.isAuthorized) NeonGreenActive else VividViolet
                    )
                    Text(
                        text = "10 Category Suite",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 3. Active Profile & Last Optimization Result
        item {
            GamingCard(
                borderColor = GamingCardBorder,
                backgroundColor = GamingSurface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE PROFILE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = uiState.activeProfile.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${uiState.activeProfile.quality.displayName} • ${uiState.activeProfile.frameRate.displayName} • ${uiState.activeProfile.resolution.displayName}",
                            fontSize = 11.sp,
                            color = ElectricCyan
                        )
                    }
                    StatusBadge(
                        text = uiState.activeProfile.stateBadge.label,
                        color = when (uiState.activeProfile.stateBadge) {
                            ProfileStateBadge.VERIFIED_ACTIVE -> NeonGreenActive
                            ProfileStateBadge.APPLIED -> ElectricCyan
                            ProfileStateBadge.SAVED_PREFERENCE -> VividViolet
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.activeProfile.statusSummary,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // 4. One-Tap Game-Focused Session Manager (START / STOP)
        item {
            GamingCard(
                borderColor = if (uiState.isGameSessionActive) NeonGreenActive.copy(alpha = 0.6f) else ElectricCyan.copy(alpha = 0.4f),
                backgroundColor = GamingSurface
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GAME-FOCUSED PERFORMANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.isGameSessionActive) NeonGreenActive else ElectricCyan
                            )
                            Text(
                                text = if (uiState.isGameSessionActive) {
                                    val m = uiState.sessionDurationSeconds / 60
                                    val s = uiState.sessionDurationSeconds % 60
                                    "Session Active: ${String.format("%02d:%02d", m, s)}"
                                } else {
                                    "Validates profile, sets display, and launches game"
                                },
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        if (uiState.isGameSessionActive) {
                            StatusBadge(text = "MONITORING", color = NeonGreenActive)
                        }
                    }

                    if (uiState.sessionStepProgress.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.sessionStepProgress,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!uiState.isGameSessionActive) {
                        GamingButton(
                            text = "START GAMING (ZERO OVERHEAD)",
                            onClick = {
                                onLaunchZeroOverheadGame(uiState.selectedGame, uiState.activeProfile)
                                onStartGameSession()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "start_session_button"
                        )
                    } else {
                        GamingButton(
                            text = "STOP GAMING SESSION & RESTORE",
                            onClick = onStopGameSession,
                            isSecondary = true,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "stop_session_button"
                        )
                    }
                }
            }
        }

        // 5. Quick Access Navigation Cards
        item {
            Text(
                text = "SYSTEM ENGINE MODULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Diagnostics",
                    subtitle = "Root cause audit",
                    icon = Icons.Default.Troubleshoot,
                    onClick = onNavigateToDiagnostics,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "Graphics & FPS",
                    subtitle = "10 Resolutions",
                    icon = Icons.Default.Tune,
                    onClick = onNavigateToGameSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Command Center",
                    subtitle = "10 Cat Audit",
                    icon = Icons.Default.Terminal,
                    onClick = onNavigateToCommandTestCenter,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "UE4 Sav Editor",
                    subtitle = "Active.sav GUI",
                    icon = Icons.Default.Edit,
                    onClick = onNavigateToSavEditor,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "GyroBoost Lab",
                    subtitle = "iQOO Profiles & Jitter",
                    icon = Icons.Default.ScreenRotation,
                    onClick = onNavigateToGyroBoost,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "Thermal Engine",
                    subtitle = "5-State Governor",
                    icon = Icons.Default.Thermostat,
                    onClick = onNavigateToPerformanceEngine,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Network Quality",
                    subtitle = "Socket Ping & Jitter",
                    icon = Icons.Default.NetworkCheck,
                    onClick = onNavigateToNetworkQuality,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "Frame Pacing",
                    subtitle = "Cadence Governor",
                    icon = Icons.Default.Speed,
                    onClick = onNavigateToFramePacing,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Device Capabilities",
                    subtitle = "Audit & Adapters",
                    icon = Icons.Default.DeveloperMode,
                    onClick = onNavigateToDeviceCapabilities,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "Touch Lab",
                    subtitle = "Latency & Polling",
                    icon = Icons.Default.TouchApp,
                    onClick = onNavigateToTouchLab,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Gameplay Lab",
                    subtitle = "Aim & Scope Timing",
                    icon = Icons.Default.GpsFixed,
                    onClick = onNavigateToGameplayLab,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "Self Audit",
                    subtitle = "Zero-Overhead Proof",
                    icon = Icons.Default.Memory,
                    onClick = onNavigateToSelfAudit,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Game Picker Dialog
    if (showGamePicker) {
        AlertDialog(
            onDismissRequest = { showGamePicker = false },
            title = {
                Text(
                    text = "Select Target Game",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                LazyColumn {
                    items(uiState.availableGames) { game ->
                        val isSelected = uiState.selectedGame.packageName == game.packageName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectGame(game)
                                    showGamePicker = false
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onSelectGame(game)
                                    showGamePicker = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ElectricCyan,
                                    unselectedColor = TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = game.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElectricCyan else TextPrimary
                                )
                                Text(
                                    text = "${game.packageName} • ${game.variantLabel}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGamePicker = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = GamingSurface
        )
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamingCard(
        backgroundColor = GamingSurface,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                .background(GamingSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}
