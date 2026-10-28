package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Active Game Session", color = TextPrimary, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingDarkBackground)
            )
        },
        containerColor = GamingDarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        Column {
                            Text("SESSION STATUS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Game Focus Active", color = NeonGreenActive, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        StatusBadge(text = "0% OVERHEAD", color = NeonGreenActive)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Background telemetry is suspended while in-game. Sensor and GPU resources are prioritized exclusively for PUBG Mobile.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SessionMetricCard(
                        title = "Target FPS",
                        value = "${uiState.metrics.fpsCurrent.toInt()}",
                        color = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    SessionMetricCard(
                        title = "Thermal State",
                        value = uiState.thermalState.name,
                        color = when (uiState.thermalState.name) {
                            "COOL" -> NeonGreenActive
                            "WARM" -> ElectricCyan
                            "HOT" -> NeonAmberWarning
                            else -> NeonRedAlert
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                GamingButton(
                    text = "END SESSION & GENERATE REPORT",
                    onClick = onEndSession,
                    modifier = Modifier.fillMaxWidth(),
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
fun SessionMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GamingSurfaceVariant)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(title, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
