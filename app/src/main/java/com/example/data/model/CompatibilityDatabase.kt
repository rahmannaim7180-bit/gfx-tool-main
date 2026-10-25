package com.example.data.model

enum class CompatibilityVerificationLevel(val displayName: String, val badgeColorHex: Long) {
    OFFICIALLY_DOCUMENTED("Officially Documented", 0xFF00E676),
    LOCALLY_DETECTED("Locally Detected", 0xFF00E5FF),
    COMMUNITY_REPORTED("Community Reported", 0xFFFFAB00),
    UNVERIFIED("Unverified Claim", 0xFFFF5252)
}

data class DeviceCompatibilityRecord(
    val id: String,
    val deviceModel: String,
    val manufacturer: String,
    val chipset: String,
    val targetGamePackage: String,
    val targetGameName: String,
    val verifiedFpsTarget: String,
    val verifiedGraphicsQuality: String,
    val displayMaxRefreshHz: Int,
    val oemGamingIntegration: String,
    val shizukuAdbSupported: Boolean,
    val verificationLevel: CompatibilityVerificationLevel,
    val notes: String
) {
    companion object {
        val KNOWN_DEVICE_RECORDS = listOf(
            DeviceCompatibilityRecord(
                id = "iqoo_neo9_pubg_global",
                deviceModel = "iQOO Neo 9 5G (V2338A)",
                manufacturer = "vivo / iQOO",
                chipset = "Qualcomm Snapdragon 8 Gen 2 (4nm)",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "120 FPS (Ultra Extreme)",
                verifiedGraphicsQuality = "Smooth (60 FPS on HDR, 40 FPS on Ultra HD)",
                displayMaxRefreshHz = 144,
                oemGamingIntegration = "OriginOS 6 Game Space / Multi-Turbo Game Priority",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.OFFICIALLY_DOCUMENTED,
                notes = "Officially unlocked 120 FPS in PUBG Mobile 3.2+ on Smooth. OriginOS locks surface to 60Hz unless forced via Shizuku display commands."
            ),
            DeviceCompatibilityRecord(
                id = "rog_phone8_pubg_global",
                deviceModel = "ASUS ROG Phone 8 Pro",
                manufacturer = "ASUS",
                chipset = "Qualcomm Snapdragon 8 Gen 3 (4nm)",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "120 FPS (Ultra Extreme)",
                verifiedGraphicsQuality = "Smooth / Balanced",
                displayMaxRefreshHz = 165,
                oemGamingIntegration = "Armoury Crate X-Mode (Hardware Bypass Charging)",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.OFFICIALLY_DOCUMENTED,
                notes = "Supports continuous 120 FPS with active AeroActive cooler. Internal game engine capped at 120 FPS."
            ),
            DeviceCompatibilityRecord(
                id = "redmagic_9_pubg_global",
                deviceModel = "RedMagic 9 Pro",
                manufacturer = "Nubia",
                chipset = "Qualcomm Snapdragon 8 Gen 3 (4nm)",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "120 FPS (Ultra Extreme)",
                verifiedGraphicsQuality = "Smooth / Balanced",
                displayMaxRefreshHz = 120,
                oemGamingIntegration = "Game Space Red Core (Internal 22,000 RPM Fan)",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.OFFICIALLY_DOCUMENTED,
                notes = "Maintains 120 FPS indefinitely with internal fan enabled. Thermal throttling threshold > 48°C."
            ),
            DeviceCompatibilityRecord(
                id = "s24_ultra_pubg_global",
                deviceModel = "Samsung Galaxy S24 Ultra",
                manufacturer = "Samsung",
                chipset = "Qualcomm Snapdragon 8 Gen 3 for Galaxy",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "120 FPS (Ultra Extreme)",
                verifiedGraphicsQuality = "Smooth",
                displayMaxRefreshHz = 120,
                oemGamingIntegration = "One UI Game Booster (Game Plugins / Perf Z)",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.OFFICIALLY_DOCUMENTED,
                notes = "120 FPS unlocked on Smooth in PUBG 3.2+. One UI GOS regulates peak battery temperatures aggressively."
            ),
            DeviceCompatibilityRecord(
                id = "xiaomi_14_pubg_global",
                deviceModel = "Xiaomi 14 / 14 Pro",
                manufacturer = "Xiaomi",
                chipset = "Qualcomm Snapdragon 8 Gen 3",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "120 FPS (Ultra Extreme)",
                verifiedGraphicsQuality = "Smooth",
                displayMaxRefreshHz = 120,
                oemGamingIntegration = "HyperOS Game Turbo (Wild Boost 2.0)",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.COMMUNITY_REPORTED,
                notes = "120 FPS confirmed functional by community testing. Joyose framework requires high refresh rate whitelist."
            ),
            DeviceCompatibilityRecord(
                id = "oneplus_12_pubg_global",
                deviceModel = "OnePlus 12",
                manufacturer = "OnePlus",
                chipset = "Qualcomm Snapdragon 8 Gen 3",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "120 FPS (Ultra Extreme)",
                verifiedGraphicsQuality = "Smooth",
                displayMaxRefreshHz = 120,
                oemGamingIntegration = "OxygenOS Trinity Engine / HyperBoost",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.COMMUNITY_REPORTED,
                notes = "Pixelworks X7 chip enables optical frame interpolation, but native render remains 120 FPS."
            ),
            DeviceCompatibilityRecord(
                id = "generic_unverified_144fps_claim",
                deviceModel = "Universal Android 144Hz Devices",
                manufacturer = "Various",
                chipset = "Snapdragon / Dimensity",
                targetGamePackage = "com.tencent.ig",
                targetGameName = "PUBG Mobile Global",
                verifiedFpsTarget = "144 FPS (Unverified / Engine Capped at 120)",
                verifiedGraphicsQuality = "N/A",
                displayMaxRefreshHz = 144,
                oemGamingIntegration = "Standard Android Game Mode",
                shizukuAdbSupported = true,
                verificationLevel = CompatibilityVerificationLevel.UNVERIFIED,
                notes = "144 FPS in PUBG Mobile is currently an unverified claim. The Unreal Engine 4 binary tick limiter is hard-capped at 120 FPS max on all current production builds."
            )
        )
    }
}
