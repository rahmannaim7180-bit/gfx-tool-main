package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GyroBoostScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onToggleGyro: (Boolean) -> Unit,
    onSensitivityChanged: (Int, Int) -> Unit,
    onModeChanged: (GyroMode) -> Unit,
    onRunTest: () -> Unit
) {
    val gyroConfig = uiState.gyroConfig

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GyroBoost", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Enhanced Gyroscope Response", fontSize = 11.sp, color = VividViolet)
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
            // Interactive Phone & Gyroscope Reticle Canvas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GamingSurface)
                        .border(1.dp, VividViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Outer phone outline
                        drawRoundRect(
                            color = GamingCardBorder,
                            topLeft = Offset(center.x - 120.dp.toPx(), center.y - 70.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(240.dp.toPx(), 140.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Circular gyroscope reticle rings
                        drawCircle(
                            color = VividViolet.copy(alpha = 0.2f),
                            radius = 45.dp.toPx(),
                            center = center
                        )
                        drawCircle(
                            color = ElectricCyan.copy(alpha = 0.5f),
                            radius = 30.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Dynamic reticle offset based on actual gyro motion
                        val offsetX = (gyroConfig.currentX * 40f).coerceIn(-40f, 40f)
                        val offsetY = (gyroConfig.currentY * 40f).coerceIn(-40f, 40f)
                        val reticleCenter = Offset(center.x + offsetX, center.y + offsetY)

                        drawCircle(
                            color = ElectricCyan,
                            radius = 4.dp.toPx(),
                            center = reticleCenter
                        )
                        // Reticle crosshair ticks
                        drawLine(
                            color = ElectricCyan,
                            start = Offset(reticleCenter.x - 12.dp.toPx(), reticleCenter.y),
                            end = Offset(reticleCenter.x + 12.dp.toPx(), reticleCenter.y),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = ElectricCyan,
                            start = Offset(reticleCenter.x, reticleCenter.y - 12.dp.toPx()),
                            end = Offset(reticleCenter.x, reticleCenter.y + 12.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Floating telemetry tag
                    StatusBadge(
                        text = "Real-time: ${(gyroConfig.currentSamplingRateHz).toInt()} Hz",
                        color = NeonGreenActive,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    )
                }
            }

            // Enable GyroBoost Toggle
            item {
                GamingCard(
                    borderColor = if (gyroConfig.isEnabled) VividViolet.copy(alpha = 0.5f) else GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable GyroBoost",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "High-precision anti-drift sampling for PUBG Mobile",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = gyroConfig.isEnabled,
                            onCheckedChange = onToggleGyro,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextPrimary,
                                checkedTrackColor = VividViolet,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = GamingSurfaceVariant
                            ),
                            modifier = Modifier.testTag("gyroboost_switch")
                        )
                    }
                }
            }

            // Sensitivity (0 - 200%)
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text(
                        text = "Sensitivity (0 – 200%)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // X Axis
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("X Axis", fontSize = 13.sp, color = TextSecondary)
                        Text("${gyroConfig.xSensitivityPercent}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    }
                    Slider(
                        value = gyroConfig.xSensitivityPercent.toFloat(),
                        onValueChange = { onSensitivityChanged(it.roundToInt(), gyroConfig.ySensitivityPercent) },
                        valueRange = 0f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = GamingCardBorder
                        ),
                        modifier = Modifier.testTag("gyro_x_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Y Axis
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Y Axis", fontSize = 13.sp, color = TextSecondary)
                        Text("${gyroConfig.ySensitivityPercent}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VividViolet)
                    }
                    Slider(
                        value = gyroConfig.ySensitivityPercent.toFloat(),
                        onValueChange = { onSensitivityChanged(gyroConfig.xSensitivityPercent, it.roundToInt()) },
                        valueRange = 0f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = VividViolet,
                            activeTrackColor = VividViolet,
                            inactiveTrackColor = GamingCardBorder
                        ),
                        modifier = Modifier.testTag("gyro_y_slider")
                    )
                }
            }

            // Mode Selection
            item {
                Text(
                    text = "Response Mode",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GyroMode.values().forEach { mode ->
                        val isSelected = gyroConfig.mode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) VividVioletContainer else GamingSurfaceVariant)
                                .border(1.dp, if (isSelected) VividViolet else GamingCardBorder, RoundedCornerShape(8.dp))
                                .clickable { onModeChanged(mode) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) VividViolet else TextSecondary
                            )
                        }
                    }
                }
            }

            // Hardware Telemetry & Response Test
            item {
                GamingCard(
                    borderColor = GamingCardBorder,
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Gyro Response Test", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Sensor: ${gyroConfig.sensorModel}", fontSize = 11.sp, color = TextMuted)
                            Text("Jitter: ${gyroConfig.sampleJitterMs} ms", fontSize = 11.sp, color = NeonGreenActive)
                        }

                        GamingButton(
                            text = if (gyroConfig.isTestRunning) "Testing..." else "Run Test",
                            onClick = onRunTest,
                            modifier = Modifier.testTag("run_gyro_test_button")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
