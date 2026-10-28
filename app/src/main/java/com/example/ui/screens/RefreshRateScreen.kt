package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.device.DisplayModeInfo
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshRateScreen(
    uiState: UiState,
    supportedModes: List<DisplayModeInfo>,
    onBack: () -> Unit,
    onSetRefreshRateMode: (String) -> Unit
) {
    var selectedModeId by remember { mutableStateOf(supportedModes.firstOrNull { it.isCurrent }?.modeId?.toString() ?: "0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Display Refresh Rate Engine", color = TextPrimary, fontWeight = FontWeight.Bold)
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
                GamingCard {
                    Text("Active Display Mode", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.metrics.refreshRateHz} Hz (Hardware Verified)",
                        color = ElectricCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "FrameForge dynamically monitors display refresh rate lock and flags frame pacing stutter.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            item {
                Text("Supported Refresh Rate Modes", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(supportedModes) { mode ->
                val isSelected = selectedModeId == mode.modeId.toString()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricCyanContainer else GamingSurface)
                        .border(1.dp, if (isSelected) ElectricCyan else GamingCardBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            selectedModeId = mode.modeId.toString()
                            onSetRefreshRateMode(mode.modeId.toString())
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${mode.refreshRate.toInt()} Hz (${mode.width}x${mode.height})",
                            color = if (isSelected) ElectricCyan else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (mode.isCurrent) "Current System Active Mode" else "Supported Mode ID: ${mode.modeId}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = ElectricCyan)
                    }
                }
            }
        }
    }
}
