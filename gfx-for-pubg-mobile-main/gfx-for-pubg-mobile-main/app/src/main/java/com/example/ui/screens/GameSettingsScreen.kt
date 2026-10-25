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
import com.example.data.model.*
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onUpdateProfile: (GraphicsProfile) -> Unit,
    onApply: () -> Unit
) {
    val currentProfile = uiState.activeProfile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            // Selected Game Banner
            item {
                GamingCard(
                    borderColor = ElectricCyan.copy(alpha = 0.3f),
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selected Game",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = uiState.selectedGame.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = uiState.selectedGame.variantLabel,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Graphics Quality Section
            item {
                Text(
                    text = "Graphics Settings (Recommended)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Graphics Quality",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GraphicsQuality.values().forEach { quality ->
                        val isSelected = currentProfile.quality == quality
                        QualityChip(
                            label = quality.displayName,
                            isSelected = isSelected,
                            onClick = { onUpdateProfile(currentProfile.copy(quality = quality)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Frame Rate Section
            item {
                Text(
                    text = "Frame Rate Target",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FrameRateChip(
                            label = "Smooth 40",
                            isSelected = currentProfile.frameRate == FrameRateTarget.MEDIUM,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.MEDIUM)) },
                            modifier = Modifier.weight(1f)
                        )
                        FrameRateChip(
                            label = "High 60",
                            isSelected = currentProfile.frameRate == FrameRateTarget.HIGH,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.HIGH)) },
                            modifier = Modifier.weight(1f)
                        )
                        FrameRateChip(
                            label = "Ultra 72",
                            isSelected = currentProfile.frameRate == FrameRateTarget.ULTRA,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.ULTRA)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FrameRateChip(
                            label = "Extreme 60",
                            isSelected = currentProfile.frameRate == FrameRateTarget.EXTREME,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.EXTREME)) },
                            modifier = Modifier.weight(1f)
                        )
                        FrameRateChip(
                            label = "90 FPS",
                            isSelected = currentProfile.frameRate == FrameRateTarget.FPS_90,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.FPS_90)) },
                            modifier = Modifier.weight(1f)
                        )
                        FrameRateChip(
                            label = "120 FPS",
                            isSelected = currentProfile.frameRate == FrameRateTarget.FPS_120,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.FPS_120)) },
                            modifier = Modifier.weight(1f)
                        )
                        FrameRateChip(
                            label = "144 FPS",
                            isSelected = currentProfile.frameRate == FrameRateTarget.FPS_144,
                            onClick = { onUpdateProfile(currentProfile.copy(frameRate = FrameRateTarget.FPS_144)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Style Section
            item {
                Text(
                    text = "Visual Style",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GraphicsStyle.values().forEach { style ->
                        val isSelected = currentProfile.style == style
                        QualityChip(
                            label = style.displayName,
                            isSelected = isSelected,
                            onClick = { onUpdateProfile(currentProfile.copy(style = style)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Resolution Section
            item {
                Text(
                    text = "Resolution / Render Scale",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ResolutionPreset.values().forEach { res ->
                        val isSelected = currentProfile.resolution == res
                        QualityChip(
                            label = res.displayName.split(" ")[0],
                            isSelected = isSelected,
                            onClick = { onUpdateProfile(currentProfile.copy(resolution = res)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Anti-cheat & Transparency Notice
            item {
                GamingCard(
                    borderColor = NeonAmberWarning.copy(alpha = 0.4f),
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Safe Policy",
                            tint = NeonAmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Anti-Cheat Safe Configuration",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "FrameForge GFX only uses documented Android display policies and authorized profiles. It does NOT patch game binaries or tamper with memory.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Apply Button
            item {
                GamingButton(
                    text = if (currentProfile.isApplied) "Applied (Tap to Re-Apply)" else "Apply Settings",
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_game_settings_button")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun QualityChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ElectricCyanContainer else GamingSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) ElectricCyan else GamingCardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ElectricCyan else TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun FrameRateChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) VividVioletContainer else GamingSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) VividViolet else GamingCardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) VividViolet else TextSecondary,
            maxLines = 1
        )
    }
}
