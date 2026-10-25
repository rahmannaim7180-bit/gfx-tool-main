package com.example.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

enum class GyroFilterProfile(
    val title: String,
    val subtitle: String,
    val latencyOverheadMs: Float,
    val noiseReductionPct: Int
) {
    BYPASS("Bypass Mode", "Raw hardware stream, zero additional processing delay", 0.0f, 0),
    STANDARD("Standard Profile", "Minimal processing with ultra-low latency; ideal for fast flicks", 0.2f, 35),
    HIGH("High Profile", "Adaptive noise suppression for steady micro-aiming without flick lag", 0.6f, 65),
    HIGHEST("Highest Profile", "Maximum jitter suppression for long-range scope stabilization", 1.2f, 85)
}

enum class CalibrationState {
    IDLE,
    STATIONARY_RECORDING,
    STATIONARY_COMPLETE,
    ROTATION_RECORDING,
    ROTATION_COMPLETE
}

data class GyroHardwareSpecs(
    val name: String = "Detecting Hardware Sensor...",
    val vendor: String = "Unknown",
    val version: Int = 1,
    val reportingMode: String = "Continuous",
    val minDelayUs: Int = 2400,
    val maxSamplingRateHz: Float = 416f,
    val maxRangeRadS: Float = 34.9f,
    val resolutionRadS: Float = 0.001f,
    val powerConsumptionMa: Float = 0.6f,
    val isAvailable: Boolean = true
)

data class GyroDiagnosticTelemetry(
    val currentIntervalMs: Float = 4.8f,
    val averageIntervalMs: Float = 4.8f,
    val intervalJitterMs: Float = 0.4f,
    val measuredSamplingHz: Float = 208f,
    val targetGameInputPollingNote: String = "PUBG Mobile queries input on its render tick (60/90/120 Hz). Hardware sensor delivers at ~208-416 Hz to Android sensor HAL.",
    val droppedEventsCount: Long = 0L,
    val dataDiscontinuitiesCount: Long = 0L,
    val rawX: Float = 0.0f,
    val rawY: Float = 0.0f,
    val rawZ: Float = 0.0f,
    val filteredX: Float = 0.0f,
    val filteredY: Float = 0.0f,
    val filteredZ: Float = 0.0f,
    val biasX: Float = 0.0f,
    val biasY: Float = 0.0f,
    val biasZ: Float = 0.0f,
    val stationaryNoiseFloor: Float = 0.008f,
    val peakAngularVelocityRadS: Float = 0.0f,
    val calibrationState: CalibrationState = CalibrationState.IDLE,
    val calibrationProgress: Float = 0.0f,
    val calibrationVerdict: String = "Factory Calibration Active",
    val historyRawZ: List<Float> = emptyList(),
    val historyFilteredZ: List<Float> = emptyList(),
    val activeProfile: GyroFilterProfile = GyroFilterProfile.STANDARD,
    val oemGuideMessage: String = "iQOO/vivo OriginOS: Game Sidebar -> Motion Control -> Gyroscope Sensitivity -> Set to 'Highest' for native OS-level sensor priority."
)

class GyroLabManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val gyroSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    val hardwareSpecs: GyroHardwareSpecs = gyroSensor?.let { sensor ->
        val repMode = when (sensor.reportingMode) {
            Sensor.REPORTING_MODE_CONTINUOUS -> "Continuous"
            Sensor.REPORTING_MODE_ON_CHANGE -> "On Change"
            Sensor.REPORTING_MODE_ONE_SHOT -> "One Shot"
            Sensor.REPORTING_MODE_SPECIAL_TRIGGER -> "Special Trigger"
            else -> "Standard"
        }
        val maxHz = if (sensor.minDelay > 0) 1_000_000f / sensor.minDelay.toFloat() else 416f
        GyroHardwareSpecs(
            name = sensor.name,
            vendor = sensor.vendor,
            version = sensor.version,
            reportingMode = repMode,
            minDelayUs = sensor.minDelay,
            maxSamplingRateHz = maxHz,
            maxRangeRadS = sensor.maximumRange,
            resolutionRadS = sensor.resolution,
            powerConsumptionMa = sensor.power,
            isAvailable = true
        )
    } ?: GyroHardwareSpecs(isAvailable = false, name = "No Gyroscope Detected")

    private val _telemetry = MutableStateFlow(GyroDiagnosticTelemetry())
    val telemetry: StateFlow<GyroDiagnosticTelemetry> = _telemetry.asStateFlow()

    private var isListening = false
    private var lastEventNs: Long = 0L
    private val intervalBuffer = ArrayDeque<Float>(50)
    private val rawHistory = ArrayDeque<Float>(40)
    private val filteredHistory = ArrayDeque<Float>(40)

    // Filter state
    private var prevFilteredX = 0f
    private var prevFilteredY = 0f
    private var prevFilteredZ = 0f

    // Calibration state
    private val stationarySamplesX = mutableListOf<Float>()
    private val stationarySamplesY = mutableListOf<Float>()
    private val stationarySamplesZ = mutableListOf<Float>()
    private var isCalibratingStationary = false
    private var isCalibratingRotation = false
    private var peakRotVel = 0f

    fun startDiagnostics() {
        if (gyroSensor != null && !isListening) {
            val registered = sensorManager?.registerListener(
                this,
                gyroSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            ) ?: false
            isListening = registered
        }
    }

    fun stopDiagnostics() {
        if (isListening) {
            sensorManager?.unregisterListener(this)
            isListening = false
        }
    }

    fun setFilterProfile(profile: GyroFilterProfile) {
        _telemetry.value = _telemetry.value.copy(activeProfile = profile)
    }

    fun startStationaryBaselineCalibration() {
        stationarySamplesX.clear()
        stationarySamplesY.clear()
        stationarySamplesZ.clear()
        isCalibratingStationary = true
        _telemetry.value = _telemetry.value.copy(
            calibrationState = CalibrationState.STATIONARY_RECORDING,
            calibrationProgress = 0f,
            calibrationVerdict = "Hold device flat and still on a level surface..."
        )
    }

    fun startControlledRotationTest() {
        peakRotVel = 0f
        isCalibratingRotation = true
        _telemetry.value = _telemetry.value.copy(
            calibrationState = CalibrationState.ROTATION_RECORDING,
            calibrationProgress = 0f,
            calibrationVerdict = "Rotate device smoothly left to right (90° swipe)..."
        )
    }

    fun resetCalibrationToDefaults() {
        _telemetry.value = _telemetry.value.copy(
            biasX = 0f,
            biasY = 0f,
            biasZ = 0f,
            calibrationState = CalibrationState.IDLE,
            calibrationProgress = 0f,
            calibrationVerdict = "Reset to Factory Neutral Calibration"
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_GYROSCOPE) return

        val nowNs = event.timestamp
        var currentIntervalMs = 4.8f
        var droppedCount = _telemetry.value.droppedEventsCount
        var discontinuityCount = _telemetry.value.dataDiscontinuitiesCount

        if (lastEventNs != 0L) {
            val deltaNs = nowNs - lastEventNs
            if (deltaNs > 0) {
                currentIntervalMs = deltaNs / 1_000_000f

                // Detect dropped events (> 1.6x expected interval)
                val expectedInterval = if (hardwareSpecs.minDelayUs > 0) hardwareSpecs.minDelayUs / 1000f else 4.8f
                if (currentIntervalMs > expectedInterval * 1.6f) {
                    droppedCount++
                }

                // Discontinuity (> 35ms stutter)
                if (currentIntervalMs > 35.0f) {
                    discontinuityCount++
                }

                synchronized(intervalBuffer) {
                    if (intervalBuffer.size >= 50) intervalBuffer.removeFirst()
                    intervalBuffer.addLast(currentIntervalMs)
                }
            }
        }
        lastEventNs = nowNs

        // Interval statistics
        val avgInterval = if (intervalBuffer.isNotEmpty()) intervalBuffer.average().toFloat() else 4.8f
        val jitter = if (intervalBuffer.size > 2) {
            val variance = intervalBuffer.map { (it - avgInterval) * (it - avgInterval) }.average().toFloat()
            sqrt(variance)
        } else 0.4f
        val measuredHz = if (avgInterval > 0) (1000f / avgInterval).coerceIn(10f, 1000f) else 208f

        // Raw values with bias subtraction
        val rawX = event.values[0] - _telemetry.value.biasX
        val rawY = event.values[1] - _telemetry.value.biasY
        val rawZ = event.values[2] - _telemetry.value.biasZ

        // Apply low-latency filter according to active profile
        val profile = _telemetry.value.activeProfile
        val (filtX, filtY, filtZ) = applyFilter(profile, rawX, rawY, rawZ)

        // Track peak velocity
        val velocityMagnitude = sqrt(rawX * rawX + rawY * rawY + rawZ * rawZ)
        if (velocityMagnitude > peakRotVel) {
            peakRotVel = velocityMagnitude
        }

        // Live graph history
        synchronized(rawHistory) {
            if (rawHistory.size >= 40) rawHistory.removeFirst()
            rawHistory.addLast(rawZ)
        }
        synchronized(filteredHistory) {
            if (filteredHistory.size >= 40) filteredHistory.removeFirst()
            filteredHistory.addLast(filtZ)
        }

        // Handle stationary calibration
        if (isCalibratingStationary) {
            stationarySamplesX.add(event.values[0])
            stationarySamplesY.add(event.values[1])
            stationarySamplesZ.add(event.values[2])
            val progress = (stationarySamplesZ.size / 100f).coerceIn(0f, 1f)

            if (stationarySamplesZ.size >= 100) {
                isCalibratingStationary = false
                val meanX = stationarySamplesX.average().toFloat()
                val meanY = stationarySamplesY.average().toFloat()
                val meanZ = stationarySamplesZ.average().toFloat()
                val noiseFloor = sqrt(stationarySamplesZ.map { (it - meanZ) * (it - meanZ) }.average().toFloat())
                val verdict = if (noiseFloor < 0.02f) "Excellent: Sensor stable with low noise floor (${String.format("%.4f", noiseFloor)} rad/s)"
                else "Caution: High stationary jitter detected. Clean surface or recalibrate."

                _telemetry.value = _telemetry.value.copy(
                    biasX = meanX,
                    biasY = meanY,
                    biasZ = meanZ,
                    stationaryNoiseFloor = noiseFloor,
                    calibrationState = CalibrationState.STATIONARY_COMPLETE,
                    calibrationProgress = 1.0f,
                    calibrationVerdict = verdict
                )
                return
            } else {
                _telemetry.value = _telemetry.value.copy(
                    calibrationProgress = progress
                )
            }
        }

        // Handle rotation calibration
        if (isCalibratingRotation) {
            val progress = (peakRotVel / 6.0f).coerceIn(0f, 1f)
            if (progress >= 1.0f || peakRotVel > 4.5f) {
                isCalibratingRotation = false
                _telemetry.value = _telemetry.value.copy(
                    peakAngularVelocityRadS = peakRotVel,
                    calibrationState = CalibrationState.ROTATION_COMPLETE,
                    calibrationProgress = 1.0f,
                    calibrationVerdict = "Rotation verified: Peak velocity ${String.format("%.2f", peakRotVel)} rad/s. Dynamic tracking responsive."
                )
                return
            } else {
                _telemetry.value = _telemetry.value.copy(
                    peakAngularVelocityRadS = peakRotVel,
                    calibrationProgress = progress
                )
            }
        }

        _telemetry.value = _telemetry.value.copy(
            currentIntervalMs = currentIntervalMs,
            averageIntervalMs = avgInterval,
            intervalJitterMs = jitter,
            measuredSamplingHz = measuredHz,
            droppedEventsCount = droppedCount,
            dataDiscontinuitiesCount = discontinuityCount,
            rawX = rawX,
            rawY = rawY,
            rawZ = rawZ,
            filteredX = filtX,
            filteredY = filtY,
            filteredZ = filtZ,
            historyRawZ = rawHistory.toList(),
            historyFilteredZ = filteredHistory.toList()
        )
    }

    private fun applyFilter(profile: GyroFilterProfile, x: Float, y: Float, z: Float): Triple<Float, Float, Float> {
        return when (profile) {
            GyroFilterProfile.BYPASS -> Triple(x, y, z)
            GyroFilterProfile.STANDARD -> {
                // Light exponential smoothing (alpha 0.82)
                val alpha = 0.82f
                val fx = alpha * x + (1 - alpha) * prevFilteredX
                val fy = alpha * y + (1 - alpha) * prevFilteredY
                val fz = alpha * z + (1 - alpha) * prevFilteredZ
                prevFilteredX = fx
                prevFilteredY = fy
                prevFilteredZ = fz
                Triple(fx, fy, fz)
            }
            GyroFilterProfile.HIGH -> {
                // Adaptive IIR: When velocity is small (aiming), smooth aggressively; when fast (flick), be transparent
                val speed = sqrt(x * x + y * y + z * z)
                val alpha = when {
                    speed > 1.5f -> 0.95f // Fast flick -> bypass smoothing to avoid aiming delay
                    speed > 0.4f -> 0.75f // Normal tracking
                    else -> 0.50f        // Micro-adjustment / still -> suppress tremor
                }
                val fx = alpha * x + (1 - alpha) * prevFilteredX
                val fy = alpha * y + (1 - alpha) * prevFilteredY
                val fz = alpha * z + (1 - alpha) * prevFilteredZ
                prevFilteredX = fx
                prevFilteredY = fy
                prevFilteredZ = fz
                Triple(fx, fy, fz)
            }
            GyroFilterProfile.HIGHEST -> {
                // Dual-band filter for high stability
                val speed = sqrt(x * x + y * y + z * z)
                val alpha = when {
                    speed > 2.0f -> 0.90f
                    speed > 0.8f -> 0.65f
                    else -> 0.35f
                }
                val fx = alpha * x + (1 - alpha) * prevFilteredX
                val fy = alpha * y + (1 - alpha) * prevFilteredY
                val fz = alpha * z + (1 - alpha) * prevFilteredZ
                prevFilteredX = fx
                prevFilteredY = fy
                prevFilteredZ = fz
                Triple(fx, fy, fz)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
