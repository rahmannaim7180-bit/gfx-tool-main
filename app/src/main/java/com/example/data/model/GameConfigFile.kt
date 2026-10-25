package com.example.data.model

enum class ConfigDataType {
    INTEGER,
    FLOAT,
    BOOLEAN,
    STRING
}

data class ConfigKeyItem(
    val id: String,
    val key: String,
    val currentValue: String,
    val proposedValue: String,
    val dataType: ConfigDataType,
    val category: String,
    val description: String,
    val allowedRange: String,
    val isKnownSafeKey: Boolean = true,
    val isModified: Boolean = false
)

enum class ConfigSourceType(val displayName: String, val defaultPath: String, val isBinary: Boolean) {
    ACTIVE_SAV("Active.sav (UE4 SaveGame)", "Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/SaveGames/Active.sav", true),
    USER_CUSTOM_INI("UserCustom.ini (Render Config)", "Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini", false),
    EXPORTED_JSON("Exported Game Profile (JSON)", "Download/FrameForge/Active_Export.json", false)
}

data class SavGuiSettings(
    val selectedGraphicsPreset: String = "Smooth", // Super Smooth, Smooth, Balanced
    val selectedFrameRatePreset: String = "Ultra Extreme", // Extreme (60), Extreme+ (90), Ultra Extreme (120)
    val manualFps: Int = 120,
    val selectedStyle: String = "Colorful", // Classic, Colorful, Realistic, Soft, Movie
    val selectedColorblindMode: String = "Normal",
    val brightnessPercent: Int = 150
)

data class ConfigFileWorkspace(
    val sourceType: ConfigSourceType = ConfigSourceType.ACTIVE_SAV,
    val filePath: String = ConfigSourceType.ACTIVE_SAV.defaultPath,
    val lastModifiedTimestamp: Long = System.currentTimeMillis(),
    val isReadOnly: Boolean = true,
    val accessStatus: String = "Scoped Storage (Android 11+ restricts direct writes without SAF / DocumentFile)",
    val guiSettings: SavGuiSettings = SavGuiSettings(),
    val keys: List<ConfigKeyItem> = listOf(
        ConfigKeyItem(
            id = "battle_fps",
            key = "BattleFPS",
            currentValue = "120",
            proposedValue = "120",
            dataType = ConfigDataType.INTEGER,
            category = "Framerate",
            description = "In-match maximum frame deadline cap (60=Extreme, 90=Extreme+, 120=Ultra Extreme)",
            allowedRange = "30 - 120"
        ),
        ConfigKeyItem(
            id = "lobby_fps",
            key = "LobbyFPS",
            currentValue = "60",
            proposedValue = "60",
            dataType = ConfigDataType.INTEGER,
            category = "Framerate",
            description = "Main lobby frame rate cap to prevent battery drain and menu heating",
            allowedRange = "30 - 60"
        ),
        ConfigKeyItem(
            id = "art_quality",
            key = "ArtQuality",
            currentValue = "1",
            proposedValue = "1",
            dataType = ConfigDataType.INTEGER,
            category = "Graphics",
            description = "UE4 base art quality: 0=Super Smooth, 1=Smooth, 2=Balanced, 3=HD, 4=HDR",
            allowedRange = "0 - 5"
        ),
        ConfigKeyItem(
            id = "gyroscope",
            key = "Gyroscope",
            currentValue = "1",
            proposedValue = "1",
            dataType = ConfigDataType.INTEGER,
            category = "Controls",
            description = "In-game gyroscope operational state: 1=Always On, 2=Scope On, 0=Close",
            allowedRange = "0 - 2"
        ),
        ConfigKeyItem(
            id = "shadow_quality",
            key = "ShadowQuality",
            currentValue = "0",
            proposedValue = "0",
            dataType = ConfigDataType.INTEGER,
            category = "Shadows",
            description = "Dynamic cascaded shadow maps: 0=Disabled, 1=Low, 2=Med, 3=High",
            allowedRange = "0 - 3"
        ),
        ConfigKeyItem(
            id = "camera_lens_sensibility",
            key = "CameraLensSensibility",
            currentValue = "8",
            proposedValue = "8",
            dataType = ConfigDataType.INTEGER,
            category = "Controls",
            description = "Camera optical lens sensitivity scale coefficient",
            allowedRange = "1 - 100"
        ),
        ConfigKeyItem(
            id = "limit_medical",
            key = "LimitMedical",
            currentValue = "120",
            proposedValue = "120",
            dataType = ConfigDataType.INTEGER,
            category = "System",
            description = "Heuristic recovery item frame allocation limit",
            allowedRange = "30 - 144"
        ),
        ConfigKeyItem(
            id = "limit_anodyne",
            key = "LimitAnodyne",
            currentValue = "8",
            proposedValue = "8",
            dataType = ConfigDataType.INTEGER,
            category = "System",
            description = "Painkiller / booster processing thread throttle limit",
            allowedRange = "1 - 32"
        ),
        ConfigKeyItem(
            id = "resolution_quality",
            key = "ResolutionQuality",
            currentValue = "75.0",
            proposedValue = "75.0",
            dataType = ConfigDataType.FLOAT,
            category = "Resolution",
            description = "Internal 3D scene render viewport scale percentage",
            allowedRange = "50.0 - 100.0"
        ),
        ConfigKeyItem(
            id = "antialiasing_quality",
            key = "AntiAliasingQuality",
            currentValue = "0",
            proposedValue = "0",
            dataType = ConfigDataType.INTEGER,
            category = "PostProcess",
            description = "Hardware MSAA / Temporal AA sample count (0=Off, 1=2x, 2=4x)",
            allowedRange = "0 - 2"
        )
    )
)
