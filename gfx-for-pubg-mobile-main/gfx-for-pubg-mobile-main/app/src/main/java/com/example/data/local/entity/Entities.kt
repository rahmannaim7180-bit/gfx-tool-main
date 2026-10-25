package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,
    val gamePackage: String,
    val name: String,
    val profileType: String,
    val graphicsQuality: String,
    val frameRateTarget: String,
    val style: String,
    val resolution: String,
    val renderScalePercent: Int = 100,
    val shadows: String = "DISABLED",
    val antiAliasing: String = "DISABLED",
    val textureQuality: String = "LOW",
    val effectsQuality: String = "LOW",
    val audioQuality: String = "HIGH",
    val gyroBoostEnabled: Boolean = true,
    val gyroXSensitivity: Int = 120,
    val gyroYSensitivity: Int = 120,
    val performanceMode: String = "BALANCED",
    val verificationState: String = "SAVED_PREFERENCE",
    val lastAppliedResult: String = "Saved in persistent profile store",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "benchmark_records")
data class BenchmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val commandTitle: String,
    val command: String,
    val executionResult: String,
    val exitCode: Int,
    val isRolledBack: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "settings_backup")
data class SettingsBackupEntity(
    @PrimaryKey
    val settingKey: String,
    val originalValue: String,
    val modifiedValue: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRestored: Boolean = false
)

@Entity(tableName = "config_file_backups")
data class ConfigFileBackupEntity(
    @PrimaryKey
    val id: String,
    val fileName: String,
    val gamePackage: String,
    val originalContent: String,
    val modifiedContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRestored: Boolean = false
)
