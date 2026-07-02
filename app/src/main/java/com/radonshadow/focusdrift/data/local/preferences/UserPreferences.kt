package com.radonshadow.focusdrift.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.radonshadow.focusdrift.di.UserPrefsDataStore
import com.radonshadow.focusdrift.domain.model.OrbSkin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    @UserPrefsDataStore private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val ADHD_SETUP_COMPLETE = booleanPreferencesKey("adhd_setup_complete")
        val DRIFT_TIMES = stringSetPreferencesKey("drift_times")
        val FOCUS_KILLERS = stringSetPreferencesKey("focus_killers")
        val MAIN_GOAL = stringPreferencesKey("main_goal")
        val SELECTED_ORB_SKIN = stringPreferencesKey("selected_orb_skin")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val AVATAR_COLOR = stringPreferencesKey("avatar_color")
    }

    val isOnboardingComplete: Flow<Boolean> =
        dataStore.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    val isAdhdSetupComplete: Flow<Boolean> =
        dataStore.data.map { it[Keys.ADHD_SETUP_COMPLETE] ?: false }

    val selectedOrbSkinId: Flow<String> =
        dataStore.data.map { it[Keys.SELECTED_ORB_SKIN] ?: OrbSkin.DEFAULT.id }

    val displayName: Flow<String> =
        dataStore.data.map { it[Keys.DISPLAY_NAME] ?: "You" }

    val avatarColor: Flow<String> =
        dataStore.data.map { it[Keys.AVATAR_COLOR] ?: "#7C6FE0" }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setAdhdSetup(driftTimes: Set<String>, focusKillers: Set<String>, mainGoal: String) {
        dataStore.edit {
            it[Keys.DRIFT_TIMES] = driftTimes
            it[Keys.FOCUS_KILLERS] = focusKillers
            it[Keys.MAIN_GOAL] = mainGoal
            it[Keys.ADHD_SETUP_COMPLETE] = true
        }
    }

    suspend fun setSelectedOrbSkin(itemId: String) {
        dataStore.edit { it[Keys.SELECTED_ORB_SKIN] = itemId }
    }

    suspend fun setDisplayName(name: String) {
        dataStore.edit { it[Keys.DISPLAY_NAME] = name }
    }
}
