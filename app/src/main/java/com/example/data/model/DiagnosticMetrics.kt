package com.example.data.model

enum class BottleneckSeverity(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical")
}

enum class DiagnosticCategory(val displayName: String) {
    FPS_CAP("Game FPS Cap vs Display Mode"),
    DISPLAY_REFRESH("Display Refresh Rate & OriginOS Policy"),
    THERMAL_THROTTLE("Thermal Headroom & Power Restrictions"),
    CPU_BOTTLENECK("CPU Load & Thread Scheduling"),
    GPU_BOTTLENECK("GPU Workload & Fillrate"),
    FRAME_PACING("Frame Pacing & Micro-Stutter"),
    SENSOR_TIMING("Gyroscope & Touch Latency"),
    MEMORY_PRESSURE("RAM Pressure & Background Churn"),
    ADB_VERIFICATION("ADB / Shizuku Command State"),
    STORAGE_CONFIG("Scoped Storage & Config File Access"),
    CONFLICTING_SETTINGS("Conflicting Optimizations")
}

enum class ConfidenceLevel(val displayName: String) {
    HIGH("High Confidence"),
    MEDIUM("Medium Confidence"),
    LOW("Estimated / Low Confidence")
}

enum class MeasurementType(val displayName: String) {
    DIRECT_MEASURED("Directly Measured"),
    INFERRED("Inferred Architecture")
}

enum class AppActionCapability(val displayName: String) {
    DIRECTLY_APPLY("FrameForge Can Apply Directly"),
    REQUIRES_IN_GAME_SETTINGS("Requires In-Game Action"),
    REQUIRES_SHIZUKU_ADB("Requires Shizuku / ADB"),
    INFORMATIONAL_ONLY("Hardware Limitation / Info Only")
}

data class PerformanceDiagnosticItem(
    val id: String,
    val category: DiagnosticCategory,
    val title: String,
    val severity: BottleneckSeverity,
    val evidence: String,
    val confidence: ConfidenceLevel,
    val measurementType: MeasurementType,
    val recommendedAction: String,
    val canAppApplyAction: AppActionCapability,
    val actionLabel: String? = null,
    val actionId: String? = null
)

data class DiagnosticMetrics(
    val fpsCurrent: Float = 0f,
    val fpsAvg: Float = 0f,
    val isGameFpsDirectlyMeasurable: Boolean = false,
    val fpsMeasurementSource: String = "Applet UI Choreographer (Direct game surface FPS requires privileged overlay)",
    val frameTimeMsAvg: Float = 8.3f,
    val p50Ms: Float = 8.3f,
    val p95Ms: Float = 11.2f,
    val p99Ms: Float = 16.7f,
    val stutterCount: Int = 0,
    val refreshRateHz: Int = 144,
    val batteryPct: Int = 85,
    val isCharging: Boolean = false,
    val tempCelsius: Float = 38.0f,
    val thermalHeadroom: Float = 0.85f, // 0.0 (throttled) to 1.0 (cool)
    val thermalStatusString: String = "Nominal",
    val isThermalThrottling: Boolean = false,
    val cpuUsagePct: Int = 22,
    val gpuUsagePct: Int = 35,
    val ramUsedMb: Long = 5120L,
    val ramTotalMb: Long = 16384L,
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

data class BottleneckAnalysis(
    val primaryLabel: String = "System Nominal",
    val primaryPercentage: Int = 35,
    val cpuLoadPct: Int = 22,
    val gpuLoadPct: Int = 35,
    val memoryLoadPct: Int = 31,
    val thermalLoadPct: Int = 25,
    val networkLatencyMs: Int = 24,
    val topBottlenecks: List<BottleneckItem> = emptyList()
)

// Priority 2: ADB/Shizuku Command Registry & Test Center Models
enum class CommandCategory(val categoryNumber: Int, val displayName: String) {
    DEVICE_INFO(1, "Device & Android Information"),
    DISPLAY_STATE(2, "Display Modes & Refresh-Rate State"),
    GAME_PACKAGE(3, "Game Package & Launch Activity"),
    AUTHORIZATION(4, "ADB / Shizuku Authorization"),
    GAME_MODE(5, "Game Mode & Performance Operations"),
    GRAPHICS_RESOLUTION(6, "Graphics & Resolution Operations"),
    STATE_RESTORE(7, "Original State Capture & Restoration"),
    GYRO_DIAGNOSTICS(8, "Gyroscope Diagnostic Availability"),
    CONFIG_ACCESS(9, "Configuration File Access Permissions"),
    SESSION_DETECTION(10, "Background Session Detection")
}

enum class AuthorizationLevel(val displayName: String) {
    NONE("No Special Auth Required"),
    SHIZUKU_ADB("Shizuku / ADB Shell Required"),
    ROOT("Root Access Required")
}

enum class CommandClassification(val displayName: String) {
    READ_ONLY("Read-Only (Diagnostic)"),
    STATE_CHANGING("State-Changing (Reversible)")
}

enum class CommandTestStatus(val displayName: String) {
    NOT_TESTED("NOT TESTED"),
    PASS_VERIFIED("PASS — VERIFIED"),
    PASS_PARTIALLY_VERIFIED("PASS — RESULT PARTIALLY VERIFIED"),
    UNSUPPORTED("UNSUPPORTED"),
    PERMISSION_DENIED("PERMISSION DENIED"),
    FAILED("FAILED"),
    RESTORE_TEST_FAILED("RESTORE TEST FAILED")
}

data class CommandRegistryItem(
    val id: String,
    val name: String,
    val command: String,
    val purpose: String,
    val category: CommandCategory,
    val authorization: AuthorizationLevel,
    val classification: CommandClassification,
    val compatibility: String,
    val validationSteps: String,
    val expectedResult: String,
    val actualResult: String? = null,
    val status: CommandTestStatus = CommandTestStatus.NOT_TESTED,
    val failureReason: String? = null,
    val rollbackCommand: String? = null,
    val verifiedStateChange: Boolean = false,
    val originalStateCaptured: String? = null
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
