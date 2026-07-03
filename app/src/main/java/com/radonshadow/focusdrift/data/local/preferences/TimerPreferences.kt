package com.radonshadow.focusdrift.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.radonshadow.focusdrift.core.constants.TimerConstants
import com.radonshadow.focusdrift.di.TimerPrefsDataStore
import com.radonshadow.focusdrift.domain.model.FocusSound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TimerPreferences @Inject constructor(
    @TimerPrefsDataStore private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val FOCUS_MINUTES = intPreferencesKey("focus_minutes")
        val DAILY_GOAL_SESSIONS = intPreferencesKey("daily_goal_sessions")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val FOCUS_SOUND_ID = stringPreferencesKey("focus_sound_id")
        val FOCUS_SOUND_VOLUME = floatPreferencesKey("focus_sound_volume")
    }

    val focusMinutes: Flow<Int> =
        dataStore.data.map { it[Keys.FOCUS_MINUTES] ?: TimerConstants.ONBOARDING_DEFAULT_FOCUS_MINUTES }

    val dailyGoalSessions: Flow<Int> =
        dataStore.data.map { it[Keys.DAILY_GOAL_SESSIONS] ?: TimerConstants.DEFAULT_DAILY_SESSION_GOAL }

    val soundEnabled: Flow<Boolean> =
        dataStore.data.map { it[Keys.SOUND_ENABLED] ?: true }

    val hapticsEnabled: Flow<Boolean> =
        dataStore.data.map { it[Keys.HAPTICS_ENABLED] ?: true }

    val selectedFocusSoundId: Flow<String> =
        dataStore.data.map { it[Keys.FOCUS_SOUND_ID] ?: FocusSound.NONE.id }

    val focusSoundVolume: Flow<Float> =
        dataStore.data.map { it[Keys.FOCUS_SOUND_VOLUME] ?: 0.5f }

    suspend fun setFocusMinutes(minutes: Int) {
        dataStore.edit { it[Keys.FOCUS_MINUTES] = minutes.coerceIn(TimerConstants.MIN_FOCUS_MINUTES, TimerConstants.MAX_FOCUS_MINUTES) }
    }

    suspend fun setDailyGoalSessions(sessions: Int) {
        dataStore.edit { it[Keys.DAILY_GOAL_SESSIONS] = sessions.coerceAtLeast(1) }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setSelectedFocusSound(id: String) {
        dataStore.edit { it[Keys.FOCUS_SOUND_ID] = id }
    }

    suspend fun setFocusSoundVolume(volume: Float) {
        dataStore.edit { it[Keys.FOCUS_SOUND_VOLUME] = volume.coerceIn(0f, 1f) }
    }
}
