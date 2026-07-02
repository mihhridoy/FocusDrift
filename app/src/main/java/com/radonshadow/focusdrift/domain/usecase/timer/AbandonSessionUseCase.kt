package com.radonshadow.focusdrift.domain.usecase.timer

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.model.FocusSession
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusSessionRepository
import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import javax.inject.Inject

class AbandonSessionUseCase @Inject constructor(
    private val timerController: FocusTimerController,
    private val focusSessionRepository: FocusSessionRepository
) {
    suspend operator fun invoke(
        sessionType: SessionType,
        plannedDurationMs: Long,
        actualDurationMs: Long,
        task: String,
        userId: String = AppConstants.DEFAULT_USER_ID
    ) {
        timerController.abandon()
        val now = System.currentTimeMillis()
        focusSessionRepository.insertSession(
            FocusSession(
                userId = userId,
                sessionType = sessionType,
                plannedDurationMs = plannedDurationMs,
                actualDurationMs = actualDurationMs,
                task = task,
                startedAt = now - actualDurationMs,
                completedAt = now,
                wasAbandoned = true
            )
        )
    }
}
