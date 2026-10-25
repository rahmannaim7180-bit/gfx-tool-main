package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
fun CommandTestCenterScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onRunSingleTest: (String) -> Unit,
    onRunAllTests: () -> Unit,
    getSanitizedReport: () -> String
) {
    var selectedCategoryFilter by remember { mutableStateOf<CommandCategory?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportContent by remember { mutableStateOf("") }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val displayedCommands = if (selectedCategoryFilter != null) {
        uiState.commandRegistry.filter { it.category == selectedCategoryFilter }
    } else {
        uiState.commandRegistry
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Command Test Center",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "ADB & Shizuku 10-category verification suite",
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
                        onClick = {
                            reportContent = getSanitizedReport()
                            showReportDialog = true
                        },
                        modifier = Modifier.testTag("export_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Report",
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
            // Action Banner: Run All 10 Categories
            item {
                GamingCard(
                    borderColor = ElectricCyan.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "10 Independent Audit Categories",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Device info, refresh rate, game mode, state capture & rollback",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    GamingButton(
                        text = if (uiState.isTestingRunning) "Auditing Commands..." else "Run All 10 Category Tests",
                        onClick = onRunAllTests,
                        enabled = !uiState.isTestingRunning,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "run_all_tests_button"
                    )
                }
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All (10)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyanContainer,
                                selectedLabelColor = ElectricCyan,
                                containerColor = GamingSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                    items(CommandCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text("Cat ${cat.categoryNumber}: ${cat.displayName.take(16)}...", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyanContainer,
                                selectedLabelColor = ElectricCyan,
                                containerColor = GamingSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }

            // Command Items List
            items(displayedCommands) { cmd ->
                CommandRegistryCard(
                    item = cmd,
                    onRunTest = { onRunSingleTest(cmd.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Sanitized Diagnostic Report Modal
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Text(
                    text = "Sanitized Diagnostic Report",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Confidential serial numbers, MAC addresses, and Android IDs are stripped.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingDarkBackground)
                            .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = reportContent,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(reportContent))
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Copy to Clipboard", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = GamingSurface
        )
    }
}

@Composable
fun CommandRegistryCard(
    item: CommandRegistryItem,
    onRunTest: () -> Unit
) {
    val statusColor = when (item.status) {
        CommandTestStatus.PASS_VERIFIED -> NeonGreenActive
        CommandTestStatus.PASS_PARTIALLY_VERIFIED -> BlueAccent
        CommandTestStatus.UNSUPPORTED -> VividViolet
        CommandTestStatus.PERMISSION_DENIED -> NeonAmberWarning
        CommandTestStatus.FAILED, CommandTestStatus.RESTORE_TEST_FAILED -> NeonRedAlert
        CommandTestStatus.NOT_TESTED -> TextMuted
    }

    GamingCard(
        borderColor = if (item.status != CommandTestStatus.NOT_TESTED) statusColor.copy(alpha = 0.5f) else GamingCardBorder,
        backgroundColor = GamingSurface
    ) {
        // Category & Name Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CATEGORY ${item.category.categoryNumber}: ${item.category.displayName}",
                    fontSize = 10.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            StatusBadge(
                text = item.status.displayName,
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Classification & Authorization Badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GamingSurfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.classification.displayName,
                    fontSize = 10.sp,
                    color = if (item.classification == CommandClassification.STATE_CHANGING) NeonAmberWarning else TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GamingSurfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.authorization.displayName,
                    fontSize = 10.sp,
                    color = if (item.authorization == AuthorizationLevel.NONE) NeonGreenActive else ElectricCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Command Snippet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(GamingDarkBackground)
                .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Text(
                text = item.command,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = ElectricCyan
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Purpose & Validation Info
        Text(
            text = "Purpose: ${item.purpose}",
            fontSize = 11.sp,
            color = TextSecondary
        )
        Text(
            text = "Validation: ${item.validationSteps}",
            fontSize = 11.sp,
            color = TextMuted
        )

        // Actual Result (if tested)
        if (item.actualResult != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(GamingSurfaceVariant)
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "ACTUAL RESULT:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Text(
                        text = item.actualResult,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    )
                }
            }
        }

        if (item.rollbackCommand != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rollback: ${item.rollbackCommand}",
                fontSize = 10.sp,
                color = NeonAmberWarning
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Test Button
        GamingButton(
            text = if (item.status == CommandTestStatus.NOT_TESTED) "Test Operation" else "Re-Test Operation",
            onClick = onRunTest,
            isSecondary = item.status != CommandTestStatus.NOT_TESTED,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            testTag = "test_cmd_${item.id}"
        )
    }
}
