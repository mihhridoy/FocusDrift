package com.radonshadow.focusdrift.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.setOnboardingComplete(true)
            onDone()
        }
    }
}
