package com.radonshadow.focusdrift.domain.usecase.habit

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.core.constants.RewardConstants
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import javax.inject.Inject

class CompleteHabitTodayUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(habitId: Long, userId: String = AppConstants.DEFAULT_USER_ID) {
        if (habitRepository.isCompletedToday(habitId)) return
        habitRepository.completeHabitToday(habitId)
        userProgressRepository.awardXp(userId, RewardConstants.XP_PER_HABIT_COMPLETE)
        userProgressRepository.awardCoins(userId, RewardConstants.COINS_PER_HABIT)
    }
}
