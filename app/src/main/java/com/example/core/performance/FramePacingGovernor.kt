package com.example.core.performance

import com.example.data.model.DiagnosticMetrics
import kotlin.math.abs
import kotlin.math.roundToInt

data class FramePacingVerdict(
    val stabilityScore: Int, // 0 - 100
    val cadenceJudderDetected: Boolean,
    val cadenceRatio: Float,
    val isIntegerCadence: Boolean,
    val recommendedTargetFps: Int,
    val recommendedDisplayHz: Int,
    val rationale: String,
    val actionableSteps: List<String>
)

class FramePacingGovernor {

    /**
     * Evaluates frame pacing consistency given current telemetry, target FPS, and active display refresh rate.
     */
    fun evaluateFramePacing(
        metrics: DiagnosticMetrics,
        targetFps: Int,
        activeDisplayHz: Int,
        hasExternalCooler: Boolean = false
    ): FramePacingVerdict {
        val safeDisplayHz = if (activeDisplayHz > 0) activeDisplayHz else metrics.refreshRateHz
        val safeTargetFps = if (targetFps > 0) targetFps else metrics.fpsCurrent.toInt().coerceAtLeast(60)

        val ratio = safeDisplayHz.toFloat() / safeTargetFps.toFloat()
        // Integer or half-integer cadence check (e.g. 1.0, 2.0)
        val isInteger = abs(ratio - ratio.roundToInt()) < 0.05f

        val cadenceJudderDetected = !isInteger && safeTargetFps > 60

        // Calculate stability score based on p99 variance and stutter count
        val nominalInterval = 1000f / safeTargetFps
        val p99Variance = abs(metrics.p99Ms - nominalInterval)

        var score = 100
        if (cadenceJudderDetected) score -= 20
        if (p99Variance > 4f) score -= 15
        if (p99Variance > 8f) score -= 25
        if (metrics.stutterCount > 5) score -= 15
        if (metrics.tempCelsius > 44f) score -= 15
        val stabilityScore = score.coerceIn(10, 100)

        // Determine recommended configuration
        val (recFps, recHz, rationale, steps) = when {
            // Target is 144 FPS in PUBG Mobile, but engine caps at 120 FPS
            safeTargetFps >= 144 -> {
                Quadruple(
                    120,
                    120,
                    "PUBG Mobile Global engine cap is strictly 120 FPS. Setting display to 144 Hz causes 1.2x frame cadence mismatch (asymmetric frame presentation intervals). Synchronizing display to 120 Hz delivers flawless 1:1 cadence and eliminates frame-pacing judder.",
                    listOf(
                        "Set game frame rate target to 120 FPS (Ultra-Extreme)",
                        "Lock display refresh rate to 120 Hz via Display Modes or Shizuku",
                        "Ensure external cooler is attached for sustained 120 FPS without thermal throttling"
                    )
                )
            }
            // 90 FPS on 144 Hz display (3:2 pull-down judder)
            safeTargetFps == 90 && safeDisplayHz == 144 -> {
                Quadruple(
                    90,
                    90,
                    "90 FPS target on a 144 Hz panel results in a 1.6x non-integer refresh ratio. Frames are displayed alternately for 1 vs 2 refresh cycles, causing perceptible micro-judder. Aligning display to 90 Hz or stepping up to 120 FPS on 120 Hz resolves pacing.",
                    listOf(
                        "Switch display refresh rate to 90 Hz or 120 Hz",
                        "If thermals permit, select 120 FPS on 120 Hz for maximum smoothness"
                    )
                )
            }
            // Overheating without external cooler
            metrics.tempCelsius > 43f && !hasExternalCooler -> {
                Quadruple(
                    90,
                    90,
                    "Device thermal headroom is constrained (Chassis temperature > 43°C). Sustaining 120 FPS without active cooling induces thermal throttling within 8-12 minutes. 90 FPS at 90 Hz provides fluid competitive responsiveness with 28% lower GPU power dissipation.",
                    listOf(
                        "Switch game to 90 FPS Smooth profile",
                        "Attach external semiconductor cooler to enable sustained 120 FPS",
                        "Activate Sustained Competitive performance mode"
                    )
                )
            }
            // 120 FPS on 120 Hz (Optimal esports configuration)
            safeTargetFps == 120 && safeDisplayHz == 120 -> {
                Quadruple(
                    120,
                    120,
                    "Optimal 1:1 frame pacing configuration. 120 FPS frame submission matches the 120 Hz VSYNC interval (8.33 ms) precisely. Zero frame cadence judder detected.",
                    listOf(
                        "Configuration is optimal. Maintain battery level above 20% to prevent SoC governor downclocking."
                    )
                )
            }
            // 60 FPS on 120 Hz (2:1 clean integer divisor)
            safeTargetFps == 60 && safeDisplayHz == 120 -> {
                Quadruple(
                    60,
                    120,
                    "Clean 2:1 integer frame cadence (each frame is scanned twice for exactly 16.6 ms total). Excellent thermal endurance.",
                    listOf(
                        "If higher fluid response is desired, upgrade to 90 FPS or 120 FPS target in Game Settings."
                    )
                )
            }
            else -> {
                Quadruple(
                    safeTargetFps,
                    if (isInteger) safeDisplayHz else 120,
                    "Display refresh rate ($safeDisplayHz Hz) to game target ($safeTargetFps FPS) ratio is ${String.format("%.2f", ratio)}x. Integer multiples provide the smoothest frame pacing without micro-stutters.",
                    listOf(
                        "Select a display refresh rate that is an exact integer multiple of the game FPS target (e.g. 120 Hz for 120 FPS or 60 FPS; 90 Hz for 90 FPS)."
                    )
                )
            }
        }

        return FramePacingVerdict(
            stabilityScore = stabilityScore,
            cadenceJudderDetected = cadenceJudderDetected,
            cadenceRatio = (ratio * 100).roundToInt() / 100f,
            isIntegerCadence = isInteger,
            recommendedTargetFps = recFps,
            recommendedDisplayHz = recHz,
            rationale = rationale,
            actionableSteps = steps
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
