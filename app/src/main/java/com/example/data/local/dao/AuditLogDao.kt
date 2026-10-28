package com.example.data.local.dao

import com.example.data.local.entity.AuditLogEntity
import kotlinx.coroutines.flow.Flow

interface AuditLogDao {
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>
    suspend fun insertLog(log: AuditLogEntity)
    suspend fun insertAuditLog(log: AuditLogEntity) {
        insertLog(log)
    }
    suspend fun updateLog(log: AuditLogEntity)
    suspend fun clearLogs()
}
