package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.core.device.DeviceSummary
import com.example.data.model.DeviceCompatibilityRecord
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceComparisonScreen(
    currentDevice: DeviceSummary,
    initialComparisonRecord: DeviceCompatibilityRecord? = null,
    onBack: () -> Unit
) {
    val candidateRecords = remember {
        DeviceCompatibilityRecord.KNOWN_DEVICE_RECORDS.filter { it.id != "generic_unverified_144fps_claim" }
    }

    var selectedComparison by remember {
        mutableStateOf(
            initialComparisonRecord ?: candidateRecords.firstOrNull { it.id == "rog_phone8_pubg_global" }
            ?: candidateRecords.first()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Device Architecture Comparison", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Hardware Capabilities • Thermal Budgets • Refresh Policies", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("device_comp_back_button")) {
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
            // Reference Phone Selector
            item {
                Text("Select Reference Phone", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(candidateRecords) { record ->
                        val isSelected = record.id == selectedComparison.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElectricCyanContainer else GamingSurface)
                                .border(1.dp, if (isSelected) ElectricCyan else GamingCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedComparison = record }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = record.deviceModel.take(18),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElectricCyan else TextSecondary
                            )
                        }
                    }
                }
            }

            // Head-to-Head Header Card
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
                        // User Device
                        Column(modifier = Modifier.weight(1f)) {
                            Text("YOUR DEVICE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(currentDevice.model, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${currentDevice.manufacturer} • Android ${currentDevice.androidVersion}", fontSize = 11.sp, color = TextSecondary)
                        }

                        Text("VS", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ElectricCyan, modifier = Modifier.padding(horizontal = 12.dp))

                        // Target Device
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("REFERENCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VividViolet)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(selectedComparison.deviceModel.take(16), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(selectedComparison.manufacturer, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Comparison Breakdown Table
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text("Architectural Comparison", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    ComparisonRow(
                        attribute = "SoC / Chipset",
                        yourValue = currentDevice.chipset,
                        refValue = selectedComparison.chipset
                    )

                    ComparisonRow(
                        attribute = "Max Panel Refresh",
                        yourValue = "${currentDevice.maxRefreshRateHz} Hz",
                        refValue = "${selectedComparison.displayMaxRefreshHz} Hz"
                    )

                    ComparisonRow(
                        attribute = "RAM Capacity",
                        yourValue = "${currentDevice.totalRamMb / 1024} GB LPDDR5X",
                        refValue = "12 - 16 GB LPDDR5X"
                    )

                    ComparisonRow(
                        attribute = "PUBG Max Official FPS",
                        yourValue = "120 FPS (Smooth)",
                        refValue = selectedComparison.verifiedFpsTarget
                    )

                    ComparisonRow(
                        attribute = "OEM Gaming Suite",
                        yourValue = "OriginOS Multi-Turbo",
                        refValue = selectedComparison.oemGamingIntegration
                    )

                    ComparisonRow(
                        attribute = "Cooling Strategy",
                        yourValue = "VC Chamber + External Cooler",
                        refValue = if (selectedComparison.deviceModel.contains("RedMagic")) "Internal 22k RPM Active Fan" else "Vapor Chamber / AeroActive"
                    )
                }
            }

            // Key Takeaways & Strategy
            item {
                GamingCard(
                    borderColor = NeonGreenActive.copy(alpha = 0.4f),
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.7f)
                ) {
                    Text("FrameForge Engineering Assessment", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Engine Parity: Both devices share the same 120 FPS game engine ceiling in PUBG Mobile Global. No phone renders at 144 FPS in-game.\n\n" +
                                "2. Refresh Rate Alignment: While your phone supports 144 Hz display hardware, running 120 FPS on 144 Hz produces 1.2x frame cadence judder. Synchronizing your display to 120 Hz produces the exact same zero-jitter 1:1 cadence found on reference gaming flagships.\n\n" +
                                "3. Sustained Thermal Performance: Snapdragon 8 Gen 2 on your device provides exceptional CPU/GPU efficiency. With an external semiconductor cooler attached, your phone sustains 120 FPS with identical frame stability to built-in fan flagship devices.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
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
private fun ComparisonRow(
    attribute: String,
    yourValue: String,
    refValue: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(attribute, fontSize = 11.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(yourValue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ElectricCyan, modifier = Modifier.weight(1f))
            Text(refValue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.weight(1f))
        }
        Divider(color = GamingCardBorder.copy(alpha = 0.4f), modifier = Modifier.padding(top = 6.dp))
    }
}
