package com.radonshadow.focusdrift.data.mapper

import com.radonshadow.focusdrift.data.local.entity.HabitEntity
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.HabitFrequency

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    userId = userId,
    name = name,
    emoji = emoji,
    frequency = HabitFrequency.valueOf(frequency),
    customDays = customDays.split(",").filter { it.isNotBlank() }.map { it.trim().toInt() }.toSet(),
    reminderEnabled = reminderEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    createdAt = createdAt,
    colorTint = colorTint
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    userId = userId,
    name = name,
    emoji = emoji,
    frequency = frequency.name,
    customDays = customDays.joinToString(","),
    reminderEnabled = reminderEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    createdAt = createdAt,
    colorTint = colorTint
)
