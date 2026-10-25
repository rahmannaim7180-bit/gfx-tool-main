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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GamingCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(
    uiState: UiState,
    onBack: () -> Unit
) {
    var gameTurboEnabled by remember { mutableStateOf(true) }
    var selectedRefreshRate by remember { mutableStateOf("120 Hz") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            // Display & Refresh Section
            item {
                Text("Display & Refresh", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current Mode", fontSize = 14.sp, color = TextPrimary)
                        Text(
                            text = "${uiState.diagnosticMetrics.refreshRateHz} Hz (Auto)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GamingCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Set Refresh Rate", fontSize = 14.sp, color = TextPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("60 Hz", "90 Hz", "120 Hz").forEach { rate ->
                                val isSel = selectedRefreshRate == rate
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) ElectricCyanContainer else GamingSurfaceVariant)
                                        .border(1.dp, if (isSel) ElectricCyan else GamingCardBorder, RoundedCornerShape(6.dp))
                                        .clickable { selectedRefreshRate = rate }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        rate,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) ElectricCyan else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Power & Performance
            item {
                Text("Power & Performance", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Power Mode", fontSize = 14.sp, color = TextPrimary)
                        Text("High Performance", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VividViolet)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GamingCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Game Turbo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Prioritizes game thread scheduling", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = gameTurboEnabled,
                            onCheckedChange = { gameTurboEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = ElectricCyan)
                        )
                    }
                }
            }

            // Game Information
            item {
                Text("Game Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            item {
                GamingCard(backgroundColor = GamingSurface) {
                    DetailRow("Package Name", uiState.selectedGame.packageName)
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Version", uiState.selectedGame.installedVersion ?: "3.2.0")
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Target Engine", uiState.selectedGame.targetEngine)
                }
            }

            // Device Information
            item {
                Text("Device Information", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }

            item {
                GamingCard(backgroundColor = GamingSurface) {
                    DetailRow("Model", uiState.deviceSummary.model)
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Manufacturer", uiState.deviceSummary.manufacturer)
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Android OS", "Android ${uiState.deviceSummary.androidVersion} (API ${uiState.deviceSummary.apiLevel})")
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Total Memory", "${uiState.deviceSummary.totalRamMb} MB")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
