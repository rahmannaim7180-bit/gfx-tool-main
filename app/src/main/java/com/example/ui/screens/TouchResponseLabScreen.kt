package com.example.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.touch.TouchLatencyMetrics
import com.example.ui.components.GamingCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TouchResponseLabScreen(
    metrics: TouchLatencyMetrics,
    onRecordTouchEvent: (MotionEvent) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    var touchPos by remember { mutableStateOf<Offset?>(null) }
    var touchHistory by remember { mutableStateOf(listOf<Offset>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Touch Response Lab", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Touch-to-Draw Latency • Polling Rate • Micro-Aim Jitter", fontSize = 11.sp, color = ElectricCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("touch_lab_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onReset, modifier = Modifier.testTag("touch_lab_reset_button")) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = TextSecondary)
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
            // Interactive Touch Sampling Canvas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GamingSurface)
                        .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .pointerInteropFilter { motionEvent ->
                            onRecordTouchEvent(motionEvent)
                            when (motionEvent.actionMasked) {
                                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                    touchPos = Offset(motionEvent.x, motionEvent.y)
                                    touchHistory = (touchHistory + Offset(motionEvent.x, motionEvent.y)).takeLast(25)
                                }
                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                    touchPos = null
                                }
                            }
                            true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background grid
                        val step = 40.dp.toPx()
                        for (x in 0..(size.width / step).toInt()) {
                            drawLine(
                                color = GamingSurfaceBorder.copy(alpha = 0.3f),
                                start = Offset(x * step, 0f),
                                end = Offset(x * step, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..(size.height / step).toInt()) {
                            drawLine(
                                color = GamingSurfaceBorder.copy(alpha = 0.3f),
                                start = Offset(0f, y * step),
                                end = Offset(size.width, y * step),
                                strokeWidth = 1f
                            )
                        }

                        // Trail points
                        touchHistory.forEachIndexed { idx, point ->
                            val alpha = (idx + 1) / touchHistory.size.toFloat()
                            drawCircle(
                                color = ElectricCyan.copy(alpha = alpha * 0.5f),
                                radius = 6f * alpha,
                                center = point
                            )
                        }

                        // Current contact point
                        touchPos?.let { pos ->
                            drawCircle(
                                color = ElectricCyan,
                                radius = 18f,
                                center = pos,
                                style = Stroke(width = 2.5f)
                            )
                            drawCircle(
                                color = ElectricCyan.copy(alpha = 0.3f),
                                radius = 28f,
                                center = pos
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4f,
                                center = pos
                            )
                        }
                    }

                    if (touchPos == null && touchHistory.isEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.TouchApp, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("TAP, SWIPE, OR FLICK IN THIS AREA", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp, letterSpacing = 1.sp)
                            Text("Evaluates hardware digitizer response and motion dispatch jitter", fontSize = 11.sp, color = TextTertiary)
                        }
                    }
                }
            }

            // Real-Time Touch Metrics Card
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("DIGITIZER TELEMETRY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Touch-to-Draw Latency", fontSize = 10.sp, color = TextTertiary)
                                Text(
                                    "${String.format("%.1f", metrics.avgTouchToDrawLatencyMs)} ms",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (metrics.avgTouchToDrawLatencyMs < 16f) ElectricCyan else CoralRed
                                )
                                Text("Min: ${String.format("%.1f", metrics.minLatencyMs)} • Max: ${String.format("%.1f", metrics.maxLatencyMs)} ms", fontSize = 9.sp, color = TextTertiary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Estimated Polling Rate", fontSize = 10.sp, color = TextTertiary)
                                Text(
                                    "${metrics.estimatedTouchSamplingRateHz} Hz",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                                Text("Interval: ${String.format("%.2f", metrics.touchEventIntervalMs)} ms", fontSize = 9.sp, color = TextTertiary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Swipe Consistency", fontSize = 10.sp, color = TextTertiary)
                                Text("${metrics.tapConsistencyScore} / 100", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Interval Jitter", fontSize = 10.sp, color = TextTertiary)
                                Text("±${String.format("%.2f", metrics.swipeJitterMs)} ms", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (metrics.swipeJitterMs < 2.0f) ElectricCyan else CoralRed)
                            }
                        }
                    }
                }
            }

            // Guided Aiming Consistency Analysis
            item {
                GamingCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("MICRO-AIMING & RECOIL READINESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            when {
                                metrics.estimatedTouchSamplingRateHz >= 240 ->
                                    "High-Speed Digitizer Active: 240Hz+ touch sampling minimizes aiming input lag for rapid spray transfers and quick scope alignment."
                                metrics.estimatedTouchSamplingRateHz >= 120 ->
                                    "Standard High-Speed Digitizer: 120Hz touch sampling active. Sufficient for standard flicking and steady recoil pull-down."
                                else ->
                                    "Standard 60Hz Polling: Recommend enabling 'Ultra Game Mode Touch Acceleration' in OriginOS Game Sidebar for elevated touch sampling."
                            },
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
