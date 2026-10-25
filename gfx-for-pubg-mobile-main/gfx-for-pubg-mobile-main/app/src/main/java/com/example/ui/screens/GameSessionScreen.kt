package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.RealTimeLineChart
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSessionScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onEndSession: () -> Unit
) {
    val durationMin = uiState.sessionDurationSeconds / 60
    val durationSec = uiState.sessionDurationSeconds % 60
    val formattedDuration = "${durationMin}m ${durationSec}s"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Game Session", fontWeight = FontWeight.Bold, color = TextPrimary)
                        StatusBadge(text = "Active", color = NeonGreenActive)
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
            // Running Game Card
            item {
                GamingCard(
                    borderColor = NeonGreenActive.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = NeonGreenActive,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.selectedGame.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Running • $formattedDuration",
                                fontSize = 12.sp,
                                color = NeonGreenActive,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Big 3 Telemetry KPIs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SessionMetricCard(
                        label = "FPS",
                        value = "${uiState.diagnosticMetrics.fpsCurrent}",
                        color = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    SessionMetricCard(
                        label = "Temp",
                        value = "${uiState.diagnosticMetrics.tempCelsius.toInt()}°C",
                        color = NeonAmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                    SessionMetricCard(
                        label = "Battery",
                        value = "${uiState.diagnosticMetrics.batteryPct}%",
                        color = NeonGreenActive,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Live FPS & Temperature Chart
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Live Telemetry Timeline", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("FPS • Temp", fontSize = 11.sp, color = ElectricCyan)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val chartData = if (uiState.diagnosticMetrics.fpsHistory.isNotEmpty()) {
                        uiState.diagnosticMetrics.fpsHistory
                    } else {
                        listOf(88.4f, 89.2f, 89.7f, 89.0f, 90.1f, 88.7f, 89.7f, 89.5f)
                    }

                    RealTimeLineChart(
                        dataPoints = chartData,
                        lineColor = ElectricCyan,
                        minVal = 60f,
                        maxVal = 120f
                    )
                }
            }

            // Active Optimizations in-session
            item {
                Text("Active In-Game Optimizations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            item {
                GamingCard(backgroundColor = GamingSurface) {
                    SessionOptimizationItem("Performance Mode", "ON", ElectricCyan)
                    Spacer(modifier = Modifier.height(10.dp))
                    SessionOptimizationItem("GyroBoost (200%)", "ON", VividViolet)
                    Spacer(modifier = Modifier.height(10.dp))
                    SessionOptimizationItem("High Refresh Rate (120Hz)", "ON", NeonGreenActive)
                    Spacer(modifier = Modifier.height(10.dp))
                    SessionOptimizationItem("Background Activity", "REDUCED", ElectricCyan)
                }
            }

            // End Session Hero Button
            item {
                GamingButton(
                    text = "End Session",
                    onClick = onEndSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("end_game_session_button")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SessionMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GamingCard(
        modifier = modifier,
        backgroundColor = GamingSurfaceVariant
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun SessionOptimizationItem(
    title: String,
    status: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, color = TextPrimary)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
