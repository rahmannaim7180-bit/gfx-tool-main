package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettingsScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onUpdateProfile: (GraphicsProfile) -> Unit,
    onApply: () -> Unit,
    onSaveProfile: () -> Unit,
    onResetProfile: () -> Unit
) {
    val currentProfile = uiState.activeProfile
    var showResolutionModal by remember { mutableStateOf(false) }
    var showGraphicsModal by remember { mutableStateOf(false) }
    var showCapabilityModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Game Graphics & FPS",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Resolution, frame rate target & render profiles",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
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
                actions = {
                    IconButton(
                        onClick = { showCapabilityModal = true },
                        modifier = Modifier.testTag("graphics_capability_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Capability Report",
                            tint = ElectricCyan
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
            // Selected Game & Profile Status Card
            item {
                GamingCard(
                    borderColor = ElectricCyan.copy(alpha = 0.4f),
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
                                text = uiState.selectedGame.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Package: ${uiState.selectedGame.packageName} (${uiState.selectedGame.variantLabel})",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        StatusBadge(
                            text = currentProfile.stateBadge.label,
                            color = when (currentProfile.stateBadge) {
                                ProfileStateBadge.VERIFIED_ACTIVE -> NeonGreenActive
                                ProfileStateBadge.APPLIED -> ElectricCyan
                                ProfileStateBadge.SAVED_PREFERENCE -> VividViolet
                            }
                        )
                    }
                }
            }

            // Setting 1: Resolution (Clickable Card triggering Choose Option Modal)
            item {
                SettingSelectionCard(
                    title = "Resolution",
                    subtitle = currentProfile.resolution.displayName,
                    icon = Icons.Default.AspectRatio,
                    onClick = { showResolutionModal = true },
                    testTag = "setting_resolution"
                )
            }

            // Setting 2: Graphics Quality (Clickable Card triggering Choose Option Modal)
            item {
                SettingSelectionCard(
                    title = "Graphics Quality",
                    subtitle = currentProfile.quality.displayName,
                    icon = Icons.Default.Tune,
                    onClick = { showGraphicsModal = true },
                    testTag = "setting_graphics"
                )
            }

            // Setting 3: Frame Rate Target (Distinguishing Display Refresh vs Game Engine FPS)
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Frame Rate Target",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Panel: 144Hz | PUBG Engine: Up to 120 FPS on Snapdragon 8 Gen 2",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            FrameRateTarget.HIGH,
                            FrameRateTarget.FPS_90,
                            FrameRateTarget.FPS_120,
                            FrameRateTarget.FPS_144
                        ).forEach { target ->
                            val isSelected = currentProfile.frameRate == target
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ChipSelectedBackground else GamingSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) if (target.isHardwareTestOnly) NeonAmberWarning else NeonGreenActive else GamingCardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onUpdateProfile(currentProfile.copy(frameRate = target)) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = target.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) {
                                                if (target.isHardwareTestOnly) NeonAmberWarning else NeonGreenActive
                                            } else TextPrimary
                                        )
                                        if (target.supportExplanation.isNotEmpty()) {
                                            Text(
                                                text = target.supportExplanation,
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = if (target.isHardwareTestOnly) NeonAmberWarning else NeonGreenActive,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Setting 4: Style (Classic, Colorful, Realistic, Soft, Movie)
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text(
                        text = "Visual Style",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GraphicsStyle.values().forEach { style ->
                            val isSelected = currentProfile.style == style
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ChipSelectedBackground else GamingSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) ElectricCyan else GamingCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onUpdateProfile(currentProfile.copy(style = style)) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = style.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElectricCyan else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Setting 5: Shadows & MSAA Toggles
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dynamic Shadows",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Disabling saves draw-calls and CPU frame time",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = currentProfile.shadows != ShadowQuality.DISABLED,
                            onCheckedChange = { enabled ->
                                onUpdateProfile(
                                    currentProfile.copy(
                                        shadows = if (enabled) ShadowQuality.LOW else ShadowQuality.DISABLED
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ElectricCyanContainer
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = GamingCardBorder
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Anti-Aliasing (MSAA)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Disabling reduces GPU texture fillrate load",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = currentProfile.antiAliasing != AntiAliasingMode.DISABLED,
                            onCheckedChange = { enabled ->
                                onUpdateProfile(
                                    currentProfile.copy(
                                        antiAliasing = if (enabled) AntiAliasingMode.X2 else AntiAliasingMode.DISABLED
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ElectricCyanContainer
                            )
                        )
                    }
                }
            }

            // Distinction Notice Card
            item {
                GamingCard(
                    borderColor = VividViolet.copy(alpha = 0.4f),
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = VividViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "System Display vs In-Game Render Resolution",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Changing Android system resolution (wm size via ADB) adjusts OS framebuffer resolution, but DOES NOT alter PUBG Mobile's internal Unreal Engine 3D viewport scaling. Internal render scaling is governed by UE4 Active.sav ResolutionQuality.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Controls: Apply, Save, Reset
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GamingButton(
                        text = "Apply",
                        onClick = onApply,
                        modifier = Modifier.weight(1.2f),
                        testTag = "btn_apply_profile"
                    )
                    GamingButton(
                        text = "Save",
                        onClick = onSaveProfile,
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_save_profile"
                    )
                    GamingButton(
                        text = "Reset",
                        onClick = onResetProfile,
                        isSecondary = true,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_reset_profile"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal 1: Choose Resolution Option (Matching Screenshot IMG-20240504-WA0009-461x1024.webp)
    if (showResolutionModal) {
        AlertDialog(
            onDismissRequest = { showResolutionModal = false },
            title = {
                Text(
                    text = "Choose option",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp)) {
                    items(ResolutionPreset.values().toList()) { res ->
                        val isSelected = currentProfile.resolution == res
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateProfile(currentProfile.copy(resolution = res))
                                    showResolutionModal = false
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onUpdateProfile(currentProfile.copy(resolution = res))
                                    showResolutionModal = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ElectricCyan,
                                    unselectedColor = TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = res.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElectricCyan else TextPrimary
                                )
                                Text(
                                    text = res.description,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showResolutionModal = false }) {
                    Text("CANCEL", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GamingSurface
        )
    }

    // Modal 2: Choose Graphics Quality (Matching Screenshot IMG-20240504-WA0010-461x1024.webp)
    if (showGraphicsModal) {
        AlertDialog(
            onDismissRequest = { showGraphicsModal = false },
            title = {
                Text(
                    text = "Choose option",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    GraphicsQuality.values().forEach { quality ->
                        val isSelected = currentProfile.quality == quality
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateProfile(currentProfile.copy(quality = quality))
                                    showGraphicsModal = false
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onUpdateProfile(currentProfile.copy(quality = quality))
                                    showGraphicsModal = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ElectricCyan,
                                    unselectedColor = TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = quality.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElectricCyan else TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGraphicsModal = false }) {
                    Text("CANCEL", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GamingSurface
        )
    }

    // Modal 3: Graphics Capability Report
    if (showCapabilityModal) {
        AlertDialog(
            onDismissRequest = { showCapabilityModal = false },
            title = {
                Text(
                    text = "Graphics Capability Report",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Device: iQOO Neo 9 5G (Snapdragon 8 Gen 2 / 144Hz AMOLED)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Official 120 FPS: Supported in PUBG Mobile 3.2+ when Graphics is set to Smooth.\n" +
                                "• 144 FPS Status: Display hardware supports 144Hz, but PUBG Mobile Unreal Engine is locked to 120 FPS max.\n" +
                                "• OriginOS Display Policy: Game Space dynamically switches to 60Hz unless forced via Shizuku display commands.\n" +
                                "• Scoped Storage: Android 11+ protects /Android/data. In-game changes require manual in-game selection or SAF export.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showCapabilityModal = false }) {
                    Text("Got it", color = ElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GamingSurface
        )
    }
}

@Composable
fun SettingSelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    GamingCard(
        backgroundColor = GamingSurface,
        onClick = onClick,
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GamingSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = subtitle, fontSize = 12.sp, color = ElectricCyan)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}
