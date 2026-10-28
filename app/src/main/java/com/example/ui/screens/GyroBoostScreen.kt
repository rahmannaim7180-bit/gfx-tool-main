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
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GyroMode
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GyroBoostScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onToggleGyroBoost: (Boolean) -> Unit,
    onSetGyroMode: (GyroMode) -> Unit,
    onUpdateSensitivity: (Int, Int) -> Unit
) {
    val config = uiState.gyroConfig

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Gyro Boost Engine",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Hardware Sensor Cadence & Aim Smoothing",
                            fontSize = 11.sp,
                            color = NeonGreenActive
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("gyro_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GamingDarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = GamingDarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Master Boost Switch Card
            item {
                GamingCard(
                    borderColor = if (config.isEnabled) NeonGreenActive else GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GYROSCOPE BOOST OPTIMIZATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (config.isEnabled) "ACTIVE (High Sampling Lock)" else "STANDBY",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (config.isEnabled) NeonGreenActive else TextPrimary
                            )
                        }
                        Switch(
                            checked = config.isEnabled,
                            onCheckedChange = { onToggleGyroBoost(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GamingDarkBackground,
                                checkedTrackColor = NeonGreenActive,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = GamingSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "SELECT BOOSTING PROFILE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            // 1. Bypass Mode
            item {
                GyroProfileSelectionCard(
                    title = "Bypass Mode",
                    badge = "0ms Added Latency",
                    description = "Raw sensor passthrough with zero filtering. Direct 1:1 hardware readout for players who prefer natural unprocessed sensor behavior.",
                    isSelected = config.mode == GyroMode.RAW_PASSTHROUGH,
                    onClick = { onSetGyroMode(GyroMode.RAW_PASSTHROUGH) }
                )
            }

            // 2. Standard Profile
            item {
                GyroProfileSelectionCard(
                    title = "Standard Profile",
                    badge = "Balanced Aim",
                    description = "Lightweight sensor jitter attenuation. Smooths micro-tremors during close and mid-range combat without causing aim lag or drag.",
                    isSelected = config.mode == GyroMode.LIGHT_FILTER,
                    onClick = { onSetGyroMode(GyroMode.LIGHT_FILTER) }
                )
            }

            // 3. High Profile
            item {
                GyroProfileSelectionCard(
                    title = "High Profile",
                    badge = "120 FPS Competitive",
                    description = "Enhanced motion stabilization and recoil flick tracking. Ideal for aggressive assault rifle spray control and 90/120 FPS high refresh panels.",
                    isSelected = config.mode == GyroMode.AGGRESSIVE_STABILIZATION,
                    onClick = { onSetGyroMode(GyroMode.AGGRESSIVE_STABILIZATION) }
                )
            }

            // 4. Highest Profile
            item {
                GyroProfileSelectionCard(
                    title = "Highest Profile",
                    badge = "Ultra Scope Lock",
                    description = "Maximum micro-shake cancellation. Locks long-range scopes (4x, 6x, 8x) dead on target for precision sniper flicking with anti-drift compensation.",
                    isSelected = config.mode == GyroMode.PREDICTIVE_AIM,
                    onClick = { onSetGyroMode(GyroMode.PREDICTIVE_AIM) }
                )
            }

            // Sensitivity Multiplier Controls
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text(
                        text = "SENSITIVITY MULTIPLIER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Horizontal Axis (Yaw)", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${config.xMultiplier}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                    }
                    Slider(
                        value = config.xMultiplier,
                        onValueChange = { onUpdateSensitivity((it * 100).toInt(), (config.yMultiplier * 100).toInt()) },
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreenActive,
                            activeTrackColor = NeonGreenActive,
                            inactiveTrackColor = GamingCardBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Vertical Axis (Pitch / Recoil)", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${config.yMultiplier}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                    }
                    Slider(
                        value = config.yMultiplier,
                        onValueChange = { onUpdateSensitivity((config.xMultiplier * 100).toInt(), (it * 100).toInt()) },
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreenActive,
                            activeTrackColor = NeonGreenActive,
                            inactiveTrackColor = GamingCardBorder
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GyroProfileSelectionCard(
    title: String,
    badge: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GamingSurfaceVariant else GamingSurface)
            .border(
                1.5.dp,
                if (isSelected) NeonGreenActive else GamingCardBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) NeonGreenActive else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(
                        text = badge,
                        color = if (isSelected) NeonGreenActive else TextSecondary
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = NeonGreenActive,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
