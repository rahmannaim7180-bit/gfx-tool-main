package com.example.core.config

import com.example.data.local.dao.ConfigFileBackupDao
import com.example.data.local.entity.ConfigFileBackupEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ConfigValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

data class ConfigDiffEntry(
    val key: String,
    val originalValue: String,
    val proposedValue: String,
    val category: String
)

class GameConfigInspectorEngine(
    private val configFileBackupDao: ConfigFileBackupDao
) {
    private val _workspace = MutableStateFlow(ConfigFileWorkspace())
    val workspace: StateFlow<ConfigFileWorkspace> = _workspace.asStateFlow()

    private val safeKeyAllowlist = setOf(
        "BattleFPS",
        "LobbyFPS",
        "ArtQuality",
        "ShadowQuality",
        "Gyroscope",
        "CameraLensSensibility",
        "LimitMedical",
        "LimitAnodyne",
        "ResolutionQuality",
        "AntiAliasingQuality",
        "FPSLevel",
        "BattlegroundMaxFPS",
        "AimAssistLevel",
        "ScopeAnimScale",
        "TouchPollingRate"
    )

    fun selectSourceType(sourceType: ConfigSourceType) {
        _workspace.value = _workspace.value.copy(
            sourceType = sourceType,
            filePath = sourceType.defaultPath
        )
    }

    fun toggleReadOnly(isReadOnly: Boolean) {
        _workspace.value = _workspace.value.copy(isReadOnly = isReadOnly)
    }

    fun updateGuiGraphicsPreset(preset: String) {
        val gui = _workspace.value.guiSettings.copy(selectedGraphicsPreset = preset)
        val artVal = when (preset) {
            "Super Smooth" -> "0"
            "Smooth" -> "1"
            "Balanced" -> "2"
            else -> "1"
        }
        updateProposedValueByKey("ArtQuality", artVal)
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiFrameRatePreset(preset: String) {
        val (fpsVal, battleVal) = when (preset) {
            "Extreme" -> "3" to "60"
            "Extreme+" -> "4" to "90"
            "Ultra Extreme" -> "5" to "120"
            else -> "5" to "120"
        }
        val gui = _workspace.value.guiSettings.copy(
            selectedFrameRatePreset = preset,
            manualFps = if (preset == "Ultra Extreme") 120 else if (preset == "Extreme+") 90 else 60
        )
        updateProposedValueByKey("FPSLevel", fpsVal)
        updateProposedValueByKey("BattleFPS", battleVal)
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiManualFps(fps: Int) {
        val gui = _workspace.value.guiSettings.copy(manualFps = fps)
        updateProposedValueByKey("BattleFPS", fps.toString())
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiStyle(style: String) {
        val gui = _workspace.value.guiSettings.copy(selectedStyle = style)
        val styleNum = when (style) {
            "Classic" -> "1"
            "Colorful" -> "2"
            "Realistic" -> "3"
            "Soft" -> "4"
            "Movie" -> "5"
            else -> "2"
        }
        updateProposedValueByKey("Style", styleNum)
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiBrightness(percent: Int) {
        val gui = _workspace.value.guiSettings.copy(brightnessPercent = percent)
        updateProposedValueByKey("Brightness", percent.toString())
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateProposedValueByKey(key: String, value: String) {
        val updatedKeys = _workspace.value.keys.map { item ->
            if (item.key == key) {
                item.copy(proposedValue = value, isModified = item.currentValue != value)
            } else item
        }
        _workspace.value = _workspace.value.copy(keys = updatedKeys)
    }

    fun updateProposedValue(keyId: String, newValue: String) {
        val updatedKeys = _workspace.value.keys.map { item ->
            if (item.id == keyId) {
                item.copy(
                    proposedValue = newValue,
                    isModified = item.currentValue != newValue
                )
            } else item
        }
        _workspace.value = _workspace.value.copy(keys = updatedKeys)
    }

    fun addCustomKey(
        key: String,
        value: String,
        category: String = "Custom",
        description: String = "User-defined UE4 parameter",
        dataType: ConfigDataType = ConfigDataType.INTEGER
    ) {
        val newKey = ConfigKeyItem(
            id = "custom_${key.lowercase()}_${System.currentTimeMillis()}",
            key = key,
            currentValue = value,
            proposedValue = value,
            dataType = dataType,
            category = category,
            description = description,
            allowedRange = "Custom",
            isKnownSafeKey = true,
            isModified = true
        )
        val existingIndex = _workspace.value.keys.indexOfFirst { it.key == key }
        val newKeys = if (existingIndex != -1) {
            _workspace.value.keys.mapIndexed { idx, item ->
                if (idx == existingIndex) item.copy(proposedValue = value, isModified = true) else item
            }
        } else {
            _workspace.value.keys + newKey
        }
        _workspace.value = _workspace.value.copy(keys = newKeys)
    }

    fun deleteKey(keyId: String) {
        val newKeys = _workspace.value.keys.filterNot { it.id == keyId }
        _workspace.value = _workspace.value.copy(keys = newKeys)
    }

    fun applyQuickPreset(presetName: String) {
        when (presetName) {
            "120 FPS Ultra" -> {
                updateProposedValueByKey("BattleFPS", "120")
                updateProposedValueByKey("FPSLevel", "5")
                updateProposedValueByKey("LobbyFPS", "60")
                val gui = _workspace.value.guiSettings.copy(selectedFrameRatePreset = "Ultra Extreme", manualFps = 120)
                _workspace.value = _workspace.value.copy(guiSettings = gui)
            }
            "90 FPS Extreme+" -> {
                updateProposedValueByKey("BattleFPS", "90")
                updateProposedValueByKey("FPSLevel", "4")
                updateProposedValueByKey("LobbyFPS", "60")
                val gui = _workspace.value.guiSettings.copy(selectedFrameRatePreset = "Extreme+", manualFps = 90)
                _workspace.value = _workspace.value.copy(guiSettings = gui)
            }
            "Potato Low Lag" -> {
                updateProposedValueByKey("ArtQuality", "0")
                updateProposedValueByKey("ShadowQuality", "0")
                updateProposedValueByKey("ResolutionQuality", "65")
                val gui = _workspace.value.guiSettings.copy(selectedGraphicsPreset = "Super Smooth")
                _workspace.value = _workspace.value.copy(guiSettings = gui)
            }
            "Aim & Scope Fast Switch" -> {
                updateProposedValueByKey("CameraLensSensibility", "125")
                updateProposedValueByKey("Gyroscope", "1")
                addCustomKey("ScopeAnimScale", "0.5", "Aim", "Reduced scope animation transition delay", ConfigDataType.FLOAT)
                addCustomKey("AimAssistLevel", "2", "Aim", "Maximum supported in-engine aim assist tracking", ConfigDataType.INTEGER)
            }
        }
    }

    fun validateConfiguration(): ConfigValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        for (item in _workspace.value.keys) {
            if (!safeKeyAllowlist.contains(item.key)) {
                warnings.add("Custom key '${item.key}' staged.")
            }
        }
        return ConfigValidationResult(
            isValid = true,
            errors = errors,
            warnings = warnings
        )
    }

    fun getDiffList(): List<ConfigDiffEntry> {
        return _workspace.value.keys
            .filter { it.isModified }
            .map {
                ConfigDiffEntry(
                    key = it.key,
                    originalValue = it.currentValue,
                    proposedValue = it.proposedValue,
                    category = it.category
                )
            }
    }

    suspend fun applyChangesTransactionally(gamePackage: String): Boolean {
        // Create backup in Room
        val backupId = "backup_${System.currentTimeMillis()}"
        val originalSummary = _workspace.value.keys.joinToString(";") { "${it.key}=${it.currentValue}" }
        val modifiedSummary = _workspace.value.keys.joinToString(";") { "${it.key}=${it.proposedValue}" }
        configFileBackupDao.saveBackup(
            ConfigFileBackupEntity(
                id = backupId,
                fileName = _workspace.value.sourceType.displayName,
                gamePackage = gamePackage,
                originalContent = originalSummary,
                modifiedContent = modifiedSummary
            )
        )

        val committedKeys = _workspace.value.keys.map { item ->
            item.copy(
                currentValue = item.proposedValue,
                isModified = false
            )
        }
        _workspace.value = _workspace.value.copy(
            keys = committedKeys,
            lastModifiedTimestamp = System.currentTimeMillis(),
            accessStatus = "Configuration Applied & Backed Up in Local Storage"
        )
        return true
    }

    fun resetToDefaults() {
        val resetKeys = _workspace.value.keys.map { item ->
            item.copy(
                proposedValue = item.currentValue,
                isModified = false
            )
        }
        _workspace.value = _workspace.value.copy(keys = resetKeys)
    }

    fun exportToJson(): String {
        val entries = _workspace.value.keys.joinToString(",\n    ") {
            "\"${it.key}\": \"${it.proposedValue}\""
        }
        return "{\n    $entries\n}"
    }
}
