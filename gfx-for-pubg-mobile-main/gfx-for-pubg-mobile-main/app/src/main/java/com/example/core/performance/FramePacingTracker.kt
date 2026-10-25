package com.example.core.performance

import android.view.Choreographer
import com.example.data.model.DiagnosticMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class FramePacingTracker : Choreographer.FrameCallback {

    private var isTracking = false
    private var lastFrameTimeNs: Long = 0L
    private val frameDurationsMs = ArrayDeque<Float>(120)
    private var stutterCount = 0

    private val _metrics = MutableStateFlow(DiagnosticMetrics())
    val metrics: StateFlow<DiagnosticMetrics> = _metrics.asStateFlow()

    fun startTracking() {
        if (!isTracking) {
            isTracking = true
            lastFrameTimeNs = 0L
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun stopTracking() {
        if (isTracking) {
            isTracking = false
            Choreographer.getInstance().removeFrameCallback(this)
        }
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isTracking) return

        if (lastFrameTimeNs != 0L) {
            val deltaNs = frameTimeNanos - lastFrameTimeNs
            if (deltaNs > 0) {
                val frameTimeMs = deltaNs / 1_000_000f

                synchronized(frameDurationsMs) {
                    if (frameDurationsMs.size >= 120) frameDurationsMs.removeFirst()
                    frameDurationsMs.addLast(frameTimeMs)
                }

                // Expected interval for 120Hz = 8.33ms; 90Hz = 11.1ms; 60Hz = 16.6ms
                val nominalIntervalMs = 1000f / _metrics.value.refreshRateHz
                if (frameTimeMs > (nominalIntervalMs * 1.5f)) {
                    stutterCount++
                }

                computeStats()
            }
        }
        lastFrameTimeNs = frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun computeStats() {
        val list = synchronized(frameDurationsMs) { frameDurationsMs.toList() }
        if (list.size < 5) return

        val sorted = list.sorted()
        val avgMs = list.average().toFloat()
        val currentFps = (1000f / (list.lastOrNull() ?: 11.1f)).coerceIn(10f, 144f)
        val avgFps = (1000f / avgMs).coerceIn(10f, 144f)

        val p50 = sorted[(sorted.size * 0.50).toInt().coerceIn(0, sorted.lastIndex)]
        val p95 = sorted[(sorted.size * 0.95).toInt().coerceIn(0, sorted.lastIndex)]
        val p99 = sorted[(sorted.size * 0.99).toInt().coerceIn(0, sorted.lastIndex)]

        _metrics.value = _metrics.value.copy(
            fpsCurrent = (currentFps * 10).roundToInt() / 10f,
            fpsAvg = (avgFps * 10).roundToInt() / 10f,
            frameTimeMsAvg = (avgMs * 10).roundToInt() / 10f,
            p50Ms = (p50 * 10).roundToInt() / 10f,
            p95Ms = (p95 * 10).roundToInt() / 10f,
            p99Ms = (p99 * 10).roundToInt() / 10f,
            stutterCount = stutterCount,
            frameTimeHistory = list.takeLast(40),
            fpsHistory = list.takeLast(40).map { (1000f / it).coerceIn(10f, 144f) }
        )
    }

    fun updateRefreshRate(refreshRateHz: Int) {
        _metrics.value = _metrics.value.copy(refreshRateHz = refreshRateHz)
    }

    fun updateTelemetry(battery: Int, temp: Float, cpu: Int, gpu: Int, ramUsed: Long, ramTotal: Long) {
        _metrics.value = _metrics.value.copy(
            batteryPct = battery,
            tempCelsius = temp,
            cpuUsagePct = cpu,
            gpuUsagePct = gpu,
            ramUsedMb = ramUsed,
            ramTotalMb = ramTotal
        )
    }
}
