package com.example

import android.app.Application
import com.example.core.config.GameConfigInspectorEngine
import com.example.core.device.DeviceDiagnosticsProvider
import com.example.core.device.DisplayRefreshManager
import com.example.core.performance.BottleneckDetector
import com.example.core.performance.FramePacingTracker
import com.example.core.performance.PerformanceController
import com.example.core.profile.ProfileRepository
import com.example.core.sensors.GyroBoostEngine
import com.example.core.shizuku.ShizukuIntegrationManager
import com.example.data.local.AppDatabase
import com.example.data.preferences.UserPreferencesRepository

class FrameForgeApplication : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var preferencesRepository: UserPreferencesRepository
        private set
    lateinit var profileRepository: ProfileRepository
        private set
    lateinit var gameConfigInspectorEngine: GameConfigInspectorEngine
        private set
    lateinit var diagnosticsProvider: DeviceDiagnosticsProvider
        private set
    lateinit var displayRefreshManager: DisplayRefreshManager
        private set
    lateinit var gyroBoostEngine: GyroBoostEngine
        private set
    lateinit var framePacingTracker: FramePacingTracker
        private set
    lateinit var bottleneckDetector: BottleneckDetector
        private set
    lateinit var performanceController: PerformanceController
        private set
    lateinit var shizukuManager: ShizukuIntegrationManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        preferencesRepository = UserPreferencesRepository(this)
        profileRepository = ProfileRepository(database.profileDao())
        gameConfigInspectorEngine = GameConfigInspectorEngine(database.configFileBackupDao())
        diagnosticsProvider = DeviceDiagnosticsProvider(this)
        displayRefreshManager = DisplayRefreshManager(this)
        gyroBoostEngine = GyroBoostEngine(this)
        framePacingTracker = FramePacingTracker()
        bottleneckDetector = BottleneckDetector(this)
        performanceController = PerformanceController(this, database.settingsBackupDao())
        shizukuManager = ShizukuIntegrationManager(this, database.auditLogDao())
    }
}
