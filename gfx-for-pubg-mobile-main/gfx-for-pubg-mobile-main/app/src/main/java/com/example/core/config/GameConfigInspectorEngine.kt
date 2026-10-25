package com.example.core.config

import com.example.data.local.dao.ConfigFileBackupDao
import com.example.data.local.entity.ConfigFileBackupEntity
import com.example.data.model.ConfigFileWorkspace
import com.example.data.model.ConfigKeyItem
import com.example.data.model.ConfigSourceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

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

    // Strict allowlist of safe configuration keys
    private val safeKeyAllowlist = setOf(
        "FPSLevel",
        "BattlegroundMaxFPS",
        "ResolutionQuality",
        "ShadowQuality",
        "AntiAliasingQuality",
        "TextureQuality",
        "EffectsQuality",
        "PostProcessQuality",
        "ViewDistanceQuality",
        "AudioQualityLevel",
        "bEnableDynamicResolution"
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
                com.example.data.model.ConfigDataType.INTEGER -> {
                    val intVal = item.proposedValue.toIntOrNull()
                    if (intVal == null) {
                        errors.add("${item.key} must be a valid integer.")
                    } else if (item.key == "FPSLevel" && intVal !in 1..6) {
                        errors.add("FPSLevel must be between 1 and 6.")
                    } else if (item.key == "BattlegroundMaxFPS" && intVal !in 30..144) {
                        warnings.add("Max FPS target ${intVal} may exceed official game server caps.")
                    }
                }
                com.example.data.model.ConfigDataType.FLOAT -> {
                    val floatVal = item.proposedValue.toFloatOrNull()
                    if (floatVal == null) {
                        errors.add("${item.key} must be a valid decimal number.")
                    } else if (item.key == "ResolutionQuality" && floatVal !in 50f..100f) {
                        errors.add("ResolutionQuality must be between 50.0 and 100.0%.")
                    }
                }
                com.example.data.model.ConfigDataType.BOOLEAN -> {
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

        // Create backup record in Room
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

        // Commit proposed values into current values
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
