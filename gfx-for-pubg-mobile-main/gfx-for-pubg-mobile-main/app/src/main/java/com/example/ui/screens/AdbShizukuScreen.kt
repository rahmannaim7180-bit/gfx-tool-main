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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdbCommand
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdbShizukuScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onExecuteCommand: (AdbCommand) -> Unit,
    onRollbackCommand: (AdbCommand) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCommandForDialog by remember { mutableStateOf<AdbCommand?>(null) }
    var customCommandText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    var copiedScriptType by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ADB / Shizuku Commands", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
        bottomBar = {
            // Status bar matching screenshot
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = GamingSurface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ADB / Shizuku Status", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.shizukuStatus.statusMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.shizukuStatus.isAuthorized) NeonGreenActive else TextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            selectedCommandForDialog = AdbCommand.PREDEFINED_COMMANDS.first()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyanContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (uiState.shizukuStatus.isAuthorized) "Connected" else "Connect",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
            // Tab row: Predefined vs Custom
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingSurfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 0) ElectricCyanContainer else Color.Transparent)
                            .clickable { selectedTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Predefined",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) ElectricCyan else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 1) ElectricCyanContainer else Color.Transparent)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Custom",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) ElectricCyan else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 2) ElectricCyanContainer else Color.Transparent)
                            .clickable { selectedTab = 2 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Scripts",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) ElectricCyan else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 3) ElectricCyanContainer else Color.Transparent)
                            .clickable { selectedTab = 3 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Audit Log",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 3) ElectricCyan else TextSecondary
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                items(AdbCommand.PREDEFINED_COMMANDS) { cmd ->
                    GamingCard(
                        backgroundColor = GamingSurface,
                        borderColor = GamingCardBorder,
                        onClick = { selectedCommandForDialog = cmd }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cmd.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(cmd.description, fontSize = 11.sp, color = TextSecondary)
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    }
                }
            } else if (selectedTab == 1) {
                item {
                    GamingCard(backgroundColor = GamingSurface) {
                        Text("Execute Custom Safe ADB Command", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "All commands are checked against the strict typed allowlist before execution. Security-compromising commands are blocked.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customCommandText,
                            onValueChange = { customCommandText = it },
                            placeholder = { Text("e.g. settings put system peak_refresh_rate 144.0", fontSize = 12.sp, color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GamingCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        GamingButton(
                            text = "Validate & Execute",
                            onClick = {
                                if (customCommandText.isNotBlank()) {
                                    val customCmd = AdbCommand(
                                        id = "custom_${System.currentTimeMillis()}",
                                        title = "Custom Command",
                                        description = "User typed command",
                                        command = customCommandText
                                    )
                                    onExecuteCommand(customCmd)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else if (selectedTab == 2) {
                // Standalone Shell Scripts Tab
                val setupScriptContent = """
#!/bin/bash
# FrameForge GFX — Flagship 144Hz & OriginOS 6 Optimization
adb shell cmd game set --mode 2 com.tencent.ig
adb shell settings put system peak_refresh_rate 144.0
adb shell settings put system min_refresh_rate 144.0
adb shell settings put system user_refresh_rate 144
adb shell settings put global fps_limit 144
adb shell settings put global vivo_multi_turbo 1
adb shell cmd activity track-associations
adb shell pm trim-caches 1000M
echo "[✓] Applied 144Hz & Game Turbo for PUBG Mobile!"
""".trimIndent()

                val rollbackScriptContent = """
#!/bin/bash
# FrameForge GFX — Factory Defaults Rollback
adb shell cmd game reset com.tencent.ig
adb shell settings delete system peak_refresh_rate
adb shell settings delete system min_refresh_rate
adb shell settings delete system user_refresh_rate
adb shell settings delete global fps_limit
adb shell settings delete global vivo_multi_turbo
echo "[✓] Settings restored to factory defaults!"
""".trimIndent()

                item {
                    GamingCard(
                        borderColor = ElectricCyan.copy(alpha = 0.5f),
                        backgroundColor = GamingSurface
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("adb_gaming_setup.sh", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Automated 144Hz lock & Multi-Turbo configuration script", fontSize = 11.sp, color = TextSecondary)
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(setupScriptContent))
                                    copiedScriptType = "setup"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyanContainer),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (copiedScriptType == "setup") "Copied!" else "Copy",
                                    color = ElectricCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingDarkBackground)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = setupScriptContent,
                                fontSize = 10.sp,
                                color = NeonGreenActive,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }

                item {
                    GamingCard(
                        borderColor = VividViolet.copy(alpha = 0.4f),
                        backgroundColor = GamingSurface
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("adb_gaming_rollback.sh", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Instant rollback script to restore OriginOS defaults", fontSize = 11.sp, color = TextSecondary)
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(rollbackScriptContent))
                                    copiedScriptType = "rollback"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VividVioletContainer),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (copiedScriptType == "rollback") "Copied!" else "Copy",
                                    color = VividViolet,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingDarkBackground)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = rollbackScriptContent,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }

                item {
                    GamingCard(
                        borderColor = NeonAmberWarning.copy(alpha = 0.4f),
                        backgroundColor = GamingSurfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = NeonAmberWarning, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "How to run: Connect your phone to your PC with USB debugging enabled (or pair wireless debugging), then run 'bash adb_gaming_setup.sh'. All commands are safe, standard Android settings with 100% reversible rollback.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                // Audit Log Tab (selectedTab == 3)
                if (uiState.recentAuditLogs.isEmpty()) {
                    item {
                        GamingCard(backgroundColor = GamingSurface) {
                            Text("No Audit Logs Recorded", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Executed ADB/Shizuku commands and rollbacks will appear here with timestamps.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                } else {
                    items(uiState.recentAuditLogs) { log ->
                        GamingCard(backgroundColor = GamingSurfaceVariant) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(log.commandTitle, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                StatusBadge(
                                    text = if (log.isRolledBack) "ROLLED BACK" else if (log.exitCode == 0) "SUCCESS" else "ERROR",
                                    color = if (log.isRolledBack) NeonAmberWarning else if (log.exitCode == 0) NeonGreenActive else NeonRedAlert
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.command, fontSize = 11.sp, color = ElectricCyan, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.executionResult, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Command Detail / Execution Dialog
    if (selectedCommandForDialog != null) {
        val cmd = selectedCommandForDialog!!
        AlertDialog(
            onDismissRequest = { selectedCommandForDialog = null },
            title = {
                Text(cmd.title, fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(cmd.description, fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Command string:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(GamingDarkBackground)
                            .padding(8.dp)
                    ) {
                        Text(cmd.command, fontSize = 11.sp, color = NeonGreenActive, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }

                    if (cmd.rollbackCommand != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Rollback string:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VividViolet)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingDarkBackground)
                                .padding(8.dp)
                        ) {
                            Text(cmd.rollbackCommand, fontSize = 11.sp, color = TextSecondary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onExecuteCommand(cmd)
                        selectedCommandForDialog = null
                    }
                ) {
                    Text("Execute", color = ElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (cmd.isReversible && cmd.rollbackCommand != null) {
                    TextButton(
                        onClick = {
                            onRollbackCommand(cmd)
                            selectedCommandForDialog = null
                        }
                    ) {
                        Text("Rollback", color = NeonAmberWarning)
                    }
                } else {
                    TextButton(onClick = { selectedCommandForDialog = null }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            },
            containerColor = GamingSurface
        )
    }
}
