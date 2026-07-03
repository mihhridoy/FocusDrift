package com.radonshadow.focusdrift.ui.screens.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.data.local.preferences.TimerPreferences
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.core.constants.TimerConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val timerPreferences: TimerPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun toggleDriftTime(id: String) {
        _uiState.update {
            val updated = it.selectedDriftTimes.toMutableSet()
            if (!updated.remove(id)) updated.add(id)
            it.copy(selectedDriftTimes = updated)
        }
    }

    fun toggleFocusKiller(id: String) {
        _uiState.update {
            val updated = it.selectedFocusKillers.toMutableSet()
            if (!updated.remove(id)) updated.add(id)
            it.copy(selectedFocusKillers = updated)
        }
    }

    fun setFocusMinutes(minutes: Int) {
        _uiState.update { it.copy(focusMinutes = minutes.coerceIn(TimerConstants.MIN_FOCUS_MINUTES, TimerConstants.MAX_FOCUS_MINUTES)) }
    }

    fun finishSetup(onDone: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                userPreferences.setAdhdSetup(
                    driftTimes = state.selectedDriftTimes,
                    focusKillers = state.selectedFocusKillers,
                    mainGoal = ""
                )
                timerPreferences.setFocusMinutes(state.focusMinutes)
                onDone()
            } catch (t: Throwable) {
                Log.e("SetupViewModel", "finishSetup failed", t)
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = "Couldn't continue: ${t.message ?: t::class.simpleName}")
                }
            }
        }
    }
}
