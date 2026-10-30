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
import androidx.compose.ui.graphics.Color
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
    onLaunchZeroOverheadGame: (GameTarget, com.example.data.model.GraphicsProfile) -> Unit = { _, _ -> },
    onApplyIqooOptimization: () -> Unit = {}
) {
    var showGamePicker by remember { mutableStateOf(false) }
    var isQuickOptimized by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Minimalist Header
        item {
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
                            .background(GamingSurfaceVariant)
                            .border(1.dp, GamingCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FF",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = NeonGreenActive
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FRAMEFORGE GFX",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "IOS-TIER ENGINE | ZERO-OVERHEAD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonGreenActive,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                StatusBadge(text = "ONLINE", color = NeonGreenActive)
            }
        }

        // Active Target Game Card
        item {
            GamingCard(
                borderColor = GamingCardBorder,
                backgroundColor = GamingSurface,
                onClick = { showGamePicker = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE TARGET GAME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.selectedGame.displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${uiState.selectedGame.variantLabel} | ${uiState.selectedGame.packageName}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    StatusBadge(
                        text = if (uiState.selectedGame.isInstalled) "INSTALLED" else "SUPPORTED",
                        color = NeonGreenActive
                    )
                }
            }
        }

        // iQOO Neo 9 (PD2338C) Dedicated Hardware Tuning Card
        item {
            GamingCard(
                borderColor = NeonGreenActive,
                backgroundColor = GamingSurface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PRIMARY TARGET HARDWARE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "iQOO Neo 9 (PD2338C)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonGreenActive
                        )
                    }
                    StatusBadge(text = "120 FPS / 120Hz 1:1 LOCK", color = NeonGreenActive)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Snapdragon 8 Gen 2 (4nm) • vivo Q1 Display Processor • 16GB LPDDR5X • OriginOS 6/7. Physical hardware integer cadence tuned for zero display micro-judder.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniOptStatus(name = "Cadence", value = "1:1 Integer", isGood = true, modifier = Modifier.weight(1f))
                    MiniOptStatus(name = "Multi-Turbo", value = "Enabled", isGood = true, modifier = Modifier.weight(1f))
                    MiniOptStatus(name = "Cooler Bound", value = if (uiState.rawPerformanceState.isExternalCoolerActive) "Active" else "Standby", isGood = true, modifier = Modifier.weight(1f))
                    MiniOptStatus(name = "Touch Polling", value = "240Hz Anti-Lag", isGood = true, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                GamingButton(
                    text = "APPLY iQOO HARDWARE TUNING",
                    onClick = onApplyIqooOptimization,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "apply_iqoo_opt_btn"
                )
            }
        }

        // Consolidated In-Game Optimization Status Card
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
                    Text(
                        text = "IN-GAME PERFORMANCE STATUS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    StatusBadge(text = "WORKABLE FILE", color = NeonGreenActive)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniOptStatus(name = "FPS Cap", value = "120 FPS", isGood = true, modifier = Modifier.weight(1f))
                    MiniOptStatus(name = "Aim Assist", value = "Strong", isGood = true, modifier = Modifier.weight(1f))
                    MiniOptStatus(name = "Touch Polling", value = "240 Hz", isGood = true, modifier = Modifier.weight(1f))
                    MiniOptStatus(name = "Scope Switch", value = "Instant", isGood = true, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                GamingButton(
                    text = "ONE-TAP OPTIMIZE & LAUNCH MATCH",
                    onClick = {
                        isQuickOptimized = true
                        onLaunchZeroOverheadGame(uiState.selectedGame, uiState.activeProfile)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "quick_optimize_launch_btn"
                )
            }
        }

        // Primary Feature Modules Grid
        item {
            Text(
                text = "CORE MODULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeNavCard(
                    title = "UE4 SAV Editor",
                    subtitle = "Active.sav Key-Value",
                    icon = Icons.Default.EditNote,
                    statusText = "Workable",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSavEditor
                )
                HomeNavCard(
                    title = "Gyro Boost",
                    subtitle = "Bypass, Std, High, Highest",
                    icon = Icons.Default.ScreenRotation,
                    statusText = "Ready",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToGyroBoost
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeNavCard(
                    title = "Touch Lab",
                    subtitle = "Anti-Thermal Touch Delay",
                    icon = Icons.Default.TouchApp,
                    statusText = "240Hz",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTouchLab
                )
                HomeNavCard(
                    title = "Game Engine",
                    subtitle = "iOS 120 FPS & Aim Assist",
                    icon = Icons.Default.RocketLaunch,
                    statusText = "Unlocked",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPerformanceEngine
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeNavCard(
                    title = "Shizuku / ADB",
                    subtitle = "Shell Connection & V3",
                    icon = Icons.Default.Terminal,
                    statusText = if (uiState.shizukuStatus.isAuthorized) "Connected" else "Standby",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCommandTestCenter
                )
                HomeNavCard(
                    title = "Network & Hitreg",
                    subtitle = "Bullet Ping & Packet Sync",
                    icon = Icons.Default.Wifi,
                    statusText = "Low Ping",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToNetworkQuality
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Game Picker Dialog
    if (showGamePicker) {
        AlertDialog(
            onDismissRequest = { showGamePicker = false },
            title = {
                Text(
                    text = "Select PUBG Target Variant",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.availableGames.forEach { game ->
                        val isSelected = game.packageName == uiState.selectedGame.packageName
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GamingSurfaceVariant else GamingSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonGreenActive else GamingCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectGame(game)
                                    showGamePicker = false
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = game.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) NeonGreenActive else TextPrimary
                                    )
                                    Text(
                                        text = game.variantLabel,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = NeonGreenActive,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
            containerColor = GamingSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun MiniOptStatus(
    name: String,
    value: String,
    isGood: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GamingSurfaceVariant)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = name, fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isGood) NeonGreenActive else NeonRedAlert
        )
    }
}

@Composable
private fun HomeNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    statusText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(GamingSurface)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                StatusBadge(text = statusText, color = NeonGreenActive)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
