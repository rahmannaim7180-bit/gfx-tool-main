package com.example.data.local.entity

data class ConfigFileBackupEntity(
    val id: String,
    val gamePackage: String,
    val fileName: String,
    val originalContent: String,
    val modifiedContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRestored: Boolean = false
)
