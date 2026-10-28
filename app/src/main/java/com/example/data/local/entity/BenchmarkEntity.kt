package com.example.data.local.entity

data class BenchmarkEntity(
    val id: Long = System.currentTimeMillis(),
    val gamePackage: String,
    val modeName: String,
    val avgFps: Float,
    val avgFrameTimeMs: Float,
    val stabilityP95Pct: Float,
    val stutterRatePct: Float,
    val batteryPerHourPct: Float,
    val tempCelsius: Float,
    val coolerUsed: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis()
)
