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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
fun SavEditorScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onUpdateGuiGraphics: (String) -> Unit,
    onUpdateGuiFrameRate: (String) -> Unit,
    onUpdateGuiManualFps: (Int) -> Unit,
    onUpdateGuiStyle: (String) -> Unit,
    onUpdateGuiBrightness: (Int) -> Unit,
    onUpdateProposedValue: (String, String) -> Unit,
    onApplySavChanges: () -> Unit,
    onExportSavJson: () -> String,
    onAddCustomKey: (String, String) -> Unit = { _, _ -> },
    onApplyQuickPreset: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(1) } // Default directly to Key-Value tab as requested
    var searchQuery by remember { mutableStateOf("") }
    var editingKeyItem by remember { mutableStateOf<ConfigKeyItem?>(null) }
    var editValueText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newKeyName by remember { mutableStateOf("") }
    var newKeyValue by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportContent by remember { mutableStateOf("") }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val workspace = uiState.configWorkspace
    val gui = workspace.guiSettings

    val filteredKeys = remember(searchQuery, workspace.keys) {
        if (searchQuery.isBlank()) {
            workspace.keys
        } else {
            workspace.keys.filter {
                it.key.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val modifiedCount = remember(workspace.keys) {
        workspace.keys.count { it.isModified }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "UE4 SAV & Config Editor",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Active.sav | Key-Value Engine",
                            fontSize = 11.sp,
                            color = NeonGreenActive
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("sav_back_button")) {
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
                            exportContent = onExportSavJson()
                            showExportDialog = true
                        },
                        modifier = Modifier.testTag("sav_export_button")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export Profile", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GamingDarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = GamingDarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // High-Contrast Minimalist Tab Bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GamingDarkBackground,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonGreenActive,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "GUI Presets",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) TextPrimary else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Key - Value Editor",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) TextPrimary else TextSecondary,
                                fontSize = 13.sp
                            )
                            if (modifiedCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonGreenActive)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$modifiedCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GamingDarkBackground
                                    )
                                }
                            }
                        }
                    }
                )
            }

            if (selectedTab == 0) {
                // GUI PRESETS TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        GamingCard(borderColor = GamingCardBorder, backgroundColor = GamingSurface) {
                            Text(
                                text = "GRAPHICS QUALITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Super Smooth", "Smooth", "Balanced").forEach { preset ->
                                    val isSelected = gui.selectedGraphicsPreset == preset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ChipSelectedBackground else GamingSurfaceVariant)
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonGreenActive else GamingCardBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onUpdateGuiGraphics(preset) }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = preset,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) NeonGreenActive else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        GamingCard(borderColor = GamingCardBorder, backgroundColor = GamingSurface) {
                            Text(
                                text = "FRAMERATE TIER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Extreme", "Extreme+", "Ultra Extreme").forEach { preset ->
                                    val isSelected = gui.selectedFrameRatePreset == preset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ChipSelectedBackground else GamingSurfaceVariant)
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonGreenActive else GamingCardBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onUpdateGuiFrameRate(preset) }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (preset == "Ultra Extreme") "120 FPS" else if (preset == "Extreme+") "90 FPS" else "60 FPS",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) NeonGreenActive else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        GamingCard(borderColor = GamingCardBorder, backgroundColor = GamingSurface) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Manual In-Match FPS Target",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${gui.manualFps} FPS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonGreenActive
                                )
                            }
                            Slider(
                                value = gui.manualFps.toFloat(),
                                onValueChange = { onUpdateGuiManualFps(it.toInt()) },
                                valueRange = 30f..144f,
                                steps = 114,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonGreenActive,
                                    activeTrackColor = NeonGreenActive,
                                    inactiveTrackColor = GamingCardBorder
                                )
                            )
                        }
                    }

                    item {
                        GamingButton(
                            text = "APPLY SAV CONFIGURATION",
                            onClick = onApplySavChanges,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "apply_sav_button"
                        )
                    }
                }
            } else {
                // KEY - VALUE TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick 1-Tap Presets
                    item {
                        Column {
                            Text(
                                text = "QUICK PRESET INJECTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf("120 FPS Ultra", "90 FPS Extreme+", "Potato Low Lag", "Aim & Scope Fast Switch")) { preset ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(GamingSurfaceVariant)
                                            .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
                                            .clickable { onApplyQuickPreset(preset) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = preset,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search & Add New Key Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Filter parameters...", color = TextMuted, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sav_search_field"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TextPrimary,
                                    unfocusedBorderColor = GamingCardBorder,
                                    focusedContainerColor = GamingSurface,
                                    unfocusedContainerColor = GamingSurface,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            Button(
                                onClick = {
                                    newKeyName = ""
                                    newKeyValue = ""
                                    showAddDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GamingSurfaceVariant,
                                    contentColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .height(56.dp)
                                    .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Custom Key", tint = NeonGreenActive)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Storage and status pill
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingSurfaceVariant)
                                .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active.sav Target",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            StatusBadge(text = "WORKABLE FILE", color = NeonGreenActive)
                        }
                    }

                    // Key-Value Rows
                    items(filteredKeys, key = { it.id }) { item ->
                        SavKeyRowCard(
                            item = item,
                            onEditClick = {
                                editingKeyItem = item
                                editValueText = item.proposedValue
                            }
                        )
                    }

                    // Commit Changes Button
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        GamingButton(
                            text = if (modifiedCount > 0) "COMMIT $modifiedCount MODIFIED KEYS" else "COMMIT & SAVE BACKUP",
                            onClick = onApplySavChanges,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "commit_sav_kv_button"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Edit Value Dialog
    if (editingKeyItem != null) {
        val target = editingKeyItem!!
        AlertDialog(
            onDismissRequest = { editingKeyItem = null },
            title = {
                Text(
                    text = "Edit Parameter: ${target.key}",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = target.description,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Allowed Range: ${target.allowedRange}",
                        fontSize = 11.sp,
                        color = NeonGreenActive
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editValueText,
                        onValueChange = { editValueText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreenActive,
                            unfocusedBorderColor = GamingCardBorder,
                            focusedContainerColor = GamingSurfaceVariant,
                            unfocusedContainerColor = GamingSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProposedValue(target.id, editValueText)
                        editingKeyItem = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreenActive,
                        contentColor = GamingDarkBackground
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Update", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingKeyItem = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Add New Key Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Custom UE4 Key",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Define custom engine parameter for Active.sav or UserCustom.ini injection.", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = newKeyName,
                        onValueChange = { newKeyName = it },
                        placeholder = { Text("Key name (e.g. BattleFPS)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreenActive,
                            unfocusedBorderColor = GamingCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = newKeyValue,
                        onValueChange = { newKeyValue = it },
                        placeholder = { Text("Value (e.g. 120)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreenActive,
                            unfocusedBorderColor = GamingCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKeyName.isNotBlank() && newKeyValue.isNotBlank()) {
                            onAddCustomKey(newKeyName.trim(), newKeyValue.trim())
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreenActive, contentColor = GamingDarkBackground),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Save Key", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Export JSON Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(text = "Export Active.sav Config", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text("Configuration JSON output:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GamingSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = exportContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportContent))
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = GamingDarkBackground)
                ) {
                    Text("Copy", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = GamingSurface
        )
    }
}

@Composable
private fun SavKeyRowCard(
    item: ConfigKeyItem,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GamingSurface)
            .border(
                1.dp,
                if (item.isModified) NeonGreenActive else GamingCardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onEditClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.key,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.category,
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                    if (item.isModified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MODIFIED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonGreenActive
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (item.isModified) NeonGreenContainer else GamingSurfaceVariant)
                        .border(
                            1.dp,
                            if (item.isModified) NeonGreenActive else GamingCardBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item.proposedValue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isModified) NeonGreenActive else TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
