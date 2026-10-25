package com.example.data.model

enum class PerformanceMode(val displayName: String, val tagLine: String) {
    BALANCED("Balanced", "Best for daily gaming & normal thermal envelope"),
    STABLE_COMPETITIVE("Stable Competitive", "Consistent FPS & frame-time smoothness"),
    RAW_PERFORMANCE("Raw Performance Mode", "Max supported CPU/GPU hints & lowest input latency (Beta)")
}

data class ActiveOptimizationItem(
    val id: String,
    val title: String,
    val description: String,
    val isSupported: Boolean,
    val isApplied: Boolean,
    val requiresShizuku: Boolean = false,
    val statusNote: String = "Not Applied"
)

data class RawPerformanceState(
    val isEnabled: Boolean = false,
    val useAdbShizuku: Boolean = false,
    val applyWhenLaunchingGame: Boolean = true,
    val autoRestoreAfterGameExit: Boolean = true,
    val isExternalCoolerActive: Boolean = false,
    val optimizations: List<ActiveOptimizationItem> = listOf(
        ActiveOptimizationItem(
            id = "cpu_hints",
            title = "CPU Performance Hints",
            description = "Requests Android ADPF performance session & high-affinity scheduling",
            isSupported = true,
            isApplied = false,
            statusNote = "Ready to request"
        ),
        ActiveOptimizationItem(
            id = "gpu_hints",
            title = "GPU Performance Hints",
            description = "Requests surface frame pacing hints and minimizes composition stall",
            isSupported = true,
            isApplied = false,
            statusNote = "Ready to request"
        ),
        ActiveOptimizationItem(
            id = "background_reduction",
            title = "Background Activity Reduction",
            description = "Throttles background telemetry, trims cache, and lowers process churn",
            isSupported = true,
            isApplied = false,
            statusNote = "Safe trimming enabled"
        ),
        ActiveOptimizationItem(
            id = "high_refresh_rate",
            title = "High Refresh Rate Support",
            description = "Requests maximum supported display mode ID (90Hz / 120Hz)",
            isSupported = true,
            isApplied = false,
            statusNote = "Display capability verified"
        ),
        ActiveOptimizationItem(
            id = "game_mode_adpf",
            title = "Android Game Mode API (cmd game)",
            description = "Invokes system game manager performance mode via Shizuku/ADB",
            isSupported = true,
            isApplied = false,
            requiresShizuku = true,
            statusNote = "Requires Shizuku authorization"
        )
    )
)

enum class GyroMode(val displayName: String) {
    STANDARD("Standard"),
    ENHANCED("Enhanced"),
    PRO("Pro")
}

data class GyroBoostConfig(
    val isEnabled: Boolean = true,
    val xSensitivityPercent: Int = 120,
    val ySensitivityPercent: Int = 120,
    val mode: GyroMode = GyroMode.ENHANCED,
    val antiDriftFilter: Boolean = true,
    val sensorModel: String = "Detecting sensor...",
    val maxSamplingRateHz: Float = 400f,
    val currentSamplingRateHz: Float = 200f,
    val sampleJitterMs: Float = 1.2f,
    val currentX: Float = 0.02f,
    val currentY: Float = -0.01f,
    val currentZ: Float = 0.00f,
    val isTestRunning: Boolean = false,
    val testScore: Int = 96
)
