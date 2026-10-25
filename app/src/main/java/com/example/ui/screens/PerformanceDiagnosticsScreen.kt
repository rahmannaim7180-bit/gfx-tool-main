package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun PerformanceDiagnosticsScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onActionClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Performance Diagnostics",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Root-cause bottleneck & optimization audit",
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
                    IconButton(onClick = onRefresh, modifier = Modifier.testTag("refresh_diagnostics")) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
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
            // Header Overview Card
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
                            imageVector = Icons.Default.Troubleshoot,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Target: iQOO Neo 9 5G (OriginOS 6)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Auditing why optimizations may not alter feel in PUBG Mobile",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val highCount = uiState.performanceDiagnostics.count { it.severity == BottleneckSeverity.HIGH }
                        val medCount = uiState.performanceDiagnostics.count { it.severity == BottleneckSeverity.MEDIUM }

                        StatusBadge(
                            text = "$highCount High Attention",
                            color = if (highCount > 0) NeonAmberWarning else NeonGreenActive,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(
                            text = "$medCount Medium Notes",
                            color = VividViolet,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Diagnostic Items List
            items(uiState.performanceDiagnostics) { item ->
                DiagnosticItemCard(
                    item = item,
                    onActionClick = { item.actionId?.let { onActionClick(it) } }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DiagnosticItemCard(
    item: PerformanceDiagnosticItem,
    onActionClick: () -> Unit
) {
    val borderColor = when (item.severity) {
        BottleneckSeverity.HIGH, BottleneckSeverity.CRITICAL -> NeonAmberWarning.copy(alpha = 0.6f)
        BottleneckSeverity.MEDIUM -> VividViolet.copy(alpha = 0.5f)
        BottleneckSeverity.LOW -> ElectricCyan.copy(alpha = 0.3f)
    }

    GamingCard(
        borderColor = borderColor,
        backgroundColor = GamingSurface
    ) {
        // Title & Severity Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category.displayName,
                    fontSize = 11.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            StatusBadge(
                text = item.severity.displayName,
                color = when (item.severity) {
                    BottleneckSeverity.HIGH, BottleneckSeverity.CRITICAL -> NeonAmberWarning
                    BottleneckSeverity.MEDIUM -> VividViolet
                    BottleneckSeverity.LOW -> NeonGreenActive
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Badges: Confidence & Measurement Type
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GamingSurfaceVariant)
                    .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.confidence.displayName,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GamingSurfaceVariant)
                    .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.measurementType.displayName,
                    fontSize = 10.sp,
                    color = if (item.measurementType == MeasurementType.DIRECT_MEASURED) NeonGreenActive else BlueAccent
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GamingSurfaceVariant)
                    .border(1.dp, GamingCardBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.canAppApplyAction.displayName,
                    fontSize = 10.sp,
                    color = when (item.canAppApplyAction) {
                        AppActionCapability.DIRECTLY_APPLY -> NeonGreenActive
                        AppActionCapability.REQUIRES_IN_GAME_SETTINGS -> NeonAmberWarning
                        AppActionCapability.REQUIRES_SHIZUKU_ADB -> VividViolet
                        AppActionCapability.INFORMATIONAL_ONLY -> TextMuted
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Evidence Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(GamingDarkBackground)
                .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "EVIDENCE COLLECTED:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.evidence,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recommended Action
        Text(
            text = "RECOMMENDED ACTION:",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricCyan
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.recommendedAction,
            fontSize = 13.sp,
            color = TextPrimary,
            lineHeight = 17.sp
        )

        // Action Button (if applicable)
        if (item.actionLabel != null && item.actionId != null) {
            Spacer(modifier = Modifier.height(12.dp))
            GamingButton(
                text = item.actionLabel,
                onClick = onActionClick,
                isSecondary = item.canAppApplyAction != AppActionCapability.DIRECTLY_APPLY,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                testTag = "diag_action_${item.id}"
            )
        }
    }
}
