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
import com.example.data.model.NetworkPingResult
import com.example.data.model.NetworkQualityReport
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.MetricBox
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkQualityScreen(
    report: NetworkQualityReport,
    onBack: () -> Unit,
    onRunDiagnostics: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Network Quality & Latency", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Real Socket Diagnostics • Jitter • Transport", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("network_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onRunDiagnostics,
                        enabled = !report.isTestingActive,
                        modifier = Modifier.testTag("network_run_probe_action")
                    ) {
                        if (report.isTestingActive) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = ElectricCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Run Probe", tint = ElectricCyan)
                        }
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
            // Stability Rating Card
            item {
                GamingCard(
                    borderColor = if (report.packetLossPct > 0f) Color(0xFFFF5252) else NeonGreenActive.copy(alpha = 0.5f),
                    backgroundColor = GamingSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Connection Stability Rating", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = report.connectionStabilityRating,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.packetLossPct > 0f) Color(0xFFFF5252) else NeonGreenActive
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Active Transport: ${report.activeTransport}",
                                fontSize = 11.sp,
                                color = ElectricCyan
                            )
                        }

                        Icon(
                            imageVector = if (report.activeTransport.contains("Wi-Fi", ignoreCase = true)) Icons.Default.Wifi else Icons.Default.CellTower,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Key Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "Avg Socket Ping",
                        value = if (report.averageLatencyMs > 0) "${report.averageLatencyMs.toInt()}" else "--",
                        unit = "ms",
                        subtitle = "Edge connect time",
                        color = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Jitter",
                        value = if (report.jitterMs > 0) String.format("%.1f", report.jitterMs) else "--",
                        unit = "ms",
                        subtitle = "Variance (packet delta)",
                        color = if (report.jitterMs > 10f) Color(0xFFFF9800) else NeonGreenActive,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Packet Loss",
                        value = "${report.packetLossPct.toInt()}",
                        unit = "%",
                        subtitle = "Socket drops",
                        color = if (report.packetLossPct > 0f) Color(0xFFFF5252) else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Bandwidth & DNS Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "Link Downlink",
                        value = "${report.linkDownstreamMbps}",
                        unit = "Mbps",
                        subtitle = "Local interface band",
                        color = VividViolet,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "DNS Lookup",
                        value = "${report.dnsResolutionMs}",
                        unit = "ms",
                        subtitle = "Name resolution",
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Run Diagnostics Button
            item {
                GamingButton(
                    text = if (report.isTestingActive) "Running Socket Probes..." else "Run Network Diagnostics",
                    onClick = onRunDiagnostics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_network_diagnostics_button"),
                    enabled = !report.isTestingActive
                )
            }

            // Diagnostic Endpoints Probe Results
            item {
                Text(
                    text = "Probed Global Endpoints",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (report.recentPings.isEmpty()) {
                item {
                    GamingCard(backgroundColor = GamingSurfaceVariant.copy(alpha = 0.5f)) {
                        Text(
                            text = "Tap 'Run Network Diagnostics' to measure real-time TCP socket latency to tier-1 global gaming edge backbones.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(report.recentPings) { ping ->
                    EndpointPingItem(ping = ping)
                }
            }

            // Honest Disclosure Card
            item {
                GamingCard(
                    borderColor = VividViolet.copy(alpha = 0.5f),
                    backgroundColor = GamingSurfaceVariant.copy(alpha = 0.7f)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = VividViolet,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Physical Network vs. In-Match Ping Disclosure",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Diagnostic Ping: Directly measures round-trip TCP connection time to anycast nodes (1.1.1.1 / 8.8.8.8) to verify your local Wi-Fi router and ISP physical queue health.\n• In-Match Ping: Dictated entirely by Krafton/Tencent game servers. If match-making places you in an overseas datacenter, physical distance will cause higher in-game latency regardless of device optimization.\n• FrameForge does not use fake 'ping reducing VPN' snake oil.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Recommendations
            item {
                GamingCard(backgroundColor = GamingSurface) {
                    Text("Telemetry Observations & Advice", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    report.recommendations.forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonGreenActive,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = rec, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun EndpointPingItem(ping: NetworkPingResult) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GamingSurface)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ping.endpoint.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("${ping.endpoint.host}:${ping.endpoint.port} • DNS: ${ping.dnsLookupMs}ms", fontSize = 11.sp, color = TextMuted)
            }

            if (ping.isSuccessful) {
                StatusBadge(
                    text = "${ping.latencyMs} ms",
                    color = if (ping.latencyMs < 35) NeonGreenActive else if (ping.latencyMs < 75) ElectricCyan else Color(0xFFFF9800)
                )
            } else {
                StatusBadge(text = "Failed", color = Color(0xFFFF5252))
            }
        }
    }
}
