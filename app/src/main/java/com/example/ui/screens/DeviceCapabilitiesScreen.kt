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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.adapter.CapabilityEntry
import com.example.core.adapter.CapabilityStatus
import com.example.core.device.DeviceSummary
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCapabilitiesScreen(
    deviceSummary: DeviceSummary,
    capabilities: List<CapabilityEntry>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Device Capability Engine", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${deviceSummary.manufacturer} ${deviceSummary.model} • Android ${deviceSummary.androidVersion}", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("device_cap_back_button")) {
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
            // Header Card: Authentic Capability Model Notice
            item {
                GamingCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricCyan.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capability-Aware Universal Engine", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "FrameForge never fabricates benchmark scores or spoof device models to PUBG servers. " +
                            "Every capability below is evaluated against real Android HAL APIs, system frameworks, and verified OEM integrations.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Architecture Overview Card
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hardware Architecture", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Chipset / SoC", fontSize = 11.sp, color = TextTertiary)
                                Text(deviceSummary.socName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Column {
                                Text("RAM Capacity", fontSize = 11.sp, color = TextTertiary)
                                Text("${deviceSummary.ramTotalMb / 1024} GB LPDDR5X", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Column {
                                Text("Display Panel", fontSize = 11.sp, color = TextTertiary)
                                Text("${deviceSummary.maxRefreshRate}Hz", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ElectricCyan)
                            }
                        }
                    }
                }
            }

            // Capability Matrix Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("FEATURE AUDIT MATRIX", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                    Text("${capabilities.size} Capabilities Evaluated", fontSize = 11.sp, color = ElectricCyan)
                }
            }

            // Capability Entries
            items(capabilities) { entry ->
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Text(entry.category, fontSize = 11.sp, color = TextTertiary)
                            }

                            val badgeColor = Color(entry.status.badgeColorHex)
                            Surface(
                                color = badgeColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    entry.status.label,
                                    color = badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(entry.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GamingSurfaceLight.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Detail: ${entry.technicalDetails}",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                        }

                        entry.actionHint?.let { hint ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(hint, fontSize = 11.sp, color = ElectricCyan, fontWeight = FontWeight.Medium)
                            }
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
