package com.example

import com.example.core.shizuku.CommandTestCenterEngine
import com.example.data.local.entity.AuditLogEntity
import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class FrameForgeEngineTest {

    @Test
    fun testPubgMobileTargetCatalog() {
        val games = GameTarget.SUPPORTED_GAMES
        assertTrue(games.isNotEmpty())

        val pubgGlobal = games.find { it.packageName == "com.tencent.ig" }
        assertNotNull(pubgGlobal)
        assertEquals("PUBG MOBILE", pubgGlobal?.displayName)

        val bgmi = games.find { it.packageName == "com.pubg.imobile" }
        assertNotNull(bgmi)
        assertEquals("BATTLEGROUNDS MOBILE INDIA", bgmi?.displayName)

        val pubgKr = games.find { it.packageName == "com.pubg.krmobile" }
        assertNotNull(pubgKr)
        assertEquals("PUBG MOBILE KR", pubgKr?.displayName)
    }

    @Test
    fun testGraphicsProfileDefaults() {
        val profile = GraphicsProfile()
        assertEquals(GraphicsQuality.SMOOTH, profile.quality)
        assertEquals(FrameRateTarget.FPS_120, profile.frameRate)
        assertFalse(profile.isApplied)
    }

    @Test
    fun testTenResolutionPresetsPresence() {
        val presets = ResolutionPreset.values()
        assertEquals(10, presets.size)

        val auto = presets.find { it == ResolutionPreset.AUTO }
        assertNotNull(auto)

        val vga = presets.find { it == ResolutionPreset.RES_640x480 }
        assertNotNull(vga)
        assertEquals(640, vga?.width)
        assertEquals(480, vga?.height)

        val fhd = presets.find { it == ResolutionPreset.RES_1920x1080 }
        assertNotNull(fhd)
        assertEquals(1920, fhd?.width)
        assertEquals(1080, fhd?.height)

        val qhd = presets.find { it == ResolutionPreset.RES_2560x1440 }
        assertNotNull(qhd)
        assertEquals(2560, qhd?.width)
        assertEquals(1440, qhd?.height)
    }

    @Test
    fun testCommandTestCenterTenCategories() {
        val registry = CommandTestCenterEngine.initializeCommandRegistry()
        assertTrue(registry.isNotEmpty())

        // Ensure all 10 categories are represented
        val coveredCategories = registry.map { it.category }.distinct()
        assertEquals(10, coveredCategories.size)

        // Check Category 1: Device Info
        val cat1 = registry.filter { it.category == CommandCategory.DEVICE_INFO }
        assertTrue(cat1.isNotEmpty())

        // Check Category 2: Display State
        val cat2 = registry.filter { it.category == CommandCategory.DISPLAY_STATE }
        assertTrue(cat2.isNotEmpty())

        // Check Category 5: Game Mode
        val cat5 = registry.filter { it.category == CommandCategory.GAME_MODE }
        assertTrue(cat5.any { it.command.contains("cmd game set --mode 2") })

        // Check Category 6: Graphics/Resolution
        val cat6 = registry.filter { it.category == CommandCategory.GRAPHICS_RESOLUTION }
        assertTrue(cat6.any { it.command.contains("peak_refresh_rate 144.0") })

        // Check Category 7: State Restore
        val cat7 = registry.filter { it.category == CommandCategory.STATE_RESTORE }
        assertTrue(cat7.isNotEmpty())
    }

    @Test
    fun testPredefinedAdbCommandSafety() {
        val commands = AdbCommand.PREDEFINED_COMMANDS
        assertTrue(commands.isNotEmpty())

        commands.forEach { cmd ->
            assertFalse(cmd.command.contains("rm -rf"))
            assertFalse(cmd.command.contains("su "))
            assertFalse(cmd.command.contains("setenforce 0"))
            assertFalse(cmd.command.contains("reboot"))
        }
    }

    @Test
    fun testGyroSensitivityBounds() {
        val testX = 250
        val boundedX = testX.coerceIn(50, 200)
        assertEquals(200, boundedX)

        val testNegative = -20
        val boundedNegative = testNegative.coerceIn(50, 200)
        assertEquals(50, boundedNegative)
    }

    @Test
    fun testPercentileCalculations() {
        val sampleDurations = listOf(8.2f, 8.3f, 8.4f, 8.4f, 8.5f, 9.1f, 11.2f, 16.7f, 22.0f, 33.4f).sorted()
        val p50 = sampleDurations[(sampleDurations.size * 0.50).toInt()]
        val p95 = sampleDurations[(sampleDurations.size * 0.95).toInt().coerceIn(0, sampleDurations.lastIndex)]

        assertTrue(p50 < p95)
        assertTrue(p50 >= 8.2f)
    }

    @Test
    fun testAuditLogEntityCreation() {
        val log = AuditLogEntity(
            commandTitle = "Set Game Mode",
            command = "cmd game set --mode 2 com.tencent.ig",
            executionResult = "Mode set to: 2",
            exitCode = 0
        )
        assertEquals(0, log.exitCode)
        assertFalse(log.isRolledBack)
        assertTrue(log.timestamp > 0)
    }

    @Test
    fun test144HzAndOriginOSCommands() {
        val commands = AdbCommand.PREDEFINED_COMMANDS
        val cmd144 = commands.find { it.id == "set_refresh_rate_144" }
        assertNotNull(cmd144)
        assertTrue(cmd144!!.command.contains("144.0"))
        assertTrue(cmd144.command.contains("user_refresh_rate 144"))

        val multiTurbo = commands.find { it.id == "originos_multi_turbo" }
        assertNotNull(multiTurbo)
        assertTrue(multiTurbo!!.command.contains("vivo_multi_turbo 1"))

        val fpsUnlock = commands.find { it.id == "lock_fps_limit_144" }
        assertNotNull(fpsUnlock)
        assertTrue(fpsUnlock!!.command.contains("fps_limit 144"))
    }

    @Test
    fun testPerformanceModesDefinition() {
        val modes = PerformanceMode.values()
        assertTrue(modes.contains(PerformanceMode.SUSTAINED_COMPETITIVE))
        assertTrue(modes.contains(PerformanceMode.RAW_PERFORMANCE))
        assertTrue(modes.contains(PerformanceMode.THERMAL_RECOVERY))
        assertTrue(modes.contains(PerformanceMode.BALANCED))
    }

    @Test
    fun testFramePacingGovernorEvaluation() {
        val governor = com.example.core.performance.FramePacingGovernor()
        val metrics = DiagnosticMetrics(
            refreshRateHz = 120,
            fpsCurrent = 120f,
            p50Ms = 8.3f,
            p95Ms = 8.4f,
            p99Ms = 8.6f,
            tempCelsius = 38.0f
        )

        // 120 FPS on 120 Hz: 1:1 Integer Cadence
        val verdictOptimal = governor.evaluateFramePacing(metrics, 120, 120, false)
        assertFalse(verdictOptimal.cadenceJudderDetected)
        assertTrue(verdictOptimal.isIntegerCadence)
        assertEquals(1.0f, verdictOptimal.cadenceRatio, 0.05f)
        assertEquals(120, verdictOptimal.recommendedTargetFps)
        assertEquals(120, verdictOptimal.recommendedDisplayHz)
        assertTrue(verdictOptimal.stabilityScore >= 80)

        // 120 FPS on 144 Hz display: Cadence Judder
        val verdict144Mismatch = governor.evaluateFramePacing(metrics, 120, 144, false)
        assertTrue(verdict144Mismatch.cadenceJudderDetected)
        assertFalse(verdict144Mismatch.isIntegerCadence)
        assertEquals(1.2f, verdict144Mismatch.cadenceRatio, 0.05f)

        // Thermal throttle condition without cooler
        val hotMetrics = metrics.copy(tempCelsius = 45.0f)
        val verdictThrottled = governor.evaluateFramePacing(hotMetrics, 120, 120, false)
        assertEquals(90, verdictThrottled.recommendedTargetFps)
    }

    @Test
    fun testCompatibilityDatabaseRecords() {
        val records = DeviceCompatibilityRecord.KNOWN_DEVICE_RECORDS
        assertTrue(records.isNotEmpty())

        val iqoo = records.find { it.id == "iqoo_neo9_pubg_global" }
        assertNotNull(iqoo)
        assertEquals("iQOO Neo 9 5G (V2338A)", iqoo?.deviceModel)
        assertEquals(144, iqoo?.displayMaxRefreshHz)
        assertEquals(CompatibilityVerificationLevel.OFFICIALLY_DOCUMENTED, iqoo?.verificationLevel)
        assertTrue(iqoo!!.notes.contains("OriginOS"))

        val rog = records.find { it.id == "rog_phone8_pubg_global" }
        assertNotNull(rog)
        assertEquals(165, rog?.displayMaxRefreshHz)

        val redmagic = records.find { it.id == "redmagic_9_pubg_global" }
        assertNotNull(redmagic)
        assertTrue(redmagic!!.oemGamingIntegration.contains("Fan"))
    }

    @Test
    fun testNetworkPresetEndpoints() {
        val endpoints = NetworkTestEndpoint.PRESET_ENDPOINTS
        assertEquals(3, endpoints.size)

        val cloudflare = endpoints.find { it.id == "cloudflare" }
        assertNotNull(cloudflare)
        assertEquals("1.1.1.1", cloudflare?.host)
        assertTrue(cloudflare!!.isDefault)

        val google = endpoints.find { it.id == "google" }
        assertNotNull(google)
        assertEquals("8.8.8.8", google?.host)

        val defaultReport = NetworkQualityReport()
        assertTrue(defaultReport.isConnected)
        assertFalse(defaultReport.isTestingActive)
        assertTrue(defaultReport.recommendations.any { it.contains("Krafton") || it.contains("Tencent") })
    }

    @Test
    fun testGyroFilterProfiles() {
        val profiles = com.example.core.sensors.GyroFilterProfile.values()
        assertEquals(4, profiles.size)

        val bypass = com.example.core.sensors.GyroFilterProfile.BYPASS
        assertEquals(0.0f, bypass.latencyOverheadMs, 0.001f)
        assertEquals(0, bypass.noiseReductionPct)

        val standard = com.example.core.sensors.GyroFilterProfile.STANDARD
        assertEquals(0.2f, standard.latencyOverheadMs, 0.001f)
        assertEquals(35, standard.noiseReductionPct)

        val high = com.example.core.sensors.GyroFilterProfile.HIGH
        assertEquals(0.6f, high.latencyOverheadMs, 0.001f)
        assertEquals(65, high.noiseReductionPct)

        val highest = com.example.core.sensors.GyroFilterProfile.HIGHEST
        assertEquals(1.2f, highest.latencyOverheadMs, 0.001f)
        assertEquals(85, highest.noiseReductionPct)
    }

    @Test
    fun testThermalFiveStateTransitions() {
        val states = com.example.core.thermal.ThermalState.values()
        assertEquals(5, states.size)
        assertTrue(states.contains(com.example.core.thermal.ThermalState.COOL))
        assertTrue(states.contains(com.example.core.thermal.ThermalState.WARM))
        assertTrue(states.contains(com.example.core.thermal.ThermalState.HOT))
        assertTrue(states.contains(com.example.core.thermal.ThermalState.THROTTLING_RISK))
        assertTrue(states.contains(com.example.core.thermal.ThermalState.RECOVERING))

        val optimalVerdict = com.example.core.thermal.MatchReadinessVerdict.OPTIMAL
        assertTrue(optimalVerdict.label.contains("8v8 WOW"))
    }

    @Test
    fun testTouchLatencyMetricsCalculations() {
        val manager = com.example.core.touch.TouchLatencyManager()
        val metrics = manager.metrics.value
        assertEquals(0L, metrics.totalSampleCount)
        assertTrue(metrics.avgTouchToDrawLatencyMs > 0f)
        assertTrue(metrics.estimatedTouchSamplingRateHz >= 120)
    }

    @Test
    fun testCapabilityStatuses() {
        val statuses = com.example.core.adapter.CapabilityStatus.values()
        assertEquals(5, statuses.size)
        assertTrue(statuses.contains(com.example.core.adapter.CapabilityStatus.AVAILABLE))
        assertTrue(statuses.contains(com.example.core.adapter.CapabilityStatus.SUPPORTED_UNAUTHORIZED))
        assertTrue(statuses.contains(com.example.core.adapter.CapabilityStatus.APPLIED_VERIFIED))
        assertTrue(statuses.contains(com.example.core.adapter.CapabilityStatus.FAILED))
        assertTrue(statuses.contains(com.example.core.adapter.CapabilityStatus.UNSUPPORTED))
    }

    @Test
    fun testRawPerformanceProfileDefault() {
        val rawState = RawPerformanceState()
        assertTrue(rawState.isEnabled)
        assertEquals(PerformanceMode.RAW_PERFORMANCE, rawState.currentMode)
        assertTrue(rawState.applyWhenLaunchingGame)
        assertTrue(rawState.autoRestoreAfterGameExit)
    }

    @Test
    fun testPostMatchReportFields() {
        val report = com.example.core.gameplay.PostMatchReport(
            gameTitle = "PUBG MOBILE",
            packageName = "com.tencent.ig",
            durationSeconds = 1200L,
            initialTempCelsius = 35.0f,
            finalTempCelsius = 39.5f,
            tempDeltaCelsius = 4.5f,
            initialBatteryPct = 90,
            finalBatteryPct = 78,
            batteryDrainPct = 12,
            appliedProfileSummary = "Smooth @ 120 FPS",
            zeroOverheadVerified = true
        )

        assertEquals("com.tencent.ig", report.packageName)
        assertEquals(1200L, report.durationSeconds)
        assertEquals(4.5f, report.tempDeltaCelsius, 0.01f)
        assertEquals(12, report.batteryDrainPct)
        assertTrue(report.zeroOverheadVerified)
    }

    @Test
    fun testIqooNeo9DeviceDiagnosticsSpecs() {
        val summary = com.example.core.device.DeviceSummary(
            model = "PD2338C",
            manufacturer = "vivo",
            board = "kalama",
            chipset = "Qualcomm Snapdragon 8 Gen 2",
            androidVersion = "14",
            customRomName = "OriginOS 6",
            apiLevel = 34,
            isFlagshipClass = true,
            totalRamMb = 16384L,
            availableRamMb = 11200L,
            totalStorageGb = 512L,
            availableStorageGb = 380L,
            batteryPct = 95,
            isCharging = false,
            batteryTempCelsius = 34.5f,
            thermalHeadroom = 0.95f,
            thermalStatus = "NORMAL",
            displayModeSummary = "120Hz 1:1 Cadence",
            currentRefreshRateHz = 120f,
            maxRefreshRateHz = 144,
            supportedRefreshRates = listOf(60f, 90f, 120f, 144f),
            isVivoOriginOsTarget = true,
            firmwareVersion = "PD2338C_A_16.2.14.1.W10.V000L1",
            coprocessor = "vivo Q1 Display Coprocessor"
        )

        assertEquals("PD2338C", summary.model)
        assertEquals("PD2338C_A_16.2.14.1.W10.V000L1", summary.firmwareVersion)
        assertEquals("vivo Q1 Display Coprocessor", summary.coprocessor)
        assertEquals(16384L, summary.totalRamMb)
        assertEquals(512L, summary.totalStorageGb)
    }

    @Test
    fun testGyroModeProfilesDefinition() {
        val modes = GyroMode.values()
        assertEquals(4, modes.size)
        assertTrue(modes.contains(GyroMode.RAW_PASSTHROUGH))
        assertTrue(modes.contains(GyroMode.LIGHT_FILTER))
        assertTrue(modes.contains(GyroMode.AGGRESSIVE_STABILIZATION))
        assertTrue(modes.contains(GyroMode.PREDICTIVE_AIM))

        assertEquals("Bypass Mode (Raw 1:1)", GyroMode.RAW_PASSTHROUGH.displayName)
        assertEquals("Highest Profile (Pro E-Sports)", GyroMode.PREDICTIVE_AIM.displayName)
    }

    @Test
    fun testUserSettingsStateDefaults() {
        val state = com.example.data.preferences.UserSettingsState()
        assertEquals(2, state.schemaVersion)
        assertEquals("com.tencent.ig", state.selectedGamePackage)
        assertEquals("Super Smooth", state.selectedGraphicsPreset)
        assertEquals(120, state.preferredFps)
        assertEquals(120, state.preferredRefreshRateHz)
        assertEquals(PerformanceMode.RAW_PERFORMANCE, state.performanceMode)
        assertFalse(state.isExternalCoolerActive)
        assertEquals(GyroMode.LIGHT_FILTER, state.gyroMode)
        assertEquals(100, state.gyroXSensitivityPercent)
        assertEquals(100, state.gyroYSensitivityPercent)
        assertTrue(state.antiThermalTouchEnabled)
        assertTrue(state.zeroOverheadGamingEnabled)
    }

    @Test
    fun testThreeOperatingStatesAndTransitions() {
        val states = com.example.core.performance.SustainedOperatingState.values()
        assertEquals(3, states.size)
        assertTrue(states.contains(com.example.core.performance.SustainedOperatingState.INITIAL_PERFORMANCE))
        assertTrue(states.contains(com.example.core.performance.SustainedOperatingState.SUSTAINED_PERFORMANCE))
        assertTrue(states.contains(com.example.core.performance.SustainedOperatingState.RECOVERY))

        assertEquals("Initial Performance (Phase A)", com.example.core.performance.SustainedOperatingState.INITIAL_PERFORMANCE.displayName)
        assertEquals("Sustained Performance (Phase B)", com.example.core.performance.SustainedOperatingState.SUSTAINED_PERFORMANCE.displayName)
        assertEquals("Non-Disruptive Recovery (Phase C)", com.example.core.performance.SustainedOperatingState.RECOVERY.displayName)
    }

    @Test
    fun testEnduranceSixStagesCoverage() {
        val stages = com.example.core.performance.EnduranceStage.values()
        assertEquals(6, stages.size)
        assertEquals(0, stages.first().startMin)
        assertEquals(240, stages.last().endMin)

        assertTrue(stages.contains(com.example.core.performance.EnduranceStage.STAGE_1_WARMUP))
        assertTrue(stages.contains(com.example.core.performance.EnduranceStage.STAGE_2_HEAT_ACCUMULATION))
        assertTrue(stages.contains(com.example.core.performance.EnduranceStage.STAGE_3_MATCH_2_STEADY))
        assertTrue(stages.contains(com.example.core.performance.EnduranceStage.STAGE_4_THERMAL_EQUILIBRIUM))
        assertTrue(stages.contains(com.example.core.performance.EnduranceStage.STAGE_5_EXTENDED_PLAY))
        assertTrue(stages.contains(com.example.core.performance.EnduranceStage.STAGE_6_ENDURANCE_BOUNDARY))
    }

    @Test
    fun testRecoveryCauseAndEventHandling() {
        val causes = com.example.core.performance.RecoveryCause.values()
        assertEquals(4, causes.size)
        assertTrue(causes.contains(com.example.core.performance.RecoveryCause.THERMAL_THROTTLING))
        assertTrue(causes.contains(com.example.core.performance.RecoveryCause.MEMORY_PRESSURE))
        assertTrue(causes.contains(com.example.core.performance.RecoveryCause.POWER_RESTRICTION))
        assertTrue(causes.contains(com.example.core.performance.RecoveryCause.CADENCE_MISMATCH))

        val event = com.example.core.performance.RecoveryEvent(
            cause = com.example.core.performance.RecoveryCause.THERMAL_THROTTLING,
            description = "Battery temp reached 41.8°C",
            mitigationAction = "Bounded ADPF boost hints"
        )
        assertEquals("Battery temp reached 41.8°C", event.description)
        assertEquals(com.example.core.performance.RecoveryCause.THERMAL_THROTTLING, event.cause)
    }

    @Test
    fun testTouchStabilityDiagnosticMetrics() {
        val manager = com.example.core.touch.TouchLatencyManager()
        manager.startDiagnosticSession(displayHz = 120, systemState = "Normal")

        val activeMetrics = manager.metrics.value
        assertTrue(activeMetrics.isDiagnosticActive)
        assertEquals(120, activeMetrics.activeDisplayHz)
        assertEquals("Normal", activeMetrics.systemStateDuringTest)

        manager.stopDiagnosticSession()
        val stoppedMetrics = manager.metrics.value
        assertFalse(stoppedMetrics.isDiagnosticActive)
    }
}

