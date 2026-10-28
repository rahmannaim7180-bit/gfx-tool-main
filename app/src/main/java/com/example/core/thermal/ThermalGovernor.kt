package com.example.core.thermal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThermalState(val displayName: String, val description: String) {
    COOL("Cool", "Normal gaming conditions, maximum sustained performance target"),
    WARM("Warm", "Rising thermal load, monitor frame pacing, maintain profile"),
    HOT("Hot", "Elevated skin temperature. Advise external cooling or 90 FPS cap"),
    THROTTLING_RISK("Throttling Risk", "Severe thermal trip. Back off aggressive hints, lock to stable 60/90 FPS"),
    RECOVERING("Recovering", "Cooling down from throttling. Gradually restoring sustainable settings")
}

enum class MatchReadinessVerdict(val label: String, val scoreColorHex: Long) {
    OPTIMAL("Ready for 8v8 WOW / Intense Matches", 0xFF00FFCC),
    CAUTION("Moderate Load - Sustained 90 FPS Recommended", 0xFFFFB74D),
    HIGH_RISK("High Throttling Risk - Cool Down Device First", 0xFFFF5252)
}

data class MatchReadinessReport(
    val score: Int, // 0 - 100
    val verdict: MatchReadinessVerdict,
    val batteryTempCelsius: Float,
    val thermalHeadroom: Float,
    val batteryLevelPct: Int,
    val recommendations: List<String>,
    val targetSustainableFps: Int
)

data class ThermalSnapshot(
    val thermalState: ThermalState = ThermalState.COOL,
    val thermalHeadroom: Float = 0.85f,
    val batteryTempCelsius: Float = 36.5f,
    val isCharging: Boolean = false,
    val batteryPct: Int = 85,
    val osThermalStatusCode: Int = 0,
    val osThermalStatusString: String = "NONE",
    val isExternalCoolerActive: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis()
)

class ThermalGovernor(private val context: Context) {

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    private val _snapshot = MutableStateFlow(ThermalSnapshot())
    val snapshot: StateFlow<ThermalSnapshot> = _snapshot.asStateFlow()

