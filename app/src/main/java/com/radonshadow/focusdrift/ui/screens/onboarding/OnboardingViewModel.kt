package com.radonshadow.focusdrift.ui.screens.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                userPreferences.setOnboardingComplete(true)
                onDone()
            } catch (t: Throwable) {
                Log.e("OnboardingViewModel", "completeOnboarding failed", t)
                _errorMessage.value = "Couldn't continue: ${t.message ?: t::class.simpleName}"
            }
        }
    }
}
