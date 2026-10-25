package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BenchmarkEntity
import com.example.data.local.entity.ConfigFileBackupEntity
import com.example.data.local.entity.ProfileEntity
import com.example.data.local.entity.SettingsBackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM game_profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM game_profiles WHERE gamePackage = :pkg ORDER BY updatedAt DESC")
    fun getProfilesForGame(pkg: String): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM game_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Query("SELECT * FROM game_profiles WHERE gamePackage = :pkg AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultProfileForGame(pkg: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Query("DELETE FROM game_profiles WHERE id = :id")
    suspend fun deleteProfile(id: String)

    @Query("DELETE FROM game_profiles WHERE gamePackage = :pkg")
    suspend fun deleteAllForGame(pkg: String)
}

@Dao
interface BenchmarkDao {
    @Query("SELECT * FROM benchmark_records WHERE gamePackage = :pkg ORDER BY recordedAt DESC")
    fun getBenchmarksForGame(pkg: String): Flow<List<BenchmarkEntity>>

    @Insert
    suspend fun insertBenchmark(benchmark: BenchmarkEntity): Long

    @Query("DELETE FROM benchmark_records WHERE gamePackage = :pkg")
    suspend fun clearBenchmarks(pkg: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert
    suspend fun insertLog(log: AuditLogEntity): Long

    @Update
    suspend fun updateLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clearLogs()
}

@Dao
interface SettingsBackupDao {
    @Query("SELECT * FROM settings_backup WHERE isRestored = 0")
    suspend fun getPendingRestorations(): List<SettingsBackupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBackup(backup: SettingsBackupEntity)

    @Query("UPDATE settings_backup SET isRestored = 1 WHERE settingKey = :key")
    suspend fun markRestored(key: String)

    @Query("DELETE FROM settings_backup")
    suspend fun clearBackups()
}

@Dao
interface ConfigFileBackupDao {
    @Query("SELECT * FROM config_file_backups WHERE gamePackage = :pkg ORDER BY timestamp DESC")
    fun getBackupsForGame(pkg: String): Flow<List<ConfigFileBackupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBackup(backup: ConfigFileBackupEntity)

    @Query("DELETE FROM config_file_backups WHERE id = :id")
    suspend fun deleteBackup(id: String)
}
