package com.example.data.model

enum class PerformanceMode(val displayName: String, val tagLine: String) {
    SUSTAINED_COMPETITIVE("Sustained Competitive", "Prioritizes consistent frame-time pacing & thermal stability over peak burst"),
    RAW_PERFORMANCE("Raw Performance", "Requests maximum ADPF CPU/GPU hints & 144Hz display policy with active thermal safeguards"),
    THERMAL_RECOVERY("Thermal Recovery", "Protective thermal back-off: throttles aggressive hints & advises reducing rendering workload"),
    BALANCED("Balanced", "Default power management & standard operating thermal boundaries")
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
    val isEnabled: Boolean = true,
    val useAdbShizuku: Boolean = false,
    val applyWhenLaunchingGame: Boolean = true,
    val autoRestoreAfterGameExit: Boolean = true,
    val isExternalCoolerActive: Boolean = false,
    val thermalHeadroom: Float = 0.85f,
    val currentMode: PerformanceMode = PerformanceMode.RAW_PERFORMANCE,
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
            description = "Requests maximum supported display mode ID (120Hz / 144Hz)",
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
    RAW_PASSTHROUGH("Bypass Mode (Raw 1:1)"),
    LIGHT_FILTER("Standard Profile (Balanced)"),
    AGGRESSIVE_STABILIZATION("High Profile (Ultra Smooth)"),
    PREDICTIVE_AIM("Highest Profile (Pro E-Sports)")
}

data class GyroBoostConfig(
    val isEnabled: Boolean = true,
    val xSensitivityPercent: Int = 100,
    val ySensitivityPercent: Int = 100,
    val mode: GyroMode = GyroMode.LIGHT_FILTER,
    val antiDriftFilter: Boolean = true,
    val sensorModel: String = "Detecting sensor...",
    val maxSamplingRateHz: Float = 416f,
    val currentSamplingRateHz: Float = 200f,
    val sampleJitterMs: Float = 1.2f,
    val observedIntervalMs: Float = 5.0f,
    val currentX: Float = 0.0f,
    val currentY: Float = 0.0f,
    val currentZ: Float = 0.0f,
    val isTestRunning: Boolean = false,
    val testScore: Int = 96,
    val oemIntegrationStatus: String = "OriginOS does not expose third-party raw gyro multiplier APIs; In-Game Sensitivity Assistant active"
) {
    val xMultiplier: Float get() = (xSensitivityPercent / 100f).coerceIn(0.5f, 2.5f)
    val yMultiplier: Float get() = (ySensitivityPercent / 100f).coerceIn(0.5f, 2.5f)
}
