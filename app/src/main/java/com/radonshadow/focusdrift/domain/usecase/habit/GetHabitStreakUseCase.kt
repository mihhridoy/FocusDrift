package com.radonshadow.focusdrift.domain.usecase.habit

import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitStreakUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(habitId: Long): Flow<StreakInfo> = habitRepository.observeStreakInfo(habitId)
}
