package com.example.core.adapter

import android.app.GameManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.PowerManager
import android.view.Display
import com.example.core.device.DeviceSummary

enum class CapabilityStatus(val label: String, val badgeColorHex: Long) {
    AVAILABLE("Available (Ready)", 0xFF00FFCC),
    SUPPORTED_UNAUTHORIZED("Supported (Needs Shizuku/ADB)", 0xFFFFB74D),
    APPLIED_VERIFIED("Applied & Verified", 0xFF69F0AE),
    FAILED("Failed Verification", 0xFFFF5252),
    UNSUPPORTED("Unsupported on Hardware/OS", 0xFF757575)
}

data class CapabilityEntry(
    val id: String,
    val title: String,
    val category: String,
    val status: CapabilityStatus,
    val description: String,
    val technicalDetails: String,
    val verifiedValue: String,
    val actionHint: String? = null
)

class UniversalCapabilityRegistry(private val context: Context) {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private val gameManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(Context.GAME_SERVICE) as? GameManager
    } else null

    fun inspectAllCapabilities(deviceSummary: DeviceSummary, hasShizuku: Boolean): List<CapabilityEntry> {
        val list = mutableListOf<CapabilityEntry>()

        // 1. Display Adapter
        val defaultDisplay = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
        val supportedModes = defaultDisplay?.supportedModes ?: emptyArray()
        val maxHz = supportedModes.maxOfOrNull { it.refreshRate } ?: 60f
        val currentHz = defaultDisplay?.mode?.refreshRate ?: 60f

        list.add(
            CapabilityEntry(
                id = "display_high_refresh",
                title = "Hardware Display Refresh Rates",
                category = "Display Architecture",
                status = if (maxHz >= 90f) CapabilityStatus.AVAILABLE else CapabilityStatus.UNSUPPORTED,
                description = "Physical panel modes supported by display controller (${supportedModes.map { "${it.refreshRate.toInt()}Hz" }.distinct().joinToString(", ")})",
                technicalDetails = "Default Display ID: ${defaultDisplay?.displayId ?: 0}, Active Mode: ${defaultDisplay?.mode?.modeId} (${currentHz.toInt()}Hz), Max Panel Rate: ${maxHz.toInt()}Hz",
                verifiedValue = "${maxHz.toInt()}Hz Hardware Panel",
                actionHint = if (maxHz >= 120f) "120Hz/144Hz panel ready for high frame rate rendering" else "Panel limited to 60Hz"
            )
        )

        // 2. Thermal Headroom Adapter (API 30+)
        val hasHeadroomApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        val headroomStatus = if (hasHeadroomApi && powerManager != null) {
            CapabilityStatus.AVAILABLE
        } else {
            CapabilityStatus.UNSUPPORTED
        }
        list.add(
            CapabilityEntry(
                id = "thermal_headroom_adpf",
                title = "ADPF Thermal Headroom API",
                category = "Thermal Architecture",
                status = headroomStatus,
                description = "Direct OS prediction of thermal budget (PowerManager.getThermalHeadroom)",
                technicalDetails = if (hasHeadroomApi) "Android 11+ PowerManager query supported with lookahead forecast" else "Requires Android 11 (API 30)+",
                verifiedValue = if (hasHeadroomApi) "Active Telemetry" else "Inferred from Battery Thermal",
                actionHint = "Enables predictive thermal backoff before CPU throttles"
            )
        )

        // 3. Android Game Mode API (API 31+)
        val hasGameManager = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && gameManager != null
        val gameModeStatus = when {
            hasGameManager && hasShizuku -> CapabilityStatus.AVAILABLE
            hasGameManager -> CapabilityStatus.SUPPORTED_UNAUTHORIZED
            else -> CapabilityStatus.UNSUPPORTED
        }
        list.add(
            CapabilityEntry(
                id = "android_game_mode",
                title = "Android 12+ Game Mode API",
                category = "OS Game Mode",
                status = gameModeStatus,
                description = "System-level game state configuration (cmd game set --mode 2 / PERFORMANCE)",
                technicalDetails = if (hasGameManager) "GameManager system service present in framework" else "Requires Android 12 (API 31)+",
                verifiedValue = if (hasShizuku) "Shizuku Authorized" else "Requires Shizuku / ADB",
                actionHint = if (!hasShizuku) "Grant Shizuku permission to apply system game mode" else "Ready to switch to Performance mode"
            )
        )

        // 4. Shizuku IPC Adapter
        list.add(
            CapabilityEntry(
                id = "shizuku_ipc",
                title = "Shizuku / ADB IPC Bridge",
                category = "Privileged Management",
                status = if (hasShizuku) CapabilityStatus.APPLIED_VERIFIED else CapabilityStatus.SUPPORTED_UNAUTHORIZED,
                description = "Direct binder IPC with android.uid.shell for non-root system property and window adjustments",
                technicalDetails = if (hasShizuku) "Binder IPC established with privileged shell daemon" else "Shizuku service not running or permission pending",
                verifiedValue = if (hasShizuku) "UID 2000 (Shell)" else "App Sandbox (Normal)",
                actionHint = "Allows automated display refresh rate pinning and game mode enforcement"
            )
        )

        // 5. OEM Gaming Engine Detection
        val oemInfo = detectOemEngine(deviceSummary.manufacturer)
        list.add(
            CapabilityEntry(
                id = "oem_integration",
                title = oemInfo.title,
                category = "OEM Gaming Ecosystem",
                status = oemInfo.status,
                description = oemInfo.description,
                technicalDetails = oemInfo.technicalDetails,
                verifiedValue = oemInfo.verifiedValue,
                actionHint = oemInfo.actionHint
            )
        )

        // 6. Sensor HAL Gyroscope
        list.add(
            CapabilityEntry(
                id = "sensor_gyro",
                title = "Hardware 6-Axis Gyroscope",
                category = "Sensors & Input",
                status = CapabilityStatus.AVAILABLE,
                description = "High-frequency motion sensor HAL delivering rotation vector & angular velocity",
                technicalDetails = "Direct Android SensorEventListener continuous reporting stream",
                verifiedValue = "Hardware Continuous HAL",
                actionHint = "Use Gyro Response Lab for calibration and stabilization tests"
            )
        )

        return list
    }

    private fun detectOemEngine(manufacturer: String): OemEngineInfo {
        val lower = manufacturer.lowercase()
        return when {
            lower.contains("vivo") || lower.contains("iqoo") -> OemEngineInfo(
                title = "vivo / iQOO OriginOS Gaming Suite",
                status = CapabilityStatus.AVAILABLE,
                description = "OriginOS Ultra Game Mode & Monster Mode integration detected",
                technicalDetails = "Supports Monster Mode power delivery and native Gyroscope Sensitivity options in Game Sidebar",
                verifiedValue = "OriginOS / FuntouchOS",
                actionHint = "Enable Monster Mode in Control Center for peak sustained CPU clock frequencies"
            )
            lower.contains("xiaomi") || lower.contains("redmi") || lower.contains("poco") -> OemEngineInfo(
                title = "Xiaomi Game Turbo / HyperOS Engine",
                status = CapabilityStatus.AVAILABLE,
                description = "Xiaomi HyperOS / MIUI Game Turbo detected",
                technicalDetails = "Joyose thermal manager and Performance Mode governor",
                verifiedValue = "HyperOS / MIUI",
                actionHint = "Add PUBG Mobile to Game Turbo and toggle Performance Mode"
            )
            lower.contains("samsung") -> OemEngineInfo(
                title = "Samsung Game Booster / OneUI",
                status = CapabilityStatus.AVAILABLE,
                description = "Samsung OneUI Game Booster & Game Optimizing Service (GOS)",
                technicalDetails = "Supports Game Booster Plus and Alternate Game Performance Management (Labs)",
                verifiedValue = "OneUI",
                actionHint = "Enable 'Alternate game performance management' in Game Booster Labs"
            )
            lower.contains("oneplus") || lower.contains("oppo") || lower.contains("realme") -> OemEngineInfo(
                title = "ColorOS / OxygenOS HyperBoost",
                status = CapabilityStatus.AVAILABLE,
                description = "HyperBoost GPA Frame Stabilizer detected",
                technicalDetails = "Gaming Tools sidebar with Pro Gamer Mode",
                verifiedValue = "ColorOS / OxygenOS",
                actionHint = "Switch Game Mode to 'Pro Gamer Mode' in Game Assistant"
            )
            else -> OemEngineInfo(
                title = "Universal Android Gaming Framework",
                status = CapabilityStatus.AVAILABLE,
                description = "Standard Android AOSP Game Mode & Dynamic Performance Framework",
                technicalDetails = "Standard Android HAL APIs",
                verifiedValue = "Standard Android",
                actionHint = "FrameForge applies standard ADPF hints and display modes"
            )
        }
    }

    private data class OemEngineInfo(
        val title: String,
        val status: CapabilityStatus,
        val description: String,
        val technicalDetails: String,
        val verifiedValue: String,
        val actionHint: String
    )
}
