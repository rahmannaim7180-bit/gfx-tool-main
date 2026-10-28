package com.example.data.local.entity

data class SettingsBackupEntity(
    val settingKey: String,
    val originalValue: String,
    val modifiedValue: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRestored: Boolean = false
)
