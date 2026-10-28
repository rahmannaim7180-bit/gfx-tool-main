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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
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

data class WhitelistDeviceOption(
    val id: String,
    val name: String,
    val subtitle: String,
    val targetFps: Int,
    val benefits: String
)

val WHITELIST_DEVICES = listOf(
    WhitelistDeviceOption(
        id = "iphone_18_promax",
        name = "iPhone 18 Pro Max",
        subtitle = "iOS Ultra 120 FPS Protocol",
        targetFps = 120,
        benefits = "120 FPS locked, zero-deadzone gyro, 100% bullet registration, ultra-fast scope switch"
    ),
    WhitelistDeviceOption(
        id = "s26_ultra",
        name = "Samsung Galaxy S26 Ultra",
        subtitle = "Snapdragon 8 Gen 5 Extreme",
        targetFps = 120,
        benefits = "Vulkan 1.3 pipeline, extreme shader pre-caching, sustainable 120 FPS"
    ),
    WhitelistDeviceOption(
        id = "redmagic_12_pro",
        name = "RedMagic 12 Pro",
        subtitle = "Cyber E-Sports Gaming Rig",
        targetFps = 144,
        benefits = "144Hz panel sync, 960Hz touch sampling rate emulation, raw frame pacing"
    ),
    WhitelistDeviceOption(
        id = "native_device",
        name = "Native Device Profile",
        subtitle = "System Standard Mode",
        targetFps = 90,
        benefits = "Uses your device's default build properties and thermal limits"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceEngineScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSelectMode: (PerformanceMode) -> Unit,
    onToggleExternalCooler: (Boolean) -> Unit,
    onApplyOptimizations: () -> Unit,
    onRestoreSettings: () -> Unit,
    onSelectWhitelistDevice: (String) -> Unit = {}
) {
    var selectedDevice by remember { mutableStateOf("iPhone 18 Pro Max") }
    var isIosOptimizationActive by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Game Optimization & Whitelist",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "iOS-Tier 120 FPS, Bullet Registration & Whitelist Engine",
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Whitelist Active Banner
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
                                text = "SERVER WHITELIST DEVICE SPOOFING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$selectedDevice (Whitelisted)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreenActive
                            )
                        }
                        StatusBadge(text = "120 FPS UNLOCKED", color = NeonGreenActive)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Game server identifies this device as a flagship whitelisted model, unlocking the highest 120 FPS graphics tier, high-frequency tickrate server sync, and minimum touch queue delay.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            // Phone Selection Header
            item {
                Text(
                    text = "SELECT SERVER WHITELIST MODEL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            // Whitelist Devices List
            items(WHITELIST_DEVICES) { dev ->
                val isSelected = selectedDevice == dev.name
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
                        .clickable {
                            selectedDevice = dev.name
                            onSelectWhitelistDevice(dev.name)
                            onSelectMode(PerformanceMode.RAW_PERFORMANCE)
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = dev.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NeonGreenActive else TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(
                                    text = "${dev.targetFps} FPS",
                                    color = if (isSelected) NeonGreenActive else TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = dev.subtitle,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dev.benefits,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = NeonGreenActive,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // iOS-Level Combat Optimizations Card
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text(
                        text = "IOS-TIER MATCH COMBAT ENHANCEMENTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CombatEnhancementRow(
                        title = "100% Bullet Registration & Network Pacing",
                        description = "TCP_NODELAY and UDP socket pacing eliminates ghost hitmarkers during close spray fights.",
                        isActive = isIosOptimizationActive
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CombatEnhancementRow(
                        title = "Instant ADS Scope-In & Gun Switch",
                        description = "Trims animation queue delays for faster scope opening and immediate weapon swap response.",
                        isActive = isIosOptimizationActive
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CombatEnhancementRow(
                        title = "iOS-Grade Gyro Aim Assist Curve",
                        description = "Zero-deadzone gyro motion tracking with aim assist magnetism alignment.",
                        isActive = isIosOptimizationActive
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CombatEnhancementRow(
                        title = "Anti-Thermal 120 FPS Frequency Lock",
                        description = "CPU/GPU performance hint override to prevent frame drops in intense gunfights.",
                        isActive = isIosOptimizationActive
                    )
                }
            }

            // Apply Actions
            item {
                GamingButton(
                    text = "APPLY IOS-LEVEL 120 FPS & AIM OPTIMIZATIONS",
                    onClick = {
                        isIosOptimizationActive = true
                        onApplyOptimizations()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "apply_ios_optimization_button"
                )
            }

            item {
                OutlinedButton(
                    onClick = onRestoreSettings,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Default System Profile", fontSize = 13.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CombatEnhancementRow(
    title: String,
    description: String,
    isActive: Boolean
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
            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Speed,
            contentDescription = null,
            tint = if (isActive) NeonGreenActive else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}
