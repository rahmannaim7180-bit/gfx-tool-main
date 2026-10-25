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
}
