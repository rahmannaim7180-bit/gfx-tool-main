package com.example.data.local.dao

import com.example.data.local.entity.SettingsBackupEntity

interface SettingsBackupDao {
    suspend fun saveBackup(backup: SettingsBackupEntity)
    suspend fun getPendingRestorations(): List<SettingsBackupEntity>
    suspend fun markRestored(settingKey: String)
    suspend fun clearBackups()
}
