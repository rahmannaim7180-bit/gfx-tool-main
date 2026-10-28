package com.example.data.preferences

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("frameforge_user_prefs", Context.MODE_PRIVATE)

    private val _hasSeenOnboarding = MutableStateFlow(prefs.getBoolean("has_seen_onboarding", true))
    val hasSeenOnboarding: Flow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _selectedGameId = MutableStateFlow(prefs.getString("selected_game_id", "pubg_global") ?: "pubg_global")
    val selectedGameId: Flow<String> = _selectedGameId.asStateFlow()

    suspend fun setHasSeenOnboarding(seen: Boolean) {
        prefs.edit().putBoolean("has_seen_onboarding", seen).apply()
        _hasSeenOnboarding.value = seen
    }

    suspend fun setSelectedGameId(gameId: String) {
        prefs.edit().putString("selected_game_id", gameId).apply()
        _selectedGameId.value = gameId
    }
}
