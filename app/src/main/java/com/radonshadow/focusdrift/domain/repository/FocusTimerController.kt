package com.radonshadow.focusdrift.domain.repository

import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.domain.model.SessionType
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over the running [com.radonshadow.focusdrift.service.FocusTimerService]. The timer
 * must keep counting in a foreground service (a ViewModel-scoped coroutine gets killed by the OS
 * once the app backgrounds), so use cases talk to the service through this controller instead of
 * holding their own countdown state.
 */
interface FocusTimerController {
    val sessionState: StateFlow<SessionState>

    fun start(durationMs: Long, sessionType: SessionType, task: String, sessionNumber: Int, dailyGoalSessions: Int)
    fun pause()
    fun resume()
    fun markDrifting()
    fun resumeFromDrift()
    fun completeEarly()
    fun abandon()
}
