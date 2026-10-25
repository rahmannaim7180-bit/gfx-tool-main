package com.example

import com.example.data.local.entity.AuditLogEntity
import com.example.data.model.AdbCommand
import com.example.data.model.GameTarget
import com.example.data.model.GraphicsProfile
import com.example.data.model.GraphicsQuality
import com.example.data.model.FrameRateTarget
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
        assertEquals(FrameRateTarget.FPS_90, profile.frameRate)
        assertFalse(profile.isApplied)
    }

    @Test
    fun testPredefinedAdbCommandSafety() {
        val commands = AdbCommand.PREDEFINED_COMMANDS
        assertTrue(commands.isNotEmpty())

        commands.forEach { cmd ->
            // Ensure no dangerous shell commands are predefined
            assertFalse(cmd.command.contains("rm -rf"))
            assertFalse(cmd.command.contains("su "))
            assertFalse(cmd.command.contains("setenforce 0"))
            assertFalse(cmd.command.contains("reboot"))
        }
    }

    @Test
    fun testGyroSensitivityBounds() {
        val testX = 250
        val boundedX = testX.coerceIn(0, 200)
        assertEquals(200, boundedX)

        val testNegative = -20
        val boundedNegative = testNegative.coerceIn(0, 200)
        assertEquals(0, boundedNegative)
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
    fun testAllowlistCompoundCommandVerification() {
        val allowedPrefixes = listOf(
            "cmd game set --mode",
            "settings put system peak_refresh_rate",
            "settings put system min_refresh_rate",
            "settings put system user_refresh_rate",
            "settings put global fps_limit",
            "settings put global vivo_multi_turbo"
        )

        val compound144 = "settings put system peak_refresh_rate 144.0 && settings put system min_refresh_rate 144.0 && settings put system user_refresh_rate 144"
        val subCmds = compound144.split("&&").map { it.trim() }
        val isAllowed = subCmds.all { sub ->
            allowedPrefixes.any { prefix -> sub.startsWith(prefix) }
        }
        assertTrue(isAllowed)

        val maliciousCmd = "settings put system peak_refresh_rate 144.0 && rm -rf /data"
        val malSubCmds = maliciousCmd.split("&&").map { it.trim() }
        val isMaliciousAllowed = malSubCmds.all { sub ->
            allowedPrefixes.any { prefix -> sub.startsWith(prefix) }
        }
        assertFalse(isMaliciousAllowed)
    }
}
