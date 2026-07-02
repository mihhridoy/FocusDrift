package com.radonshadow.focusdrift.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    suspend fun resolveStartDestination(): Screen {
        val onboarded = userPreferences.isOnboardingComplete.first()
        if (!onboarded) return Screen.Onboarding
        val setupDone = userPreferences.isAdhdSetupComplete.first()
        return if (!setupDone) Screen.AdhdSetup else Screen.Home
    }
}
