package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onRestoreSettings: () -> Unit,
    onSetAnimationScale: (Float) -> Unit = {},
    onRestoreAnimationScales: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & More", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingDarkBackground)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SettingsOptionCard(
                    title = "App Preferences",
                    subtitle = "Theme, Language, Telemetry Notifications",
                    icon = Icons.Default.Tune,
                    onClick = {}
                )
            }

            item {
                SettingsOptionCard(
                    title = "Game Profiles",
                    subtitle = "Manage your game settings & customized presets",
                    icon = Icons.Default.FolderSpecial,
                    onClick = {}
                )
            }

            item {
                SettingsOptionCard(
                    title = "Performance Logs",
                    subtitle = "View diagnostic benchmark history and reports",
                    icon = Icons.Default.History,
                    onClick = {}
                )
            }

            // System Animation Scales (Priority 11)
            item {
                GamingCard(
                    borderColor = VividViolet.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Text("System Animation Scales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Window: ${uiState.animationState.currentWindowScale}x • Transition: ${uiState.animationState.currentTransitionScale}x • Animator: ${uiState.animationState.currentAnimatorScale}x",
                        fontSize = 11.sp,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reducing animation durations eliminates task-switching delays and perceived latency when toggling overlays during gaming sessions.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSetAnimationScale(1.0f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.animationState.currentWindowScale == 1.0f) ElectricCyanContainer else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("1.0x (Stock)", fontSize = 11.sp, color = if (uiState.animationState.currentWindowScale == 1.0f) ElectricCyan else TextPrimary)
                        }

                        Button(
                            onClick = { onSetAnimationScale(0.5f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.animationState.currentWindowScale == 0.5f) ElectricCyanContainer else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("0.5x (Fast)", fontSize = 11.sp, color = if (uiState.animationState.currentWindowScale == 0.5f) ElectricCyan else TextPrimary)
                        }

                        Button(
                            onClick = { onSetAnimationScale(0.0f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.animationState.currentWindowScale == 0.0f) ElectricCyanContainer else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Off (Instant)", fontSize = 11.sp, color = if (uiState.animationState.currentWindowScale == 0.0f) ElectricCyan else TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.animationState.lastActionStatus,
                        fontSize = 10.sp,
                        color = if (uiState.shizukuStatus.isAuthorized) NeonGreenActive else TextMuted
                    )
                }
            }

            item {
                SettingsOptionCard(
                    title = "Backup & Restore",
                    subtitle = "Save and restore device-managed settings",
                    icon = Icons.Default.Restore,
                    onClick = onRestoreSettings
                )
            }

            item {
                SettingsOptionCard(
                    title = "About FrameForge GFX",
                    subtitle = "Version 1.0.0 (Flagship Release)",
                    icon = Icons.Default.Info,
                    onClick = {}
                )
            }

            item {
                SettingsOptionCard(
                    title = "Join Our Community",
                    subtitle = "Get tips, updates and competitive gameplay guides",
                    icon = Icons.Default.Groups,
                    onClick = {}
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SettingsOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GamingCard(
        backgroundColor = GamingSurface,
        onClick = onClick
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
    }
}
