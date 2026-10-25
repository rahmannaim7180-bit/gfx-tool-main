package com.example.data.model

enum class ProfileType(val label: String, val description: String) {
    SUPER_SMOOTH_POTATO("Super Smooth / Potato", "Minimum supported rendering workload, lowest draw calls, competitive latency"),
    POTATO_LOW("Potato / Competitive Low", "Minimizes supported rendering workload and visual overhead"),
    BALANCED("Balanced", "Balanced visuals, sustained temperatures, and stable frame rates"),
    SMOOTH_COMPETITIVE("Smooth Competitive", "Lowest practical graphics workload with highest supported FPS (120 FPS)"),
    VISUAL_QUALITY("Visual Quality", "Preserves higher visual fidelity within sustainable thermal envelope"),
    CUSTOM("Custom Profile", "User-configured rendering resolution, target FPS, and visual effects")
}

enum class GraphicsQuality(val displayName: String, val renderScaleHint: Float) {
    SUPER_SMOOTH("Super Smooth", 0.65f),
    SMOOTH("Smooth", 0.75f),
    BALANCED("Balanced", 0.85f),
    HD("HD", 1.0f),
    HDR("HDR", 1.0f),
    ULTRA_HD("Ultra HD", 1.0f)
}

enum class FrameRateTarget(val displayName: String, val fpsValue: Int, val isHardwareTestOnly: Boolean = false, val supportExplanation: String = "") {
    MEDIUM("Medium (40 FPS)", 40, false, "Standard power saving mode"),
    HIGH("High (60 FPS)", 60, false, "Standard 60 FPS competitive baseline"),
    ULTRA("Ultra (72 FPS)", 72, false, "Extended frame rate on supported displays"),
    EXTREME("Extreme (60 FPS)", 60, false, "Standard high performance option in game settings"),
    FPS_90("90 FPS (Extreme+)", 90, false, "Supported on Smooth / Balanced quality"),
    FPS_120("120 FPS (Ultra Extreme)", 120, false, "Officially supported on Snapdragon 8 Gen 2 / iQOO Neo 9 in PUBG Mobile 3.2+"),
    FPS_144("144 FPS (Hardware Test Only)", 144, true, "Display hardware runs at 144Hz, but PUBG Mobile Unreal Engine is capped at 120 FPS max")
}

enum class GraphicsStyle(val displayName: String) {
    CLASSIC("Classic"),
    COLORFUL("Colorful"),
    REALISTIC("Realistic"),
    SOFT("Soft"),
    MOVIE("Movie")
}

enum class ResolutionPreset(val displayName: String, val width: Int, val height: Int, val description: String) {
    AUTO("Default (Auto)", 0, 0, "Uses device native rendering resolution without scaling"),
    RES_640x480("640×480 (VGA)", 640, 480, "Minimum rendering workload; maximum frame time stability on hot days"),
    RES_768x480("768×480 (WVGA)", 768, 480, "Wide low-resolution candidate profile"),
    RES_800x600("800×600 (SVGA)", 800, 600, "Low overhead SVGA rendering viewport"),
    RES_960x540("960×540 (qHD)", 960, 540, "Quarter HD balance between visibility and shader fillrate"),
    RES_1280x720("1280×720 (HD)", 1280, 720, "Optimal esports resolution; lowest shader load for 120 FPS"),
    RES_1440x810("1440×810 (WXGA+)", 1440, 810, "Sub-1080p esports candidate profile"),
    RES_1600x900("1600×900 (HD+)", 1600, 900, "High definition 900p viewport"),
    RES_1920x1080("1920×1080 (FHD)", 1920, 1080, "Full HD native fidelity; crisp long-range spotting"),
    RES_2560x1440("2560×1440 (2K)(Tablet)", 2560, 1440, "Maximum 2K resolution for large tablets & external displays")
}

enum class ShadowQuality(val displayName: String) {
    DISABLED("Disabled"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class AntiAliasingMode(val displayName: String) {
    DISABLED("Disabled"),
    X2("2x MSAA"),
    X4("4x MSAA")
}

enum class TextureQuality(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class EffectsQuality(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class AudioQuality(val displayName: String) {
    LOW("Low"),
    HIGH("High"),
    ULTRA("Ultra")
}

enum class ProfileStateBadge(val label: String) {
    SAVED_PREFERENCE("Saved Preference"),
    APPLIED("Applied to Session"),
    VERIFIED_ACTIVE("Independently Verified")
}

data class GraphicsCapabilityReport(
    val directInGameWritable: Boolean = false,
    val requiresSafDocumentTree: Boolean = true,
    val systemResolutionApplicableViaAdb: Boolean = true,
    val displayRefreshLockApplicable: Boolean = true,
    val gameEngineFpsCap: Int = 120,
    val hardwareDisplayMaxHz: Int = 144,
    val summary: String = "PUBG Mobile Global supports up to 120 FPS on Snapdragon 8 Gen 2 when Smooth is selected in-game. System display refresh rate can be locked to 144Hz via Shizuku/ADB."
)

data class GraphicsProfile(
    val id: String = "profile_default",
    val name: String = "Smooth Competitive 120",
    val gamePackage: String = "com.tencent.ig",
    val type: ProfileType = ProfileType.SMOOTH_COMPETITIVE,
    val quality: GraphicsQuality = GraphicsQuality.SMOOTH,
    val frameRate: FrameRateTarget = FrameRateTarget.FPS_120,
    val style: GraphicsStyle = GraphicsStyle.CLASSIC,
    val resolution: ResolutionPreset = ResolutionPreset.AUTO,
    val renderScalePercent: Int = 100, // 50% to 100%
    val shadows: ShadowQuality = ShadowQuality.DISABLED,
    val antiAliasing: AntiAliasingMode = AntiAliasingMode.DISABLED,
    val textureQuality: TextureQuality = TextureQuality.LOW,
    val effectsQuality: EffectsQuality = EffectsQuality.LOW,
    val audioQuality: AudioQuality = AudioQuality.HIGH,
    val stateBadge: ProfileStateBadge = ProfileStateBadge.SAVED_PREFERENCE,
    val isApplied: Boolean = false,
    val appliedTimestamp: Long = 0L,
    val statusSummary: String = "Configured for competitive low-latency frame delivery",
    val verifiedNote: String = "Candidate configuration saved in persistent profile store."
)
