package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.PerformanceMode
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceEngineScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSelectMode: (PerformanceMode) -> Unit,
    onToggleUseAdbShizuku: (Boolean) -> Unit,
    onToggleApplyOnLaunch: (Boolean) -> Unit,
    onToggleAutoRestore: (Boolean) -> Unit,
    onApply: () -> Unit,
    onNavigateToRawDetails: () -> Unit
) {
    val currentMode = uiState.performanceMode
    val rawState = uiState.rawPerformanceState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Performance Engine", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Smart Optimization for Your Game", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Modes
            item {
                ModeSelectionCard(
                    title = "Balanced",
                    subtitle = "Best for daily gaming",
                    isSelected = currentMode == PerformanceMode.BALANCED,
                    accentColor = ElectricCyan,
                    onClick = { onSelectMode(PerformanceMode.BALANCED) }
                )
            }

            item {
                ModeSelectionCard(
                    title = "Stable Competitive",
                    subtitle = "Consistent FPS & smoothness",
                    isSelected = currentMode == PerformanceMode.STABLE_COMPETITIVE,
                    accentColor = NeonGreenActive,
                    onClick = { onSelectMode(PerformanceMode.STABLE_COMPETITIVE) }
                )
            }

            item {
                ModeSelectionCard(
                    title = "Raw Performance Mode (Beta)",
                    subtitle = "Max CPU/GPU (Optional) • Recommended for Flagship Devices",
                    isSelected = currentMode == PerformanceMode.RAW_PERFORMANCE,
                    accentColor = VividViolet,
                    badgeText = "Flagship",
                    onClick = {
                        onSelectMode(PerformanceMode.RAW_PERFORMANCE)
                    },
                    onDetailClick = onNavigateToRawDetails
                )
            }

            // Advanced Options
            item {
                Text(
                    text = "Advanced Options",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                GamingCard(backgroundColor = GamingSurface) {
                    // Option 1: Use ADB / Shizuku
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Use ADB / Shizuku (Optional)", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                            Text("Allows elevated Android Game Manager mode", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = rawState.useAdbShizuku,
                            onCheckedChange = onToggleUseAdbShizuku,
                            colors = SwitchDefaults.colors(checkedTrackColor = ElectricCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GamingCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 2: Apply when launching game
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Apply when launching game", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                            Text("Automatically initializes mode upon game launch", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = rawState.applyWhenLaunchingGame,
                            onCheckedChange = onToggleApplyOnLaunch,
                            colors = SwitchDefaults.colors(checkedTrackColor = ElectricCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GamingCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 3: Auto restore after game exit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto restore after game exit", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                            Text("Restores standard thermal and display policies when exiting", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = rawState.autoRestoreAfterGameExit,
                            onCheckedChange = onToggleAutoRestore,
                            colors = SwitchDefaults.colors(checkedTrackColor = ElectricCyan)
                        )
                    }
                }
            }

            // Apply Button
            item {
                GamingButton(
                    text = "Apply",
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_performance_mode_button")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ModeSelectionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    badgeText: String? = null,
    onClick: () -> Unit,
    onDetailClick: (() -> Unit)? = null
) {
    GamingCard(
        borderColor = if (isSelected) accentColor else GamingCardBorder,
        backgroundColor = if (isSelected) GamingSurfaceVariant else GamingSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VividViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badgeText, fontSize = 10.sp, color = VividViolet, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
            )
        }

        if (onDetailClick != null && isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onDetailClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Configure Raw Performance Details →", color = VividViolet, fontSize = 12.sp)
            }
        }
    }
}
