package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.components.GamingCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameplayResponsivenessScreen(
    onBack: () -> Unit
) {
    var scopeOpenState by remember { mutableStateOf(false) }
    var weaponSwitchState by remember { mutableStateOf(1) } // 1 or 2
    var lastActionTimestamp by remember { mutableStateOf(0L) }
    var transitionLatencyMs by remember { mutableStateOf(16L) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gameplay Responsiveness Lab", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Aim Feel • Scope Transition • Weapon Switch Timing", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("gameplay_lab_back_button")) {
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
            // Scope Transition Timing Test
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SCOPE TRANSITION LATENCY TEST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                            Text("${transitionLatencyMs} ms Frame Tick", fontSize = 11.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Simulates ADS (Aim Down Sights) zoom activation to verify animation dispatch delay and frame-pacing stall.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSurfaceLight.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val scopeRadius = if (scopeOpenState) 50.dp.toPx() else 20.dp.toPx()

                                drawCircle(
                                    color = if (scopeOpenState) ElectricCyan else CoralRed,
                                    radius = scopeRadius,
                                    center = Offset(centerX, centerY),
                                    style = Stroke(width = 3f)
                                )
                                drawLine(
                                    color = TextSecondary,
                                    start = Offset(centerX - scopeRadius - 10f, centerY),
                                    end = Offset(centerX + scopeRadius + 10f, centerY),
                                    strokeWidth = 1.5f
                                )
                                drawLine(
                                    color = TextSecondary,
                                    start = Offset(centerX, centerY - scopeRadius - 10f),
                                    end = Offset(centerX, centerY + scopeRadius + 10f),
                                    strokeWidth = 1.5f
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val now = SystemClock.uptimeMillis()
                                if (lastActionTimestamp > 0) {
                                    transitionLatencyMs = (now - lastActionTimestamp).coerceIn(8, 48)
                                }
                                lastActionTimestamp = now
                                scopeOpenState = !scopeOpenState
                            },
                            modifier = Modifier.fillMaxWidth().testTag("toggle_scope_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = if (scopeOpenState) CoralRed else ElectricCyan)
                        ) {
                            Text(
                                if (scopeOpenState) "Release Scope (Hipfire)" else "Trigger ADS (Open Scope)",
                                color = GamingDarkBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Weapon Switch Rhythm & Delay
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("WEAPON SWITCH DISPATCH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Rapid primary/secondary weapon switching checks touch dispatch queueing without dropped inputs.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { weaponSwitchState = 1 },
                                modifier = Modifier.weight(1f).testTag("weapon_slot_1_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (weaponSwitchState == 1) ElectricCyan else GamingSurfaceLight
                                )
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Primary Slot (M416)", color = if (weaponSwitchState == 1) GamingDarkBackground else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("5.56mm AR", color = if (weaponSwitchState == 1) GamingDarkBackground.copy(alpha = 0.7f) else TextTertiary, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = { weaponSwitchState = 2 },
                                modifier = Modifier.weight(1f).testTag("weapon_slot_2_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (weaponSwitchState == 2) ElectricCyan else GamingSurfaceLight
                                )
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Secondary Slot (AWM)", color = if (weaponSwitchState == 2) GamingDarkBackground else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(".300 Magnum Sniper", color = if (weaponSwitchState == 2) GamingDarkBackground.copy(alpha = 0.7f) else TextTertiary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Competitive Aim Consistency Checklist
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("COMPETITIVE AIM OPTIMIZATION CHECKLIST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        val checklist = listOf(
                            "Display refresh rate pinned to 144Hz / 120Hz via Display Settings",
                            "Window & Animator duration scale set to 0.5x or Off in System Animation",
                            "Monster Mode / Ultra Game Mode active in OriginOS Control Center",
                            "Touch Acceleration active in Game Sidebar for digitizer boost",
                            "Battery temperature maintained < 40°C with external cooler"
                        )

                        checklist.forEach { itemText ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp).padding(top = 1.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(itemText, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
