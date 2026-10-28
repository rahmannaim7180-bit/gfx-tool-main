package com.example.core.performance

import android.content.Context
import com.example.data.model.*

class BottleneckDetector(private val context: Context) {

    fun getCpuUsagePercentage(): Int = 28

    fun analyzeBottlenecks(
        currentFps: Float = 60f,
        targetFps: Float = 60f,
        batteryLevel: Int = 85,
        isCharging: Boolean = false,
        currentFrameTimeMs: Float = 16.6f,
        gpuLoadEst: Int = 35,
        isExternalCoolerActive: Boolean = false
    ): BottleneckAnalysis {
        val items = mutableListOf<BottleneckItem>()
        if (currentFps < targetFps * 0.9f || gpuLoadEst > 75) {
            items.add(
                BottleneckItem(
                    id = "gpu_disparity",
                    title = "GPU Frame Time Disparity",
                    severity = BottleneckSeverity.MEDIUM,
                    description = "GPU bound rendering detected during dense scene composition.",
                    recommendation = "Reduce shadow resolution or apply Raw Performance profile."
                )
            )
        }
        return BottleneckAnalysis(
            primaryLabel = if (items.isNotEmpty()) "Render Geometry" else "System Nominal",
            primaryPercentage = if (items.isNotEmpty()) 45 else 20,
            cpuLoadPct = 28,
            gpuLoadPct = if (items.isNotEmpty()) 78 else gpuLoadEst,
            memoryLoadPct = 35,
            thermalLoadPct = if (isExternalCoolerActive) 18 else 32,
            networkLatencyMs = 28,
            topBottlenecks = items
        )
    }
}
