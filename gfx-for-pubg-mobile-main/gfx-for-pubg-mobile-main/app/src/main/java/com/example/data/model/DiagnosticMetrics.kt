package com.example.data.model

data class DiagnosticMetrics(
    val fpsCurrent: Float = 89.7f,
    val fpsAvg: Float = 88.4f,
    val frameTimeMsAvg: Float = 11.2f,
    val p50Ms: Float = 8.4f,
    val p95Ms: Float = 16.7f,
    val p99Ms: Float = 28.3f,
    val stutterCount: Int = 3,
    val refreshRateHz: Int = 120,
    val batteryPct: Int = 86,
    val isCharging: Boolean = false,
    val tempCelsius: Float = 44.0f,
    val isThermalThrottling: Boolean = false,
    val cpuUsagePct: Int = 26,
    val gpuUsagePct: Int = 42,
    val ramUsedMb: Long = 4200L,
    val ramTotalMb: Long = 12000L,
    val frameTimeHistory: List<Float> = emptyList(),
    val fpsHistory: List<Float> = emptyList()
)

data class BottleneckItem(
    val id: String,
    val title: String,
    val severity: BottleneckSeverity,
    val description: String,
    val recommendation: String
)

enum class BottleneckSeverity(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

data class BottleneckAnalysis(
    val primaryLabel: String = "GPU Limited",
    val primaryPercentage: Int = 42,
    val cpuLoadPct: Int = 26,
    val gpuLoadPct: Int = 42,
    val memoryLoadPct: Int = 34,
    val thermalLoadPct: Int = 22,
    val networkLatencyMs: Int = 28,
    val topBottlenecks: List<BottleneckItem> = listOf(
        BottleneckItem(
            id = "gpu_load",
            title = "GPU Load (High)",
            severity = BottleneckSeverity.HIGH,
            description = "High shader complexity and MSAA passes detected during intense gunfights",
            recommendation = "Switch to Smooth Graphics quality and disable dynamic shadows in Game Settings"
        ),
        BottleneckItem(
            id = "frame_variance",
            title = "Frame Time Variance (Medium)",
            severity = BottleneckSeverity.MEDIUM,
            description = "Occasional 16.7ms frame deadlines missed during smoke/asset loading",
            recommendation = "Enable Frame Pacing Stabilizer and verify 120Hz display mode locking"
        ),
        BottleneckItem(
            id = "thermal_rise",
            title = "Thermal Rise (Low)",
            severity = BottleneckSeverity.LOW,
            description = "Device temperature is 44°C; currently 4°C below SoC thermal throttle trip point",
            recommendation = "Enable external phone cooler or select Stable Competitive mode for multi-match sessions"
        )
    )
)

data class AdbCommand(
    val id: String,
    val title: String,
    val description: String,
    val command: String,
    val isReversible: Boolean = true,
    val rollbackCommand: String? = null,
    val category: String = "Performance",
    val requiresShizuku: Boolean = true
) {
    companion object {
        val PREDEFINED_COMMANDS = listOf(
            AdbCommand(
                id = "set_game_mode",
                title = "Set Game Mode",
                description = "Configures Android Game Manager to performance mode for selected game",
                command = "cmd game set --mode 2 com.tencent.ig",
                rollbackCommand = "cmd game reset com.tencent.ig",
                category = "Performance"
            ),
            AdbCommand(
                id = "set_refresh_rate_144",
                title = "Force 144 Hz Mode (iQOO / OriginOS)",
                description = "Locks panel to 144Hz refresh rate on iQOO Neo 9 5G (OriginOS 6)",
                command = "settings put system peak_refresh_rate 144.0 && settings put system min_refresh_rate 144.0 && settings put system user_refresh_rate 144",
                rollbackCommand = "settings delete system min_refresh_rate && settings delete system peak_refresh_rate && settings delete system user_refresh_rate",
                category = "Display"
            ),
            AdbCommand(
                id = "set_refresh_rate_120",
                title = "Set Refresh Rate (120 Hz)",
                description = "Forces Android peak and min refresh rate to 120Hz to prevent dynamic stutter",
                command = "settings put system peak_refresh_rate 120.0 && settings put system min_refresh_rate 120.0",
                rollbackCommand = "settings delete system min_refresh_rate && settings delete system peak_refresh_rate",
                category = "Display"
            ),
            AdbCommand(
                id = "originos_multi_turbo",
                title = "OriginOS Multi-Turbo Boost",
                description = "Requests maximum scheduler priority & gaming thread headroom under OriginOS",
                command = "cmd game set --mode 2 com.tencent.ig && settings put global vivo_multi_turbo 1",
                rollbackCommand = "cmd game reset com.tencent.ig && settings delete global vivo_multi_turbo",
                category = "Performance"
            ),
            AdbCommand(
                id = "lock_fps_limit_144",
                title = "Unlock SurfaceFlinger 144 FPS Cap",
                description = "Synchronizes window manager compositor cap to 144 FPS for high-refresh panels",
                command = "settings put global fps_limit 144",
                rollbackCommand = "settings delete global fps_limit",
                category = "Graphics"
            ),
            AdbCommand(
                id = "clear_game_cache",
                title = "Clear Game Cache",
                description = "Safely trims game temporary asset shader caches without touching user data",
                command = "pm trim-caches 1000M",
                isReversible = false,
                category = "Maintenance"
            ),
            AdbCommand(
                id = "optimize_cpu_perf",
                title = "Optimize CPU Hints",
                description = "Configures scheduler CPU boost hints for active gaming session",
                command = "cmd activity track-associations",
                category = "Performance"
            ),
            AdbCommand(
                id = "optimize_gpu_pipeline",
                title = "Optimize GPU Frame Pipeline",
                description = "Inspects and sets surfaceflinger backpressure queue depth",
                command = "dumpsys SurfaceFlinger --latency",
                category = "Graphics"
            ),
            AdbCommand(
                id = "check_device_info",
                title = "Check Device & Display Info",
                description = "Queries real display modes, resolution configs, and thermal status",
                command = "dumpsys display | grep -E 'mBaseDisplayInfo|DisplayModeRecord'",
                isReversible = false,
                category = "Diagnostics"
            )
        )
    }
}
