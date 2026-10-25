package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.dao.BenchmarkDao
import com.example.data.local.dao.ConfigFileBackupDao
import com.example.data.local.dao.ProfileDao
import com.example.data.local.dao.SettingsBackupDao
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BenchmarkEntity
import com.example.data.local.entity.ConfigFileBackupEntity
import com.example.data.local.entity.ProfileEntity
import com.example.data.local.entity.SettingsBackupEntity

@Database(
    entities = [
        ProfileEntity::class,
        BenchmarkEntity::class,
        AuditLogEntity::class,
        SettingsBackupEntity::class,
        ConfigFileBackupEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun benchmarkDao(): BenchmarkDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun settingsBackupDao(): SettingsBackupDao
    abstract fun configFileBackupDao(): ConfigFileBackupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "frameforge_gfx.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
