package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
fun SettingsScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onRestoreSettings: () -> Unit,
    onSetAnimationScale: (Float) -> Unit = {},
    onRestoreAnimationScales: () -> Unit = {},
    onExportSettings: () -> String = { "" },
    onImportSettings: (String) -> Boolean = { false },
    onResetToDefaults: () -> Unit = {}
) {
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var importInputText by remember { mutableStateOf("") }
    var importErrorMessage by remember { mutableStateOf<String?>(null) }
    var copiedToClipboard by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Settings & Backup", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 17.sp)
                        Text("iQOO Neo 9 Persistent Config Engine", fontSize = 11.sp, color = NeonGreenActive)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_btn")) {
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
            // Target Hardware Identification
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
                            Text(
                                text = "HARDWARE TARGET PROFILE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "iQOO Neo 9 (PD2338C)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreenActive
                            )
                        }
                        StatusBadge(text = "PERSISTENT V2", color = NeonGreenActive)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All tuning parameters, gyro profiles, cadence preferences, and external cooler bindings are saved to persistent storage and survive app restarts and reboots.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            // Permanent Settings Export / Import / Factory Reset
            item {
                GamingCard(
                    borderColor = GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PERMANENT SETTINGS STORAGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        StatusBadge(text = "LOCAL DATASTORE", color = NeonGreenActive)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Export your verified iQOO Neo 9 gaming tuning configuration as JSON for backup, or restore to verified factory raw performance defaults.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                exportedJsonText = onExportSettings()
                                copiedToClipboard = false
                                showExportDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_settings_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreenActive),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreenActive)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                importInputText = ""
                                importErrorMessage = null
                                showImportDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("import_settings_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GamingCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reset_defaults_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonRedAlert),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRedAlert)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // System Animation Scales
            item {
                GamingCard(
                    borderColor = GamingCardBorder,
                    backgroundColor = GamingSurface
                ) {
                    Text("System Animation Scales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Window: ${uiState.animationState.currentWindowScale}x • Transition: ${uiState.animationState.currentTransitionScale}x • Animator: ${uiState.animationState.currentAnimatorScale}x",
                        fontSize = 11.sp,
                        color = NeonGreenActive
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Eliminating animation durations prevents task-switching delays and perceived latency when toggling overlays during gaming sessions.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSetAnimationScale(1.0f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.animationState.currentWindowScale == 1.0f) GamingSurfaceLight else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("1.0x (Stock)", fontSize = 11.sp, color = if (uiState.animationState.currentWindowScale == 1.0f) NeonGreenActive else TextPrimary)
                        }

                        Button(
                            onClick = { onSetAnimationScale(0.5f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.animationState.currentWindowScale == 0.5f) GamingSurfaceLight else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("0.5x (Fast)", fontSize = 11.sp, color = if (uiState.animationState.currentWindowScale == 0.5f) NeonGreenActive else TextPrimary)
                        }

                        Button(
                            onClick = { onSetAnimationScale(0.0f) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.animationState.currentWindowScale == 0.0f) GamingSurfaceLight else GamingSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Off (Instant)", fontSize = 11.sp, color = if (uiState.animationState.currentWindowScale == 0.0f) NeonGreenActive else TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.animationState.lastActionStatus,
                        fontSize = 10.sp,
                        color = if (uiState.shizukuStatus.isAuthorized) NeonGreenActive else TextSecondary
                    )
                }
            }

            // Restore All System Settings
            item {
                SettingsOptionCard(
                    title = "System Rollback Audit",
                    subtitle = "Revert display refresh rate, touch parameters, and system props to stock",
                    icon = Icons.Default.Restore,
                    onClick = onRestoreSettings
                )
            }

            // About
            item {
                SettingsOptionCard(
                    title = "About FrameForge GFX",
                    subtitle = "Engineering Edition • iQOO Neo 9 (PD2338C) Precision Tuning",
                    icon = Icons.Default.Info,
                    onClick = {}
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text("Exported Settings JSON", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Copy this configuration to transfer or back up your device profile:", fontSize = 12.sp, color = TextSecondary)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingSurfaceVariant)
                            .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = exportedJsonText,
                            fontSize = 11.sp,
                            color = NeonGreenActive,
                            lineHeight = 15.sp
                        )
                    }
                    if (copiedToClipboard) {
                        Text("Copied to clipboard!", fontSize = 11.sp, color = NeonGreenActive, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("FrameForge Settings", exportedJsonText)
                        clipboard.setPrimaryClip(clip)
                        copiedToClipboard = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreenActive)
                ) {
                    Text("Copy JSON", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = GamingSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text("Import Settings JSON", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste valid FrameForge Settings JSON below:", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = importInputText,
                        onValueChange = {
                            importInputText = it
                            importErrorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("Paste JSON here...", color = TextSecondary, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreenActive,
                            unfocusedBorderColor = GamingCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )
                    importErrorMessage?.let { err ->
                        Text(text = err, fontSize = 11.sp, color = NeonRedAlert, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importInputText.isNotBlank()) {
                            val success = onImportSettings(importInputText.trim())
                            if (success) {
                                showImportDialog = false
                            } else {
                                importErrorMessage = "Invalid settings JSON structure or schema version."
                            }
                        } else {
                            importErrorMessage = "Please paste settings JSON first."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreenActive)
                ) {
                    Text("Apply Settings", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Factory Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text("Reset to Factory Defaults?", fontWeight = FontWeight.Bold, color = NeonRedAlert)
            },
            text = {
                Text(
                    text = "This will reset all gaming profiles, touch calibrations, and gyro sensitivities to the factory verified iQOO Neo 9 Raw Performance configuration.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToDefaults()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRedAlert)
                ) {
                    Text("Confirm Reset", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SettingsOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GamingCard(
        backgroundColor = GamingSurface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}
