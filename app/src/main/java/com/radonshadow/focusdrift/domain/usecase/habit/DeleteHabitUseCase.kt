package com.radonshadow.focusdrift.domain.usecase.habit

import com.radonshadow.focusdrift.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long) = habitRepository.deleteHabit(habitId)
}
