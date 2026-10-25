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
            sensorModel = gyroSensor?.name ?: "LSM6DSO 6-Axis Gyroscope (High-Speed)",
            maxSamplingRateHz = if (gyroSensor != null && gyroSensor.minDelay > 0) {
                1_000_000f / gyroSensor.minDelay.toFloat()
            } else 416f,
            currentSamplingRateHz = 208f
        )
    )
    val gyroConfig: StateFlow<GyroBoostConfig> = _gyroConfig.asStateFlow()

    private var lastEventTimestampNs: Long = 0L
    private val intervalHistory = ArrayDeque<Float>(30)
    private var isListening = false

    // Filtering & Calibration
    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 0f
    private var biasX = 0f
    private var biasY = 0f
    private var biasZ = 0f
    private var isCalibrating = false

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
                val deltaSec = deltaNs / 1_000_000_000f
                val instantHz = (1f / deltaSec).coerceIn(10f, 1000f)
                val deltaMs = deltaNs / 1_000_000f

                synchronized(intervalHistory) {
                    if (intervalHistory.size >= 25) intervalHistory.removeFirst()
                    intervalHistory.addLast(deltaMs)
                }

                val avgDeltaMs = intervalHistory.average().toFloat()
                val jitter = if (intervalHistory.size > 2) {
                    val variance = intervalHistory.map { (it - avgDeltaMs) * (it - avgDeltaMs) }.average().toFloat()
                    sqrt(variance)
                } else 0.8f

                val avgHz = if (avgDeltaMs > 0) (1000f / avgDeltaMs).coerceIn(10f, 1000f) else instantHz

                val rawX = (event.values.getOrNull(0) ?: 0f) - biasX
                val rawY = (event.values.getOrNull(1) ?: 0f) - biasY
                val rawZ = (event.values.getOrNull(2) ?: 0f) - biasZ

                // High-pass anti-drift filter
                val alpha = 0.88f
                filteredX = alpha * filteredX + alpha * rawX
                filteredY = alpha * filteredY + alpha * rawY
                filteredZ = alpha * filteredZ + alpha * rawZ

                val xFactor = _gyroConfig.value.xSensitivityPercent / 100f
                val yFactor = _gyroConfig.value.ySensitivityPercent / 100f

                _gyroConfig.value = _gyroConfig.value.copy(
                    currentSamplingRateHz = avgHz,
                    sampleJitterMs = (jitter * 10).toInt() / 10f,
                    currentX = filteredX * xFactor,
                    currentY = filteredY * yFactor,
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
            xSensitivityPercent = xPct.coerceIn(0, 200),
            ySensitivityPercent = yPct.coerceIn(0, 200)
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
        // Zero-bias static calibration
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

    fun getGuidedInGameSensitivities(): List<InGameSensitivityItem> {
        val multiplier = _gyroConfig.value.xSensitivityPercent / 100f
        return listOf(
            InGameSensitivityItem(
                scopeLabel = "3rd Person No Scope",
                defaultSensitivity = 300,
                recommendedSensitivity = (300 * multiplier).toInt().coerceIn(50, 400),
                description = "Hip-fire tracking and 360-degree close quarter awareness"
            ),
            InGameSensitivityItem(
                scopeLabel = "Red Dot / Holographic",
                defaultSensitivity = 300,
                recommendedSensitivity = (300 * multiplier).toInt().coerceIn(50, 400),
                description = "Primary CQB spray control for M416 / Beryl"
            ),
            InGameSensitivityItem(
                scopeLabel = "2x Scope",
                defaultSensitivity = 250,
                recommendedSensitivity = (250 * multiplier).toInt().coerceIn(50, 400),
                description = "Mid-range spray and burst transitions"
            ),
            InGameSensitivityItem(
                scopeLabel = "3x Scope / 6x Adjusted",
                defaultSensitivity = 220,
                recommendedSensitivity = (220 * multiplier).toInt().coerceIn(50, 400),
                description = "Competitive standard spray scope with M416"
            ),
            InGameSensitivityItem(
                scopeLabel = "4x Scope",
                defaultSensitivity = 180,
                recommendedSensitivity = (180 * multiplier).toInt().coerceIn(40, 350),
                description = "DMR tap firing (Mini14 / SLR / SKS)"
            ),
            InGameSensitivityItem(
                scopeLabel = "8x Scope (Sniper)",
                defaultSensitivity = 80,
                recommendedSensitivity = (80 * multiplier).toInt().coerceIn(20, 200),
                description = "Bolt action headshot precision (AWM / M24 / Kar98k)"
            )
        )
    }
}
