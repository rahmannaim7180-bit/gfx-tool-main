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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceEngineScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSelectMode: (PerformanceMode) -> Unit,
    onToggleExternalCooler: (Boolean) -> Unit,
    onApplyOptimizations: () -> Unit,
    onRestoreSettings: () -> Unit
) {
    val currentMode = uiState.performanceMode
    val isCoolerActive = uiState.rawPerformanceState.isExternalCoolerActive

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Thermal & Performance Engine",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "ADPF hints, thermal headroom & sustained modes",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Thermal Headroom Gauge Card
            item {
                GamingCard(
                    borderColor = when (currentMode) {
                        PerformanceMode.THERMAL_RECOVERY -> NeonRedAlert
                        PerformanceMode.RAW_PERFORMANCE -> ElectricCyan
                        PerformanceMode.SUSTAINED_COMPETITIVE -> NeonGreenActive
                        PerformanceMode.BALANCED -> GamingCardBorder
                    },
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "THERMAL HEADROOM & STATUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            Text(
                                text = "${uiState.deviceSummary.batteryTempCelsius}°C (${uiState.deviceSummary.thermalStatus})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }
                        StatusBadge(
                            text = currentMode.displayName,
                            color = when (currentMode) {
                                PerformanceMode.THERMAL_RECOVERY -> NeonRedAlert
                                PerformanceMode.RAW_PERFORMANCE -> ElectricCyan
                                PerformanceMode.SUSTAINED_COMPETITIVE -> NeonGreenActive
                                PerformanceMode.BALANCED -> TextSecondary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val headroom = uiState.deviceSummary.thermalHeadroom
                    LinearProgressIndicator(
                        progress = { headroom },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (headroom > 0.5f) NeonGreenActive else if (headroom > 0.25f) NeonAmberWarning else NeonRedAlert,
                        trackColor = GamingDarkBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Forecast Headroom: ${(headroom * 100).toInt()}% • PowerManager API 30+ Direct telemetry",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Mode Selector: 3 Modes
            item {
                Text(
                    text = "OPERATING PERFORMANCE MODES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }

            // Mode 1: SUSTAINED COMPETITIVE
            item {
                ModeSelectionCard(
                    mode = PerformanceMode.SUSTAINED_COMPETITIVE,
                    title = "1. Sustained Competitive (Recommended)",
                    description = "Prioritizes consistent frame pacing and thermal boundary control over brief peak clock bursts. Ideal for 1-4 hour PUBG Mobile sessions.",
                    isSelected = currentMode == PerformanceMode.SUSTAINED_COMPETITIVE,
                    accentColor = NeonGreenActive,
                    onClick = { onSelectMode(PerformanceMode.SUSTAINED_COMPETITIVE) },
                    testTag = "mode_sustained_competitive"
                )
            }

            // Mode 2: RAW PERFORMANCE
            item {
                ModeSelectionCard(
                    mode = PerformanceMode.RAW_PERFORMANCE,
                    title = "2. Raw Performance (Opt-In)",
                    description = "Requests Android Dynamic Performance Framework (ADPF) CPU/GPU thread hints and forces 144Hz panel refresh rate with active thermal safety.",
                    isSelected = currentMode == PerformanceMode.RAW_PERFORMANCE,
                    accentColor = ElectricCyan,
                    onClick = { onSelectMode(PerformanceMode.RAW_PERFORMANCE) },
                    testTag = "mode_raw_performance"
                )
            }

            // Mode 3: THERMAL RECOVERY
            item {
                ModeSelectionCard(
                    mode = PerformanceMode.THERMAL_RECOVERY,
                    title = "3. Thermal Recovery (Safety)",
                    description = "Automatic protective throttle: releases aggressive scheduler boosts when SoC approaches throttle threshold. Recommends dropping render resolution.",
                    isSelected = currentMode == PerformanceMode.THERMAL_RECOVERY,
                    accentColor = NeonRedAlert,
                    onClick = { onSelectMode(PerformanceMode.THERMAL_RECOVERY) },
                    testTag = "mode_thermal_recovery"
                )
            }

            // External Phone Cooler Condition Card
            item {
                GamingCard(
                    borderColor = if (isCoolerActive) ElectricCyan else GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCoolerActive) ElectricCyan.copy(alpha = 0.2f) else GamingSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AcUnit,
                                    contentDescription = null,
                                    tint = if (isCoolerActive) ElectricCyan else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "External Phone Cooler Attached",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isCoolerActive) "Active condition: heat sink dissipation recorded" else "Records condition; thermal safety remains active",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = isCoolerActive,
                            onCheckedChange = onToggleExternalCooler,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ElectricCyanContainer
                            )
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GamingButton(
                        text = "Apply Mode Optimizations",
                        onClick = onApplyOptimizations,
                        modifier = Modifier.weight(1.3f),
                        testTag = "apply_performance_mode_btn"
                    )
                    GamingButton(
                        text = "Restore Defaults",
                        onClick = onRestoreSettings,
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "restore_defaults_btn"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ModeSelectionCard(
    mode: PerformanceMode,
    title: String,
    description: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    GamingCard(
        backgroundColor = if (isSelected) ChipSelectedBackground else GamingSurface,
        borderColor = if (isSelected) accentColor else GamingCardBorder,
        onClick = onClick,
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) accentColor else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
