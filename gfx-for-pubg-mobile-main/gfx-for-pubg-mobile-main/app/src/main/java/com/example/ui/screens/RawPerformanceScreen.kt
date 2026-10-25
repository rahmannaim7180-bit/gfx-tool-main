package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RawPerformanceScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onToggleRawPerformance: (Boolean) -> Unit,
    onToggleExternalCooler: (Boolean) -> Unit,
    onApplyOptimizations: () -> Unit,
    onRestoreAllSettings: () -> Unit
) {
    val rawState = uiState.rawPerformanceState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Raw Performance Mode", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VividViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Beta", fontSize = 10.sp, color = VividViolet, fontWeight = FontWeight.Bold)
                        }
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
            // Description Banner
            item {
                Text(
                    text = "Prioritize maximum CPU/GPU performance, low input latency and minimum background interference. Higher power consumption and temperature may occur. Hardware and game restrictions still apply.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }

            // Master Toggle
            item {
                GamingCard(
                    borderColor = if (rawState.isEnabled) VividViolet else GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Raw Performance Mode", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                            Text("Opt-in flagship gaming tuning engine", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = rawState.isEnabled,
                            onCheckedChange = onToggleRawPerformance,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = VividViolet,
                                checkedThumbColor = TextPrimary
                            ),
                            modifier = Modifier.testTag("raw_performance_toggle")
                        )
                    }
                }
            }

            // External Cooler Toggle
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("External Phone Cooler Attached", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                            Text("Informs benchmark analyzer to account for external dissipation", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = rawState.isExternalCoolerActive,
                            onCheckedChange = onToggleExternalCooler,
                            colors = SwitchDefaults.colors(checkedTrackColor = ElectricCyan),
                            modifier = Modifier.testTag("external_cooler_toggle")
                        )
                    }
                }
            }

            // Active Optimizations List
            item {
                Text(
                    text = "Active Optimizations",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            items(rawState.optimizations) { item ->
                GamingCard(
                    backgroundColor = GamingSurfaceVariant,
                    borderColor = if (item.isApplied) NeonGreenActive.copy(alpha = 0.5f) else GamingCardBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(item.description, fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Status: ${item.statusNote}",
                                fontSize = 11.sp,
                                color = if (item.isApplied) NeonGreenActive else TextMuted
                            )
                        }

                        StatusBadge(
                            text = if (item.isApplied) "Applied" else "Not Applied",
                            color = if (item.isApplied) NeonGreenActive else TextMuted
                        )
                    }
                }
            }

            // Safety & Policy Disclaimer
            item {
                GamingCard(
                    borderColor = ElectricCyan.copy(alpha = 0.3f),
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.4f)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "This mode is optional and may not be available on all devices. Some optimizations require additional permissions or supported integrations.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GamingButton(
                        text = "Apply Optimizations",
                        onClick = onApplyOptimizations,
                        enabled = rawState.isEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_raw_optimizations_button")
                    )

                    GamingButton(
                        text = "Restore All Managed Settings",
                        onClick = onRestoreAllSettings,
                        isSecondary = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("restore_all_settings_button")
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
