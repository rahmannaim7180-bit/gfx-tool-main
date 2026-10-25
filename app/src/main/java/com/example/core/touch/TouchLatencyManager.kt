package com.example.core.touch

import android.os.SystemClock
import android.view.MotionEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

data class TouchLatencyMetrics(
    val lastTouchToDrawLatencyMs: Float = 0f,
    val avgTouchToDrawLatencyMs: Float = 14.2f,
    val minLatencyMs: Float = 8.5f,
    val maxLatencyMs: Float = 28.0f,
    val touchEventIntervalMs: Float = 8.3f,
    val estimatedTouchSamplingRateHz: Int = 240,
    val tapConsistencyScore: Int = 92,
    val swipeJitterMs: Float = 1.1f,
    val totalSampleCount: Long = 0L,
    val recentLatencies: List<Float> = emptyList()
)

class TouchLatencyManager {

    private val _metrics = MutableStateFlow(TouchLatencyMetrics())
    val metrics: StateFlow<TouchLatencyMetrics> = _metrics.asStateFlow()

    private var lastEventTimeMs: Long = 0L
    private val intervals = ArrayDeque<Float>(40)
    private val latencies = ArrayDeque<Float>(40)

    fun recordTouchEvent(event: MotionEvent, drawTimestampMs: Long = SystemClock.uptimeMillis()) {
        val eventTimeMs = event.eventTime
        val touchToDrawMs = (drawTimestampMs - eventTimeMs).coerceAtLeast(0).toFloat()

        if (lastEventTimeMs != 0L) {
            val interval = (eventTimeMs - lastEventTimeMs).toFloat()
            if (interval in 1.0f..100.0f) {
                synchronized(intervals) {
                    if (intervals.size >= 40) intervals.removeFirst()
                    intervals.addLast(interval)
                }
            }
        }
        lastEventTimeMs = eventTimeMs

        synchronized(latencies) {
            if (latencies.size >= 40) latencies.removeFirst()
            latencies.addLast(touchToDrawMs)
        }

        val avgLatency = if (latencies.isNotEmpty()) latencies.average().toFloat() else 14f
        val minLat = if (latencies.isNotEmpty()) latencies.minOrNull() ?: 8f else 8f
        val maxLat = if (latencies.isNotEmpty()) latencies.maxOrNull() ?: 28f else 28f

        val avgInterval = if (intervals.isNotEmpty()) intervals.average().toFloat() else 8.3f
        val estHz = if (avgInterval > 0) (1000f / avgInterval).toInt().coerceIn(60, 1000) else 240

        val jitter = if (intervals.size > 2) {
            val variance = intervals.map { (it - avgInterval) * (it - avgInterval) }.average().toFloat()
            sqrt(variance)
        } else 1.1f

        val consistency = (100 - (jitter * 10).toInt()).coerceIn(40, 100)

        _metrics.value = TouchLatencyMetrics(
            lastTouchToDrawLatencyMs = touchToDrawMs,
            avgTouchToDrawLatencyMs = avgLatency,
            minLatencyMs = minLat,
            maxLatencyMs = maxLat,
            touchEventIntervalMs = avgInterval,
            estimatedTouchSamplingRateHz = estHz,
            tapConsistencyScore = consistency,
            swipeJitterMs = jitter,
            totalSampleCount = _metrics.value.totalSampleCount + 1,
            recentLatencies = latencies.toList()
        )
    }

    fun reset() {
        intervals.clear()
        latencies.clear()
        lastEventTimeMs = 0L
        _metrics.value = TouchLatencyMetrics()
    }
}
