package com.example.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "frameforge_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_GAME_ID = stringPreferencesKey("selected_game_id")
        val PERFORMANCE_MODE = stringPreferencesKey("performance_mode")
        val RAW_PERFORMANCE_OPT_IN = booleanPreferencesKey("raw_performance_opt_in")
        val EXTERNAL_COOLER_ACTIVE = booleanPreferencesKey("external_cooler_active")
        val GYROBOOST_ENABLED = booleanPreferencesKey("gyroboost_enabled")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    val selectedGameId: Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.SELECTED_GAME_ID] ?: "pubg_global"
    }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.HAS_SEEN_ONBOARDING] ?: false
    }

    suspend fun setSelectedGameId(gameId: String) {
        context.dataStore.edit { it[PreferencesKeys.SELECTED_GAME_ID] = gameId }
    }

    suspend fun setHasSeenOnboarding(seen: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAS_SEEN_ONBOARDING] = seen }
    }
}
