package com.example.data.preferences

import android.content.Context
import com.example.data.model.GyroMode
import com.example.data.model.PerformanceMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

data class UserSettingsState(
    val schemaVersion: Int = 2,
    val selectedGamePackage: String = "com.tencent.ig",
    val selectedGraphicsPreset: String = "Super Smooth",
    val preferredFps: Int = 120,
    val preferredRefreshRateHz: Int = 120,
    val performanceMode: PerformanceMode = PerformanceMode.RAW_PERFORMANCE,
    val isExternalCoolerActive: Boolean = false,
    val gyroMode: GyroMode = GyroMode.LIGHT_FILTER,
    val gyroXSensitivityPercent: Int = 100,
    val gyroYSensitivityPercent: Int = 100,
    val antiDriftFilterEnabled: Boolean = true,
    val antiThermalTouchEnabled: Boolean = true,
    val touchSamplingTargetHz: Int = 240,
    val zeroOverheadGamingEnabled: Boolean = true,
    val autoRestoreAfterGameExit: Boolean = true,
    val preferredDisplayResolution: String = "AUTO",
    val shizukuAutoConnect: Boolean = true,
    val selectedEndpointId: String = "cloudflare",
    val animationScaleTarget: Float = 0.5f,
    val hasSeenOnboarding: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("frameforge_user_prefs", Context.MODE_PRIVATE)

    private val _settingsState = MutableStateFlow(loadSettings())
    val settingsState: StateFlow<UserSettingsState> = _settingsState.asStateFlow()

    init {
        migrateIfNeeded()
    }

    private fun migrateIfNeeded() {
        val currentVersion = prefs.getInt(KEY_SCHEMA_VERSION, 1)
        if (currentVersion < CURRENT_SCHEMA_VERSION) {
            prefs.edit().putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION).apply()
            _settingsState.value = loadSettings()
        }
    }

    private fun loadSettings(): UserSettingsState {
        val modeName = prefs.getString(KEY_PERFORMANCE_MODE, PerformanceMode.RAW_PERFORMANCE.name) ?: PerformanceMode.RAW_PERFORMANCE.name
        val perfMode = runCatching { PerformanceMode.valueOf(modeName) }.getOrDefault(PerformanceMode.RAW_PERFORMANCE)

        val gyroModeName = prefs.getString(KEY_GYRO_MODE, GyroMode.LIGHT_FILTER.name) ?: GyroMode.LIGHT_FILTER.name
        val gyroMode = runCatching { GyroMode.valueOf(gyroModeName) }.getOrDefault(GyroMode.LIGHT_FILTER)

        return UserSettingsState(
            schemaVersion = prefs.getInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION),
            selectedGamePackage = prefs.getString(KEY_SELECTED_GAME, "com.tencent.ig") ?: "com.tencent.ig",
            selectedGraphicsPreset = prefs.getString(KEY_GRAPHICS_PRESET, "Super Smooth") ?: "Super Smooth",
            preferredFps = prefs.getInt(KEY_PREFERRED_FPS, 120),
            preferredRefreshRateHz = prefs.getInt(KEY_REFRESH_RATE, 120),
            performanceMode = perfMode,
            isExternalCoolerActive = prefs.getBoolean(KEY_EXTERNAL_COOLER, false),
            gyroMode = gyroMode,
            gyroXSensitivityPercent = prefs.getInt(KEY_GYRO_X_SENS, 100),
            gyroYSensitivityPercent = prefs.getInt(KEY_GYRO_Y_SENS, 100),
            antiDriftFilterEnabled = prefs.getBoolean(KEY_ANTI_DRIFT, true),
            antiThermalTouchEnabled = prefs.getBoolean(KEY_ANTI_THERMAL_TOUCH, true),
            touchSamplingTargetHz = prefs.getInt(KEY_TOUCH_SAMPLING, 240),
            zeroOverheadGamingEnabled = prefs.getBoolean(KEY_ZERO_OVERHEAD, true),
            autoRestoreAfterGameExit = prefs.getBoolean(KEY_AUTO_RESTORE, true),
            preferredDisplayResolution = prefs.getString(KEY_RESOLUTION_PREF, "AUTO") ?: "AUTO",
            shizukuAutoConnect = prefs.getBoolean(KEY_SHIZUKU_AUTO_CONNECT, true),
            selectedEndpointId = prefs.getString(KEY_ENDPOINT_ID, "cloudflare") ?: "cloudflare",
            animationScaleTarget = prefs.getFloat(KEY_ANIMATION_SCALE, 0.5f),
            hasSeenOnboarding = prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, true)
        )
    }

    fun updateSettings(transform: (UserSettingsState) -> UserSettingsState) {
        val newState = transform(_settingsState.value)
        _settingsState.value = newState
        saveToPrefs(newState)
    }

    private fun saveToPrefs(state: UserSettingsState) {
        prefs.edit()
            .putInt(KEY_SCHEMA_VERSION, state.schemaVersion)
            .putString(KEY_SELECTED_GAME, state.selectedGamePackage)
            .putString(KEY_GRAPHICS_PRESET, state.selectedGraphicsPreset)
            .putInt(KEY_PREFERRED_FPS, state.preferredFps)
            .putInt(KEY_REFRESH_RATE, state.preferredRefreshRateHz)
            .putString(KEY_PERFORMANCE_MODE, state.performanceMode.name)
            .putBoolean(KEY_EXTERNAL_COOLER, state.isExternalCoolerActive)
            .putString(KEY_GYRO_MODE, state.gyroMode.name)
            .putInt(KEY_GYRO_X_SENS, state.gyroXSensitivityPercent)
            .putInt(KEY_GYRO_Y_SENS, state.gyroYSensitivityPercent)
            .putBoolean(KEY_ANTI_DRIFT, state.antiDriftFilterEnabled)
            .putBoolean(KEY_ANTI_THERMAL_TOUCH, state.antiThermalTouchEnabled)
            .putInt(KEY_TOUCH_SAMPLING, state.touchSamplingTargetHz)
            .putBoolean(KEY_ZERO_OVERHEAD, state.zeroOverheadGamingEnabled)
            .putBoolean(KEY_AUTO_RESTORE, state.autoRestoreAfterGameExit)
            .putString(KEY_RESOLUTION_PREF, state.preferredDisplayResolution)
            .putBoolean(KEY_SHIZUKU_AUTO_CONNECT, state.shizukuAutoConnect)
            .putString(KEY_ENDPOINT_ID, state.selectedEndpointId)
            .putFloat(KEY_ANIMATION_SCALE, state.animationScaleTarget)
            .putBoolean(KEY_HAS_SEEN_ONBOARDING, state.hasSeenOnboarding)
            .apply()
    }

    fun exportSettingsJson(): String {
        val s = _settingsState.value
        val json = JSONObject()
        json.put("schemaVersion", s.schemaVersion)
        json.put("deviceModel", "iQOO Neo 9 (PD2338C)")
        json.put("selectedGamePackage", s.selectedGamePackage)
        json.put("selectedGraphicsPreset", s.selectedGraphicsPreset)
        json.put("preferredFps", s.preferredFps)
        json.put("preferredRefreshRateHz", s.preferredRefreshRateHz)
        json.put("performanceMode", s.performanceMode.name)
        json.put("isExternalCoolerActive", s.isExternalCoolerActive)
        json.put("gyroMode", s.gyroMode.name)
        json.put("gyroXSensitivityPercent", s.gyroXSensitivityPercent)
        json.put("gyroYSensitivityPercent", s.gyroYSensitivityPercent)
        json.put("antiDriftFilterEnabled", s.antiDriftFilterEnabled)
        json.put("antiThermalTouchEnabled", s.antiThermalTouchEnabled)
        json.put("touchSamplingTargetHz", s.touchSamplingTargetHz)
        json.put("zeroOverheadGamingEnabled", s.zeroOverheadGamingEnabled)
        json.put("autoRestoreAfterGameExit", s.autoRestoreAfterGameExit)
        json.put("preferredDisplayResolution", s.preferredDisplayResolution)
        json.put("shizukuAutoConnect", s.shizukuAutoConnect)
        json.put("selectedEndpointId", s.selectedEndpointId)
        json.put("animationScaleTarget", s.animationScaleTarget.toDouble())
        return json.toString(2)
    }

    fun importSettingsJson(jsonStr: String): Boolean {
        return runCatching {
            val json = JSONObject(jsonStr)
            val perfName = json.optString("performanceMode", PerformanceMode.RAW_PERFORMANCE.name)
            val perfMode = runCatching { PerformanceMode.valueOf(perfName) }.getOrDefault(PerformanceMode.RAW_PERFORMANCE)

            val gyroName = json.optString("gyroMode", GyroMode.LIGHT_FILTER.name)
            val gyroMode = runCatching { GyroMode.valueOf(gyroName) }.getOrDefault(GyroMode.LIGHT_FILTER)

            val imported = UserSettingsState(
                schemaVersion = CURRENT_SCHEMA_VERSION,
                selectedGamePackage = json.optString("selectedGamePackage", "com.tencent.ig"),
                selectedGraphicsPreset = json.optString("selectedGraphicsPreset", "Super Smooth"),
                preferredFps = json.optInt("preferredFps", 120),
                preferredRefreshRateHz = json.optInt("preferredRefreshRateHz", 120),
                performanceMode = perfMode,
                isExternalCoolerActive = json.optBoolean("isExternalCoolerActive", false),
                gyroMode = gyroMode,
                gyroXSensitivityPercent = json.optInt("gyroXSensitivityPercent", 100),
                gyroYSensitivityPercent = json.optInt("gyroYSensitivityPercent", 100),
                antiDriftFilterEnabled = json.optBoolean("antiDriftFilterEnabled", true),
                antiThermalTouchEnabled = json.optBoolean("antiThermalTouchEnabled", true),
                touchSamplingTargetHz = json.optInt("touchSamplingTargetHz", 240),
                zeroOverheadGamingEnabled = json.optBoolean("zeroOverheadGamingEnabled", true),
                autoRestoreAfterGameExit = json.optBoolean("autoRestoreAfterGameExit", true),
                preferredDisplayResolution = json.optString("preferredDisplayResolution", "AUTO"),
                shizukuAutoConnect = json.optBoolean("shizukuAutoConnect", true),
                selectedEndpointId = json.optString("selectedEndpointId", "cloudflare"),
                animationScaleTarget = json.optDouble("animationScaleTarget", 0.5).toFloat(),
                hasSeenOnboarding = true
            )
            _settingsState.value = imported
            saveToPrefs(imported)
            true
        }.getOrDefault(false)
    }

    fun resetToDefaults() {
        val defaultState = UserSettingsState()
        _settingsState.value = defaultState
        saveToPrefs(defaultState)
    }

    companion object {
        const val CURRENT_SCHEMA_VERSION = 2

        private const val KEY_SCHEMA_VERSION = "schema_version"
        private const val KEY_SELECTED_GAME = "selected_game_package"
        private const val KEY_GRAPHICS_PRESET = "selected_graphics_preset"
        private const val KEY_PREFERRED_FPS = "preferred_fps"
        private const val KEY_REFRESH_RATE = "preferred_refresh_rate_hz"
        private const val KEY_PERFORMANCE_MODE = "performance_mode"
        private const val KEY_EXTERNAL_COOLER = "is_external_cooler_active"
        private const val KEY_GYRO_MODE = "gyro_mode"
        private const val KEY_GYRO_X_SENS = "gyro_x_sens"
        private const val KEY_GYRO_Y_SENS = "gyro_y_sens"
        private const val KEY_ANTI_DRIFT = "anti_drift_filter_enabled"
        private const val KEY_ANTI_THERMAL_TOUCH = "anti_thermal_touch_enabled"
        private const val KEY_TOUCH_SAMPLING = "touch_sampling_target_hz"
        private const val KEY_ZERO_OVERHEAD = "zero_overhead_gaming_enabled"
        private const val KEY_AUTO_RESTORE = "auto_restore_after_game_exit"
        private const val KEY_RESOLUTION_PREF = "preferred_display_resolution"
        private const val KEY_SHIZUKU_AUTO_CONNECT = "shizuku_auto_connect"
        private const val KEY_ENDPOINT_ID = "selected_endpoint_id"
        private const val KEY_ANIMATION_SCALE = "animation_scale_target"
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
    }
}
