package com.radonshadow.focusdrift.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.core.constants.TimerConstants
import com.radonshadow.focusdrift.core.extensions.minutesToMillis
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.data.local.preferences.TimerPreferences
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import com.radonshadow.focusdrift.domain.usecase.progress.GetUserProgressUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.AbandonSessionUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.PauseSessionUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.ResumeSessionUseCase
import com.radonshadow.focusdrift.domain.usecase.timer.StartFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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
    private val subscriptionRepository: SubscriptionRepository,
    getUserProgressUseCase: GetUserProgressUseCase,
    userPreferences: UserPreferences
) : ViewModel() {

    private var sessionNumber = 1
    private val taskState = MutableStateFlow("")

    private data class TimerSettings(
        val focusMinutes: Int,
        val dailyGoalSessions: Int,
        val focusSoundId: String,
        val focusSoundVolume: Float
    )

    private data class SessionGateInfo(val isPro: Boolean, val sessionsToday: Int)

    private val timerSettings = combine(
        timerPreferences.focusMinutes,
        timerPreferences.dailyGoalSessions,
        timerPreferences.selectedFocusSoundId,
        timerPreferences.focusSoundVolume
    ) { focusMinutes, dailyGoal, soundId, volume -> TimerSettings(focusMinutes, dailyGoal, soundId, volume) }

    private val sessionGateInfo = combine(
        subscriptionRepository.observeSubscriptionStatus(),
        getUserProgressUseCase()
    ) { status, progress -> SessionGateInfo(status.isPro, progress.sessionsToday) }

    val uiState = combine(
        timerController.sessionState,
        timerSettings,
        userPreferences.selectedOrbSkinId,
        taskState,
        sessionGateInfo
    ) { sessionState, settings, orbSkinId, task, gate ->
        TimerUiState(
            sessionState = sessionState,
            task = task,
            focusMinutes = settings.focusMinutes,
            dailyGoalSessions = settings.dailyGoalSessions,
            nextSessionNumber = sessionNumber,
            selectedOrbSkinId = orbSkinId,
            selectedFocusSoundId = settings.focusSoundId,
            focusSoundVolume = settings.focusSoundVolume,
            isPro = gate.isPro,
            sessionsCompletedToday = gate.sessionsToday,
            freeSessionCapReached = !gate.isPro && gate.sessionsToday >= SubscriptionConstants.FREE_SESSIONS_PER_DAY
        )
    }.stateInViewModel(viewModelScope, TimerUiState())

    fun updateTask(task: String) {
        taskState.value = task
    }

    fun selectFocusSound(id: String) {
        viewModelScope.launch { timerPreferences.setSelectedFocusSound(id) }
    }

    fun setFocusMinutes(minutes: Int) {
        viewModelScope.launch { timerPreferences.setFocusMinutes(minutes) }
    }

    fun setFocusSoundVolume(volume: Float) {
        viewModelScope.launch { timerPreferences.setFocusSoundVolume(volume) }
        timerController.setFocusSoundVolume(volume)
    }

    fun startFocusSession() {
        val state = uiState.value
        if (state.freeSessionCapReached) return
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

    fun resetToIdle() = timerController.resetToIdle()

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
