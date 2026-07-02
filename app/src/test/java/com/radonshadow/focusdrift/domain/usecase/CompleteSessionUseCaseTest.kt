package com.radonshadow.focusdrift.domain.usecase

import com.radonshadow.focusdrift.core.constants.RewardConstants
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.model.UserProgress
import com.radonshadow.focusdrift.domain.model.XpReward
import com.radonshadow.focusdrift.domain.repository.FocusSessionRepository
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import com.radonshadow.focusdrift.domain.usecase.timer.CompleteSessionUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CompleteSessionUseCaseTest {

    private val focusSessionRepository: FocusSessionRepository = mock()
    private val userProgressRepository: UserProgressRepository = mock()
    private lateinit var useCase: CompleteSessionUseCase

    @Before
    fun setUp() {
        useCase = CompleteSessionUseCase(focusSessionRepository, userProgressRepository)
    }

    @Test
    fun `ordinary focus session awards base xp and coins only`() = runTest {
        val progress = UserProgress(userId = "u1", sessionsToday = 1, dailyGoalSessions = 5, currentStreak = 2)
        whenever(userProgressRepository.getUserProgress("u1")).thenReturn(progress)
        whenever(userProgressRepository.awardXp("u1", RewardConstants.XP_PER_FOCUS_SESSION))
            .thenReturn(XpReward(RewardConstants.XP_PER_FOCUS_SESSION, 0, 100, 150, 1, 1, false))

        val result = useCase(
            sessionType = SessionType.FOCUS,
            plannedDurationMs = 25 * 60_000L,
            actualDurationMs = 25 * 60_000L,
            task = "Writing",
            userId = "u1"
        )

        assertEquals(RewardConstants.XP_PER_FOCUS_SESSION, result.xpEarned)
        assertEquals(RewardConstants.COINS_PER_SESSION, result.coinsEarned)
        assertEquals(false, result.dailyGoalHit)
    }

    @Test
    fun `hitting the daily goal adds the daily bonus on top of the base reward`() = runTest {
        // sessionsToday=4, dailyGoal=5 -> this session brings the count to 5, hitting goal
        val progressBefore = UserProgress(userId = "u1", sessionsToday = 4, dailyGoalSessions = 5, currentStreak = 0)
        val progressAfter = progressBefore.copy(sessionsToday = 5, currentStreak = 1)
        whenever(userProgressRepository.getUserProgress("u1")).thenReturn(progressBefore, progressAfter)
        whenever(userProgressRepository.awardXp(eq("u1"), any()))
            .thenReturn(XpReward(0, 0, 0, 0, 1, 1, false))

        val result = useCase(
            sessionType = SessionType.FOCUS,
            plannedDurationMs = 25 * 60_000L,
            actualDurationMs = 25 * 60_000L,
            task = "",
            userId = "u1"
        )

        assertEquals(true, result.dailyGoalHit)
        assertEquals(RewardConstants.XP_PER_FOCUS_SESSION + RewardConstants.XP_DAILY_GOAL_BONUS, result.xpEarned)
        assertEquals(RewardConstants.COINS_PER_SESSION + RewardConstants.COINS_DAILY_GOAL_BONUS, result.coinsEarned)
    }

    @Test
    fun `breaks are logged but never award xp or coins`() = runTest {
        whenever(userProgressRepository.getUserProgress("u1")).thenReturn(UserProgress(userId = "u1"))

        val result = useCase(
            sessionType = SessionType.SHORT_BREAK,
            plannedDurationMs = 5 * 60_000L,
            actualDurationMs = 5 * 60_000L,
            task = "",
            userId = "u1"
        )

        assertEquals(0, result.xpEarned)
        assertEquals(0, result.coinsEarned)
    }
}
