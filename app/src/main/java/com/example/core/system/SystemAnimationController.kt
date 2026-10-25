package com.example.core.system

import android.content.Context
import android.provider.Settings
import com.example.data.local.dao.AuditLogDao
import com.example.data.local.entity.AuditLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

data class AnimationScaleSnapshot(
    val windowScale: Float = 1.0f,
    val transitionScale: Float = 1.0f,
    val animatorDurationScale: Float = 1.0f,
    val timestamp: Long = System.currentTimeMillis()
)

data class AnimationControllerState(
    val currentWindowScale: Float = 1.0f,
    val currentTransitionScale: Float = 1.0f,
    val currentAnimatorScale: Float = 1.0f,
    val savedSnapshot: AnimationScaleSnapshot? = null,
    val isOptimized: Boolean = false,
    val lastActionStatus: String = "Normal System Scale (1.0x)"
)

class SystemAnimationController(
    private val context: Context,
    private val auditLogDao: AuditLogDao
) {

    private val _state = MutableStateFlow(AnimationControllerState())
    val state: StateFlow<AnimationControllerState> = _state.asStateFlow()

    init {
        readCurrentScales()
    }

    fun readCurrentScales() {
        try {
            val resolver = context.contentResolver
            val window = Settings.Global.getFloat(resolver, Settings.Global.WINDOW_ANIMATION_SCALE, 1.0f)
            val transition = Settings.Global.getFloat(resolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1.0f)
            val animator = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)

            _state.value = _state.value.copy(
                currentWindowScale = window,
                currentTransitionScale = transition,
                currentAnimatorScale = animator,
                isOptimized = (window <= 0.5f && transition <= 0.5f && animator <= 0.5f)
            )
        } catch (e: Exception) {
            // Read fallback
        }
    }

    suspend fun applyAnimationScalePreset(targetScale: Float, isShizukuAvailable: Boolean): Boolean = withContext(Dispatchers.IO) {
        readCurrentScales()

        // Capture snapshot before modifying if not already saved
        if (_state.value.savedSnapshot == null) {
            _state.value = _state.value.copy(
                savedSnapshot = AnimationScaleSnapshot(
                    windowScale = _state.value.currentWindowScale,
                    transitionScale = _state.value.currentTransitionScale,
                    animatorDurationScale = _state.value.currentAnimatorScale
                )
            )
        }

        val scaleStr = String.format(java.util.Locale.US, "%.1f", targetScale)

        val commands = listOf(
            "settings put global window_animation_scale $scaleStr",
            "settings put global transition_animation_scale $scaleStr",
            "settings put global animator_duration_scale $scaleStr"
        )

        var successCount = 0
        for (cmd in commands) {
            val result = executeCommand(cmd, isShizukuAvailable)
            if (result) successCount++
        }

        val allSucceeded = successCount == commands.size
        readCurrentScales()

        val statusText = if (allSucceeded) {
            "Applied Animation Scale: ${scaleStr}x (Window, Transition, Animator)"
        } else {
            "Requires ADB/Shizuku permission (WRITE_SECURE_SETTINGS) to modify system animation scales."
        }

        _state.value = _state.value.copy(
            lastActionStatus = statusText
        )

        auditLogDao.insertLog(
            AuditLogEntity(
                commandTitle = "Set Animation Scale",
                command = "settings put global animation_scale ${scaleStr}x",
                executionResult = "Target: ${scaleStr}x. Succeeded: $allSucceeded. Shizuku: $isShizukuAvailable",
                exitCode = if (allSucceeded) 0 else 1,
                isRolledBack = false
            )
        )

        allSucceeded
    }

    suspend fun restoreDefaultScales(isShizukuAvailable: Boolean): Boolean = withContext(Dispatchers.IO) {
        val snapshot = _state.value.savedSnapshot ?: AnimationScaleSnapshot(1.0f, 1.0f, 1.0f)

        val commands = listOf(
            "settings put global window_animation_scale ${String.format(java.util.Locale.US, "%.1f", snapshot.windowScale)}",
            "settings put global transition_animation_scale ${String.format(java.util.Locale.US, "%.1f", snapshot.transitionScale)}",
            "settings put global animator_duration_scale ${String.format(java.util.Locale.US, "%.1f", snapshot.animatorDurationScale)}"
        )

        var success = true
        for (cmd in commands) {
            if (!executeCommand(cmd, isShizukuAvailable)) {
                success = false
            }
        }

        readCurrentScales()
        _state.value = _state.value.copy(
            lastActionStatus = if (success) "Restored animation scales to baseline." else "Failed to restore (requires Shizuku/ADB)"
        )

        auditLogDao.insertLog(
            AuditLogEntity(
                commandTitle = "Restore Animation Scale",
                command = "settings put global animation_scale 1.0x",
                executionResult = "Restored to baseline: window=${snapshot.windowScale}, trans=${snapshot.transitionScale}, anim=${snapshot.animatorDurationScale}",
                exitCode = if (success) 0 else 1,
                isRolledBack = true
            )
        )

        success
    }

    private fun executeCommand(command: String, isShizukuAvailable: Boolean): Boolean {
        return try {
            // Execute via Runtime if permission exists or root, else return false
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }
}
