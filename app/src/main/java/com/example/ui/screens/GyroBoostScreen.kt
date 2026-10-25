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
import com.example.core.sensors.InGameSensitivityItem
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
    onToggleGyro: (Boolean) -> Unit,
    onSensitivityChanged: (Int, Int) -> Unit,
    onModeChanged: (GyroMode) -> Unit,
    onRunTest: () -> Unit,
    onCalibrateZeroBias: () -> Unit,
    guidedSensitivities: List<InGameSensitivityItem>
) {
    val gyroConfig = uiState.gyroConfig

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GyroBoost", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                        Text("Telemetry & In-Game Sensitivity Assistant", fontSize = 11.sp, color = VividViolet)
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
            // Interactive Gyroscope Reticle Canvas
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

                        // Outer reticle rings
                        drawCircle(
                            color = GamingCardBorder,
                            radius = 65.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = VividViolet.copy(alpha = 0.4f),
                            radius = 45.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawCircle(
                            color = ElectricCyan.copy(alpha = 0.3f),
                            radius = 20.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Crosshairs
                        drawLine(
                            color = VividViolet.copy(alpha = 0.5f),
                            start = Offset(center.x - 75.dp.toPx(), center.y),
                            end = Offset(center.x + 75.dp.toPx(), center.y),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = VividViolet.copy(alpha = 0.5f),
                            start = Offset(center.x, center.y - 75.dp.toPx()),
                            end = Offset(center.x, center.y + 75.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Dynamic reticle position based on live gyro filtered events
                        val offsetX = (gyroConfig.currentX * 15f).coerceIn(-40f, 40f)
                        val offsetY = (gyroConfig.currentY * 15f).coerceIn(-40f, 40f)

                        drawCircle(
                            color = ElectricCyan,
                            radius = 6.dp.toPx(),
                            center = Offset(center.x + offsetX, center.y + offsetY)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${gyroConfig.currentSamplingRateHz.toInt()} Hz • Jitter: ${gyroConfig.sampleJitterMs}ms",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Real Hardware Telemetry Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GamingCard(modifier = Modifier.weight(1f), backgroundColor = GamingSurface) {
                        Text("Sampling Rate", fontSize = 10.sp, color = TextMuted)
                        Text("${gyroConfig.currentSamplingRateHz.toInt()} Hz", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        Text("Hardware Max: ${gyroConfig.maxSamplingRateHz.toInt()}Hz", fontSize = 9.sp, color = TextSecondary)
                    }
                    GamingCard(modifier = Modifier.weight(1f), backgroundColor = GamingSurface) {
                        Text("Event Interval", fontSize = 10.sp, color = TextMuted)
                        Text("${String.format("%.2f", gyroConfig.observedIntervalMs)} ms", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                        Text("Jitter: ±${gyroConfig.sampleJitterMs}ms", fontSize = 9.sp, color = TextSecondary)
                    }
                    GamingCard(modifier = Modifier.weight(1f), backgroundColor = GamingSurface) {
                        Text("Precision Score", fontSize = 10.sp, color = TextMuted)
                        Text("${gyroConfig.testScore}/100", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VividViolet)
                        Text("Anti-Drift: Active", fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }

            // Honest OEM Integration Audit Banner
            item {
                GamingCard(
                    borderColor = VividViolet.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = VividViolet,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OriginOS Gyroscope Integration Audit",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "OriginOS does not provide third-party raw gyro multiplier APIs to apps without system signature. Direct memory injection into PUBG Mobile is blocked by anti-cheat and Android security. FrameForge provides genuine hardware telemetry, zero-bias calibration, and an In-Game Sensitivity Calculator.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // In-Game Sensitivity Multiplier Assistant
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text(
                        text = "In-Game Sensitivity Assistant",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Set your desired multiplier to calculate exact recommended in-game sensitivity values for PUBG Mobile",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sensitivity Multiplier", fontSize = 12.sp, color = TextPrimary)
                        Text("${gyroConfig.xSensitivityPercent}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    }

                    Slider(
                        value = gyroConfig.xSensitivityPercent.toFloat(),
                        onValueChange = { onSensitivityChanged(it.toInt(), it.toInt()) },
                        valueRange = 50f..200f,
                        steps = 30,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = GamingCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "RECOMMENDED IN-GAME SETTINGS (Apply inside PUBG Mobile > Sensitivity > Gyroscope):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    guidedSensitivities.forEach { scope ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = scope.scopeLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(text = scope.description, fontSize = 10.sp, color = TextMuted)
                            }
                            Text(
                                text = "${scope.recommendedSensitivity}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreenActive
                            )
                        }
                        HorizontalDivider(color = GamingCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }

            // Zero-Bias Calibration & Precision Test Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GamingButton(
                        text = "Calibrate Zero-Bias",
                        onClick = onCalibrateZeroBias,
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "calibrate_gyro_btn"
                    )
                    GamingButton(
                        text = "Run Precision Test",
                        onClick = onRunTest,
                        modifier = Modifier.weight(1f),
                        testTag = "run_gyro_test_btn"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
