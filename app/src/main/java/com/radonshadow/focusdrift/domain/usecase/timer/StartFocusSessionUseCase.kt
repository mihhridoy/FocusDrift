package com.radonshadow.focusdrift.domain.usecase.timer

import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(
    private val timerController: FocusTimerController
) {
    operator fun invoke(
        durationMs: Long,
        sessionType: SessionType = SessionType.FOCUS,
        task: String = "",
        sessionNumber: Int = 1,
        dailyGoalSessions: Int = 5
    ) {
        timerController.start(durationMs, sessionType, task, sessionNumber, dailyGoalSessions)
    }
}
