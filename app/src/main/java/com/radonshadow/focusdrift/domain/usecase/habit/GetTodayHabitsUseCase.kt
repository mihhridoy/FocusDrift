package com.radonshadow.focusdrift.domain.usecase.habit

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(userId: String = AppConstants.DEFAULT_USER_ID): Flow<List<Habit>> =
        habitRepository.observeHabits(userId)
}
