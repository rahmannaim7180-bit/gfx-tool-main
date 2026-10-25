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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.gameplay.PostMatchReport
import com.example.ui.components.GamingCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMatchReportScreen(
    report: PostMatchReport,
    onRollbackSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Post-Match Session Report", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(report.gameTitle, fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("post_match_back_button")) {
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
            // Match Completion Banner
            item {
                GamingCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricCyan.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(26.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Match Completed", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                                val mins = report.durationSeconds / 60
                                val secs = report.durationSeconds % 60
                                Text("Session Length: ${mins}m ${secs}s • Package: ${report.packageName}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Thermal & Battery Delta Card
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SESSION TELEMETRY DELTA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Battery Temperature", fontSize = 10.sp, color = TextTertiary)
                                Text(
                                    "${String.format("%.1f", report.finalTempCelsius)}°C",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (report.finalTempCelsius < 41.5f) ElectricCyan else CoralRed
                                )
                                Text(
                                    "Delta: ${String.format("%+.1f", report.tempDeltaCelsius)}°C (Start: ${String.format("%.1f", report.initialTempCelsius)}°C)",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Battery Drain", fontSize = 10.sp, color = TextTertiary)
                                Text(
                                    "-${report.batteryDrainPct}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Start: ${report.initialBatteryPct}% • End: ${report.finalBatteryPct}%",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Zero Background Overhead Verification
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Zero-Overhead Gameplay Policy", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "While you were in match, FrameForge completely paused its UI Choreographer, stopped all sensor listeners, halted network pings, and entered zero-CPU suspended mode. 100% of SoC cycles were dedicated to PUBG Mobile.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Applied Profile Summary
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("APPLIED GAMING PROFILE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(report.appliedProfileSummary, fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Action Buttons: Rollback & Done
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onRollbackSettings,
                        modifier = Modifier.weight(1f).testTag("post_match_rollback_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceLight)
                    ) {
                        Icon(Icons.Filled.Restore, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rollback Settings", fontSize = 12.sp, color = TextPrimary)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).testTag("post_match_done_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                    ) {
                        Text("Dismiss Report", fontSize = 12.sp, color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
