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
    ACTIVE_SAV("Active.sav (Unreal Engine Save)", "Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/SaveGames/Active.sav", true),
    USER_CUSTOM_INI("UserCustom.ini (Render Config)", "Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini", false),
    EXPORTED_JSON("Exported Game Profile (JSON)", "Download/FrameForge/Active_Export.json", false)
}

data class ConfigFileWorkspace(
    val sourceType: ConfigSourceType = ConfigSourceType.ACTIVE_SAV,
    val filePath: String = ConfigSourceType.ACTIVE_SAV.defaultPath,
    val lastModifiedTimestamp: Long = System.currentTimeMillis(),
    val isReadOnly: Boolean = true,
    val accessStatus: String = "Scoped Storage Protected (Inspect Legitimate Snapshot / Exported File)",
    val keys: List<ConfigKeyItem> = listOf(
        ConfigKeyItem(
            id = "fps_level",
            key = "FPSLevel",
            currentValue = "4",
            proposedValue = "4",
            dataType = ConfigDataType.INTEGER,
            category = "Framerate",
            description = "Unreal Engine in-game FPS tier: 1=30, 2=40, 3=60, 4=90, 6=120",
            allowedRange = "1 - 6"
        ),
        ConfigKeyItem(
            id = "battleground_max_fps",
            key = "BattlegroundMaxFPS",
            currentValue = "90",
            proposedValue = "90",
            dataType = ConfigDataType.INTEGER,
            category = "Framerate",
            description = "Main game loop frame deadline cap (30, 60, 90, 120)",
            allowedRange = "30 - 120"
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
            id = "shadow_quality",
            key = "ShadowQuality",
            currentValue = "0",
            proposedValue = "0",
            dataType = ConfigDataType.INTEGER,
            category = "Shadows",
            description = "Dynamic cascaded shadow map cascade depth (0=Disabled, 1=Low, 2=Med, 3=High)",
            allowedRange = "0 - 3"
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
        ),
        ConfigKeyItem(
            id = "texture_quality",
            key = "TextureQuality",
            currentValue = "0",
            proposedValue = "0",
            dataType = ConfigDataType.INTEGER,
            category = "Quality",
            description = "Mipmap bias and texture filtering mip level (0=Low, 1=Med, 2=High)",
            allowedRange = "0 - 2"
        ),
        ConfigKeyItem(
            id = "effects_quality",
            key = "EffectsQuality",
            currentValue = "0",
            proposedValue = "0",
            dataType = ConfigDataType.INTEGER,
            category = "Quality",
            description = "Particle system density and smoke/flash shader complexity",
            allowedRange = "0 - 2"
        ),
        ConfigKeyItem(
            id = "audio_quality",
            key = "AudioQualityLevel",
            currentValue = "1",
            proposedValue = "1",
            dataType = ConfigDataType.INTEGER,
            category = "System",
            description = "Wwise audio sampling rate and spatial buffer channels (0=Low, 1=High, 2=Ultra)",
            allowedRange = "0 - 2"
        )
    )
)
