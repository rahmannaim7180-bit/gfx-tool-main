package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingCard
import com.example.ui.components.MetricBox
import com.example.ui.components.RealTimeLineChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FramePacingScreen(
    uiState: UiState,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val metrics = uiState.diagnosticMetrics

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Frame Pacing & Performance", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            // Tab Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingSurfaceVariant)
                        .padding(4.dp)
                ) {
                    listOf("FPS", "Frame Time", "Stutter", "Graph").forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedTab == index) ElectricCyanContainer else Color.Transparent)
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) ElectricCyan else TextSecondary
                            )
                        }
                    }
                }
            }

            // Big KPIs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        title = "FPS",
                        value = "${metrics.fpsCurrent}",
                        subtitle = "Avg FPS: ${metrics.fpsAvg}",
                        color = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Frame Time (ms)",
                        value = "${metrics.frameTimeMsAvg}",
                        subtitle = "Target: 8.3ms",
                        color = VividViolet,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Real-Time Frame-Time Chart
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Live Frame Time History (ms)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("120 Hz Target", fontSize = 11.sp, color = NeonGreenActive)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val chartData = if (metrics.frameTimeHistory.isNotEmpty()) {
                        metrics.frameTimeHistory
                    } else {
                        listOf(8.4f, 8.5f, 9.2f, 8.3f, 11.4f, 8.2f, 8.4f, 16.7f, 8.5f, 9.0f, 8.4f, 8.3f, 8.5f)
                    }

                    RealTimeLineChart(
                        dataPoints = chartData,
                        lineColor = NeonGreenActive,
                        minVal = 5f,
                        maxVal = 30f
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 min", fontSize = 10.sp, color = TextMuted)
                        Text("5 min", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }

            // Frame Time Percentiles
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text("Frame Time Percentiles", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PercentileItem(label = "P50 (Median)", value = "${metrics.p50Ms} ms", color = ElectricCyan)
                        PercentileItem(label = "P95", value = "${metrics.p95Ms} ms", color = VividViolet)
                        PercentileItem(label = "P99", value = "${metrics.p99Ms} ms", color = NeonAmberWarning)
                    }
                }
            }

            // Stutter Events & Refresh Rate
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        title = "Stutter Events",
                        value = "${metrics.stutterCount}",
                        subtitle = "(in 5 min)",
                        color = NeonAmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Refresh Rate",
                        value = "${metrics.refreshRateHz} Hz",
                        subtitle = "(Stable locked)",
                        color = NeonGreenActive,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PercentileItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
