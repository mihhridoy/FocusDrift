package com.radonshadow.focusdrift.data.repository

import com.radonshadow.focusdrift.data.local.dao.HabitCompletionDao
import com.radonshadow.focusdrift.data.local.dao.HabitDao
import com.radonshadow.focusdrift.data.local.entity.HabitCompletionEntity
import com.radonshadow.focusdrift.data.mapper.toDomain
import com.radonshadow.focusdrift.data.mapper.toEntity
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) : HabitRepository {

    override fun observeHabits(userId: String): Flow<List<Habit>> =
        habitDao.observeHabits(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun createHabit(habit: Habit): Long = habitDao.insert(habit.toEntity())

    override suspend fun deleteHabit(habitId: Long) = habitDao.deleteById(habitId)

    override suspend fun completeHabitToday(habitId: Long) {
        val today = LocalDate.now().format(DATE_FORMATTER)
        completionDao.insert(
            HabitCompletionEntity(
                habitId = habitId,
                completedDate = today,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun isCompletedToday(habitId: Long): Boolean {
        val today = LocalDate.now().format(DATE_FORMATTER)
        return completionDao.isCompletedOn(habitId, today)
    }

    override fun observeStreakInfo(habitId: Long): Flow<StreakInfo> =
        completionDao.observeCompletions(habitId).map { completions ->
            buildStreakInfo(habitId, completions.map { it.completedDate }.toSet())
        }

    override suspend fun getStreakInfo(habitId: Long): StreakInfo =
        buildStreakInfo(habitId, completionDao.getCompletions(habitId).map { it.completedDate }.toSet())

    private fun buildStreakInfo(habitId: Long, completedDates: Set<String>): StreakInfo {
        val today = LocalDate.now()
        val isCompletedToday = completedDates.contains(today.format(DATE_FORMATTER))

        var streakAnchor = if (isCompletedToday) today else today.minusDays(1)
        var currentStreak = 0
        while (completedDates.contains(streakAnchor.format(DATE_FORMATTER))) {
            currentStreak++
            streakAnchor = streakAnchor.minusDays(1)
        }

        val isPaused = !isCompletedToday && currentStreak == 0 && completedDates.isNotEmpty()

        var longestStreak = 0
        var running = 0
        val sortedDates = completedDates.mapNotNull { runCatching { LocalDate.parse(it, DATE_FORMATTER) }.getOrNull() }.sorted()
        var previous: LocalDate? = null
        for (date in sortedDates) {
            running = if (previous != null && previous.plusDays(1) == date) running + 1 else 1
            longestStreak = maxOf(longestStreak, running)
            previous = date
        }
        longestStreak = maxOf(longestStreak, currentStreak)

        val weekStart = today.minusDays(today.dayOfWeek.value - 1L) // Monday
        val weekCompletion = (0..6).map { offset ->
            completedDates.contains(weekStart.plusDays(offset.toLong()).format(DATE_FORMATTER))
        }

        return StreakInfo(
            habitId = habitId,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            isCompletedToday = isCompletedToday,
            isPaused = isPaused,
            weekCompletion = weekCompletion
        )
    }
}
