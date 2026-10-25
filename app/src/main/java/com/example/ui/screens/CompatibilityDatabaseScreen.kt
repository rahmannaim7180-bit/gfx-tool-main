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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompatibilityVerificationLevel
import com.example.data.model.DeviceCompatibilityRecord
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityDatabaseScreen(
    onBack: () -> Unit,
    onCompareWithDevice: (DeviceCompatibilityRecord) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf<CompatibilityVerificationLevel?>(null) }
    var selectedRecord by remember { mutableStateOf<DeviceCompatibilityRecord?>(null) }

    val filteredRecords = remember(searchQuery, selectedLevel) {
        DeviceCompatibilityRecord.KNOWN_DEVICE_RECORDS.filter { record ->
            val matchesQuery = searchQuery.isBlank() ||
                    record.deviceModel.contains(searchQuery, ignoreCase = true) ||
                    record.chipset.contains(searchQuery, ignoreCase = true) ||
                    record.manufacturer.contains(searchQuery, ignoreCase = true)
            val matchesLevel = selectedLevel == null || record.verificationLevel == selectedLevel
            matchesQuery && matchesLevel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Compatibility Database", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Verified Devices • OEM Skins • Verified Caps", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("compat_back_button")) {
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
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search phone model, chipset, brand...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compat_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GamingCardBorder,
                        focusedContainerColor = GamingSurface,
                        unfocusedContainerColor = GamingSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedLevel == null,
                            onClick = { selectedLevel = null },
                            label = { Text("All Tiers", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyanContainer,
                                selectedLabelColor = ElectricCyan
                            )
                        )
                    }
                    items(CompatibilityVerificationLevel.values().toList()) { level ->
                        FilterChip(
                            selected = selectedLevel == level,
                            onClick = { selectedLevel = if (selectedLevel == level) null else level },
                            label = { Text(level.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(level.badgeColorHex).copy(alpha = 0.2f),
                                selectedLabelColor = Color(level.badgeColorHex)
                            )
                        )
                    }
                }
            }

            // Device records count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hardware Profiles (${filteredRecords.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "PUBG Mobile 3.2+ Database",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Records List
            items(filteredRecords) { record ->
                DeviceRecordCard(
                    record = record,
                    onSelect = { selectedRecord = record },
                    onCompare = { onCompareWithDevice(record) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Sheet for detail view
    selectedRecord?.let { record ->
        AlertDialog(
            onDismissRequest = { selectedRecord = null },
            title = {
                Text(record.deviceModel, fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Chipset: ${record.chipset}", color = ElectricCyan, fontSize = 13.sp)
                    Text("Target Game: ${record.targetGameName}", color = TextSecondary, fontSize = 12.sp)
                    Text("Verified FPS: ${record.verifiedFpsTarget}", fontWeight = FontWeight.Bold, color = NeonGreenActive, fontSize = 13.sp)
                    Text("Graphics: ${record.verifiedGraphicsQuality}", color = TextPrimary, fontSize = 12.sp)
                    Text("Max Display: ${record.displayMaxRefreshHz} Hz", color = TextPrimary, fontSize = 12.sp)
                    Text("OEM Integration: ${record.oemGamingIntegration}", color = TextSecondary, fontSize = 12.sp)
                    Divider(color = GamingCardBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Text("Technical Notes & Findings:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                    Text(record.notes, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val r = selectedRecord
                    selectedRecord = null
                    if (r != null) onCompareWithDevice(r)
                }) {
                    Text("Compare With My Phone", color = ElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRecord = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = GamingSurface,
            tonalElevation = 6.dp
        )
    }
}

@Composable
private fun DeviceRecordCard(
    record: DeviceCompatibilityRecord,
    onSelect: () -> Unit,
    onCompare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GamingSurface)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.deviceModel,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${record.manufacturer} • ${record.chipset}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                StatusBadge(
                    text = record.verificationLevel.displayName,
                    color = Color(record.verificationLevel.badgeColorHex)
                )
            }

            Divider(color = GamingCardBorder.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Verified FPS Target", fontSize = 10.sp, color = TextMuted)
                    Text(record.verifiedFpsTarget, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonGreenActive)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Display Max", fontSize = 10.sp, color = TextMuted)
                    Text("${record.displayMaxRefreshHz} Hz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
            }

            Text(
                text = record.notes,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Compare Specs →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    modifier = Modifier.clickable(onClick = onCompare)
                )
            }
        }
    }
}
