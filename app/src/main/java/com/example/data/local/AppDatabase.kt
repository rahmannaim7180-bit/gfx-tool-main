package com.example.data.local

import android.content.Context
import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class AppDatabase private constructor() {

    private val _profiles = MutableStateFlow<Map<String, ProfileEntity>>(emptyMap())
    private val _auditLogs = MutableStateFlow<List<AuditLogEntity>>(emptyList())
    private val _settingsBackups = MutableStateFlow<Map<String, SettingsBackupEntity>>(emptyMap())
    private val _configBackups = MutableStateFlow<Map<String, ConfigFileBackupEntity>>(emptyMap())
    private val _benchmarks = MutableStateFlow<List<BenchmarkEntity>>(emptyList())

    private val _profileDao = object : ProfileDao {
        override fun getAllProfiles(): Flow<List<ProfileEntity>> =
            _profiles.map { it.values.toList() }

        override fun getProfilesForGame(gamePackage: String): Flow<List<ProfileEntity>> =
            _profiles.map { it.values.filter { p -> p.gamePackage == gamePackage } }

        override suspend fun getProfileById(id: String): ProfileEntity? =
            _profiles.value[id]

        override suspend fun getDefaultProfileForGame(gamePackage: String): ProfileEntity? =
            _profiles.value.values.find { it.gamePackage == gamePackage && it.isDefault }

        override suspend fun saveProfile(profile: ProfileEntity) {
            _profiles.value = _profiles.value + (profile.id to profile)
        }

        override suspend fun updateProfile(profile: ProfileEntity) {
            saveProfile(profile)
        }

        override suspend fun deleteProfile(id: String) {
            _profiles.value = _profiles.value - id
        }

        override suspend fun deleteAllForGame(gamePackage: String) {
            _profiles.value = _profiles.value.filterValues { it.gamePackage != gamePackage }
        }
    }

    private val _auditLogDao = object : AuditLogDao {
        override fun getRecentAuditLogs(): Flow<List<AuditLogEntity>> = _auditLogs

        override suspend fun insertLog(log: AuditLogEntity) {
            _auditLogs.value = listOf(log) + _auditLogs.value
        }

        override suspend fun updateLog(log: AuditLogEntity) {
            _auditLogs.value = _auditLogs.value.map { if (it.id == log.id) log else it }
        }

        override suspend fun clearLogs() {
            _auditLogs.value = emptyList()
        }
    }

    private val _settingsBackupDao = object : SettingsBackupDao {
        override suspend fun saveBackup(backup: SettingsBackupEntity) {
            _settingsBackups.value = _settingsBackups.value + (backup.settingKey to backup)
        }

        override suspend fun getPendingRestorations(): List<SettingsBackupEntity> =
            _settingsBackups.value.values.filter { !it.isRestored }

        override suspend fun markRestored(settingKey: String) {
            val existing = _settingsBackups.value[settingKey] ?: return
            _settingsBackups.value = _settingsBackups.value + (settingKey to existing.copy(isRestored = true))
        }

        override suspend fun clearBackups() {
            _settingsBackups.value = emptyMap()
        }
    }

    private val _configFileBackupDao = object : ConfigFileBackupDao {
        override fun getBackupsForGame(gamePackage: String): Flow<List<ConfigFileBackupEntity>> =
            _configBackups.map { it.values.filter { b -> b.gamePackage == gamePackage } }

        override suspend fun saveBackup(backup: ConfigFileBackupEntity) {
            _configBackups.value = _configBackups.value + (backup.id to backup)
        }

        override suspend fun deleteBackup(id: String) {
            _configBackups.value = _configBackups.value - id
        }
    }

    private val _benchmarkDao = object : BenchmarkDao {
        override fun getBenchmarksForGame(gamePackage: String): Flow<List<BenchmarkEntity>> =
            _benchmarks.map { it.filter { b -> b.gamePackage == gamePackage } }

        override suspend fun insertBenchmark(benchmark: BenchmarkEntity) {
            _benchmarks.value = listOf(benchmark) + _benchmarks.value
        }

        override suspend fun clearBenchmarks(gamePackage: String) {
            _benchmarks.value = _benchmarks.value.filter { it.gamePackage != gamePackage }
        }
    }

    fun profileDao(): ProfileDao = _profileDao
    fun auditLogDao(): AuditLogDao = _auditLogDao
    fun settingsBackupDao(): SettingsBackupDao = _settingsBackupDao
    fun configFileBackupDao(): ConfigFileBackupDao = _configFileBackupDao
    fun benchmarkDao(): BenchmarkDao = _benchmarkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppDatabase().also { INSTANCE = it }
            }
        }
    }
}
