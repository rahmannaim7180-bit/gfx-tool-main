package com.example.core.performance

import android.view.Choreographer
import com.example.data.model.DiagnosticMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FramePacingTracker : Choreographer.FrameCallback {
    private val _metrics = MutableStateFlow(
        DiagnosticMetrics(
            fpsCurrent = 60f,
            fpsAvg = 60f,
            frameTimeMsAvg = 16.6f,
            p95Ms = 17.2f,
            p99Ms = 19.8f,
            stutterCount = 0,
            refreshRateHz = 60,
            thermalHeadroom = 0.88f
        )
    )
    val metrics: StateFlow<DiagnosticMetrics> = _metrics.asStateFlow()

    private var isTracking = false
    private var lastFrameTimeNs: Long = 0L

    fun startTracking() {
        if (!isTracking) {
            isTracking = true
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    fun stopTracking() {
        isTracking = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isTracking) return
        if (lastFrameTimeNs > 0) {
            val deltaMs = (frameTimeNanos - lastFrameTimeNs) / 1_000_000f
            if (deltaMs in 5f..100f) {
                val fps = (1000f / deltaMs).coerceIn(1f, 144f)
                _metrics.value = _metrics.value.copy(
                    fpsCurrent = fps,
                    frameTimeMsAvg = deltaMs
                )
            }
        }
        lastFrameTimeNs = frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun updateRefreshRate(rate: Int) {
        _metrics.value = _metrics.value.copy(refreshRateHz = rate)
    }

    fun updateTelemetry(
        battery: Int = 85,
        temp: Float = 36.5f,
        cpu: Int = 25,
        gpu: Int = 35,
        ramUsed: Long = 4000L,
        ramTotal: Long = 16000L,
        fps: Int = 60,
        avgFrameTime: Float = 16.6f,
        p95: Int = 18,
        p99: Int = 20,
        jankFrames: Long = 0,
        totalFrames: Long = 100
    ) {
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
