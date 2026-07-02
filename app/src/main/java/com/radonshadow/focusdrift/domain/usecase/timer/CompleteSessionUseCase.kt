package com.radonshadow.focusdrift.domain.usecase.timer

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.core.constants.RewardConstants
import com.radonshadow.focusdrift.core.extensions.toMinutes
import com.radonshadow.focusdrift.domain.model.FocusSession
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusSessionRepository
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import javax.inject.Inject

data class SessionCompletionResult(
    val xpEarned: Int,
    val coinsEarned: Int,
    val xpBefore: Int,
    val xpAfter: Int,
    val currentStreak: Int,
    val dailyGoalHit: Boolean
)

/**
 * Single source of truth for XP + coin awards. Every path that finishes a session — the timer
 * service ticking down to zero, or the user tapping "done early" — must go through this use case
 * so rewards are never granted twice or from two different places.
 */
class CompleteSessionUseCase @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(
        sessionType: SessionType,
        plannedDurationMs: Long,
        actualDurationMs: Long,
        task: String,
        userId: String = AppConstants.DEFAULT_USER_ID
    ): SessionCompletionResult {
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
                wasAbandoned = false
            )
        )

        if (sessionType != SessionType.FOCUS) {
            val progress = userProgressRepository.getUserProgress(userId)
            return SessionCompletionResult(0, 0, progress.totalXp, progress.totalXp, progress.currentStreak, false)
        }

        val progressBefore = userProgressRepository.getUserProgress(userId)
        val xpReward = userProgressRepository.awardXp(userId, RewardConstants.XP_PER_FOCUS_SESSION)
        userProgressRepository.awardCoins(userId, RewardConstants.COINS_PER_SESSION)
        userProgressRepository.recordSessionCompleted(userId, actualDurationMs.toMinutes())

        var totalXp = RewardConstants.XP_PER_FOCUS_SESSION
        var totalCoins = RewardConstants.COINS_PER_SESSION

        val sessionsToday = progressBefore.sessionsToday + 1
        val dailyGoalHit = sessionsToday == progressBefore.dailyGoalSessions
        if (dailyGoalHit) {
            userProgressRepository.awardXp(userId, RewardConstants.XP_DAILY_GOAL_BONUS)
            userProgressRepository.awardCoins(userId, RewardConstants.COINS_DAILY_GOAL_BONUS)
            userProgressRepository.incrementStreak(userId)
            totalXp += RewardConstants.XP_DAILY_GOAL_BONUS
            totalCoins += RewardConstants.COINS_DAILY_GOAL_BONUS
        }

        val progressAfter = userProgressRepository.getUserProgress(userId)

        if (progressAfter.currentStreak > 0 && progressAfter.currentStreak % 7 == 0 && dailyGoalHit) {
            userProgressRepository.awardXp(userId, RewardConstants.XP_STREAK_7_DAY_BONUS)
            userProgressRepository.awardCoins(userId, RewardConstants.COINS_STREAK_7_DAY_BONUS)
            totalXp += RewardConstants.XP_STREAK_7_DAY_BONUS
            totalCoins += RewardConstants.COINS_STREAK_7_DAY_BONUS
        }

        return SessionCompletionResult(
            xpEarned = totalXp,
            coinsEarned = totalCoins,
            xpBefore = xpReward.xpBefore,
            xpAfter = xpReward.xpAfter,
            currentStreak = progressAfter.currentStreak,
            dailyGoalHit = dailyGoalHit
        )
    }
}
