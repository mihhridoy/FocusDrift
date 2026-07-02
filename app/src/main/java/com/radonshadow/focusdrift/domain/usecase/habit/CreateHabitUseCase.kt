package com.radonshadow.focusdrift.domain.usecase.habit

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.HabitFrequency
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(
        name: String,
        emoji: String,
        frequency: HabitFrequency,
        customDays: Set<Int> = emptySet(),
        reminderEnabled: Boolean = false,
        reminderHour: Int = 20,
        reminderMinute: Int = 0,
        colorTint: String,
        userId: String = AppConstants.DEFAULT_USER_ID
    ): Long = habitRepository.createHabit(
        Habit(
            userId = userId,
            name = name,
            emoji = emoji,
            frequency = frequency,
            customDays = customDays,
            reminderEnabled = reminderEnabled,
            reminderHour = reminderHour,
            reminderMinute = reminderMinute,
            createdAt = System.currentTimeMillis(),
            colorTint = colorTint
        )
    )
}
