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
    onExportSavJson: () -> String
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: UE4 Sav Editor (GUI), 1: Sav Editor (Key-Value)
    var searchQuery by remember { mutableStateOf("") }
    var editingKeyItem by remember { mutableStateOf<ConfigKeyItem?>(null) }
    var editValueText by remember { mutableStateOf("") }
    var showDiffDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportContent by remember { mutableStateOf("") }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val workspace = uiState.configWorkspace
    val gui = workspace.guiSettings

    val filteredKeys = remember(searchQuery, workspace.keys) {
        if (searchQuery.isBlank()) workspace.keys else workspace.keys.filter {
            it.key.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "UE4 Sav Editor",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Safe inspection & candidate configuration generator",
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
                            exportContent = onExportSavJson()
                            showExportDialog = true
                        },
                        modifier = Modifier.testTag("export_sav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export JSON",
                            tint = ElectricCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingDarkBackground)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector: Matching screenshot "UE4 Sav Editor (GUI)" vs "Sav Editor"
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GamingSurface,
                contentColor = ElectricCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ElectricCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "UE4 Sav Editor (GUI)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 0) ElectricCyan else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_sav_gui")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Sav Editor (Key-Value)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 1) ElectricCyan else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_sav_kv")
                )
            }

            if (selectedTab == 0) {
                // Tab 0: UE4 Sav Editor (GUI)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Game Variant Banner
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
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Target Version: ${uiState.selectedGame.displayName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Unreal Engine Active.sav parameters",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Graphics (Lower the settings if game lags)
                    item {
                        Column {
                            Text(
                                text = "Graphics (Lower settings for stable 120 FPS)",
                                fontSize = 12.sp,
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
                                            .background(if (isSelected) ChipSelectedBackground else GamingSurface)
                                            .border(
                                                1.dp,
                                                if (isSelected) ElectricCyan else GamingCardBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onUpdateGuiGraphics(preset) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = preset,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ElectricCyan else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Frame Rate (Higher settings for better performance)
                    item {
                        Column {
                            Text(
                                text = "Frame Rate (Ultra Extreme unlocks 120 FPS)",
                                fontSize = 12.sp,
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
                                            .background(if (isSelected) ChipSelectedBackground else GamingSurface)
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonGreenActive else GamingCardBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onUpdateGuiFrameRate(preset) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (preset == "Ultra Extreme") "120 FPS" else preset,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) NeonGreenActive else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Manual FPS Slider
                    item {
                        GamingCard(backgroundColor = GamingSurface) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Manual FPS Cap",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${gui.manualFps} FPS",
                                    fontSize = 14.sp,
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

                    // Style (Classic, Colorful, Realistic, Soft, Movie)
                    item {
                        Column {
                            Text(
                                text = "Style (Select your preferred visual tone)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf("Classic", "Colorful", "Realistic", "Soft", "Movie")) { style ->
                                    val isSelected = gui.selectedStyle == style
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ChipSelectedBackground else GamingSurface)
                                            .border(
                                                1.dp,
                                                if (isSelected) ElectricCyan else GamingCardBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onUpdateGuiStyle(style) }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = style,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ElectricCyan else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Brightness (Set your preferred brightness)
                    item {
                        GamingCard(backgroundColor = GamingSurface) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "In-Game Brightness",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${gui.brightnessPercent}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ElectricCyan
                                )
                            }
                            Slider(
                                value = gui.brightnessPercent.toFloat(),
                                onValueChange = { onUpdateGuiBrightness(it.toInt()) },
                                valueRange = 80f..150f,
                                steps = 70,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricCyan,
                                    activeTrackColor = ElectricCyan,
                                    inactiveTrackColor = GamingCardBorder
                                )
                            )
                        }
                    }

                    // Apply SAV Button
                    item {
                        GamingButton(
                            text = "Apply SAV Configuration",
                            onClick = onApplySavChanges,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "apply_sav_button"
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                // Tab 1: Sav Editor (Key-Value)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search UE4 parameters...", color = TextMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("sav_search_field"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GamingCardBorder,
                                focusedContainerColor = GamingSurface,
                                unfocusedContainerColor = GamingSurface,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    // Scoped Storage notice
                    item {
                        GamingCard(
                            borderColor = VividViolet.copy(alpha = 0.4f),
                            backgroundColor = GamingSurfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = VividViolet, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Android 11+ Scoped Storage prevents arbitrary overwrites. Edits are staged, validated, and backed up in Room with export support.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Key-Value Items
                    items(filteredKeys) { item ->
                        SavKeyItemRow(
                            item = item,
                            onEditClick = {
                                editingKeyItem = item
                                editValueText = item.proposedValue
                            }
                        )
                    }

                    // Transactional Commit button
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        GamingButton(
                            text = "Commit Changes & Save Backup",
                            onClick = onApplySavChanges,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "commit_sav_kv_button"
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
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
                    text = "Edit ${target.key}",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
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
                        text = "Allowed Range: ${target.allowedRange} (${target.dataType.name})",
                        fontSize = 11.sp,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editValueText,
                        onValueChange = { editValueText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
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
                        onUpdateProposedValue(target.id, editValueText)
                        editingKeyItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Update", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingKeyItem = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GamingSurface
        )
    }

    // Export JSON Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "Exported Active.sav Profile",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingDarkBackground)
                        .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = exportContent,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportContent))
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Copy JSON", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
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
fun SavKeyItemRow(
    item: ConfigKeyItem,
    onEditClick: () -> Unit
) {
    GamingCard(
        backgroundColor = GamingSurface,
        borderColor = if (item.isModified) ElectricCyan else GamingCardBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.key,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "value: ${item.proposedValue}",
                    fontSize = 12.sp,
                    color = if (item.isModified) ElectricCyan else TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.testTag("edit_key_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${item.key}",
                    tint = if (item.isModified) ElectricCyan else TextSecondary
                )
            }
        }
    }
}
