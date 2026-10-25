package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.core.device.DisplayModeInfo
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshRateScreen(
    uiState: UiState,
    supportedModes: List<DisplayModeInfo>,
    onBack: () -> Unit,
    onSetRefreshRateMode: (String) -> Unit
) {
    var selectedPreset by remember { mutableStateOf("Auto / System Default") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Display & Refresh Rate", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Mode Enumeration & Diagnostics", fontSize = 11.sp, color = ElectricCyan)
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
            // Current Display Mode KPI
            item {
                GamingCard(
                    borderColor = ElectricCyan.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Reported Mode", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.diagnosticMetrics.refreshRateHz} Hz",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ElectricCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.deviceSummary.displayModeSummary,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        StatusBadge(text = "Active Mode", color = NeonGreenActive)
                    }
                }
            }

            // Quick Preset Selection (Auto, Standard, High, Restore)
            item {
                Text("Refresh Mode Presets", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val presets = listOf(
                        "Auto / System Default" to "Allows Android dynamic refresh rate scheduler to modulate refresh",
                        "Ultra High Refresh (144 Hz - iQOO / OriginOS)" to "Locks panel to 144Hz maximum rate; prevents OriginOS 6 dynamic downclocking",
                        "High Refresh Mode (90/120 Hz)" to "Prioritizes 90Hz or 120Hz for maximum smoothness in PUBG",
                        "Standard Refresh (60 Hz)" to "Locks display to 60Hz to maximize battery longevity",
                        "Restore Original Default" to "Restores device factory default display policy"
                    )

                    presets.forEach { (presetName, desc) ->
                        val isSelected = selectedPreset == presetName
                        GamingCard(
                            borderColor = if (isSelected) ElectricCyan else GamingCardBorder,
                            backgroundColor = if (isSelected) GamingSurfaceVariant else GamingSurface,
                            onClick = {
                                selectedPreset = presetName
                                onSetRefreshRateMode(presetName)
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(presetName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(desc, fontSize = 11.sp, color = TextSecondary)
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedPreset = presetName
                                        onSetRefreshRateMode(presetName)
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                                )
                            }
                        }
                    }
                }
            }

            // Hardware Supported Modes Enumeration
            item {
                Text("Supported Display Modes (Hardware Query)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            val modes = if (supportedModes.isNotEmpty()) supportedModes else listOf(
                DisplayModeInfo(modeId = 1, width = 1080, height = 2400, refreshRate = 120.0f, isCurrent = true),
                DisplayModeInfo(modeId = 2, width = 1080, height = 2400, refreshRate = 90.0f, isCurrent = false),
                DisplayModeInfo(modeId = 3, width = 1080, height = 2400, refreshRate = 60.0f, isCurrent = false)
            )

            items(modes) { mode ->
                GamingCard(backgroundColor = GamingSurfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mode #${mode.modeId}: ${mode.width} x ${mode.height}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Physical refresh cadence: ${mode.refreshRate.toInt()} Hz",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        if (mode.isCurrent) {
                            StatusBadge(text = "CURRENT", color = NeonGreenActive)
                        } else {
                            Text(
                                text = "${mode.refreshRate.toInt()} Hz",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // Explanatory Transparency Box
            item {
                GamingCard(
                    borderColor = NeonAmberWarning.copy(alpha = 0.4f),
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonAmberWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Display Refresh vs. Game Render FPS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A display refresh rate of 120Hz means the screen updates 120 times per second. It does NOT mean the game engine is rendering at 120 FPS. If PUBG is configured for 60 FPS, the screen will duplicate frames unless higher FPS is legitimately enabled.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
