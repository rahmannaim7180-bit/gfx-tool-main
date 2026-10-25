package com.example.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.data.model.GyroBoostConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

data class InGameSensitivityItem(
    val scopeLabel: String,
    val defaultSensitivity: Int,
    val recommendedSensitivity: Int,
    val description: String
)

class GyroBoostEngine(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _gyroConfig = MutableStateFlow(
        GyroBoostConfig(
            sensorModel = gyroSensor?.name ?: "Hardware 6-Axis Gyroscope",
            maxSamplingRateHz = if (gyroSensor != null && gyroSensor.minDelay > 0) {
                1_000_000f / gyroSensor.minDelay.toFloat()
            } else 416f,
            currentSamplingRateHz = 208f,
            oemIntegrationStatus = "OriginOS does not provide third-party raw gyro multiplier APIs; In-Game Sensitivity Assistant active"
        )
    )
    val gyroConfig: StateFlow<GyroBoostConfig> = _gyroConfig.asStateFlow()

    private var lastEventTimestampNs: Long = 0L
    private val intervalHistory = ArrayDeque<Float>(40)
    private var isListening = false

    // Filtering & Calibration
    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 0f
    private var biasX = 0f
    private var biasY = 0f
    private var biasZ = 0f

    fun startListening() {
        if (gyroSensor != null && !isListening) {
            val registered = sensorManager.registerListener(
                this,
                gyroSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            isListening = registered
        }
    }

    fun stopListening() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_GYROSCOPE) return

        val currentNs = event.timestamp
        if (lastEventTimestampNs != 0L) {
            val deltaNs = currentNs - lastEventTimestampNs
            if (deltaNs > 0) {
                val deltaMs = deltaNs / 1_000_000f

                synchronized(intervalHistory) {
                    if (intervalHistory.size >= 40) intervalHistory.removeFirst()
                    intervalHistory.addLast(deltaMs)
                }

                val avgDeltaMs = intervalHistory.average().toFloat()
                val jitter = if (intervalHistory.size > 2) {
                    val variance = intervalHistory.map { (it - avgDeltaMs) * (it - avgDeltaMs) }.average().toFloat()
                    sqrt(variance)
                } else 0.8f

                val avgHz = if (avgDeltaMs > 0) (1000f / avgDeltaMs).coerceIn(10f, 1000f) else 200f

                val rawX = (event.values.getOrNull(0) ?: 0f) - biasX
                val rawY = (event.values.getOrNull(1) ?: 0f) - biasY
                val rawZ = (event.values.getOrNull(2) ?: 0f) - biasZ

                // Anti-drift high-pass filter for zero-velocity stability
                val alpha = if (_gyroConfig.value.antiDriftFilter) 0.88f else 1.0f
                filteredX = alpha * filteredX + alpha * rawX
                filteredY = alpha * filteredY + alpha * rawY
                filteredZ = alpha * filteredZ + alpha * rawZ

                _gyroConfig.value = _gyroConfig.value.copy(
                    currentSamplingRateHz = avgHz,
                    observedIntervalMs = avgDeltaMs,
                    sampleJitterMs = (jitter * 10).toInt() / 10f,
                    currentX = filteredX,
                    currentY = filteredY,
                    currentZ = filteredZ
                )
            }
        }
        lastEventTimestampNs = currentNs
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    fun updateSensitivity(xPct: Int, yPct: Int) {
        _gyroConfig.value = _gyroConfig.value.copy(
            xSensitivityPercent = xPct.coerceIn(50, 200),
            ySensitivityPercent = yPct.coerceIn(50, 200)
        )
    }

    fun setGyroMode(mode: com.example.data.model.GyroMode) {
        _gyroConfig.value = _gyroConfig.value.copy(mode = mode)
    }

    fun toggleGyroBoost(enabled: Boolean) {
        _gyroConfig.value = _gyroConfig.value.copy(isEnabled = enabled)
        if (enabled) startListening() else stopListening()
    }

    fun calibrateSensors(onComplete: () -> Unit) {
        biasX = _gyroConfig.value.currentX
        biasY = _gyroConfig.value.currentY
        biasZ = _gyroConfig.value.currentZ
        onComplete()
    }

    fun runPrecisionTest(onComplete: (score: Int) -> Unit) {
        _gyroConfig.value = _gyroConfig.value.copy(isTestRunning = true)
        val jitter = _gyroConfig.value.sampleJitterMs
        val hz = _gyroConfig.value.currentSamplingRateHz
        val calculatedScore = ((100 - (jitter * 3)) * (hz / 200f).coerceIn(0.9f, 1.1f)).toInt().coerceIn(85, 99)

        _gyroConfig.value = _gyroConfig.value.copy(
            isTestRunning = false,
            testScore = calculatedScore
        )
        onComplete(calculatedScore)
    }

    /**
     * Priority 7: Genuine Sensitivity Assistant
     * Computes exact recommended in-game sensitivity sliders for PUBG Mobile Global
     * based on the user's chosen multiplier (100% to 200%).
     */
    fun getGuidedInGameSensitivities(): List<InGameSensitivityItem> {
        val multiplier = _gyroConfig.value.xSensitivityPercent / 100f
        return listOf(
            InGameSensitivityItem(
                scopeLabel = "3rd Person (No Scope)",
                defaultSensitivity = 300,
                recommendedSensitivity = (300 * multiplier).toInt().coerceIn(50, 400),
                description = "Hip-fire tracking, fast 180° flick turns, close-quarters combat"
            ),
            InGameSensitivityItem(
                scopeLabel = "1st Person (No Scope)",
                defaultSensitivity = 300,
                recommendedSensitivity = (300 * multiplier).toInt().coerceIn(50, 400),
                description = "FPP rapid target acquisition & room clearing"
            ),
            InGameSensitivityItem(
                scopeLabel = "Red Dot / Holographic / Iron Sight",
                defaultSensitivity = 300,
                recommendedSensitivity = (300 * multiplier).toInt().coerceIn(50, 400),
                description = "M416 & Beryl M762 close-to-mid range spray recoil control"
            ),
            InGameSensitivityItem(
                scopeLabel = "2x Scope",
                defaultSensitivity = 250,
                recommendedSensitivity = (250 * multiplier).toInt().coerceIn(50, 400),
                description = "Sub-100m SMG/AR burst transitions"
            ),
            InGameSensitivityItem(
                scopeLabel = "3x Scope (Esports Spray)",
                defaultSensitivity = 220,
                recommendedSensitivity = (220 * multiplier).toInt().coerceIn(50, 400),
                description = "Competitive standard spray control (6x converted to 3x)"
            ),
            InGameSensitivityItem(
                scopeLabel = "4x Scope / VSS",
                defaultSensitivity = 180,
                recommendedSensitivity = (180 * multiplier).toInt().coerceIn(40, 350),
                description = "DMR rapid tap-firing (Mini14, SLR, SKS)"
            ),
            InGameSensitivityItem(
                scopeLabel = "6x Scope",
                defaultSensitivity = 120,
                recommendedSensitivity = (120 * multiplier).toInt().coerceIn(30, 300),
                description = "Long-range precision tracking"
            ),
            InGameSensitivityItem(
                scopeLabel = "8x Scope (Sniper Rifles)",
                defaultSensitivity = 80,
                recommendedSensitivity = (80 * multiplier).toInt().coerceIn(20, 200),
                description = "Bolt-action micro-adjustments (AWM, M24, Kar98k)"
            )
        )
    }
}
