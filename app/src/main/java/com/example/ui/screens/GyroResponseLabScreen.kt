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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.sensors.CalibrationState
import com.example.core.sensors.GyroDiagnosticTelemetry
import com.example.core.sensors.GyroFilterProfile
import com.example.core.sensors.GyroHardwareSpecs
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GyroResponseLabScreen(
    specs: GyroHardwareSpecs,
    telemetry: GyroDiagnosticTelemetry,
    onBack: () -> Unit,
    onStartTesting: () -> Unit,
    onStopTesting: () -> Unit,
    onSelectProfile: (GyroFilterProfile) -> Unit,
    onStartStationaryCalibration: () -> Unit,
    onStartRotationTest: () -> Unit,
    onResetCalibration: () -> Unit
) {
    var isTestingActive by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onStartTesting()
        isTestingActive = true
        onDispose {
            onStopTesting()
            isTestingActive = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gyro Response Lab", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("iQOO-Style Stabilization • Telemetry • Low-Latency Filter", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("gyro_lab_back_button")) {
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
            // Interactive Gyroscope Crosshair Reticle Canvas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingSurfaceBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val maxRadius = (size.minDimension / 2f) - 10f

                        // Outer reticle rings
                        drawCircle(
                            color = GamingSurfaceBorder,
                            radius = maxRadius,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.5f)
                        )
                        drawCircle(
                            color = GamingSurfaceBorder.copy(alpha = 0.5f),
                            radius = maxRadius * 0.6f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1f)
                        )
                        drawCircle(
                            color = ElectricCyan.copy(alpha = 0.2f),
                            radius = maxRadius * 0.25f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1f)
                        )

                        // Crosshair axis lines
                        drawLine(
                            color = GamingSurfaceBorder,
                            start = Offset(centerX - maxRadius, centerY),
                            end = Offset(centerX + maxRadius, centerY),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = GamingSurfaceBorder,
                            start = Offset(centerX, centerY - maxRadius),
                            end = Offset(centerX, centerY + maxRadius),
                            strokeWidth = 1f
                        )

                        // Active reticle position driven by real filtered gyro data
                        val reticleOffsetX = (telemetry.filteredZ * 35f).coerceIn(-maxRadius, maxRadius)
                        val reticleOffsetY = (telemetry.filteredX * 35f).coerceIn(-maxRadius, maxRadius)

                        // Reticle dot
                        drawCircle(
                            color = ElectricCyan,
                            radius = 8f,
                            center = Offset(centerX + reticleOffsetX, centerY + reticleOffsetY)
                        )
                        drawCircle(
                            color = ElectricCyan.copy(alpha = 0.35f),
                            radius = 16f,
                            center = Offset(centerX + reticleOffsetX, centerY + reticleOffsetY)
                        )
                    }

                    // Reticle overlay stats
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            "LIVE RETICLE RESPONSE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "X: ${String.format("%+.2f", telemetry.filteredX)}  Y: ${String.format("%+.2f", telemetry.filteredY)}  Z: ${String.format("%+.2f", telemetry.filteredZ)} rad/s",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        color = GamingDarkBackground.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GamingSurfaceBorder)
                    ) {
                        Text(
                            "${telemetry.measuredSamplingHz.toInt()} Hz Hardware Stream",
                            color = ElectricCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Real-time Jitter & Interval Waveform Graph
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("LIVE ROTATION WAVEFORM (Z-AXIS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                            Text("Raw vs Filtered", fontSize = 10.sp, color = TextTertiary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        val rawList = telemetry.historyRawZ
                        val filtList = telemetry.historyFilteredZ

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(GamingSurfaceLight.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                if (rawList.size > 2) {
                                    val stepX = size.width / (rawList.size - 1).coerceAtLeast(1)
                                    val midY = size.height / 2f

                                    // Draw raw waveform (grey dashed)
                                    val rawPath = Path()
                                    rawList.forEachIndexed { idx, v ->
                                        val x = idx * stepX
                                        val y = (midY - (v * 15f)).coerceIn(0f, size.height)
                                        if (idx == 0) rawPath.moveTo(x, y) else rawPath.lineTo(x, y)
                                    }
                                    drawPath(rawPath, color = TextTertiary.copy(alpha = 0.6f), style = Stroke(width = 1.5f))

                                    // Draw filtered waveform (ElectricCyan)
                                    if (filtList.size == rawList.size) {
                                        val filtPath = Path()
                                        filtList.forEachIndexed { idx, v ->
                                            val x = idx * stepX
                                            val y = (midY - (v * 15f)).coerceIn(0f, size.height)
                                            if (idx == 0) filtPath.moveTo(x, y) else filtPath.lineTo(x, y)
                                        }
                                        drawPath(filtPath, color = ElectricCyan, style = Stroke(width = 2.5f))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Interval: ${String.format("%.2f", telemetry.currentIntervalMs)} ms", fontSize = 11.sp, color = TextSecondary)
                            Text("Jitter: ±${String.format("%.2f", telemetry.intervalJitterMs)} ms", fontSize = 11.sp, color = if (telemetry.intervalJitterMs < 1.0f) ElectricCyan else CoralRed)
                            Text("Dropped: ${telemetry.droppedEventsCount}", fontSize = 11.sp, color = if (telemetry.droppedEventsCount == 0L) ElectricCyan else CoralRed)
                        }
                    }
                }
            }

            // Stabilization Profile Selector (iQOO-Style)
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("iQOO-STYLE STABILIZATION MODES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Select a low-latency signal processing profile to simulate in the test reticle. FrameForge uses adaptive filters that preserve rapid flicks while stabilizing micro-aim.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        GyroFilterProfile.values().forEach { profile ->
                            val isSelected = telemetry.activeProfile == profile
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectricCyanContainer.copy(alpha = 0.35f) else GamingSurfaceLight.copy(alpha = 0.25f))
                                    .border(
                                        1.dp,
                                        if (isSelected) ElectricCyan else GamingSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectProfile(profile) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(profile.title, fontWeight = FontWeight.Bold, color = if (isSelected) ElectricCyan else TextPrimary, fontSize = 13.sp)
                                    Text(profile.subtitle, fontSize = 10.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("+${profile.latencyOverheadMs}ms Delay", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) ElectricCyan else TextTertiary)
                                    Text("${profile.noiseReductionPct}% Filter", fontSize = 10.sp, color = TextTertiary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Guided Calibration Section
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CALIBRATION & DIAGNOSTIC VERIFICATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            telemetry.calibrationVerdict,
                            fontSize = 12.sp,
                            color = if (telemetry.calibrationState == CalibrationState.IDLE) TextSecondary else ElectricCyan,
                            fontWeight = FontWeight.Medium
                        )

                        if (telemetry.calibrationProgress > 0f && telemetry.calibrationProgress < 1f) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { telemetry.calibrationProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = ElectricCyan,
                                trackColor = GamingSurfaceBorder
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onStartStationaryCalibration,
                                modifier = Modifier.weight(1f).testTag("gyro_calib_stationary_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceLight)
                            ) {
                                Text("Stationary Bias", fontSize = 11.sp, color = TextPrimary)
                            }
                            Button(
                                onClick = onStartRotationTest,
                                modifier = Modifier.weight(1f).testTag("gyro_calib_rotation_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceLight)
                            ) {
                                Text("Rotation 90°", fontSize = 11.sp, color = TextPrimary)
                            }
                            OutlinedButton(
                                onClick = onResetCalibration,
                                modifier = Modifier.testTag("gyro_calib_reset_btn"),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GamingSurfaceBorder)
                            ) {
                                Text("Reset", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Hardware Specifications Card
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("SENSOR HARDWARE SPECIFICATIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Sensor Model", fontSize = 10.sp, color = TextTertiary)
                                Text(specs.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Vendor", fontSize = 10.sp, color = TextTertiary)
                                Text(specs.vendor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Reporting Mode", fontSize = 10.sp, color = TextTertiary)
                                Text(specs.reportingMode, fontSize = 12.sp, color = ElectricCyan)
                            }
                            Column {
                                Text("Min Delay / Max Hz", fontSize = 10.sp, color = TextTertiary)
                                Text("${specs.minDelayUs} µs (${specs.maxSamplingRateHz.toInt()} Hz)", fontSize = 12.sp, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Power Draw", fontSize = 10.sp, color = TextTertiary)
                                Text("${specs.powerConsumptionMa} mA", fontSize = 12.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            // Android Sandbox & OriginOS Policy Notice (Mandatory Honesty)
            item {
                GamingCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGold.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = NeonGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Zero-Touch PUBG Policy & OEM Guide", fontWeight = FontWeight.Bold, color = NeonGold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "• Android Sandbox Notice: A non-root application cannot universally inject or replace another application's motion sensor stream.\n" +
                            "• Safe Operation: FrameForge NEVER modifies your in-game sensitivity values or edits UserCustom.ini. Your in-game recoil settings remain 100% untouched.\n" +
                            "• iQOO / vivo (OriginOS 6) Integration: To apply system-level high-frequency gyroscope responsiveness in PUBG Mobile, swipe the Game Sidebar -> tap 'Motion Control' -> set 'Gyroscope Sensitivity' to 'Highest'.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
