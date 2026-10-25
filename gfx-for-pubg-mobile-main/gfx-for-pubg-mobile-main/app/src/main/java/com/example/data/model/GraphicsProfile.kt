package com.example.data.model

enum class ProfileType(val label: String, val description: String) {
    SUPER_SMOOTH_POTATO("Super Smooth / Potato", "Minimum supported rendering workload, lowest draw calls, competitive latency"),
    POTATO_LOW("Potato / Competitive Low", "Minimizes supported rendering workload and visual overhead"),
    BALANCED("Balanced", "Balanced visuals, sustained temperatures, and stable frame rates"),
    SMOOTH_COMPETITIVE("Smooth Competitive", "Lowest practical graphics workload with highest supported FPS"),
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

enum class FrameRateTarget(val displayName: String, val fpsValue: Int, val isHardwareTestOnly: Boolean = false) {
    POWER_SAVING("Power Saving (30)", 30),
    MEDIUM("Medium (40)", 40),
    HIGH("High (60)", 60),
    ULTRA("Ultra (72)", 72),
    EXTREME("Extreme (60)", 60),
    FPS_90("90 FPS", 90),
    FPS_120("120 FPS", 120),
    FPS_144("144 FPS (Hardware Test)", 144, true)
}

enum class GraphicsStyle(val displayName: String) {
    CLASSIC("Classic"),
    COLORFUL("Colorful"),
    REALISTIC("Realistic"),
    SOFT("Soft"),
    MOVIE("Movie")
}

enum class ResolutionPreset(val displayName: String, val width: Int, val height: Int) {
    AUTO("Default / Auto", 0, 0),
    RES_640x480("640×480 (VGA Low)", 640, 480),
    RES_768x480("768×480 (Wide 480p)", 768, 480),
    RES_800x600("800×600 (SVGA)", 800, 600),
    RES_960x540("960×540 (qHD)", 960, 540),
    RES_1280x720("1280×720 (HD 720p)", 1280, 720),
    RES_1440x810("1440×810 (900p)", 1440, 810),
    RES_1600x900("1600×900 (HD+)", 1600, 900),
    RES_1920x1080("1920×1080 (FHD 1080p)", 1920, 1080),
    RES_2560x1440("2560×1440 (2K QHD)", 2560, 1440)
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

data class GraphicsProfile(
    val id: String = "profile_default",
    val name: String = "Smooth Competitive 90",
    val gamePackage: String = "com.tencent.ig",
    val type: ProfileType = ProfileType.SMOOTH_COMPETITIVE,
    val quality: GraphicsQuality = GraphicsQuality.SMOOTH,
    val frameRate: FrameRateTarget = FrameRateTarget.FPS_90,
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
