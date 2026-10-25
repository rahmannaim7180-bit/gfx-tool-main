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
        "BattlegroundMaxFPS"
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
            "Ultra Extreme" -> "6" to "120"
            else -> "6" to "120"
        }
        val gui = _workspace.value.guiSettings.copy(
            selectedFrameRatePreset = preset,
            manualFps = battleVal.toIntOrNull() ?: 120
        )
        updateProposedValueByKey("BattleFPS", battleVal)
        updateProposedValueByKey("BattlegroundMaxFPS", battleVal)
        updateProposedValueByKey("FPSLevel", fpsVal)
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiManualFps(fps: Int) {
        val gui = _workspace.value.guiSettings.copy(manualFps = fps.coerceIn(30, 144))
        updateProposedValueByKey("BattleFPS", fps.toString())
        updateProposedValueByKey("BattlegroundMaxFPS", fps.toString())
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiStyle(style: String) {
        val gui = _workspace.value.guiSettings.copy(selectedStyle = style)
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    fun updateGuiBrightness(brightness: Int) {
        val gui = _workspace.value.guiSettings.copy(brightnessPercent = brightness.coerceIn(80, 150))
        _workspace.value = _workspace.value.copy(guiSettings = gui)
    }

    private fun updateProposedValueByKey(key: String, value: String) {
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

    fun validateConfiguration(): ConfigValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        for (item in _workspace.value.keys) {
            if (!safeKeyAllowlist.contains(item.key)) {
                warnings.add("Key '${item.key}' is outside the standard allowlist.")
            }

            when (item.dataType) {
                ConfigDataType.INTEGER -> {
                    val intVal = item.proposedValue.toIntOrNull()
                    if (intVal == null) {
                        errors.add("${item.key} must be a valid integer.")
                    } else if (item.key == "BattleFPS" && intVal !in 30..144) {
                        errors.add("BattleFPS must be between 30 and 144.")
                    } else if (item.key == "LobbyFPS" && intVal !in 30..60) {
                        errors.add("LobbyFPS should be between 30 and 60 to prevent lobby overheating.")
                    } else if (item.key == "ArtQuality" && intVal !in 0..5) {
                        errors.add("ArtQuality must be between 0 (Super Smooth) and 5 (HDR).")
                    }
                }
                ConfigDataType.FLOAT -> {
                    val floatVal = item.proposedValue.toFloatOrNull()
                    if (floatVal == null) {
                        errors.add("${item.key} must be a valid decimal number.")
                    } else if (item.key == "ResolutionQuality" && floatVal !in 50f..100f) {
                        errors.add("ResolutionQuality must be between 50.0 and 100.0%.")
                    }
                }
                ConfigDataType.BOOLEAN -> {
                    if (item.proposedValue.lowercase() !in listOf("true", "false", "0", "1")) {
                        errors.add("${item.key} must be true or false.")
                    }
                }
                else -> {}
            }
        }

        return ConfigValidationResult(
            isValid = errors.isEmpty(),
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
        val validation = validateConfiguration()
        if (!validation.isValid) return false

        val diff = getDiffList()
        if (diff.isEmpty()) return true

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
            lastModifiedTimestamp = System.currentTimeMillis()
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
