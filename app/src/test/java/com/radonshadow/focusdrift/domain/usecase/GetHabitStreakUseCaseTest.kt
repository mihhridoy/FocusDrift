package com.radonshadow.focusdrift.domain.usecase

import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import com.radonshadow.focusdrift.domain.usecase.habit.GetHabitStreakUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetHabitStreakUseCaseTest {

    private val habitRepository: HabitRepository = mock()
    private val useCase = GetHabitStreakUseCase(habitRepository)

    @Test
    fun `delegates to the repository for the given habit id`() = runTest {
        val streak = StreakInfo(
            habitId = 42L,
            currentStreak = 8,
            longestStreak = 12,
            isCompletedToday = true,
            isPaused = false,
            weekCompletion = listOf(true, true, true, true, true, true, false)
        )
        whenever(habitRepository.observeStreakInfo(42L)).thenReturn(flowOf(streak))

        val result = useCase(42L).first()

        assertEquals(streak, result)
    }
}
