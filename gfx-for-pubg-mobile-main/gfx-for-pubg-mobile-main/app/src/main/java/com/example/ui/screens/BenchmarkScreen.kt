package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.MetricBox
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchmarkScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onRunBenchmark: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Benchmark & Comparison", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
                    listOf("Results", "Graphs", "Summary").forEachIndexed { index, title ->
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
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) ElectricCyan else TextSecondary
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // Results Tab: 3-Column Comparative Table
                item {
                    GamingCard(backgroundColor = GamingSurface) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Metric", modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = TextMuted)
                            Text("Balanced", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary, textAlign = TextAlign.Center)
                            Text("Stable Comp.", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan, textAlign = TextAlign.Center)
                            Text("Raw Perf.", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VividViolet, textAlign = TextAlign.Center)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = GamingCardBorder, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        BenchmarkTableRow("Avg FPS", "72.4", "87.6", "95.2", highlightCol = 3)
                        BenchmarkTableRow("Avg Frame Time", "13.8 ms", "11.4 ms", "9.8 ms", highlightCol = 3)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Key Metrics", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))

                        BenchmarkTableRow("Stability (p95)", "58%", "76%", "91%", highlightCol = 3)
                        BenchmarkTableRow("Stutter Rate", "4.2%", "2.1%", "0.8%", highlightCol = 3)
                        BenchmarkTableRow("Battery (/hr)", "14%", "18%", "24%", highlightCol = 1)
                        BenchmarkTableRow("Temp (°C)", "41°C", "45°C", "52°C", highlightCol = 1)
                    }
                }
            } else if (selectedTab == 1) {
                // Graphs Tab: Visual Comparison Bars
                item {
                    GamingCard(backgroundColor = GamingSurface) {
                        Text("Average Sustained FPS Comparison", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))

                        CompareBar("Balanced Mode", 72.4f, 120f, TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        CompareBar("Stable Competitive", 87.6f, 120f, ElectricCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        CompareBar("Raw Performance Mode", 95.2f, 120f, VividViolet)
                    }
                }

                item {
                    GamingCard(backgroundColor = GamingSurface) {
                        Text("P95 Frame Stability Percentage (Higher is Better)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))

                        CompareBar("Balanced Mode", 58f, 100f, TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        CompareBar("Stable Competitive", 76f, 100f, ElectricCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        CompareBar("Raw Performance Mode", 91f, 100f, NeonGreenActive)
                    }
                }
            } else {
                // Summary Tab
                item {
                    GamingCard(
                        borderColor = NeonGreenActive.copy(alpha = 0.4f),
                        backgroundColor = GamingSurface
                    ) {
                        Text("Engineering Benchmark Verdict", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Raw Performance Mode provided +7.6 FPS higher sustained delivery over Stable Competitive and reduced stutter rate from 2.1% to 0.8%. However, temperature climbed by +7°C (52°C peak), making an external phone cooler highly recommended for extended sessions.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }

                item {
                    GamingCard(backgroundColor = GamingSurfaceVariant) {
                        Text("Key Benchmark Conditions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Target Game: ${uiState.selectedGame.displayName} (${uiState.selectedGame.variantLabel})", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Device: ${uiState.deviceSummary.model} (Android ${uiState.deviceSummary.androidVersion})", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Graphics Preset: Smooth + 90 FPS + Classic Style", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• External Cooler: ${if (uiState.rawPerformanceState.isExternalCoolerActive) "Active (Heat dissipation accounted)" else "Inactive"}", fontSize = 11.sp, color = ElectricCyan)
                    }
                }
            }

            // Benchmark Progress Indicator
            if (uiState.benchmarkRunning) {
                item {
                    GamingCard(backgroundColor = GamingSurfaceVariant) {
                        Text("Running Performance Benchmark Session...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uiState.benchmarkProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ElectricCyan,
                            trackColor = GamingCardBorder
                        )
                    }
                }
            }

            // Run Benchmark Hero Action
            item {
                GamingButton(
                    text = if (uiState.benchmarkRunning) "Benchmarking..." else "Run Benchmark",
                    onClick = onRunBenchmark,
                    enabled = !uiState.benchmarkRunning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_benchmark_button")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CompareBar(label: String, value: Float, max: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(String.format("%.1f", value), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (value / max).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = GamingCardBorder
        )
    }
}

@Composable
private fun BenchmarkTableRow(
    label: String,
    col1: String,
    col2: String,
    col3: String,
    highlightCol: Int = 0
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = TextSecondary)
        Text(
            text = col1,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = if (highlightCol == 1) NeonGreenActive else TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = col2,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = if (highlightCol == 2) ElectricCyan else TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = col3,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlightCol == 3) VividViolet else TextPrimary,
            textAlign = TextAlign.Center
        )
    }
}
