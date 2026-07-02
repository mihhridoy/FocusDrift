package com.radonshadow.focusdrift.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.constants.TimerConstants
import com.radonshadow.focusdrift.core.extensions.minutesToMillis
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.data.local.preferences.TimerPreferences
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import com.radonshadow.focusdrift.domain.usecase.timer.AbandonSessionUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.PauseSessionUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.ResumeSessionUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.StartFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerController: FocusTimerController,
    private val startFocusSessionUseCase: StartFocusSessionUseCase,
    private val pauseSessionUseCase: PauseSessionUseCase,
    private val resumeSessionUseCase: ResumeSessionUseCase,
    private val abandonSessionUseCase: AbandonSessionUseCase,
    private val timerPreferences: TimerPreferences,
    userPreferences: UserPreferences
) : ViewModel() {

    private var sessionNumber = 1
    private val taskState = MutableStateFlow("")

    val uiState = combine(
        timerController.sessionState,
        timerPreferences.focusMinutes,
        timerPreferences.dailyGoalSessions,
        userPreferences.selectedOrbSkinId,
        taskState
    ) { sessionState, focusMinutes, dailyGoal, orbSkinId, task ->
        TimerUiState(
            sessionState = sessionState,
            task = task,
            focusMinutes = focusMinutes,
            dailyGoalSessions = dailyGoal,
            nextSessionNumber = sessionNumber,
            selectedOrbSkinId = orbSkinId
        )
    }.stateInViewModel(viewModelScope, TimerUiState())

    fun updateTask(task: String) {
        taskState.value = task
    }

    fun startFocusSession() {
        val state = uiState.value
        startFocusSessionUseCase(
            durationMs = state.focusMinutes.minutesToMillis(),
            sessionType = SessionType.FOCUS,
            task = state.task,
            sessionNumber = sessionNumber,
            dailyGoalSessions = state.dailyGoalSessions
        )
    }

    fun startBreak(afterSessionNumber: Int) {
        val isLongBreak = afterSessionNumber % TimerConstants.SESSIONS_BEFORE_LONG_BREAK == 0
        val breakType = if (isLongBreak) SessionType.LONG_BREAK else SessionType.SHORT_BREAK
        startFocusSessionUseCase(
            durationMs = breakType.defaultDurationMs,
            sessionType = breakType,
            task = "",
            sessionNumber = afterSessionNumber
        )
    }

    fun keepGoing() {
        sessionNumber += 1
        startFocusSession()
    }

    fun pause() = pauseSessionUseCase()

    fun resume() = resumeSessionUseCase()

    fun markDrifting() = timerController.markDrifting()

    fun resumeFromDrift() = timerController.resumeFromDrift()

    fun completeEarly() = timerController.completeEarly()

    fun abandon() {
        val state = uiState.value.sessionState
        val (type, planned, elapsed) = when (state) {
            is SessionState.Running -> Triple(state.sessionType, state.totalMs, state.totalMs - state.remainingMs)
            is SessionState.Paused -> Triple(state.sessionType, state.totalMs, state.totalMs - state.remainingMs)
            is SessionState.Drifting -> Triple(state.sessionType, state.totalMs, state.totalMs - state.remainingMs)
            else -> return
        }
        viewModelScope.launch {
            abandonSessionUseCase(type, planned, elapsed, uiState.value.task)
        }
    }
}
