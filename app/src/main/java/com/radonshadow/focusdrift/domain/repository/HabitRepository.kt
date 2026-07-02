package com.radonshadow.focusdrift.domain.repository

import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.StreakInfo
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeHabits(userId: String): Flow<List<Habit>>
    suspend fun createHabit(habit: Habit): Long
    suspend fun deleteHabit(habitId: Long)
    suspend fun completeHabitToday(habitId: Long)
    suspend fun isCompletedToday(habitId: Long): Boolean
    fun observeStreakInfo(habitId: Long): Flow<StreakInfo>
    suspend fun getStreakInfo(habitId: Long): StreakInfo
}