    private var previousState: ThermalState = ThermalState.COOL

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 360)
                val tempCelsius = rawTemp / 10.0f
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = if (level >= 0 && scale > 0) (level * 100) / scale else 85
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                updateBatteryMetrics(tempCelsius, pct, isCharging)
            }
        }
    }

    init {
        // Register battery receiver for temperature and charging state
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        runCatching {
            context.registerReceiver(batteryReceiver, filter)
        }

        // Register PowerManager thermal status listener on Android 10+
        val pm = powerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && pm != null) {
            runCatching {
                pm.addThermalStatusListener { statusCode ->
                    onOsThermalStatusChanged(statusCode)
                }
            }
        }

        pollHeadroom()
    }

    fun unregister() {
        runCatching {
            context.unregisterReceiver(batteryReceiver)
        }
    }

    fun setExternalCoolerActive(active: Boolean) {
        _snapshot.value = _snapshot.value.copy(isExternalCoolerActive = active)
        evaluateThermalState()
    }

    fun pollHeadroom() {
        val pm = powerManager
        val currentHeadroom = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && pm != null) {
            runCatching {
                // Lookahead 10 seconds forecast
                pm.getThermalHeadroom(10)
            }.getOrDefault(0.85f)
        } else {
            // Inferred from battery temperature if headroom API not supported
            val temp = _snapshot.value.batteryTempCelsius
            when {
                temp < 38f -> 0.90f
                temp < 41f -> 0.70f
                temp < 44f -> 0.40f
                else -> 0.15f
            }
        }

        _snapshot.value = _snapshot.value.copy(
            thermalHeadroom = currentHeadroom.coerceIn(0.0f, 1.0f),
            timestampMs = System.currentTimeMillis()
        )
        evaluateThermalState()
    }

    private fun updateBatteryMetrics(tempCelsius: Float, pct: Int, isCharging: Boolean) {
        _snapshot.value = _snapshot.value.copy(
            batteryTempCelsius = tempCelsius,
            batteryPct = pct,
            isCharging = isCharging,
            timestampMs = System.currentTimeMillis()
        )
        evaluateThermalState()
    }

    private fun onOsThermalStatusChanged(statusCode: Int) {
        val statusString = when (statusCode) {
            PowerManager.THERMAL_STATUS_NONE -> "NONE"
            PowerManager.THERMAL_STATUS_LIGHT -> "LIGHT"
            PowerManager.THERMAL_STATUS_MODERATE -> "MODERATE"
            PowerManager.THERMAL_STATUS_SEVERE -> "SEVERE"
            PowerManager.THERMAL_STATUS_CRITICAL -> "CRITICAL"
            PowerManager.THERMAL_STATUS_EMERGENCY -> "EMERGENCY"
            PowerManager.THERMAL_STATUS_SHUTDOWN -> "SHUTDOWN"
            else -> "UNKNOWN ($statusCode)"
        }

        _snapshot.value = _snapshot.value.copy(
            osThermalStatusCode = statusCode,
            osThermalStatusString = statusString,
            timestampMs = System.currentTimeMillis()
        )
        evaluateThermalState()
    }

    private fun evaluateThermalState() {
        val snap = _snapshot.value
        val temp = snap.batteryTempCelsius
        val headroom = snap.thermalHeadroom
        val osStatus = snap.osThermalStatusCode

        val newState = when {
            // Hardware throttle trip
            osStatus >= PowerManager.THERMAL_STATUS_SEVERE || headroom < 0.20f || temp >= 45.0f -> {
                ThermalState.THROTTLING_RISK
            }
            // If we were previously at throttling risk, transition to RECOVERING first
            previousState == ThermalState.THROTTLING_RISK && (headroom < 0.60f || temp >= 41.0f) -> {
                ThermalState.RECOVERING
            }
            // Hot condition
            osStatus == PowerManager.THERMAL_STATUS_MODERATE || headroom < 0.50f || temp >= 41.5f -> {
                ThermalState.HOT
            }
            // Warm condition
            osStatus == PowerManager.THERMAL_STATUS_LIGHT || headroom < 0.75f || temp >= 38.5f -> {
                ThermalState.WARM
            }
            // Cool condition
            else -> {
                ThermalState.COOL
            }
        }

        previousState = newState
        _snapshot.value = snap.copy(thermalState = newState)
    }

    /**
     * Crowded Match Readiness Check (specifically for PUBG 8v8 WOW / Final Circles)
     */
    fun evaluateMatchReadiness(): MatchReadinessReport {
        pollHeadroom()
        val snap = _snapshot.value
        var score = 100
        val recs = mutableListOf<String>()

        // Battery temp evaluation
        when {
            snap.batteryTempCelsius < 38.0f -> {
                recs.add("Battery temperature (${snap.batteryTempCelsius}°C) is optimal for high burst rendering.")
            }
            snap.batteryTempCelsius < 41.5f -> {
                score -= 15
                recs.add("Battery temperature (${snap.batteryTempCelsius}°C) is moderately warm. Remove thick phone cases.")
            }
            snap.batteryTempCelsius < 44.0f -> {
                score -= 35
                recs.add("Battery temperature (${snap.batteryTempCelsius}°C) approaches throttling threshold. Attach external semiconductor cooler.")
            }
            else -> {
                score -= 55
                recs.add("High thermal hazard (${snap.batteryTempCelsius}°C). Device will aggressively downclock CPU big-cores during 8v8 smoke grenades.")
            }
        }

        // Charging evaluation
        if (snap.isCharging) {
            score -= 15
            recs.add("Charging while gaming adds 3-5W heat dissipation. Use bypass/direct power charging if supported by OriginOS/device.")
        }

        // Thermal Headroom evaluation
        if (snap.thermalHeadroom < 0.40f) {
            score -= 20
            recs.add("Thermal headroom is low (${(snap.thermalHeadroom * 100).toInt()}%). Target sustained 90 FPS instead of volatile 120 FPS.")
        }

        // Battery Level evaluation
        if (snap.batteryPct < 25) {
            score -= 15
            recs.add("Low battery (${snap.batteryPct}%) may trigger system power saver downclocking.")
        }

        val clampedScore = score.coerceIn(10, 100)
        val verdict = when {
            clampedScore >= 80 -> MatchReadinessVerdict.OPTIMAL
            clampedScore >= 55 -> MatchReadinessVerdict.CAUTION
            else -> MatchReadinessVerdict.HIGH_RISK
        }

        val sustainableFps = when (verdict) {
            MatchReadinessVerdict.OPTIMAL -> if (snap.isExternalCoolerActive) 120 else 90
            MatchReadinessVerdict.CAUTION -> 90
            MatchReadinessVerdict.HIGH_RISK -> 60
        }

        return MatchReadinessReport(
            score = clampedScore,
            verdict = verdict,
            batteryTempCelsius = snap.batteryTempCelsius,
            thermalHeadroom = snap.thermalHeadroom,
            batteryLevelPct = snap.batteryPct,
            recommendations = recs,
            targetSustainableFps = sustainableFps
        )
    }
}
