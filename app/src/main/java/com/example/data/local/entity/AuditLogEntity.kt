package com.example.data.local.entity

data class AuditLogEntity(
    val id: Long = System.currentTimeMillis(),
    val command: String,
    val commandTitle: String,
    val executionResult: String,
    val exitCode: Int,
    val isRolledBack: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
