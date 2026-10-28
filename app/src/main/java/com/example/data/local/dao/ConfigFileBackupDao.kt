package com.example.data.local.dao

import com.example.data.local.entity.ConfigFileBackupEntity
import kotlinx.coroutines.flow.Flow

interface ConfigFileBackupDao {
    fun getBackupsForGame(gamePackage: String): Flow<List<ConfigFileBackupEntity>>
    suspend fun saveBackup(backup: ConfigFileBackupEntity)
    suspend fun deleteBackup(id: String)
}
