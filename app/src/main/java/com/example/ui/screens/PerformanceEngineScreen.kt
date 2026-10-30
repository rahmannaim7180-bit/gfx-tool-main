package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                            text = "iQOO Neo 9 Hardware Tuning",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Snapdragon 8 Gen 2 • Q1 Display Chip • OriginOS 6/7",
                            color = NeonGreenActive,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("perf_back_button")) {
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
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Target Hardware Identification Card
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
                                text = "ACTIVE HARDWARE TARGET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "iQOO Neo 9 (PD2338C)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Firmware: ${uiState.deviceSummary.firmwareVersion}",
                                fontSize = 11.sp,
                                color = NeonGreenActive
                            )
                        }
                        StatusBadge(text = "16GB+512GB", color = NeonGreenActive)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HardwareSpecPill(name = "SoC", value = "SD 8 Gen 2", modifier = Modifier.weight(1f))
                        HardwareSpecPill(name = "Display Chip", value = "vivo Q1", modifier = Modifier.weight(1f))
                        HardwareSpecPill(name = "Panel Max", value = "144 Hz", modifier = Modifier.weight(1f))
                        HardwareSpecPill(name = "OS Layer", value = "OriginOS", modifier = Modifier.weight(1f))
                    }
                }
            }

            // Performance Mode Selector
            item {
                Text(
                    text = "SELECT PERFORMANCE POLICY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            // Mode 1: RAW_PERFORMANCE (Permanent Default)
            item {
                PerformanceModeCard(
                    title = "Raw Performance (Permanent Default)",
                    subtitle = "Maximum ADPF Session Hints & 120 FPS Priority",
                    description = "Prioritizes CPU Prime core high-affinity scheduling, fast GPU fence synchronization, and zero-overhead match execution.",
                    isSelected = currentMode == PerformanceMode.RAW_PERFORMANCE,
                    onClick = { onSelectMode(PerformanceMode.RAW_PERFORMANCE) }
                )
            }

            // Mode 2: SUSTAINED_COMPETITIVE
            item {
                PerformanceModeCard(
                    title = "Sustained Competitive Mode",
                    subtitle = "Balanced Frame Pacing & Thermal Guard",
                    description = "Locks frame times to stable vsync intervals without pushing maximum burst voltage, ideal for long tournaments.",
                    isSelected = currentMode == PerformanceMode.SUSTAINED_COMPETITIVE,
                    onClick = { onSelectMode(PerformanceMode.SUSTAINED_COMPETITIVE) }
                )
            }

            // Mode 3: THERMAL_RECOVERY
            item {
                PerformanceModeCard(
                    title = "Thermal Recovery Mode",
                    subtitle = "Protective Thermal Back-Off",
                    description = "Automatically reduces peak hints when SoC skin temperature crosses 44.5°C, preserving device safety without abrupt FPS crashes.",
                    isSelected = currentMode == PerformanceMode.THERMAL_RECOVERY,
                    onClick = { onSelectMode(PerformanceMode.THERMAL_RECOVERY) }
                )
            }

            // External Gaming Cooler Switch Card
            item {
                GamingCard(
                    borderColor = if (isCoolerActive) NeonGreenActive else GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = if (isCoolerActive) NeonGreenActive else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "EXTERNAL GAMING COOLER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isCoolerActive) "Semiconductor Cooler Active (-5.5°C Adapted)" else "Device Passive Cooling",
                                    fontSize = 11.sp,
                                    color = if (isCoolerActive) NeonGreenActive else TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = isCoolerActive,
                            onCheckedChange = { onToggleExternalCooler(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GamingDarkBackground,
                                checkedTrackColor = NeonGreenActive,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = GamingSurfaceVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When an external semiconductor fan cooler is attached to your iQOO Neo 9 backplate, thermal boundary calculations adapt by +5.5°C headroom without disabling hardware safeguards.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            // OriginOS Native Subsystem Tuning Card
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text(
                        text = "ORIGINOS 6 / 7 HARDWARE TUNING ARCHITECTURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TuningSpecRow(
                        title = "120 FPS Integer Frame-Time Lock",
                        description = "Synchronizes 120 FPS target to 120Hz display mode. Eliminates the 1.2:1 cadence judder caused by unaligned 144Hz panels.",
                        isVerified = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TuningSpecRow(
                        title = "vivo Q1 Display Coprocessor Sync",
                        description = "Enables MEMC / Super-Resolution frame timing alignment via OriginOS Ultra Game Mode display pipeline.",
                        isVerified = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TuningSpecRow(
                        title = "OriginOS Multi-Turbo Scheduler",
                        description = "Applies vivo_multi_turbo high-priority scheduling and background process suppression via Shizuku.",
                        isVerified = uiState.shizukuStatus.isAuthorized
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TuningSpecRow(
                        title = "ADPF Thermal Headroom Predictive Governor",
                        description = "Forecasts thermal load 10 seconds ahead to sustain steady clock frequencies in crowded 8v8 combat.",
                        isVerified = true
                    )
                }
            }

            // Fair Play & Server-Side Transparency Notice
            item {
                GamingCard(
                    borderColor = GamingCardBorder,
                    backgroundColor = GamingSurfaceVariant
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ENGINEERING TRANSPARENCY: FAIR PLAY & SECURITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Device whitelist eligibility for 120 FPS in PUBG Mobile is verified cryptographically by Krafton/Tencent game servers. Third-party applications cannot spoof server-side authorization. FrameForge applies native system-level ADPF performance hints, display mode locks, and touch digitizer priority directly to your physical iQOO Neo 9 hardware.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Action Buttons
            item {
                GamingButton(
                    text = "APPLY IQOO NEO 9 (PD2338C) OPTIMIZATIONS",
                    onClick = onApplyOptimizations,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "apply_iqoo_optimizations_btn"
                )
            }

            item {
                OutlinedButton(
                    onClick = onRestoreSettings,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Original System Settings", fontSize = 13.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HardwareSpecPill(
    name: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GamingSurfaceVariant)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = name, fontSize = 9.sp, color = TextMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun PerformanceModeCard(
    title: String,
    subtitle: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) GamingSurfaceVariant else GamingSurface)
            .border(
                1.5.dp,
                if (isSelected) NeonGreenActive else GamingCardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
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
                    color = if (isSelected) NeonGreenActive else TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = if (isSelected) NeonGreenActive else TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = NeonGreenActive,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TuningSpecRow(
    title: String,
    description: String,
    isVerified: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Speed,
            contentDescription = null,
            tint = if (isVerified) NeonGreenActive else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
