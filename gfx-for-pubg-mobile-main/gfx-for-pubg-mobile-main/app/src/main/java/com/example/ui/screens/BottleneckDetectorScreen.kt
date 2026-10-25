package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BottleneckSeverity
import com.example.ui.components.GamingCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottleneckDetectorScreen(
    uiState: UiState,
    onBack: () -> Unit
) {
    val analysis = uiState.bottleneckAnalysis

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bottleneck Detector", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            item {
                Text("Current Analysis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            // Primary Gauge Card matching screenshot
            item {
                GamingCard(
                    borderColor = VividViolet.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(VividVioletContainer)
                                .border(3.dp, VividViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("GPU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VividViolet)
                                Text("${analysis.primaryPercentage}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = analysis.primaryLabel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Shader complexity is primary limiter for 120Hz stability",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Load bars
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    LoadBarItem(label = "CPU", percentage = analysis.cpuLoadPct, color = ElectricCyan)
                    Spacer(modifier = Modifier.height(10.dp))
                    LoadBarItem(label = "GPU", percentage = analysis.gpuLoadPct, color = VividViolet)
                    Spacer(modifier = Modifier.height(10.dp))
                    LoadBarItem(label = "Memory", percentage = analysis.memoryLoadPct, color = BlueAccent)
                    Spacer(modifier = Modifier.height(10.dp))
                    LoadBarItem(label = "Thermal", percentage = analysis.thermalLoadPct, color = NeonAmberWarning)
                    Spacer(modifier = Modifier.height(10.dp))
                    LoadBarItem(label = "Network", percentage = 5, color = NeonGreenActive)
                }
            }

            // Top Bottlenecks Ranked List
            item {
                Text("Top Bottlenecks", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(analysis.topBottlenecks) { item ->
                GamingCard(backgroundColor = GamingSurfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        val badgeColor = when (item.severity) {
                            BottleneckSeverity.HIGH -> NeonRedAlert
                            BottleneckSeverity.MEDIUM -> NeonAmberWarning
                            BottleneckSeverity.LOW -> NeonGreenActive
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(item.severity.displayName, fontSize = 10.sp, color = badgeColor, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.description, fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Recommendation: ${item.recommendation}", fontSize = 11.sp, color = ElectricCyan)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun LoadBarItem(
    label: String,
    percentage: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text("$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = GamingCardBorder
        )
    }
}
